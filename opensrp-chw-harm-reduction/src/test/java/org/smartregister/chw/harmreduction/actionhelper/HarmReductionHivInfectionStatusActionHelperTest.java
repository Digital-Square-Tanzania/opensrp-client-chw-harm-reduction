package org.smartregister.chw.harmreduction.actionhelper;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.domain.MemberObject;

public class HarmReductionHivInfectionStatusActionHelperTest {

    @Test
    public void getPreProcessedShouldPrefillPreviousHivStatusButKeepAdherenceEditable() throws Exception {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");

        HarmReductionHivInfectionStatusActionHelper helper = new TestHarmReductionHivInfectionStatusActionHelper(
                memberObject,
                true,
                "yes",
                "facility",
                "positive",
                "yes",
                "12-34-5678-901234"
        );
        helper.onJsonFormLoaded(getHivInfectionStatusForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());

        Assert.assertEquals("yes", getField(form, "hiv_tested").getString("value"));
        Assert.assertEquals("facility", getField(form, "hiv_test_location").getString("value"));
        Assert.assertEquals("positive", getField(form, "hiv_results").getString("value"));
        Assert.assertEquals("yes", getField(form, "enrolled_into_ctc_services").getString("value"));
        Assert.assertEquals("12-34-5678-901234", getField(form, "ctc_id").getString("value"));
        Assert.assertEquals("", getField(form, "drug_adherence_status_ctc").optString("value"));
        Assert.assertEquals("native_radio", getField(form, "drug_adherence_status_ctc").getString("type"));
        Assert.assertFalse(getField(form, "drug_adherence_status_ctc").has("read_only"));
    }

    @Test
    public void getPreProcessedShouldReturnNullWhenThereIsNoPreviousFollowUpVisit() {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");

        HarmReductionHivInfectionStatusActionHelper helper = new TestHarmReductionHivInfectionStatusActionHelper(
                memberObject,
                false,
                "yes",
                "facility",
                "positive",
                "yes",
                "12-34-5678-901234"
        );
        helper.onJsonFormLoaded(getHivInfectionStatusForm(), null, null);

        Assert.assertNull(helper.getPreProcessed());
    }

    private static String getHivInfectionStatusForm() {
        return "{"
                + "\"step1\":{"
                + "\"fields\":["
                + "{"
                + "\"key\":\"hiv_tested\","
                + "\"type\":\"native_radio\","
                + "\"options\":["
                + "{\"key\":\"yes\",\"text\":\"Yes\"},"
                + "{\"key\":\"no\",\"text\":\"No\"}"
                + "]"
                + "},"
                + "{"
                + "\"key\":\"hiv_test_location\","
                + "\"type\":\"native_radio\","
                + "\"options\":["
                + "{\"key\":\"facility\",\"text\":\"Facility\"},"
                + "{\"key\":\"community\",\"text\":\"Community\"}"
                + "]"
                + "},"
                + "{"
                + "\"key\":\"hiv_results\","
                + "\"type\":\"native_radio\","
                + "\"options\":["
                + "{\"key\":\"negative\",\"text\":\"Negative\"},"
                + "{\"key\":\"positive\",\"text\":\"Positive\"},"
                + "{\"key\":\"unknown\",\"text\":\"Unknown\"}"
                + "]"
                + "},"
                + "{"
                + "\"key\":\"drug_adherence_status_ctc\","
                + "\"type\":\"native_radio\","
                + "\"options\":["
                + "{\"key\":\"good_adherence\",\"text\":\"Good adherence\"},"
                + "{\"key\":\"poor_adherence\",\"text\":\"Poor adherence\"}"
                + "]"
                + "},"
                + "{"
                + "\"key\":\"enrolled_into_ctc_services\","
                + "\"type\":\"native_radio\","
                + "\"options\":["
                + "{\"key\":\"yes\",\"text\":\"Yes\"},"
                + "{\"key\":\"no\",\"text\":\"No\"}"
                + "]"
                + "},"
                + "{"
                + "\"key\":\"ctc_id\","
                + "\"type\":\"mask_edit_text\""
                + "}"
                + "]"
                + "}"
                + "}";
    }

    private static JSONObject getField(JSONObject form, String key) throws Exception {
        for (int i = 0; i < form.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).length(); i++) {
            JSONObject field = form.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).getJSONObject(i);
            if (key.equals(field.optString(JsonFormConstants.KEY))) {
                return field;
            }
        }

        throw new AssertionError("Missing field: " + key);
    }

    private static class TestHarmReductionHivInfectionStatusActionHelper extends HarmReductionHivInfectionStatusActionHelper {
        private final boolean hasPreviousVisit;
        private final String latestHivTested;
        private final String latestHivTestLocation;
        private final String latestHivResults;
        private final String latestEnrolledIntoCtcServices;
        private final String latestCtcId;

        TestHarmReductionHivInfectionStatusActionHelper(MemberObject memberObject,
                                                        boolean hasPreviousVisit,
                                                        String latestHivTested,
                                                        String latestHivTestLocation,
                                                        String latestHivResults,
                                                        String latestEnrolledIntoCtcServices,
                                                        String latestCtcId) {
            super(memberObject);
            this.hasPreviousVisit = hasPreviousVisit;
            this.latestHivTested = latestHivTested;
            this.latestHivTestLocation = latestHivTestLocation;
            this.latestHivResults = latestHivResults;
            this.latestEnrolledIntoCtcServices = latestEnrolledIntoCtcServices;
            this.latestCtcId = latestCtcId;
        }

        @Override
        protected boolean hasPreviousFollowUpVisit() {
            return hasPreviousVisit;
        }

        @Override
        protected String getLatestHivTested() {
            return latestHivTested;
        }

        @Override
        protected String getLatestHivTestLocation() {
            return latestHivTestLocation;
        }

        @Override
        protected String getLatestHivResults() {
            return latestHivResults;
        }

        @Override
        protected String getLatestEnrolledIntoCtcServices() {
            return latestEnrolledIntoCtcServices;
        }

        @Override
        protected String getLatestCtcId() {
            return latestCtcId;
        }
    }
}
