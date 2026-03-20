package org.smartregister.chw.harmreduction.dao;

import org.junit.Assert;
import org.junit.Test;

public class HarmReductionDaoFollowUpStatusTest {

    @Test
    public void isDeceasedFollowUpStatusShouldReturnTrueForClientDeceased() {
        Assert.assertTrue(HarmReductionDao.isDeceasedFollowUpStatus("client_deceased"));
    }

    @Test
    public void isDeceasedFollowUpStatusShouldReturnFalseForDied() {
        Assert.assertFalse(HarmReductionDao.isDeceasedFollowUpStatus("died"));
    }

    @Test
    public void isDeceasedFollowUpStatusShouldReturnFalseForNonTerminalStatus() {
        Assert.assertFalse(HarmReductionDao.isDeceasedFollowUpStatus("continue_service"));
    }
}
