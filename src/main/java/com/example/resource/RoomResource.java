package com.example.resource;

import com.example.dao.DataStore;
import com.example.exception.RoomNotEmptyException;
import com.example.exception.RoomNotFoundException;
import com.example.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
public class RoomResource {

    // Get the single shared data store
    private DataStore dataStore = DataStore.getInstance();

    // GET /api/v1/rooms — returns all rooms
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getAllRooms() {
        return new ArrayList<>(dataStore.getRooms().values());
    }

    // POST /api/v1/rooms — creates a new room
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoom(Room room) {
        dataStore.addRoom(room);
        return Response.status(201).entity(room).build();
    }

    // GET /api/v1/rooms/{roomId} — returns a specific room
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Room getRoomById(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRoomById(roomId);
        if (room == null) {
            throw new RoomNotFoundException("Room with ID " + roomId + " was not found.");
        }
        return room;
    }

    // DELETE /api/v1/rooms/{roomId} — deletes a room if it has no sensors
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRoomById(roomId);

        // Check if the room exists
        if (room == null) {
            throw new RoomNotFoundException("Room with ID " + roomId + " was not found.");
        }

        // Business logic: cannot delete if it still has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Room " + roomId + " cannot be deleted because it still has " +
                room.getSensorIds().size() + " sensor(s) assigned to it."
            );
        }

        dataStore.deleteRoom(roomId);
        return Response.noContent().build(); // 204 No Content = success
    }
}
