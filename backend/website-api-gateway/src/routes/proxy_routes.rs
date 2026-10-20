use crate::app_state::AppState;
use crate::middleware::auth::auth_middleware;
use crate::proxy::forwarder::proxy_handler;
use axum::{
    body::Body, extract::State, http::Request, middleware::from_fn_with_state, routing::any, Router,
};

pub fn create_router(state: AppState) -> Router {

    // 1. PUBLIC ROUTES (Jaise Login): Inpar auth middleware nahi lagega
    let public_routes = Router::new().route(
        "/api/auth/login",
        any(|state: State<AppState>, req: Request<Body>| async move {
            let target_url = state.config.auth_service_url.clone();
            proxy_handler(state, target_url, req).await
        }),
    );

    // Sabhi incoming requests ke liye proxy handler jo auth middleware se hokar guzrega
    let private_routes = Router::new()
        // Example: User service routes
        .route(
            "/api/profile/{*path}",
            any(|state: State<AppState>, req: Request<Body>| async move {
                let target_url = state.config.profile_service_url.clone();
                proxy_handler(state, target_url, req).await
            }),
        )
        .route(
            "/api/media/{*path}",
            any(|state: State<AppState>, req: Request<Body>| async move {
                let target_url = state.config.media_service_url.clone();
                proxy_handler(state, target_url, req).await
            }),
        )
        // Aap yahan aur bhi private services add kar sakte hain (e.g., Orders, Products, etc.)
        .route_layer(from_fn_with_state(state.clone(), auth_middleware));

    // Dono routers ko merge karna
    Router::new()
    .merge(private_routes)
    .merge(public_routes)
    .with_state(state)
}

pub mod mod_file {
    // helper for mod.rs
}
