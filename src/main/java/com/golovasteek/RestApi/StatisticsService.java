package com.golovasteek.RestApi;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.logging.Logger;

@ApplicationPath("")
@Path("")
public class StatisticsService extends Application {
    private final static Logger LOGGER = Logger.getLogger(StatisticsService.class.getCanonicalName());

    @GET
    @Path("statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response statistics() {
        Statistics stats = new Statistics();
        stats.sum = 0;
        stats.avg = 0;
        stats.max = 0;
        stats.min = 0;
        stats.count = 0;
        return Response.ok(stats).build();
    }

    @POST
    @Path("transaction")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTransaction(Transaction transaction)
    {
        LOGGER.warning(Double.toString(transaction.amount));
        return Response.noContent().status(201).build();
    }
}
