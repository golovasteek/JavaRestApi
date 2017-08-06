package com.golovasteek.RestApi;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

public class EmptyStatisticsTest extends JerseyTest {
    public EmptyStatisticsTest()
    {
        super(new StatisticsService());
    }

    @Test
    public void initialStatisticsTest() {
        Statistics response = target("statistics").request().get(Statistics.class);
        Assert.assertEquals(0, response.count);
        Assert.assertEquals(0, response.sum, 0.0000001);
        Assert.assertEquals(response.avg * response.count, response.sum, 0.0000001);
    }
}
