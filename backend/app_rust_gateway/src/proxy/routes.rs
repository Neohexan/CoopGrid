use crate::config::AppConfig;
use crate::middlewares::auth_interceptor::verify_jwt_middleware;
use crate::proxy::forwarder::proxy_handler;
use crate::utils::errors::GatewayError;
use axum::{
    body::Body,
    extract::{Request, State},
    middleware,
    routing::any,
    Router,
};
use tracing::info;

/// Public Microservices Router (Bypass Token Interceptor)
/// Helper macro ya closure reusable routing logic ke liye
fn public_services_router() -> Router<AppConfig> {
    Router::new()
        // Auth Microservice Base & Wildcard (/auth and /auth/*)
        .route(
            "/auth",
            any(
                |State(cfg): State<AppConfig>, req: Request<Body>| async move {
                    proxy_handler(&cfg.auth_service_url, "/auth", req).await
                },
            ),
        )
        .route(
            "/auth/*path",
            any(
                |State(cfg): State<AppConfig>, req: Request<Body>| async move {
                    info!(
                        target: "gateway::routes",
                        method = %req.method(),
                        uri = %req.uri(),
                        target_service = "Auth-Service (Port 8002)",
                        "Routing PUBLIC request to Auth Microservice"
                    );
                    proxy_handler(&cfg.auth_service_url, "/auth", req).await
                },
            ),
        )
        // Alias Route (/uvw and /uvw/*)
        .route(
            "/uvw",
            any(
                |State(cfg): State<AppConfig>, req: Request<Body>| async move {
                    proxy_handler(&cfg.auth_service_url, "/uvw", req).await
                },
            ),
        )
        .route(
            "/uvw/*path",
            any(
                |State(cfg): State<AppConfig>, req: Request<Body>| async move {
                    info!(
                        target: "gateway::routes",
                        method = %req.method(),
                        uri = %req.uri(),
                        target_service = "Auth-Service (Port 8002)",
                        "Routing PUBLIC request via ALIAS (/uvw) to Auth Microservice"
                    );
                    proxy_handler(&cfg.auth_service_url, "/uvw", req).await
                },
            ),
        )
}

/// Protected Microservices Router (JWT Interceptor Applied)
pub fn protected_services_router(config: AppConfig) -> Router<AppConfig> {
    Router::new()
        // Base route: /profile -> Downstream /profile ya /
        .route(
            "/profile",
            any(|State(cfg): State<AppConfig>, req: Request<Body>| async move {
                proxy_handler(&cfg.profile_service_url, "/profile", req).await
            }),
        )
        // Sub-routes: /profile/me, /profile/settings, etc.
        .route(
            "/profile/*path",
            any(|State(cfg): State<AppConfig>, req: Request<Body>| async move {
                info!(
                    target: "gateway::routes",
                    method = %req.method(),
                    uri = %req.uri(),
                    target_service = "Profile-Service (Python Port 8004)",
                    "🔑 [JWT PASSED] Forwarding enriched request to Profile Microservice"
                );
                proxy_handler(&cfg.profile_service_url, "/profile", req).await
            }),
        )
        // Media Microservice Protected Routes (Audio/Video Streaming & Storage)
        .route(
            "/media/*path",
            any(|State(cfg): State<AppConfig>, req: Request<Body>| async move {
                proxy_handler(&cfg.media_service_url, "/media", req).await
            }),
        )
        // Layer enforcement: Sirf is Router tree par JWT Interceptor run hoga
        .layer(middleware::from_fn_with_state(
            config,
            verify_jwt_middleware,
        ))
}

/// Central Gateway Router Builder
pub fn build_gateway_routes(config: AppConfig) -> Router<AppConfig> {
    Router::new()
        .merge(public_services_router())
        .merge(protected_services_router(config))
        .fallback(any(|| async {
            Err::<(), GatewayError>(GatewayError::NotFound(
                "Requested route does not exist on Gateway".to_string(),
            ))
        }))
}
