from __future__ import annotations

from typing import Dict

from fastapi import APIRouter, HTTPException, Request
from fastapi.responses import JSONResponse
import logging

from .proxy import forward_request
from .services.map import SERVICE_MAP


router = APIRouter()

# logger for gateway router — use Hinglish message templates for errors/info
logger = logging.getLogger("gateway.router")

# runtime-updated map of service health/status filled by the heartbeat monitor
SERVICE_STATUS: Dict[str, Dict] = {}

# Service base URLs are defined in app.services.map.SERVICE_MAP


@router.get("/health")
async def health_check() -> JSONResponse:
	return JSONResponse(
		{
			"service": "gateway",
			"status": "ok",
			"services": SERVICE_MAP,
			"statuses": SERVICE_STATUS,
			"available_services": {
				name: info
				for name, info in SERVICE_STATUS.items()
				if info.get("healthy")
			},
		}
	)


@router.get("/heartbeat")
@router.post("/heartbeat")
async def heartbeat_check() -> JSONResponse:
	return JSONResponse(
		{
			"service": "gateway-heartbeat",
			"status": "ok",
			"services": SERVICE_MAP,
			"statuses": SERVICE_STATUS,
		}
	)


@router.get("/centralserverhealth")
async def Central_server_health(request: Request) -> JSONResponse:
	"""Return profile service health: proxy to /health or use recorded status.

	Logs problems in Hinglish so they are easy to scan in logs.
	"""
	service = "centralserver"
	target = _resolve_target(service, require_healthy=False)

	try:
		resp = await forward_request(request=request, target_base_url=target, downstream_path="health")
		logger.info("Central server service sehat hai (status=%s)", getattr(resp, "status_code", "unknown"))
		return resp
	except HTTPException as exc:
		logger.error("Central server health check fail hua — gateway se connect nahi ho paaya: %s", getattr(exc, "detail", str(exc)))
		status = SERVICE_STATUS.get(service, {"healthy": False, "error": getattr(exc, "detail", str(exc))})
		logger.info("Central server ka recorded status: %s", status)
		return JSONResponse({"service": service, "status": status})


def _resolve_target(service: str, require_healthy: bool = True) -> str:
	"""Return the target base URL for a service.

	If `require_healthy` is True and the heartbeat monitor has recorded a status
	showing the service is unhealthy, raise HTTP 503 so callers get a clear
	"service unavailable" response. If the monitor has no record yet, assume
	the service may be available and return the target to avoid blocking the
	gateway when some statuses are unknown.
	"""
	target = SERVICE_MAP.get(service)
	if not target:
		raise HTTPException(status_code=404, detail=f"Unknown service '{service}'")

	if require_healthy:
		status = SERVICE_STATUS.get(service)
		if status is not None and not status.get("healthy", False):
			raise HTTPException(
				status_code=503,
				detail=(
					f"Service '{service}' is currently unavailable"
					f" (checked: {status.get('last_checked')}, error: {status.get('error')})"
				),
			)

	return target


@router.api_route("/{service}", methods=["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"])
async def forward_service_root(service: str, request: Request):
	target = _resolve_target(service, require_healthy=False)

	# if monitor says this service is unhealthy, but other services are healthy,
	# return a friendly informational response (no error) indicating how many
	# services are online and their names.
	status = SERVICE_STATUS.get(service)
	healthy = [name for name, info in SERVICE_STATUS.items() if info.get("healthy")]
	if status is not None and not status.get("healthy", False):
		if healthy:
			return JSONResponse(
				{
					"detail": f"Service '{service}' is currently unavailable; {len(healthy)} service(s) online. Continuing to serve available services.",
					"available_services": healthy,
				}
			)
		raise HTTPException(status_code=503, detail=f"Service '{service}' is currently unavailable")

	try:
		resp = await forward_request(request=request, target_base_url=target)
	except HTTPException as exc:
		# upstream connectivity error — if any other services are healthy, return informational response
		if healthy:
			return JSONResponse(
				{
					"detail": f"Request to '{service}' failed ({exc.detail}); {len(healthy)} service(s) online.",
					"available_services": healthy,
				}
			)
		raise

	# If downstream returned an error (e.g., 404) but other services are healthy,
	# return informational response instead of propagating the error.
	if getattr(resp, "status_code", 200) >= 400 and healthy:
		return JSONResponse(
			{
				"detail": f"Service '{service}' responded with status {resp.status_code}; {len(healthy)} service(s) online.",
				"available_services": healthy,
			}
		)

	return resp


@router.api_route(
	"/{service}/{full_path:path}",
	methods=["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"],
)
async def forward_service_path(service: str, full_path: str, request: Request):
	target = _resolve_target(service, require_healthy=False)

	status = SERVICE_STATUS.get(service)
	healthy = [name for name, info in SERVICE_STATUS.items() if info.get("healthy")]
	if status is not None and not status.get("healthy", False):
		if healthy:
			return JSONResponse(
				{
					"detail": f"Service '{service}' is currently unavailable; {len(healthy)} service(s) online. Continuing to serve available services.",
					"available_services": healthy,
				}
			)
		raise HTTPException(status_code=503, detail=f"Service '{service}' is currently unavailable")

	try:
		resp = await forward_request(
			request=request,
			target_base_url=target,
			downstream_path=full_path,
		)
	except HTTPException as exc:
		if healthy:
			return JSONResponse(
				{
					"detail": f"Request to '{service}' failed ({exc.detail}); {len(healthy)} service(s) online.",
					"available_services": healthy,
				}
			)
		raise

	if getattr(resp, "status_code", 200) >= 400 and healthy:
		return JSONResponse(
			{
				"detail": f"Service '{service}' responded with status {resp.status_code}; {len(healthy)} service(s) online.",
				"available_services": healthy,
			}
		)

	return resp
