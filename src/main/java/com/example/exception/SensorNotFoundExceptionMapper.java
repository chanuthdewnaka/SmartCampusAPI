package com.example.exception;

import com.example.model.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorNotFoundExceptionMapper 
        implements ExceptionMapper<SensorNotFoundException> {

    @Override
    public Response toResponse(SensorNotFoundException exception) {
        ErrorMessage error = new ErrorMessage(
            exception.getMessage(),
            404,
            "https://smartcampus.edu/api/docs/errors"
        );
        return Response.status(404).entity(error).build();
    }
}
