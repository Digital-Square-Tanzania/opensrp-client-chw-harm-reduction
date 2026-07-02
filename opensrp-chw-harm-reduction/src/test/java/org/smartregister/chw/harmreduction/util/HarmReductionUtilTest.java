package org.smartregister.chw.harmreduction.util;

import org.junit.Assert;
import org.junit.Test;

public class HarmReductionUtilTest {

    @Test
    public void shouldUseRiskAssessmentUicFieldForRiskAssessmentEvents() {
        Assert.assertEquals(
                Constants.JSON_FORM_KEY.UIC,
                HarmReductionUtil.resolveGeneratedUicFieldCode(Constants.EVENT_TYPE.HARM_REDUCTION_RISK_ASSESSMENT)
        );
    }

    @Test
    public void shouldKeepLegacyUicIdFieldForSoberHouseEvents() {
        Assert.assertEquals(
                Constants.JSON_FORM_KEY.UIC_ID,
                HarmReductionUtil.resolveGeneratedUicFieldCode(Constants.EVENT_TYPE.HARM_REDUCTION_SOBER_HOUSE_ENROLLMENT)
        );
    }
}
