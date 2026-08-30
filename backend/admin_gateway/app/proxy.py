from __future__ import annotations

from typing import Dict, Iterable, Optional

import httpx
from fastapi import HTTPException, Request
from fastapi.responses import Response

HOP_BY_HOP_HEADERS = {
	"connection",
	"keep-alive",
	"proxy-authenticate",
	"proxy-authorization",
	"te",
	"trailer",
	"transfer-encoding",
	"upgrade",
	"host",
}


def _filter_headers(headers: Iterable[tuple[str, str]]) -> Dict[str, str]:
	return {
		key: value
		for key, value in headers
		if key.lower() not in HOP_BY_HOP_HEADERS
	}


async def forward_request(
	request: Request,
	target_base_url: str,
	downstream_path: str = "",
	timeout_seconds: float = 30.0,
) -> Response:
	"""Incoming request ko downstream service tak bhejo aur uska response wapas do."""
	target_url = f"{target_base_url.rstrip('/')}/{downstream_path.lstrip('/')}"

	try:
		body = await request.body()
		headers = _filter_headers(request.headers.items())

		async with httpx.AsyncClient(timeout=timeout_seconds) as client:
			downstream_response = await client.request(
				method=request.method,
				url=target_url,
				params=request.query_params,
				content=body,
				headers=headers,
			)
	except httpx.RequestError as exc:
		raise HTTPException(
			status_code=502,
			detail=f"Gateway could not connect to downstream service: {exc}",
		) from exc

	response_headers: Optional[Dict[str, str]] = _filter_headers(
		downstream_response.headers.items()
	)
	return Response(
		content=downstream_response.content,
		status_code=downstream_response.status_code,
		headers=response_headers,
		media_type=downstream_response.headers.get("content-type"),
	)
