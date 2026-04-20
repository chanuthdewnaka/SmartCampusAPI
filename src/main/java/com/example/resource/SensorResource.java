package com.example.resource;

import com.example.dao.DataStore;
import com.example.exception.LinkedResourceNotFoundException;
import com.example.exception.SensorNotFoundException;
import com.example.model.Room;
import com.example.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/sensors")
public class SensorResource {

    private DataStore dataStore = DataStore.getInstance();

    // GET /api/v1/sensors — returns all sensors, with optional ?type= filter
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(dataStore.getSensors().values());

        // If the type query parameter was provided, filter the list
        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = new ArrayList<>();
            for (Sensor s : allSensors) {
                if (s.getType().equalsIgnoreCase(type)) {
                    filtered.add(s);
                }
            }
            return filtered;
        }

        return allSensors;
    }

    // POST /api/v1/sensors — registers a new sensor
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSensor(Sensor sensor) {
        // Validate that the roomId exists
        Room room = dataStore.getRoomById(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException(
                "Cannot create sensor: Room with ID '" + sensor.getRoomId() + "' does not exist."
            );
        }

        // Save the sensor
        dataStore.addSensor(sensor);

        // Link the sensor to the room
        room.getSensorIds().add(sensor.getId());

        return Response.status(201).entity(sensor).build();
    }

    // GET /api/v1/sensors/{sensorId} — returns a specific sensor
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Sensor getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            throw new SensorNotFoundException("Sensor with ID " + sensorId + " was not found.");
        }
        return sensor;
    }

    // Sub-resource locator — delegates /sensors/{sensorId}/readings to SensorReadingResource
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
