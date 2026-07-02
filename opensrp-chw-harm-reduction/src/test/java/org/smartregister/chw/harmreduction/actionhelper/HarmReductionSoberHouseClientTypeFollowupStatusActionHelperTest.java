package org.smartregister.chw.harmreduction.actionhelper;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;

public class HarmReductionSoberHouseClientTypeFollowupStatusActionHelperTest {

    @Test
    public void getPreProcessedShouldHideAndPrefillContinuationStatusForNewClientFirstVisit() throws Exception {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");

        HarmReductionSoberHouseClientTypeFollowupStatusActionHelper helper =
                new TestHarmReductionSoberHouseClientTypeFollowupStatusActionHelper(
                        memberObject,
                        "new_client",
                        false
                );
        helper.onJsonFormLoaded(getFollowUpForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());
        JSONObject continuationField = getField(form, "service_continuation_status");

        Assert.assertEquals("hidden", continuationField.getString("type"));
        Assert.assertEquals("continuing_service", continuationField.getString("value"));
        Assert.assertTrue(continuationField.getBoolean("read_only"));
    }

    @Test
    public void getPreProcessedShouldLeaveContinuationFieldVisibleForExistingClient() throws Exception {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");

        HarmReductionSoberHouseClientTypeFollowupStatusActionHelper helper =
                new TestHarmReductionSoberHouseClientTypeFollowupStatusActionHelper(
                        memberObject,
                        "existing",
                        false
                );
        helper.onJsonFormLoaded(getFollowUpForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());
        JSONObject continuationField = getField(form, "service_continuation_status");

        Assert.assertEquals("native_radio", continuationField.getString("type"));
        Assert.assertFalse(continuationField.has("value"));
        Assert.assertFalse(continuationField.optBoolean("read_only"));
    }

    @Test
    public void getPreProcessedShouldLeaveContinuationFieldVisibleForNewClientWithPreviousVisit() throws Exception {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");

        HarmReductionSoberHouseClientTypeFollowupStatusActionHelper helper =
                new TestHarmReductionSoberHouseClientTypeFollowupStatusActionHelper(
                        memberObject,
                        "new_client",
                        true
                );
        helper.onJsonFormLoaded(getFollowUpForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());
        JSONObject continuationField = getField(form, "service_continuation_status");

        Assert.assertEquals("native_radio", continuationField.getString("type"));
        Assert.assertFalse(continuationField.has("value"));
        Assert.assertFalse(continuationField.optBoolean("read_only"));
    }

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

    private static String getFollowUpForm() {
        return "{"
                + "\"step1\":{"
                + "\"fields\":["
                + "{"
                + "\"key\":\"service_continuation_status\","
                + "\"type\":\"native_radio\""
                + "},"
                + "{"
                + "\"key\":\"discontinued_reason\","
                + "\"type\":\"native_radio\""
                + "},"
                + "{"
                + "\"key\":\"follow_up_status\","
                + "\"type\":\"hidden\""
                + "}"
                + "]"
                + "}"
                + "}";
    }

    private static JSONObject getField(JSONObject form, String key) throws Exception {
        for (int i = 0; i < form.getJSONObject("step1").getJSONArray("fields").length(); i++) {
            JSONObject field = form.getJSONObject("step1").getJSONArray("fields").getJSONObject(i);
            if (key.equals(field.optString("key"))) {
                return field;
            }
        }

        throw new AssertionError("Missing field: " + key);
    }

    private static class TestHarmReductionSoberHouseClientTypeFollowupStatusActionHelper
            extends HarmReductionSoberHouseClientTypeFollowupStatusActionHelper {
        private final String latestEnrollmentClientStatus;
        private final boolean hasPreviousVisit;

        TestHarmReductionSoberHouseClientTypeFollowupStatusActionHelper(MemberObject memberObject,
                                                                        String latestEnrollmentClientStatus,
                                                                        boolean hasPreviousVisit) {
            super(memberObject);
            this.latestEnrollmentClientStatus = latestEnrollmentClientStatus;
            this.hasPreviousVisit = hasPreviousVisit;
        }

        @Override
        protected boolean hasPreviousSoberHouseServiceVisit() {
            return hasPreviousVisit;
        }

        @Override
        protected String getLatestSoberHouseEnrollmentClientStatus() {
            return latestEnrollmentClientStatus;
        }
    }
}
