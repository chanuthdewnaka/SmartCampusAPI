# SmartCampusAPI

**Module:** 5COSC022W – Client-Server Architectures  
**Student Name:** Batuwitage Chanuth Dewnaka  
**Student ID:** 20240292
**UoW ID:** w2153169  
**GitHub Repository:** https://github.com/chanuthdewnaka/SmartCampusAPI

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [How to Build and Run](#how-to-build-and-run)
3. [API Endpoints Reference](#api-endpoints-reference)
4. [Sample curl Commands](#sample-curl-commands)
5. [Report – Written Answers](#report--written-answers)

---

## Project Overview

This project implements a RESTful API for the Smart Campus Sensor and Room
Management system. It is built using JAX-RS (Jersey 2.32) deployed on
Apache Tomcat, developed in Apache NetBeans.

The API manages three types of resources:

- **Rooms** – Physical spaces on campus (e.g. labs, libraries)
- **Sensors** – Devices installed inside rooms that measure things like
  temperature or CO2 levels
- **Sensor Readings** – Individual data points recorded by a sensor

All data is stored in-memory using a Singleton DataStore class. No external
database is used.

---

## How to Build and Run

### Requirements

- Apache NetBeans (version 18 or later)
- Apache Tomcat 9.x
- Java JDK 8 or later
- Maven (included with NetBeans)
- Postman (for testing)

### Steps

**Step 1** – Clone the repository.

Open Command Prompt and type this, then press Enter:

```
git clone https://github.com/chanuthdewnaka/SmartCampusAPI.git
```

**Step 2** – Open NetBeans. Go to **File** → **Open Project**.
Navigate to the cloned folder and select it. Click **Open Project**.

**Step 3** – Right-click the project in the Projects panel.
Click **Clean and Build**. Wait until you see `BUILD SUCCESS`
in the Output panel at the bottom.

**Step 4** – Right-click the project again. Click **Run**.
Tomcat will start automatically.

**Step 5** – The API is now available at:

```
http://localhost:8080/SmartCampusAPI/api/v1/
```

---

## API Endpoints Reference

### Base URL

```
http://localhost:8080/SmartCampusAPI/api/v1
```

---

### Discovery

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Returns API version, description, and available resource links |

---

### Rooms

| Method | Path | Description | Success Code |
|--------|------|-------------|--------------|
| GET | `/rooms` | Returns a list of all rooms | 200 OK |
| POST | `/rooms` | Creates a new room | 201 Created |
| GET | `/rooms/{roomId}` | Returns a specific room by ID | 200 OK |
| DELETE | `/rooms/{roomId}` | Deletes a room (only if it has no sensors) | 204 No Content |

**Room JSON structure:**

```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 50,
  "sensorIds": ["TEMP-001"]
}
```

**Possible error responses for Rooms:**

| Scenario | HTTP Status | Description |
|----------|-------------|-------------|
| Room ID not found | 404 Not Found | The requested room does not exist |
| Delete room with sensors | 409 Conflict | Cannot delete a room that still has sensors assigned |

---

### Sensors

| Method | Path | Description | Success Code |
|--------|------|-------------|--------------|
| GET | `/sensors` | Returns all sensors. Supports `?type=` filter | 200 OK |
| POST | `/sensors` | Registers a new sensor | 201 Created |
| GET | `/sensors/{sensorId}` | Returns a specific sensor by ID | 200 OK |

**Sensor JSON structure:**

```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}
```

**Query parameter:**

| Parameter | Example | Description |
|-----------|---------|-------------|
| `type` | `?type=CO2` | Filters sensors by their type |

**Possible error responses for Sensors:**

| Scenario | HTTP Status | Description |
|----------|-------------|-------------|
| Sensor ID not found | 404 Not Found | The requested sensor does not exist |
| POST with invalid roomId | 422 Unprocessable Entity | The specified room does not exist |

---

### Sensor Readings

| Method | Path | Description | Success Code |
|--------|------|-------------|--------------|
| GET | `/sensors/{sensorId}/readings` | Returns all readings for a sensor | 200 OK |
| POST | `/sensors/{sensorId}/readings` | Adds a new reading to a sensor | 201 Created |

**SensorReading JSON structure:**

```json
{
  "id": "auto-generated-uuid",
  "timestamp": 1718000000000,
  "value": 23.4
}
```

When posting a new reading, you only need to provide the value:

```json
{
  "value": 23.4
}
```

**Possible error responses for Readings:**

| Scenario | HTTP Status | Description |
|----------|-------------|-------------|
| Sensor ID not found | 404 Not Found | The parent sensor does not exist |
| Sensor under MAINTENANCE | 403 Forbidden | Cannot post readings to a sensor in maintenance mode |

---

### Standard Error Response Format

All errors return a consistent JSON structure:

```json
{
  "errorMessage": "A human-readable description of what went wrong",
  "errorCode": 404,
  "documentation": "https://smartcampus.edu/api/docs/errors"
}
```

---

## Sample curl Commands

These commands can be run in any terminal to test the API while it is running.

**Get the discovery information:**

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/
```

**Get all rooms:**

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

**Create a new room:**

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CS-205\",\"name\":\"CS Seminar Room\",\"capacity\":25}"
```

**Get a specific room:**

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

**Delete a room that has no sensors (first delete its sensors or use an empty room):**

```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/CS-205
```

**Try to delete a room that still has sensors (expect 409 error):**

```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

**Get all sensors:**

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors
```

**Get sensors filtered by type:**

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```

**Create a new sensor with a valid roomId:**

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"HUM-001\",\"type\":\"Humidity\",\"status\":\"ACTIVE\",\"currentValue\":60.0,\"roomId\":\"LIB-301\"}"
```

**Try to create a sensor with an invalid roomId (expect 422 error):**

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"HUM-002\",\"type\":\"Humidity\",\"status\":\"ACTIVE\",\"currentValue\":60.0,\"roomId\":\"FAKE-999\"}"
```

**Get all readings for a sensor:**

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

**Post a new sensor reading:**

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":24.7}"
```

---

## Report – Written Answers

### Part 1, Task 1 – JAX-RS Resource Lifecycle

By default, JAX-RS creates a **new instance** of every resource class for
each incoming HTTP request. This is called the per-request lifecycle.

This matters for data storage. If you stored data as an instance variable
inside a resource class, it would be created fresh with every request and
immediately thrown away at the end of that request. Any data added by a
POST request would vanish before the next GET request could see it.

To solve this, all data in this project is stored in a **Singleton
DataStore** (`DataStore.getInstance()`). The Singleton is created once
when the application starts and lives until the server shuts down. Every
resource class calls `DataStore.getInstance()` and gets the exact same
shared object, so all data persists correctly across requests.

---

### Part 1, Task 2 – HATEOAS

HATEOAS stands for Hypermedia as the Engine of Application State. It is a
REST design principle where API responses include links to related
resources and available actions, rather than forcing the client to
hard-code URLs.

**How it is implemented here:**

The discovery endpoint (`GET /api/v1/`) returns links to the main
resources:

```json
{
  "apiVersion": "1.0",
  "description": "Smart Campus Sensor and Room Management API",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

**Why this benefits API developers:**

A client application does not need to know the URL structure in advance.
It starts at the discovery endpoint, reads the links from the response,
and navigates from there. If the server changes a URL in the future, the
client automatically gets the updated link without any code changes. It
also makes the API self-documenting.

---

### Part 2, Task 1 – Design Decision: Returning IDs vs Full Objects

The `Room` model stores a list of `sensorIds` (String IDs) rather than
full embedded `Sensor` objects.

**Advantages of storing IDs:**

- Prevents circular references. A Sensor has a `roomId` field, and if Room
  also embedded full Sensor objects, the JSON would loop infinitely.
- Keeps responses smaller. A room with 20 sensors would otherwise return
  all sensor data every time you request the room.
- Follows REST convention. Each resource has its own canonical URL. If
  you need sensor details, you call `/sensors/{sensorId}`.

**Trade-off:** The client must make extra requests to get full sensor
details. This is acceptable because it follows the single-responsibility
principle — each endpoint does one job well.

---

### Part 2, Task 2 – DELETE Idempotency

The HTTP specification states that DELETE must be idempotent, meaning
calling it multiple times produces the same server state as calling it
once.

**How this is handled in the implementation:**

- First DELETE request: The room exists, gets removed, server returns
  204 No Content.
- Second DELETE request (same room ID): The room no longer exists,
  `DataStore.getRoomById()` returns null, the server throws a
  `RoomNotFoundException`, and returns 404 Not Found.

The server state after both calls is identical — the room does not exist.
The HTTP status code differs (204 vs 404), but the state of the data is
the same. This satisfies the definition of idempotency because the
resource is absent in both cases.

---

### Part 3, Task 1 – @Consumes and Content-Type Mismatch

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that
a method only accepts requests where the `Content-Type` header is set to
`application/json`.

If a client sends a request with `Content-Type: text/plain` or omits
the header entirely, JAX-RS automatically rejects the request and returns
**HTTP 415 Unsupported Media Type** before the method body is even
executed. The developer does not need to write any code to handle this —
the framework enforces it automatically through the annotation.

---

### Part 3, Task 2 – @QueryParam Filtering vs Path-Based Filtering

There are two approaches for filtering sensors by type:

- **Path-based:** `GET /sensors/type/CO2`
- **Query parameter:** `GET /sensors?type=CO2`

The query parameter approach (`@QueryParam`) is used in this project
because:

1. **Semantic correctness:** A path like `/sensors/type/CO2` implies
   `type/CO2` is a unique resource identifier — it is not. A query
   parameter correctly expresses that you are filtering a collection.

2. **REST convention:** The REST standard uses query parameters for
   filtering, searching, and sorting operations on collections.

3. **Optional filtering:** With `@QueryParam`, if the parameter is not
   provided, the method returns all sensors. A path-based approach would
   require a separate method or route.

4. **Multiple filters:** Query parameters scale easily. You could add
   `?type=CO2&status=ACTIVE` without changing the URL structure.

---

### Part 4 – Sub-Resource Locator Design

The `@Path("/{sensorId}/readings")` method in `SensorResource` acts as
a Sub-Resource Locator. Instead of returning a response directly, it
returns an instance of `SensorReadingResource`, which JAX-RS then uses
to handle the remainder of the request path.

**Benefits of this pattern:**

- **Separation of concerns:** All reading logic is in
  `SensorReadingResource`. All sensor logic is in `SensorResource`.
  Neither class is cluttered with the other's responsibilities.
- **Reusability:** `SensorReadingResource` could in theory be reused
  under a different parent path without duplication.
- **Easier testing:** Each class can be tested independently.
- **Readability:** The URL hierarchy (`/sensors/{id}/readings`) is
  clearly reflected in the code hierarchy.

---

### Part 5, Task 1 – HTTP 409 Conflict for Non-Empty Room Deletion

When a DELETE request is made for a room that still has sensors, the API
returns **409 Conflict** rather than allowing the deletion.

**Why 409 is the correct status code:**

A 404 would mean the room was not found — it was found, so 404 is wrong.
A 400 means the request is malformed — the request is valid, so 400 is
wrong. A 409 Conflict means "the request is valid, but it conflicts with
the current state of the resource." Deleting a room with active sensors
would leave those sensors without a parent room, breaking referential
integrity. The 409 response communicates this conflict precisely.

---

### Part 5, Task 2 – HTTP 422 vs 404 for Invalid roomId in POST /sensors

When a POST request to `/sensors` includes a `roomId` that does not
exist, the API returns **422 Unprocessable Entity** rather than 404.

**Why 422 is more appropriate than 404:**

- A **404 Not Found** means the endpoint or resource you are trying to
  reach does not exist. The `/sensors` endpoint clearly exists.
- A **422 Unprocessable Entity** means "the server understood the
  request, the syntax is correct, but the data inside it is logically
  invalid." The `roomId` field refers to a room that cannot be found,
  making the operation logically impossible.

The distinction is: 404 is about the URL, 422 is about the content of
the request body.

---

### Part 5, Task 3 – HTTP 403 for MAINTENANCE Sensor Reading

Posting a reading to a sensor with status `MAINTENANCE` returns **403
Forbidden**.

**Justification:**

The request itself is valid — the sensor exists and the reading format is
correct. However, the business rule says a sensor under maintenance must
not receive new data (it may be uncalibrated). The server is refusing the
action based on the current state of the resource, which is what 403
communicates. It tells the client "you cannot do this right now" without
implying the sensor cannot be found (404) or the request is malformed
(400).

---

### Part 5, Task 4 – ExceptionMapper and Global Safety Net

A `GlobalExceptionMapper` that catches `Throwable` is implemented as a
safety net. If any unexpected exception occurs that is not covered by a
specific mapper, this mapper catches it and returns a clean 500 Internal
Server Error JSON response.

**Why exposing stack traces to clients is a security risk:**

Stack traces reveal internal implementation details including class names,
method names, library versions, and the full call hierarchy of the
application. An attacker can use this information to identify known
vulnerabilities in specific library versions, understand the internal
architecture of the application, and craft targeted attacks. Returning a
generic error message instead hides all of this from the outside world
while still logging the full details server-side for developers to
investigate.

---

### Part 5, Task 5 – Logging Filter Design

The `LoggingFilter` implements both `ContainerRequestFilter` and
`ContainerResponseFilter`. It logs the HTTP method, request URI, and
response status code for every request automatically.

**Why filters are better than manual logging inside each method:**

Manual logging would require adding `Logger.info()` calls inside every
single resource method. This violates the DRY (Don't Repeat Yourself)
principle. If the log format needed to change, every method would need
to be updated. A filter handles this as a single cross-cutting concern
— it runs for every request without any changes to the resource classes,
and the format only needs to be changed in one place. This is also the
principle behind Aspect-Oriented Programming (AOP).

---

