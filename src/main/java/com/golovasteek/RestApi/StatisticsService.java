package com.golovasteek.RestApi;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.RuntimeDelegate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Clock;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

@ApplicationPath("/")
@Path("")
public class StatisticsService extends Application {
    private StatisticsKeeper statsKeeper = new StatisticsKeeper(60, Clock.systemUTC());
    private final Set<Class<?>> classes;

    public StatisticsService()
    {
        HashSet<Class<?>> c = new HashSet<>();
        c.add(StatisticsService.class);
        classes = Collections.unmodifiableSet(c);
    }
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @GET
    @Path("statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response statistics() {

        return Response.ok(statsKeeper.getStatistics()).build();
    }

    @POST
    @Path("transaction")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTransaction(Transaction transaction)
    {
        if (statsKeeper.addTransaction(transaction)) {
            return Response.noContent().status(201).build();
        } else {
            return Response.noContent().status(204).build();
        }
    }

    public static void main(String[] args)
    {

        URI uri = UriBuilder.fromUri("http://localhost/").port(9999).build();

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(9999), 0);
            HttpHandler handler = RuntimeDelegate.getInstance()
                    .createEndpoint(new StatisticsService(), HttpHandler.class);

            server.createContext(uri.getPath(), handler);
            server.start();

            System.out.println("Server is listening on " + uri.toString() + ". Press any key to exit");
            Scanner userInput = new Scanner(System.in);
            while(true) {

                String input = userInput.nextLine();
                System.out.println("input is '" + input + "'");

                if (!input.isEmpty()) {
                    break;
                }
            }
            server.stop(0);
        } catch (IOException ex) {
            return;
        }
    }
}
