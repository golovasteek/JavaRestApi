package com.golovasteek.RestApi;


import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Clock;

public class StatisticsTest {
    @Test
    public void simpleStatisticsTest() throws IOException {
        Transaction t = new Transaction();
        t.amount = 10;
        t.timestamp = Clock.systemUTC().millis();

        Entity<Transaction> ent = Entity.entity(t, MediaType.APPLICATION_JSON);

        URI uri = UriBuilder.fromUri("http://localhost/").port(9999).build();

        HttpServer server = HttpServer.create(new InetSocketAddress(9999), 0);
        HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(new StatisticsService(), HttpHandler.class);

        server.createContext(uri.getPath(), handler);
        server.start();

        Client client = ClientBuilder.newClient();

        Response response = client.target(uri + "transaction").request().post(ent);

        Assert.assertEquals(201, response.getStatus());

        response = client.target(uri +"statistics").request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        Statistics stats = response.readEntity(Statistics.class);
        Assert.assertEquals(1, stats.count);
        Assert.assertEquals(10, stats.sum, 0.0000001);
        Assert.assertEquals(stats.avg * stats.count, stats.sum, 0.0000001);
        server.stop(0);
    }
}
