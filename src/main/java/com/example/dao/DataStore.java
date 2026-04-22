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
        // Starts completely empty, manual creation needed for tests
    }

    // This is how other classes get access to the DataStore
    public static DataStore getInstance() {
        return INSTANCE;
    }

    // --- Room methods ---
    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Room getRoomById(String id) {
        return rooms.get(id);
    }

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public boolean deleteRoom(String id) {
        if (rooms.containsKey(id)) {
            rooms.remove(id);
            return true;
        }
        return false;
    }

    // --- Sensor methods ---
    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Sensor getSensorById(String id) {
        return sensors.get(id);
    }

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
