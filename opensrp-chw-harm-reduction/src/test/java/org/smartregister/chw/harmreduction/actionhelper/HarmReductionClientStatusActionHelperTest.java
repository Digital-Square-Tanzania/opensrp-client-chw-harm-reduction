package org.smartregister.chw.harmreduction.actionhelper;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;
import org.smartregister.chw.harmreduction.util.JsonFormUtils;

public class HarmReductionClientStatusActionHelperTest {

    @Test
    public void getPreProcessedShouldHideFollowUpStatusAndPrefillPregnancyStatusForFirstVisit() throws Exception {
        HarmReductionClientStatusActionHelper helper = new TestHarmReductionClientStatusActionHelper(getMemberObject(), false, "new_client", "yes");
        helper.onJsonFormLoaded(getClientStatusForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());

        JSONObject followUpStatusField = getField(form, "follow_up_status");
        JSONObject pregnancyStatusField = getField(form, "pregnancy_breastfeeding_status");

        Assert.assertEquals("hidden", followUpStatusField.getString("type"));
        Assert.assertEquals("continue_service", followUpStatusField.getString(JsonFormConstants.VALUE));
        Assert.assertTrue(followUpStatusField.getBoolean("read_only"));
        Assert.assertEquals("pregnant", pregnancyStatusField.getString(JsonFormConstants.VALUE));
        Assert.assertEquals("female", form.getJSONObject("global").getString("sex"));
    }

    @Test
    public void getPreProcessedShouldKeepFollowUpStatusVisibleForNonNewClientFirstVisit() throws Exception {
        HarmReductionClientStatusActionHelper helper = new TestHarmReductionClientStatusActionHelper(getMemberObject(), false, "existing_client", "yes");
        helper.onJsonFormLoaded(getClientStatusForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());
        JSONObject followUpStatusField = getField(form, "follow_up_status");
        JSONObject pregnancyStatusField = getField(form, "pregnancy_breastfeeding_status");

        Assert.assertEquals("native_radio", followUpStatusField.getString("type"));
        Assert.assertFalse(followUpStatusField.has(JsonFormConstants.VALUE));
        Assert.assertFalse(followUpStatusField.optBoolean("read_only"));
        Assert.assertEquals("pregnant", pregnancyStatusField.getString(JsonFormConstants.VALUE));
    }

    @Test
    public void getPreProcessedShouldKeepFollowUpStatusVisibleAndNotOverridePregnancyStatusAfterFirstVisit() throws Exception {
        HarmReductionClientStatusActionHelper helper = new TestHarmReductionClientStatusActionHelper(getMemberObject(), true, "new_client", "yes");
        helper.onJsonFormLoaded(getClientStatusForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());
        JSONObject followUpStatusField = getField(form, "follow_up_status");

        Assert.assertEquals("native_radio", followUpStatusField.getString("type"));
        Assert.assertFalse(followUpStatusField.has(JsonFormConstants.VALUE));
        Assert.assertFalse(followUpStatusField.optBoolean("read_only"));
        Assert.assertEquals("", JsonFormUtils.getValue(form, "pregnancy_breastfeeding_status"));
    }

    @Test
    public void evaluateStatusOnPayloadShouldUseFollowUpStatus() {
        HarmReductionClientStatusActionHelper helper = new HarmReductionClientStatusActionHelper(getMemberObject());

        helper.onPayloadReceived(getClientStatusPayload("continue_service"));
        Assert.assertEquals(BaseHarmReductionVisitAction.Status.COMPLETED, helper.evaluateStatusOnPayload());

        helper.onPayloadReceived(getClientStatusPayload(""));
        Assert.assertEquals(BaseHarmReductionVisitAction.Status.PENDING, helper.evaluateStatusOnPayload());
    }

    private static MemberObject getMemberObject() {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");
        memberObject.setGender("Female");
        return memberObject;
    }

    private static JSONObject getField(JSONObject form, String key) throws Exception {
        JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            if (key.equals(field.optString(JsonFormConstants.KEY))) {
                return field;
            }
        }

        throw new AssertionError("Missing field: " + key);
    }

    private String getClientStatusForm() {
        return "{"
                + "\"global\":{},"
                + "\"step1\":{"
                + "\"fields\":["
                + "{"
                + "\"key\":\"follow_up_status\","
                + "\"type\":\"native_radio\","
                + "\"options\":["
                + "{\"key\":\"continue_service\",\"text\":\"Continue service\"},"
                + "{\"key\":\"started_mat_services\",\"text\":\"Started MAT services\"}"
                + "]"
                + "},"
                + "{"
                + "\"key\":\"pregnancy_breastfeeding_status\","
                + "\"type\":\"native_radio\","
                + "\"options\":["
                + "{\"key\":\"pregnant\",\"text\":\"Pregnant\"},"
                + "{\"key\":\"breastfeeding\",\"text\":\"Breastfeeding\"},"
                + "{\"key\":\"not_pregnant\",\"text\":\"Not pregnant\"}"
                + "]"
                + "}"
                + "]"
                + "}"
                + "}";
    }

    private String getClientStatusPayload(String followUpStatus) {
        return "{"
                + "\"step1\":{"
                + "\"fields\":["
                + "{"
                + "\"key\":\"follow_up_status\","
                + "\"type\":\"hidden\","
                + "\"value\":\"" + followUpStatus + "\""
                + "}"
                + "]"
                + "}"
                + "}";
    }

    private static class TestHarmReductionClientStatusActionHelper extends HarmReductionClientStatusActionHelper {
        private final boolean hasVisit;
        private final String riskAssessmentClientStatus;
        private final String pregnancyStatus;

        TestHarmReductionClientStatusActionHelper(MemberObject memberObject, boolean hasVisit, String riskAssessmentClientStatus, String pregnancyStatus) {
            super(memberObject);
            this.hasVisit = hasVisit;
            this.riskAssessmentClientStatus = riskAssessmentClientStatus;
            this.pregnancyStatus = pregnancyStatus;
        }

        @Override
        protected boolean hasPreviousFollowUpVisit() {
            return hasVisit;
        }

        @Override
        protected String getRiskAssessmentClientStatus() {
            return riskAssessmentClientStatus;
        }

        @Override
        protected String getRiskAssessmentPregnancyStatus() {
            return pregnancyStatus;
        }
    }
}
