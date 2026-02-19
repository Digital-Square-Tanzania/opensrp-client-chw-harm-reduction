package org.smartregister.chw.harmreduction.dao;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class HarmReductionDaoAutoCloseTest {

    @Test
    public void testSingleRecoveryCapitalPassRowProducesOneClientThreshold() {
        Map<String, DateTime> thresholds = HarmReductionDao.findEarliestCloseThresholdByClient(
                Collections.singletonList(
                        new HarmReductionDao.SoberHouseClosureTrigger("client-1", "2025-01-15")
                )
        );

        Assert.assertEquals(1, thresholds.size());
        Assert.assertEquals("2025-01-15", thresholds.get("client-1").toString("yyyy-MM-dd"));
    }

    @Test
    public void testMultipleRecoveryCapitalPassRowsUseEarliestThresholdPerClient() {
        Map<String, DateTime> thresholds = HarmReductionDao.findEarliestCloseThresholdByClient(
                Arrays.asList(
                        new HarmReductionDao.SoberHouseClosureTrigger("client-1", "2025-03-10"),
                        new HarmReductionDao.SoberHouseClosureTrigger("client-1", "2025-02-20"),
                        new HarmReductionDao.SoberHouseClosureTrigger("client-2", "2025-04-01")
                )
        );

        Assert.assertEquals("2025-02-20", thresholds.get("client-1").toString("yyyy-MM-dd"));
        Assert.assertEquals("2025-04-01", thresholds.get("client-2").toString("yyyy-MM-dd"));
    }

    @Test
    public void testRowsAfterThresholdRemainOpen() {
        DateTime threshold = new DateTime(2025, 4, 15, 0, 0);
        boolean shouldClose = HarmReductionDao.shouldCloseOpenRecord(false, "2025-04-16", threshold);
        Assert.assertFalse(shouldClose);
    }

    @Test
    public void testNoRecoveryCapitalPassRowsReturnsNoThresholds() {
        Map<String, DateTime> thresholds = HarmReductionDao.findEarliestCloseThresholdByClient(Collections.emptyList());
        Assert.assertTrue(thresholds.isEmpty());
    }

    @Test
    public void testClosureEvaluationIsIdempotentWhenAlreadyClosed() {
        DateTime threshold = new DateTime(2025, 4, 15, 0, 0);
        String eventDate = "2025-04-15";

        Assert.assertTrue(HarmReductionDao.shouldCloseOpenRecord(false, eventDate, threshold));
        Assert.assertFalse(HarmReductionDao.shouldCloseOpenRecord(true, eventDate, threshold));
    }
}
