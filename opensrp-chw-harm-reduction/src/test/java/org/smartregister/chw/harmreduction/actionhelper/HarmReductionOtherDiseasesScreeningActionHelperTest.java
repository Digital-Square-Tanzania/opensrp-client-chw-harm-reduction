package org.smartregister.chw.harmreduction.actionhelper;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;

public class HarmReductionOtherDiseasesScreeningActionHelperTest {

    @Test
    public void evaluateStatusOnPayloadShouldCompleteWhenBothHepatitisFieldsArePresent() {
        HarmReductionOtherDiseasesScreeningActionHelper helper = new HarmReductionOtherDiseasesScreeningActionHelper();

        helper.onPayloadReceived(getPayload("not_screened", "has_symptoms", ""));

        Assert.assertEquals(BaseHarmReductionVisitAction.Status.COMPLETED, helper.evaluateStatusOnPayload());
    }

    @Test
    public void evaluateStatusOnPayloadShouldRemainPendingWhenOneHepatitisFieldIsMissing() {
        HarmReductionOtherDiseasesScreeningActionHelper helper = new HarmReductionOtherDiseasesScreeningActionHelper();

        helper.onPayloadReceived(getPayload("not_screened", "", ""));

        Assert.assertEquals(BaseHarmReductionVisitAction.Status.PENDING, helper.evaluateStatusOnPayload());
    }

    @Test
    public void evaluateStatusOnPayloadShouldSupportLegacyCombinedHepatitisField() {
        HarmReductionOtherDiseasesScreeningActionHelper helper = new HarmReductionOtherDiseasesScreeningActionHelper();

        helper.onPayloadReceived(getPayload("", "", "undergoing_treatment"));

        Assert.assertEquals(BaseHarmReductionVisitAction.Status.COMPLETED, helper.evaluateStatusOnPayload());
    }

    private static String getPayload(String hepatitisBValue, String hepatitisCValue, String legacyHepatitisBcValue) {
        return "{"
                + "\"step1\":{"
                + "\"fields\":["
                + getField("hepatitis_b_screening", hepatitisBValue) + ","
                + getField("hepatitis_c_screening", hepatitisCValue) + ","
                + getField("hepatitis_bc_screening", legacyHepatitisBcValue)
                + "]"
                + "}"
                + "}";
    }

    private static String getField(String key, String value) {
        return "{"
                + "\"key\":\"" + key + "\","
                + "\"type\":\"native_radio\","
                + "\"value\":\"" + value + "\""
                + "}";
    }
}
