package com.golovasteek.RestApi;

import java.time.Clock;

/**
    Keeps statistic for the last {@code windowSec} seconds.
    More rigorously: transactions with {@code timestamp}
    falls in last 60 integer seconds time window.
    Technically it's not the same as statistics for the last 60000 milliseconds
    what would be more precise. But such approximation seems to be reasonable
    for most application.

    One feature which I consider to be important: statistic request which is done
    immediately after adding a transaction, should return updated statistic.

    Current solution makes at most "windowSec" iteration in every handler.
    What we can consider to be constant. I.e. time needed to update or retrieve
    statistics does not depend on number of registered transactions.
 */
public class StatisticsKeeper {

    private Clock clock_;
    private int windowSecs_;

    // Cyclic buffer for the last 60 seconds of transaction statistic
    private Statistics[] perSecondStats_;
    private long startTimestampSec_;

    private void advance_(long currentTimestampSec)
    {
        long obsoleteEndsAt = currentTimestampSec - windowSecs_;
        for (
                long ts = Math.max(startTimestampSec_, obsoleteEndsAt - windowSecs_);
                ts <= obsoleteEndsAt; ++ts)
        {
            int slot = (int)(ts % windowSecs_);
            perSecondStats_[(slot)] = new Statistics();
        }
        startTimestampSec_ = obsoleteEndsAt + 1;
    }

    StatisticsKeeper(int windowSecs, Clock clock)
    {
        clock_ = clock;
        windowSecs_ = windowSecs;

        perSecondStats_ = new Statistics[windowSecs_];

        long currentTimeSec = clock_.millis() / 1000;
        startTimestampSec_ = currentTimeSec - windowSecs_ * 2;
        advance_(currentTimeSec);
    }

    public boolean addTransaction(Transaction transaction)
    {
        synchronized (this) {
            long currentTime = clock_.millis() / 1000;
            long transactionTime = transaction.timestamp / 1000;
            advance_(currentTime);

            if (transactionTime < startTimestampSec_ || transactionTime > currentTime) {
                return false;
            }

            int slot = (int) (transactionTime % windowSecs_);
            Statistics stat = perSecondStats_[slot];
            if (stat.count == 0) {
                stat.max = transaction.amount;
                stat.min = transaction.amount;
                stat.count = 1;
                stat.sum = transaction.amount;
            } else {
                // Do not touch average it doesn't make sens, and
                // can be computed at the retrieval.
                stat.count++;
                stat.max = Math.max(stat.max, transaction.amount);
                stat.min = Math.min(stat.min, transaction.amount);
                stat.sum += transaction.amount;
            }
            return true;
        }
    }

    public Statistics getStatistics()
    {
        synchronized (this) {
            advance_(clock_.millis() / 1000);
            Statistics result = new Statistics();
            for (int i = 0; i < windowSecs_; ++i) {
                Statistics current = perSecondStats_[i];
                if (current.count != 0) {
                    result.count += current.count;
                    result.max = Math.max(result.max, current.max);
                    result.min = Math.min(result.min, current.min);
                    result.sum += current.sum;
                }
            }

            if (result.count != 0) {
                result.avg = result.sum / result.count;
            } else {
                result.avg = 0;
            }
            return result;
        }
    }
}
