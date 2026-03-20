package org.smartregister.chw.harmreduction.dao;

import org.junit.Assert;
import org.junit.Test;

public class HarmReductionDaoFollowUpStatusTest {

    @Test
    public void isDeceasedFollowUpStatusShouldReturnTrueForDied() {
        Assert.assertTrue(HarmReductionDao.isDeceasedFollowUpStatus("died"));
    }

    @Test
    public void isDeceasedFollowUpStatusShouldReturnTrueForDeceased() {
        Assert.assertTrue(HarmReductionDao.isDeceasedFollowUpStatus("deceased"));
    }

    @Test
    public void isDeceasedFollowUpStatusShouldReturnFalseForNonTerminalStatus() {
        Assert.assertFalse(HarmReductionDao.isDeceasedFollowUpStatus("continue_service"));
    }
}
