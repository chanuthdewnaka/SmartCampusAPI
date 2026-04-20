package com.example.exception;

import com.example.model.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.severe("Unexpected error: " + exception.getMessage());
        ErrorMessage error = new ErrorMessage(
            "An internal server error occurred. Please contact the administrator.",
            500,
            "https://smartcampus.edu/api/docs/errors"
        );
        return Response.status(500).entity(error).build();
    }
}
