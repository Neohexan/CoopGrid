import asyncio
import logging
import httpx
from fastapi import FastAPI

# Custom Logger Config
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | [CENTRAL MONITOR] | %(message)s",
    datefmt="%H:%M:%S"
)
logger = logging.getLogger("CentralHeartbeat")

GATEWAY_TARGETS = {
    "APP_GATEWAY": {"url": "http://127.0.0.1:8000/health", "status": "UNKNOWN"},
    "ADMIN_GATEWAY": {"url": "http://127.0.0.1:8001/health", "status": "UNKNOWN"}
}

async def pulse_check_loop():
    logger.info("🚀 Starting Central Server Heartbeat Monitor (Interval: 3s)...")
    
    async with httpx.AsyncClient(timeout=1.5) as client:
        while True:
            logger.info("--------------------------------------------------")
            logger.info("💓 PINGING GATEWAYS...")
            
            for name, config in GATEWAY_TARGETS.items():
                try:
                    res = await client.get(config["url"])
                    if res.status_code == 200:
                        config["status"] = "🟢 ONLINE"
                        logger.info(f"   ↳ {name} (Port {config['url'].split(':')[-1].split('/')[0]}): 🟢 CONNECTED (200 OK)")
                    else:
                        config["status"] = "🟡 DEGRADED"
                        logger.warning(f"   ↳ {name}: 🟡 BAD RESPONSE ({res.status_code})")
                except Exception:
                    config["status"] = "🔴 OFFLINE"
                    logger.error(f"   ↳ {name}: 🔴 DISCONNECTED (Server Down)")
            
            # Har 3 second mein repeat hoga
            await asyncio.sleep(3)

def setup_central_server_heartbeat(app: FastAPI):
    # App lifespan ya startup
    @app.on_event("startup")
    async def start_pulse():
        asyncio.create_task(pulse_check_loop())

    @app.get("/system-health")
    async def get_system_health():
        return GATEWAY_TARGETS