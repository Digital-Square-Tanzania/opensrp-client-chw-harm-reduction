package org.smartregister.chw.harmreduction.actionhelper;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;

public class HarmReductionSoberHouseClientTypeFollowupStatusActionHelperTest {

    @Test
    public void getPreProcessedShouldRemoveNewClientWhenSoberHouseServiceVisitExists() throws Exception {
        HarmReductionSoberHouseClientTypeFollowupStatusActionHelper helper =
                new TestHarmReductionSoberHouseClientTypeFollowupStatusActionHelper(getMemberObject(), true, "new_client");
        helper.onJsonFormLoaded(getClientTypeForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());

        Assert.assertFalse(hasOption(getClientTypeOptions(form), "new_client"));
        Assert.assertTrue(hasOption(getClientTypeOptions(form), "returning_client"));
    }

    @Test
    public void getPreProcessedShouldRemoveNewClientWhenEnrollmentStatusIsNotNew() throws Exception {
        HarmReductionSoberHouseClientTypeFollowupStatusActionHelper helper =
                new TestHarmReductionSoberHouseClientTypeFollowupStatusActionHelper(getMemberObject(), false, "existing");
        helper.onJsonFormLoaded(getClientTypeForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());

        Assert.assertFalse(hasOption(getClientTypeOptions(form), "new_client"));
    }

    @Test
    public void getPreProcessedShouldKeepNewClientWhenNoServiceVisitExistsAndEnrollmentStatusIsNew() throws Exception {
        HarmReductionSoberHouseClientTypeFollowupStatusActionHelper helper =
                new TestHarmReductionSoberHouseClientTypeFollowupStatusActionHelper(getMemberObject(), false, "new_client");
        helper.onJsonFormLoaded(getClientTypeForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());

        Assert.assertTrue(hasOption(getClientTypeOptions(form), "new_client"));
        Assert.assertEquals(3, getClientTypeOptions(form).length());
    }

    @Test
    public void evaluateStatusOnPayloadShouldRemainBasedOnSelectedClientType() {
        HarmReductionSoberHouseClientTypeFollowupStatusActionHelper helper =
                new HarmReductionSoberHouseClientTypeFollowupStatusActionHelper(getMemberObject());

        helper.onPayloadReceived(getClientTypePayload("returning_client"));
        Assert.assertEquals(BaseHarmReductionVisitAction.Status.COMPLETED, helper.evaluateStatusOnPayload());

        helper.onPayloadReceived(getClientTypePayload(""));
        Assert.assertEquals(BaseHarmReductionVisitAction.Status.PENDING, helper.evaluateStatusOnPayload());
    }

    private static MemberObject getMemberObject() {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");
        return memberObject;
    }

    private static JSONArray getClientTypeOptions(JSONObject form) throws Exception {
        return form.getJSONObject(JsonFormConstants.STEP1)
                .getJSONArray(JsonFormConstants.FIELDS)
                .getJSONObject(0)
                .getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
    }

    private static boolean hasOption(JSONArray options, String key) throws Exception {
        for (int i = 0; i < options.length(); i++) {
            if (key.equals(options.getJSONObject(i).optString(JsonFormConstants.KEY))) {
                return true;
            }
        }

        return false;
    }

    private static String getClientTypeForm() {
        return "{"
                + "\"step1\":{"
                + "\"fields\":["
                + "{"
                + "\"key\":\"client_type\","
                + "\"type\":\"native_radio\","
                + "\"options\":["
                + "{\"key\":\"new_client\",\"text\":\"New\"},"
                + "{\"key\":\"returning_client\",\"text\":\"Repeat visit\"},"
                + "{\"key\":\"relapsed_client\",\"text\":\"Relapsed client\"}"
                + "]"
                + "}"
                + "]"
                + "}"
                + "}";
    }

    private static String getClientTypePayload(String value) {
        return "{"
                + "\"step1\":{"
                + "\"fields\":["
                + "{"
                + "\"key\":\"client_type\","
                + "\"type\":\"native_radio\","
                + "\"value\":\"" + value + "\""
                + "}"
                + "]"
                + "}"
                + "}";
    }

    private static class TestHarmReductionSoberHouseClientTypeFollowupStatusActionHelper
            extends HarmReductionSoberHouseClientTypeFollowupStatusActionHelper {
        private final boolean hasServiceVisit;
        private final String enrollmentClientStatus;

        TestHarmReductionSoberHouseClientTypeFollowupStatusActionHelper(MemberObject memberObject,
                                                                        boolean hasServiceVisit,
                                                                        String enrollmentClientStatus) {
            super(memberObject);
            this.hasServiceVisit = hasServiceVisit;
            this.enrollmentClientStatus = enrollmentClientStatus;
        }

        @Override
        protected boolean hasPreviousSoberHouseServiceVisit() {
            return hasServiceVisit;
        }

        @Override
        protected String getSoberHouseEnrollmentClientStatus() {
            return enrollmentClientStatus;
        }
    }
}
