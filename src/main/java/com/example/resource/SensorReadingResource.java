package com.example.resource;

import com.example.dao.DataStore;
import com.example.exception.SensorNotFoundException;
import com.example.exception.SensorUnavailableException;
import com.example.model.Sensor;
import com.example.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class SensorReadingResource {

    private DataStore dataStore = DataStore.getInstance();
    private String sensorId;

    // Constructor receives the sensorId from the parent resource
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings — returns all readings for a sensor
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings() {
        // Check if sensor exists
        Sensor sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            throw new SensorNotFoundException("Sensor with ID " + sensorId + " was not found.");
        }
        return dataStore.getReadingsForSensor(sensorId);
    }

    // POST /api/v1/sensors/{sensorId}/readings — adds a new reading
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        // Check if sensor exists
        Sensor sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            throw new SensorNotFoundException("Sensor with ID " + sensorId + " was not found.");
        }

        // Business logic: cannot post reading to a sensor under MAINTENANCE
        if ("MAINTENANCE".equals(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor " + sensorId + " is under MAINTENANCE and cannot accept new readings."
            );
        }

        // Generate ID and timestamp if not provided
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Save the reading
        dataStore.addReading(sensorId, reading);

        // IMPORTANT: Update the sensor's currentValue with this new reading
        sensor.setCurrentValue(reading.getValue());

        return Response.status(201).entity(reading).build();
    }
}
