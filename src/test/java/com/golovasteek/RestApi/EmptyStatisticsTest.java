package com.golovasteek.RestApi;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import javax.ws.rs.core.Application;

public class EmptyStatisticsTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new ResourceConfig(StatisticsService.class);
    }

    @Test
    public void initialStatisticsTest() {
        Statistics response = target("statistics").request().get(Statistics.class);
        Assert.assertEquals(0, response.count);
        Assert.assertEquals(0, response.sum, 0.0000001);
        Assert.assertEquals(response.avg * response.count, response.sum, 0.0000001);
    }
}
