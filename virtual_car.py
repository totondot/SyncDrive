import asyncio
import json
import math
import os

try:
    import websockets
except ModuleNotFoundError as exc:
    raise ModuleNotFoundError(
        "Missing dependency 'websockets'. Install it with "
        "'.\\.venv\\Scripts\\python.exe -m pip install -r requirements.txt' "
        "or configure your IDE to use the project's '.venv' interpreter."
    ) from exc


class VirtualCar:
    def __init__(self):
        # Starting coordinates (San Francisco)
        self.lat = 37.7749
        self.lon = -122.4194

        # Telemetry
        self.speed_kmh = 40.0
        self.heading_degrees = 90.0  # Moving East
        self.battery = 100.0

        # State
        self.is_moving = True
        self.doors_locked = False

    def update_physics(self):
        """Simulate real-world physics by updating GPS from speed and heading."""
        if not self.is_moving or self.speed_kmh <= 0:
            return

        # Rough estimation: 1 degree latitude is about 111 km.
        distance_km_per_sec = self.speed_kmh / 3600.0
        degrees_per_sec = distance_km_per_sec / 111.0

        rad = math.radians(self.heading_degrees)
        self.lat += degrees_per_sec * math.cos(rad)
        self.lon += degrees_per_sec * math.sin(rad)

        self.battery -= 0.005


car = VirtualCar()


async def handle_client(websocket):
    print("[APP] Connected to Virtual Car")

    async def broadcast_telemetry():
        while True:
            car.update_physics()

            payload = {
                "type": "TELEMETRY_UPDATE",
                "location": {"lat": car.lat, "lon": car.lon},
                "telemetry": {
                    "speed": car.speed_kmh,
                    "heading": car.heading_degrees,
                },
                "status": {
                    "battery": int(car.battery),
                    "locked": car.doors_locked,
                },
            }

            await websocket.send(json.dumps(payload))
            await asyncio.sleep(1)

    async def listen_for_commands():
        async for message in websocket:
            command = json.loads(message)
            action = command.get("action")
            payload = command.get("payload", {})

            print(f"[APP] Received Action: {action}")

            if action == "EMERGENCY_HALT":
                car.speed_kmh = 0
                car.is_moving = False
                print("   [SAFETY] Brakes applied. Vehicle stopped.")

            elif action == "TOGGLE_DOORS":
                car.doors_locked = payload.get("locked", True)
                state = "LOCKED" if car.doors_locked else "UNLOCKED"
                print(f"   [DOORS] Doors are now {state}")

            elif action == "SET_LOCATION":
                car.lat = payload.get("lat", car.lat)
                car.lon = payload.get("lon", car.lon)
                car.is_moving = False
                car.speed_kmh = 0
                print(f"   [GPS] Car teleported to: {car.lat}, {car.lon} and is in PARK.")

            elif action == "SET_DESTINATION":
                car.is_moving = True
                car.speed_kmh = 45.0
                print(
                    f"   [NAV] New route to {payload.get('lat')}, "
                    f"{payload.get('lon')}"
                )

            elif action == "START_SUMMON":
                car.is_moving = True
                car.speed_kmh = 10.0
                print("   [SUMMON] Activated. Car is creeping towards user.")

            elif action == "CANCEL_SUMMON":
                car.speed_kmh = 0
                car.is_moving = False
                print("   [SUMMON] Cancelled. Car halted.")

            elif action == "SET_CLIMATE":
                state = payload.get("state", "ON")
                temp = payload.get("temp", 72)
                print(f"   [CLIMATE] A/C turned {state}, target temp set to {temp} deg")

            elif action == "RUN_DIAGNOSTICS":
                print("   [DIAGNOSTICS] Scanning sensors...")
                await asyncio.sleep(1)
                print("   [DIAGNOSTICS] All systems GREEN. Tire pressure optimal.")

            else:
                print(f"   [WARN] Unknown command received: {action}")

    try:
        await asyncio.gather(broadcast_telemetry(), listen_for_commands())
    except websockets.exceptions.ConnectionClosed:
        print("[APP] Disconnected")
    except Exception as exc:
        print(f"[ERROR] Unexpected websocket error: {exc}")


async def main():
    print("[CAR] Virtual Car Engine Started in the Cloud...")

    port = int(os.environ.get("PORT", 8765))

    async with websockets.serve(handle_client, "0.0.0.0", port):
        await asyncio.Future()


if __name__ == "__main__":
    asyncio.run(main())
