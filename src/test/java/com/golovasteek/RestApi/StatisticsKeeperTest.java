package com.golovasteek.RestApi;


import org.junit.Assert;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class StatisticsKeeperTest {
    private final static double DELTA = 0.000001;

    class ManualClock extends Clock {
        private long millis_;

        public ManualClock (long UTCMillis)
        {
            millis_ = UTCMillis;
        }

        // Advance clock by specified number of millis
        public void advance(long millis)
        {
            millis_ += millis;
        }

        @Override
        public Instant instant()
        {
            return Instant.ofEpochMilli(millis_);
        }

        @Override
        public Clock withZone(ZoneId zoneId) {
            return null;
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }
    }

    @Test
    public void oldValueTest()
    {
        Clock clock = Clock.systemUTC();
        StatisticsKeeper keeper = new StatisticsKeeper(60, clock);
        Transaction t = new Transaction();
        t.timestamp = clock.millis();
        t.amount = 10;

        Assert.assertTrue(keeper.addTransaction(t));

        t.timestamp = t.timestamp - 5 * 1000; // 5 seconds ago
        Assert.assertTrue(keeper.addTransaction(t));

        t.timestamp = t.timestamp - 115 * 1000; // two minutes ago
        Assert.assertFalse(keeper.addTransaction(t));
    }

    @Test
    public void addTransactionsTest()
    {
        Clock clock = Clock.systemUTC();
        StatisticsKeeper keeper = new StatisticsKeeper(60, clock);

        Transaction t = new Transaction();
        t.amount = 10;
        t.timestamp = clock.millis();

        keeper.addTransaction(t);

        Statistics stat = keeper.getStatistics();

        Assert.assertEquals(1, stat.count);
        Assert.assertEquals(10, stat.sum, DELTA);
        Assert.assertEquals(10, stat.max, DELTA);
        Assert.assertEquals(10, stat.max, DELTA);
        Assert.assertEquals(10, stat.sum, DELTA);
        Assert.assertEquals(10, stat.avg, DELTA);

        t.amount = 20;
        t.timestamp -= 2000;

        keeper.addTransaction(t);
        stat = keeper.getStatistics();
        Assert.assertEquals(2, stat.count);
        Assert.assertEquals(15, stat.avg, DELTA);
    }

    @Test
    public void manualClockTest()
    {
        ManualClock clock = new ManualClock(1000000);

        StatisticsKeeper keeper = new StatisticsKeeper(3, clock);

        Transaction t = new Transaction();
        t.timestamp = clock.millis();
        t.amount = 3;

        keeper.addTransaction(t);
        Assert.assertEquals(1, keeper.getStatistics().count);
        clock.advance(1000);
        Assert.assertEquals(1, keeper.getStatistics().count);
        clock.advance(1000);
        Assert.assertEquals(1, keeper.getStatistics().count);
        clock.advance(1000);
        Assert.assertEquals(0, keeper.getStatistics().count);

        Assert.assertFalse(keeper.addTransaction(t));
        Assert.assertEquals(0,keeper.getStatistics().count);
        Assert.assertEquals(0, keeper.getStatistics().sum, DELTA);
    }

    @Test
    public void transactionFromTheFutureTest()
    {
        ManualClock clock = new ManualClock(1000000);
        StatisticsKeeper keeper = new StatisticsKeeper(3, clock);

        Transaction t = new Transaction();
        t.amount = 10;
        t.timestamp = clock.millis() + 2 * 1000;

        // Transaction 2 seconds in the future are not accepted
        Assert.assertFalse(keeper.addTransaction(t));

        // Transaction happened in the future but in the same "integer" second are accepted
        t.timestamp = clock.millis() + 100;
        Assert.assertTrue(keeper.addTransaction(t));
    }
}
