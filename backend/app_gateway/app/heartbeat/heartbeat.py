import asyncio
import logging
import httpx
from fastapi import FastAPI

# ANSI Formatting
CYAN = "\033[36m"
GREEN = "\033[32m"
YELLOW = "\033[33m"
RED = "\033[31m"
BOLD = "\033[1m"
RESET = "\033[0m"

def setup_app_gateway_heartbeat(app: FastAPI):
    @app.get("/health")
    async def health_check():
        print(f"{CYAN}{BOLD}[APP GATEWAY 8001]{RESET} 📡 Central Server Ping -> {GREEN}{BOLD}🟢 200 OK (HEALTHY){RESET}")
        return {"service": "App Gateway", "port": 8001, "status": "ONLINE"}

async def monitor_services():
    """Background loop with Vibrant Cyan/Blue Logging"""
    print(f"{CYAN}{BOLD}🚀 App Gateway Heartbeat Monitor Engine Active [Interval: 3s]{RESET}")
    
    async with httpx.AsyncClient(timeout=1.5) as client:
        while True:
            try:
                res = await client.get("http://127.0.0.1:8003/")
                if res.status_code == 200:
                    print(f"{CYAN}[APP GATEWAY]{RESET} ──► {BOLD}Central Server (8003):{RESET} {GREEN}🟢 LINK ACTIVE (200 OK){RESET}")
                else:
                    print(f"{CYAN}[APP GATEWAY]{RESET} ──► {BOLD}Central Server (8003):{RESET} {YELLOW}🟡 DEGRADED ({res.status_code}){RESET}")
            except Exception:
                print(f"{CYAN}[APP GATEWAY]{RESET} ──► {BOLD}Central Server (8003):{RESET} {RED}{BOLD}🔴 LINK DOWN (Unreachable){RESET}")
            
            await asyncio.sleep(3)