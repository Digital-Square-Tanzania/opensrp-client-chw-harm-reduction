package org.smartregister.chw.harmreduction.activity;

import org.junit.Assert;
import org.junit.Test;

public class BaseHarmReductionProfileActivityTest {

    @Test
    public void formatProfileUicIdShouldTrimAndUppercaseValue() {
        Assert.assertEquals("ABKIL126", BaseHarmReductionProfileActivity.formatProfileUicId(" abKil126 "));
    }
}
