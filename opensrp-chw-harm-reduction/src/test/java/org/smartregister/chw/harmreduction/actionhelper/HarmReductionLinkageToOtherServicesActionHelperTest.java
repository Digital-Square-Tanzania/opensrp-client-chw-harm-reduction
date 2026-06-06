package org.smartregister.chw.harmreduction.actionhelper;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;

public class HarmReductionLinkageToOtherServicesActionHelperTest {

    @Test
    public void evaluateStatusOnPayloadShouldCompleteWhenLinkageAnswerIsPresent() {
        HarmReductionLinkageToOtherServicesActionHelper helper = new HarmReductionLinkageToOtherServicesActionHelper();

        helper.onPayloadReceived(getPayload("yes"));

        Assert.assertEquals(BaseHarmReductionVisitAction.Status.COMPLETED, helper.evaluateStatusOnPayload());
    }

    @Test
    public void evaluateStatusOnPayloadShouldRemainPendingWhenLinkageAnswerIsMissing() {
        HarmReductionLinkageToOtherServicesActionHelper helper = new HarmReductionLinkageToOtherServicesActionHelper();

        helper.onPayloadReceived(getPayload(""));

        Assert.assertEquals(BaseHarmReductionVisitAction.Status.PENDING, helper.evaluateStatusOnPayload());
    }

    private static String getPayload(String linkageProvided) {
        return "{"
                + "\"step1\":{"
                + "\"fields\":["
                + "{"
                + "\"key\":\"linkage_to_other_services_provided\","
                + "\"type\":\"native_radio\","
                + "\"value\":\"" + linkageProvided + "\""
                + "}"
                + "]"
                + "}"
                + "}";
    }
}
