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
fn protected_services_router(config: AppConfig) -> Router<AppConfig> {
    Router::new()
        // Other / Profile Microservice
        .route(
            "/other",
            any(
                |State(cfg): State<AppConfig>, req: Request<Body>| async move {
                    proxy_handler(&cfg.profile_service_url, "/other", req).await
                },
            ),
        )
        .route(
            "/other/*path",
            any(
                |State(cfg): State<AppConfig>, req: Request<Body>| async move {
                    info!(
                        target: "gateway::routes",
                        method = %req.method(),
                        uri = %req.uri(),
                        target_service = "Profile-Service (Port 8004)",
                        "Routing PROTECTED request to Downstream Service"
                    );
                    proxy_handler(&cfg.profile_service_url, "/other", req).await
                },
            ),
        )
        // Middleware strictly layer-wise applied on protected routes only
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
