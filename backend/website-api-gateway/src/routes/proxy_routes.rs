use crate::app_state::AppState;
use crate::middleware::auth::auth_middleware;
use crate::proxy::forwarder::proxy_handler;
use axum::{
    body::Body,
    extract::{Path, State},
    http::Request,
    middleware::from_fn_with_state,
    response::Response,
    routing::any,
    Router,
};

/// 🌐 Main Router Entrypoint
pub fn create_router(state: AppState) -> Router {
    let public_router = Router::new().route(
        "/api/auth/{*path}",
        any(|
            State(state): State<AppState>,
             Path(path): Path<String>, 
             req: Request<Body>| async move {
            let base_url = state.config.auth_service_url.clone();
            forward_to_service(state, path, req, &base_url).await
        }),
    );

    let private_router =
        Router::new()
            .route(
                "/api/auth/private/{*path}",
                any(
                    |State(state): State<AppState>,
                     Path(path): Path<String>,
                     req: Request<Body>| async move {
                        let base_url = state.config.auth_service_url.clone();
                        forward_to_service(state, path, req, &base_url).await
                    },
                ),
            )
            .route(
                "/api/profile/{*path}",
                any(
                    |State(state): State<AppState>,
                     Path(path): Path<String>,
                     req: Request<Body>| async move {
                        let base_url = state.config.profile_service_url.clone();
                        forward_to_service(state, path, req, &base_url).await
                    },
                ),
            )
            .route(
                "/api/media/{*path}",
                any(
                    |State(state): State<AppState>,
                     Path(path): Path<String>,
                     req: Request<Body>| async move {
                        let base_url = state.config.media_service_url.clone();
                        forward_to_service(state, path, req, &base_url).await
                    },
                ),
            )
            // Sabhi private routes par auth middleware enforce karna
            .route_layer(from_fn_with_state(state.clone(), auth_middleware));

    Router::new()
        .merge(public_router)
        .merge(private_router)
        .with_state(state)
}

/// 🛠️ Common Helper: Code duplication hatane ke liye reusable function
async fn forward_to_service(
    state: AppState,
    path: String,
    req: Request<Body>,
    service_base_url: &str,
) -> Result<Response<Body>, crate::error::AppError> {
    let base_url = service_base_url.trim_end_matches('/');
    let query = req
        .uri()
        .query()
        .map(|q| format!("?{}", q))
        .unwrap_or_default();
    let target_url = format!("{}/{}{}", base_url, path, query);

    proxy_handler(State(state), target_url, req).await
}
