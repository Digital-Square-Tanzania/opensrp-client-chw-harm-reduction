package org.smartregister.chw.harmreduction.actionhelper;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;

public class HarmReductionSoberHouseClientTypeFollowupStatusActionHelperTest {

    @Test
    public void evaluateStatusOnPayloadShouldBeCompletedWhenFollowUpStatusIsPresent() {
        HarmReductionSoberHouseClientTypeFollowupStatusActionHelper helper =
                new HarmReductionSoberHouseClientTypeFollowupStatusActionHelper();

        helper.onPayloadReceived(getFollowUpStatusPayload("continuing_service"));
        Assert.assertEquals(BaseHarmReductionVisitAction.Status.COMPLETED, helper.evaluateStatusOnPayload());
    }

    @Test
    public void evaluateStatusOnPayloadShouldRemainPendingWhenFollowUpStatusIsMissing() {
        HarmReductionSoberHouseClientTypeFollowupStatusActionHelper helper =
                new HarmReductionSoberHouseClientTypeFollowupStatusActionHelper();

        helper.onPayloadReceived(getFollowUpStatusPayload(""));
        Assert.assertEquals(BaseHarmReductionVisitAction.Status.PENDING, helper.evaluateStatusOnPayload());
    }

    private static String getFollowUpStatusPayload(String value) {
        return "{"
                + "\"step1\":{"
                + "\"fields\":["
                + "{"
                + "\"key\":\"follow_up_status\","
                + "\"type\":\"hidden\","
                + "\"value\":\"" + value + "\""
                + "}"
                + "]"
                + "}"
                + "}";
    }
}
