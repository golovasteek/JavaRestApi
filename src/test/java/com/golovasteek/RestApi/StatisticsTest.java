package com.golovasteek.RestApi;


import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;

public class StatisticsTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new ResourceConfig(StatisticsService.class);
    }

    @Test
    public void simpleStatisticsTest() {
        Transaction t = new Transaction();
        t.amount = 10;
        t.timestamp = BigInteger.valueOf(1501972693).multiply(BigInteger.valueOf(1000));

        target("transaction").request().post(Entity.entity(t, MediaType.APPLICATION_JSON));

        Statistics response = target("statistics").request().get(Statistics.class);
        Assert.assertEquals(1, response.count);
        Assert.assertEquals(10, response.sum, 0.0000001);
        Assert.assertEquals(response.avg * response.count, response.sum, 0.0000001);
    }
}
