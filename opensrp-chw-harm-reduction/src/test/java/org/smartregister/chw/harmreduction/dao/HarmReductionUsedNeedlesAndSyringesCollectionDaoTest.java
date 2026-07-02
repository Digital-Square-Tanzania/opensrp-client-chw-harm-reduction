package org.smartregister.chw.harmreduction.dao;

import org.junit.Assert;
import org.junit.Test;

public class HarmReductionUsedNeedlesAndSyringesCollectionDaoTest {

    @Test
    public void resolveCollectedNeedlesAndSyringesValueShouldPreferCurrentField() {
        String value = HarmReductionUsedNeedlesAndSyringesCollectionDao.resolveCollectedNeedlesAndSyringesValue(
                "14",
                "9",
                "4",
                "5"
        );

        Assert.assertEquals("14", value);
    }

    @Test
    public void resolveCollectedNeedlesAndSyringesValueShouldFallBackToLegacyTotal() {
        String value = HarmReductionUsedNeedlesAndSyringesCollectionDao.resolveCollectedNeedlesAndSyringesValue(
                "",
                "9",
                "4",
                "5"
        );

        Assert.assertEquals("9", value);
    }

    @Test
    public void resolveCollectedNeedlesAndSyringesValueShouldComputeLegacyTotalFromComponentFields() {
        String value = HarmReductionUsedNeedlesAndSyringesCollectionDao.resolveCollectedNeedlesAndSyringesValue(
                null,
                null,
                "4",
                "5"
        );

        Assert.assertEquals("9", value);
    }
}
