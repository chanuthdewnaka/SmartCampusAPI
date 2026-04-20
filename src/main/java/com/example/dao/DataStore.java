package com.example.dao;

import com.example.model.Room;
import com.example.model.Sensor;
import com.example.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    // Key = ID (String), Value = the object
    private final Map<String, Room> rooms = new HashMap<>();
    private final Map<String, Sensor> sensors = new HashMap<>();
    private final Map<String, List<SensorReading>> sensorReadings = new HashMap<>();

    // Private constructor — prevents creating multiple instances
    private DataStore() {
        // Add some sample data so the API is not empty when you start
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room r2 = new Room("LAB-101", "Computer Lab", 30);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);

        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor s2 = new Sensor("CO2-001", "CO2", "ACTIVE", 400.0, "LAB-101");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);

        // Link sensors to rooms
        r1.getSensorIds().add(s1.getId());
        r2.getSensorIds().add(s2.getId());

        // Initialise empty reading lists for each sensor
        sensorReadings.put(s1.getId(), new ArrayList<>());
        sensorReadings.put(s2.getId(), new ArrayList<>());
    }

    // This is how other classes get access to the DataStore
    public static DataStore getInstance() {
        return INSTANCE;
    }

    // --- Room methods ---
    public Map<String, Room> getRooms() { return rooms; }

    public Room getRoomById(String id) { return rooms.get(id); }

    public void addRoom(Room room) { rooms.put(room.getId(), room); }

    public boolean deleteRoom(String id) {
        if (rooms.containsKey(id)) {
            rooms.remove(id);
            return true;
        }
        return false;
    }

    // --- Sensor methods ---
    public Map<String, Sensor> getSensors() { return sensors; }

    public Sensor getSensorById(String id) { return sensors.get(id); }

    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        // Create an empty reading list for the new sensor
        sensorReadings.put(sensor.getId(), new ArrayList<>());
    }

    // --- SensorReading methods ---
    public List<SensorReading> getReadingsForSensor(String sensorId) {
        return sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }

    public void addReading(String sensorId, SensorReading reading) {
        sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }
}
