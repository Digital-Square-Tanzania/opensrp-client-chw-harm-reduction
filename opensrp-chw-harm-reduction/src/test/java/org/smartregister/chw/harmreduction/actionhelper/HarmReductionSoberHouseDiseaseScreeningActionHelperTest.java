package org.smartregister.chw.harmreduction.actionhelper;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;

public class HarmReductionSoberHouseDiseaseScreeningActionHelperTest {

    @Test
    public void evaluateStatusOnPayloadCompletesWhenScreeningSelectionExists() {
        HarmReductionSoberHouseDiseaseScreeningActionHelper helper = new HarmReductionSoberHouseDiseaseScreeningActionHelper();

        helper.onPayloadReceived(payload(true));

        Assert.assertEquals(BaseHarmReductionVisitAction.Status.COMPLETED, helper.evaluateStatusOnPayload());
    }

    @Test
    public void evaluateStatusOnPayloadRemainsPendingWithoutScreeningSelection() {
        HarmReductionSoberHouseDiseaseScreeningActionHelper helper = new HarmReductionSoberHouseDiseaseScreeningActionHelper();

        helper.onPayloadReceived(payload(false));

        Assert.assertEquals(BaseHarmReductionVisitAction.Status.PENDING, helper.evaluateStatusOnPayload());
    }

    private static String payload(boolean selected) {
        return "{\"step1\":{\"fields\":[{"
                + "\"key\":\"screening_tests_done\","
                + "\"type\":\"check_box\","
                + "\"options\":[{\"key\":\"not_done\",\"text\":\"Not done\",\"value\":" + selected + "}]"
                + "}]}}";
    }
}
