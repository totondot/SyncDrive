import asyncio
import json
import math

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
        # Starting Coordinates (San Francisco)
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
        """Simulate real-world physics: Update GPS based on speed and heading."""
        if not self.is_moving or self.speed_kmh <= 0:
            return

        # Convert speed from km/h to degrees of latitude/longitude per second
        # (Rough estimation: 1 degree latitude is ~111 km)
        distance_km_per_sec = (self.speed_kmh / 3600.0)
        degrees_per_sec = distance_km_per_sec / 111.0

        # Calculate new position based on heading
        rad = math.radians(self.heading_degrees)
        self.lat += degrees_per_sec * math.cos(rad)
        self.lon += degrees_per_sec * math.sin(rad)

        # Drain battery slightly as we drive
        self.battery -= 0.005


car = VirtualCar()


async def handle_client(websocket):
    print("[APP] Connected to Virtual Car")

    async def broadcast_telemetry():
        while True:
            # 1. Update the car's physics
            car.update_physics()

            # 2. Package the sensor data as JSON
            payload = {
                "type": "TELEMETRY_UPDATE",
                "location": {"lat": car.lat, "lon": car.lon},
                "telemetry": {"speed": car.speed_kmh, "heading": car.heading_degrees},
                "status": {"battery": int(car.battery), "locked": car.doors_locked}
            }

            # 3. Send it to the Android App
            await websocket.send(json.dumps(payload))
            await asyncio.sleep(1)  # Broadcast every 1 second

    async def listen_for_commands():
        async for message in websocket:
            # Parse the incoming JSON message
            command = json.loads(message)
            action = command.get("action")
            payload = command.get("payload", {})

            print(f"[APP] Received Action: {action}")

            # 1. Emergency & Doors
            if action == "EMERGENCY_HALT":
                car.speed_kmh = 0
                car.is_moving = False
                print("   [SAFETY] Brakes applied. Vehicle stopped.")

            elif action == "TOGGLE_DOORS":
                car.doors_locked = payload.get("locked", True)
                state = "LOCKED" if car.doors_locked else "UNLOCKED"
                print(f"   [DOORS] Doors are now {state}")

                # 2. Location & Routing
            elif action == "SET_LOCATION":
                car.lat = payload.get("lat", car.lat)
                car.lon = payload.get("lon", car.lon)

                # --- ADD THESE TWO LINES TO STOP THE DRIFT ---
                car.is_moving = False
                car.speed_kmh = 0
                # ---------------------------------------------

                print(f"   📍 Car teleported to: {car.lat}, {car.lon} and is in PARK.")
            elif action == "SET_DESTINATION":
                car.is_moving = True
                car.speed_kmh = 45.0  # Normal driving speed
                print(f"   [NAV] New route to {payload.get('lat')}, {payload.get('lon')}")

            # 3. Summon Feature
            elif action == "START_SUMMON":
                car.is_moving = True
                car.speed_kmh = 10.0  # Slow, safe speed for parking lots
                print("   [SUMMON] Activated. Car is creeping towards user.")

            elif action == "CANCEL_SUMMON":
                car.speed_kmh = 0
                car.is_moving = False
                print("   [SUMMON] Cancelled. Car halted.")

            # 4. Climate Control
            elif action == "SET_CLIMATE":
                state = payload.get("state", "ON")
                temp = payload.get("temp", 72)
                print(f"   [CLIMATE] A/C turned {state}, target temp set to {temp} deg")

            # 5. Diagnostics & Maintenance
            elif action == "RUN_DIAGNOSTICS":
                print("   [DIAGNOSTICS] Scanning sensors...")
                await asyncio.sleep(1)  # Simulate scan time
                print("   [DIAGNOSTICS] All systems GREEN. Tire pressure optimal.")

            else:
                print(f"   [WARN] Unknown command received: {action}")

    try:
        # Run both the broadcaster and the listener at the same time
        await asyncio.gather(broadcast_telemetry(), listen_for_commands())
    except websockets.exceptions.ConnectionClosed:
        print("📱 App Disconnected (Phone closed the app).")
    except Exception as e:
        print(f"⚠️ Unexpected Error: {e}")


async def main():
    print("🚗 Virtual Car Engine Started Locally on port 8766...")

    # Listening on 0.0.0.0 allows devices on the same Wi-Fi to connect
    async with websockets.serve(handle_client, "0.0.0.0", 8766):
        await asyncio.Future()


if __name__ == "__main__":
    asyncio.run(main())
