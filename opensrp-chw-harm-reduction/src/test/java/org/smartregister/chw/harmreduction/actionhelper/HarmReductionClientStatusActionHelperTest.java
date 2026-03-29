package org.smartregister.chw.harmreduction.actionhelper;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.util.JsonFormUtils;

public class HarmReductionClientStatusActionHelperTest {

    @Test
    public void getPreProcessedShouldPrefillPregnancyStatusFromRiskAssessmentForFirstVisit() throws Exception {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");
        memberObject.setGender("Female");

        HarmReductionClientStatusActionHelper helper = new TestHarmReductionClientStatusActionHelper(
                memberObject,
                false,
                "yes"
        );
        helper.onJsonFormLoaded(getClientStatusForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());

        JSONObject pregnancyStatusField = form.getJSONObject("step1").getJSONArray("fields").getJSONObject(1);
        Assert.assertEquals("pregnancy_breastfeeding_status", pregnancyStatusField.getString(JsonFormConstants.KEY));
        Assert.assertEquals("pregnant", pregnancyStatusField.getString(JsonFormConstants.VALUE));
        Assert.assertEquals("female", form.getJSONObject("global").getString("sex"));
    }

    @Test
    public void getPreProcessedShouldNotOverridePregnancyStatusAfterFirstVisit() throws Exception {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");
        memberObject.setGender("Female");

        HarmReductionClientStatusActionHelper helper = new TestHarmReductionClientStatusActionHelper(
                memberObject,
                true,
                "yes"
        );
        helper.onJsonFormLoaded(getClientStatusForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());

        Assert.assertEquals("", JsonFormUtils.getValue(form, "pregnancy_breastfeeding_status"));
    }

    private String getClientStatusForm() {
        return "{"
                + "\"global\":{},"
                + "\"step1\":{"
                + "\"fields\":["
                + "{"
                + "\"key\":\"client_status\","
                + "\"type\":\"native_radio\","
                + "\"options\":["
                + "{\"key\":\"new\",\"text\":\"New\"},"
                + "{\"key\":\"continue_service\",\"text\":\"Continue service\"}"
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

    private static class TestHarmReductionClientStatusActionHelper extends HarmReductionClientStatusActionHelper {
        private final boolean hasVisit;
        private final String pregnancyStatus;

        TestHarmReductionClientStatusActionHelper(MemberObject memberObject, boolean hasVisit, String pregnancyStatus) {
            super(memberObject);
            this.hasVisit = hasVisit;
            this.pregnancyStatus = pregnancyStatus;
        }

        @Override
        protected boolean hasPreviousFollowUpVisit() {
            return hasVisit;
        }

        @Override
        protected String getRiskAssessmentPregnancyStatus() {
            return pregnancyStatus;
        }
    }
}
