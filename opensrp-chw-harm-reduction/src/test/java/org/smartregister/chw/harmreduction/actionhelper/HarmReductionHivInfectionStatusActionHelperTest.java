package org.smartregister.chw.harmreduction.actionhelper;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.domain.MemberObject;

public class HarmReductionHivInfectionStatusActionHelperTest {

    @Test
    public void getPreProcessedShouldPrefillAndLockFieldsWhenClientHasPreviousPositiveHivVisit() throws Exception {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");

        HarmReductionHivInfectionStatusActionHelper helper = new TestHarmReductionHivInfectionStatusActionHelper(
                memberObject,
                true,
                true,
                "facility",
                "yes",
                "12-34-5678-901234"
        );
        helper.onJsonFormLoaded(getHivInfectionStatusForm(), null, null);

        JSONObject form = new JSONObject(helper.getPreProcessed());

        Assert.assertEquals("drug_adherence_status_ctc",
                form.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS)
                        .getJSONObject(0).getString(JsonFormConstants.KEY));
        Assert.assertEquals("yes", getField(form, "hiv_tested").getString("value"));
        Assert.assertTrue(getField(form, "hiv_tested").getBoolean("read_only"));
        Assert.assertFalse(getField(form, "hiv_tested").getBoolean("editable"));
        Assert.assertEquals("facility", getField(form, "hiv_test_location").getString("value"));
        Assert.assertTrue(getField(form, "hiv_test_location").getBoolean("read_only"));
        Assert.assertFalse(getField(form, "hiv_test_location").getBoolean("editable"));
        Assert.assertEquals("positive", getField(form, "hiv_results").getString("value"));
        Assert.assertTrue(getField(form, "hiv_results").getBoolean("read_only"));
        Assert.assertFalse(getField(form, "hiv_results").getBoolean("editable"));
        Assert.assertEquals("yes", getField(form, "enrolled_into_ctc_services").getString("value"));
        Assert.assertTrue(getField(form, "enrolled_into_ctc_services").getBoolean("read_only"));
        Assert.assertFalse(getField(form, "enrolled_into_ctc_services").getBoolean("editable"));
        Assert.assertEquals("12-34-5678-901234", getField(form, "ctc_id").getString("value"));
        Assert.assertTrue(getField(form, "ctc_id").getBoolean("read_only"));
        Assert.assertFalse(getField(form, "ctc_id").getBoolean("editable"));
        Assert.assertEquals("", getField(form, "drug_adherence_status_ctc").optString("value"));
        Assert.assertEquals("native_radio", getField(form, "drug_adherence_status_ctc").getString("type"));
        Assert.assertFalse(hasOption(getField(form, "drug_adherence_status_ctc"), "not_started"));
        Assert.assertFalse(getField(form, "drug_adherence_status_ctc").has("read_only"));
        Assert.assertFalse(getField(form, "drug_adherence_status_ctc").has("editable"));
    }

    @Test
    public void getPreProcessedShouldReturnNullWhenThereIsNoPreviousFollowUpVisit() {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");

        HarmReductionHivInfectionStatusActionHelper helper = new TestHarmReductionHivInfectionStatusActionHelper(
                memberObject,
                false,
                true,
                "facility",
                "yes",
                "12-34-5678-901234"
        );
        helper.onJsonFormLoaded(getHivInfectionStatusForm(), null, null);

        Assert.assertNull(helper.getPreProcessed());
    }

    @Test
    public void getPreProcessedShouldReturnNullWhenClientHasNoPreviousPositiveHivVisit() {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("base-id");

        HarmReductionHivInfectionStatusActionHelper helper = new TestHarmReductionHivInfectionStatusActionHelper(
                memberObject,
                true,
                false,
                "facility",
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
                + "{\"key\":\"poor_adherence\",\"text\":\"Poor adherence\"},"
                + "{\"key\":\"not_started\",\"text\":\"Not started\"}"
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

    private static boolean hasOption(JSONObject field, String optionKey) throws Exception {
        for (int i = 0; i < field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
            JSONObject option = field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
            if (optionKey.equals(option.optString(JsonFormConstants.KEY))) {
                return true;
            }
        }
        return false;
    }

    private static class TestHarmReductionHivInfectionStatusActionHelper extends HarmReductionHivInfectionStatusActionHelper {
        private final boolean hasPreviousVisit;
        private final boolean hasPreviousPositiveVisit;
        private final String latestPositiveHivTestLocation;
        private final String latestPositiveEnrolledIntoCtcServices;
        private final String latestPositiveCtcId;

        TestHarmReductionHivInfectionStatusActionHelper(MemberObject memberObject,
                                                        boolean hasPreviousVisit,
                                                        boolean hasPreviousPositiveVisit,
                                                        String latestPositiveHivTestLocation,
                                                        String latestPositiveEnrolledIntoCtcServices,
                                                        String latestPositiveCtcId) {
            super(memberObject);
            this.hasPreviousVisit = hasPreviousVisit;
            this.hasPreviousPositiveVisit = hasPreviousPositiveVisit;
            this.latestPositiveHivTestLocation = latestPositiveHivTestLocation;
            this.latestPositiveEnrolledIntoCtcServices = latestPositiveEnrolledIntoCtcServices;
            this.latestPositiveCtcId = latestPositiveCtcId;
        }

        @Override
        protected boolean hasPreviousFollowUpVisit() {
            return hasPreviousVisit;
        }

        @Override
        protected boolean hasPreviousPositiveHivVisit() {
            return hasPreviousPositiveVisit;
        }

        @Override
        protected String getLatestPositiveHivTestLocation() {
            return latestPositiveHivTestLocation;
        }

        @Override
        protected String getLatestPositiveEnrolledIntoCtcServices() {
            return latestPositiveEnrolledIntoCtcServices;
        }

        @Override
        protected String getLatestPositiveCtcId() {
            return latestPositiveCtcId;
        }
    }
}
