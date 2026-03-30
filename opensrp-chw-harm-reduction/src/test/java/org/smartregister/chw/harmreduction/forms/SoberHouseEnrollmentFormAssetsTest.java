package org.smartregister.chw.harmreduction.forms;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SoberHouseEnrollmentFormAssetsTest {

    private static final List<String> ENROLLMENT_FORM_PATHS = Arrays.asList(
            "src/main/assets/json.form/harm_reduction_sober_house_enrollment.json",
            "src/main/assets/json.form-sw/harm_reduction_sober_house_enrollment.json"
    );

    @Test
    public void testEnrollmentFormsDefineTreatmentFollowUpPerPositiveScreening() throws Exception {
        for (String formPath : ENROLLMENT_FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

            Map<String, String> expectedFollowUpFields = expectedFollowUpFields(fields);
            Set<String> actualFollowUpFields = actualFollowUpFields(fields);

            Assert.assertEquals("Expected one treatment follow-up per symptom-based screening result in " + formPath,
                    expectedFollowUpFields.keySet(), actualFollowUpFields);
            Assert.assertFalse("Legacy shared treatment follow-up should be removed from " + formPath,
                    hasField(fields, "treatment_after_screening"));

            for (String followUpKey : expectedFollowUpFields.keySet()) {
                JSONObject followUpField = getField(fields, followUpKey);
                Assert.assertEquals("harm-reduction-sober-house-enrollment-rules.yml",
                        followUpField.getJSONObject("relevance")
                                .getJSONObject("rules-engine")
                                .getJSONObject("ex-rules")
                                .getString("rules-file"));
            }
        }
    }

    @Test
    public void testEnrollmentTreatmentRulesAndMappingsMatchSymptomBasedResults() throws Exception {
        JSONObject form = readJson("src/main/assets/json.form/harm_reduction_sober_house_enrollment.json");
        JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
        Map<String, String> expectedFollowUpFields = expectedFollowUpFields(fields);

        String rules = readText("src/main/assets/rule/harm-reduction-sober-house-enrollment-rules.yml");
        JSONObject clientFields = readJson("src/main/assets/ec_client_fields.json");
        Set<String> mappedColumns = mappedColumns(clientFields);

        Assert.assertFalse(rules.contains("name: step1_treatment_after_screening"));
        Assert.assertFalse(mappedColumns.contains("treatment_after_screening"));
        Assert.assertTrue(mappedColumns.contains("client_status"));

        String screeningRule = getRuleBlock(rules, "step1_screening_tests_done");
        Assert.assertTrue(screeningRule.contains("step1_client_status == 'new_client' || step1_client_status == 'relapsed'"));

        String eligibilityRule = getRuleBlock(rules, "step1_sober_house_eligible");
        Assert.assertTrue(eligibilityRule.contains("step1_client_status == 'new_client' || step1_client_status == 'relapsed'"));

        String nicknameRule = getRuleBlock(rules, "step1_nickname");
        Assert.assertTrue(nicknameRule.contains("step1_client_status == 'existing'"));
        Assert.assertTrue(nicknameRule.contains("step1_sober_house_eligible == 'yes'"));

        for (Map.Entry<String, String> entry : expectedFollowUpFields.entrySet()) {
            String followUpKey = entry.getKey();
            String resultKey = entry.getValue();

            String ruleBlock = getRuleBlock(rules, "step1_" + followUpKey);
            Assert.assertTrue("Missing client-status gate for " + followUpKey,
                    ruleBlock.contains("step1_client_status == 'new_client' || step1_client_status == 'relapsed'"));
            Assert.assertTrue("Missing treatment relevance condition for " + followUpKey,
                    ruleBlock.contains("step1_" + resultKey + " == 'has_symptoms'"));
            Assert.assertTrue("Missing relevance action for " + followUpKey,
                    ruleBlock.contains("  - \"isRelevant = true\""));

            Assert.assertTrue("Missing event mapping for " + resultKey, mappedColumns.contains(resultKey));
            Assert.assertTrue("Missing event mapping for " + followUpKey, mappedColumns.contains(followUpKey));
        }
    }

    @Test
    public void testEnrollmentFormsKeepHivStatusHidden() throws Exception {
        for (String formPath : ENROLLMENT_FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
            JSONObject hivStatusField = getField(fields, "hiv_status");

            Assert.assertEquals("hiv_status should stay hidden in " + formPath,
                    "hidden", hivStatusField.getString("type"));
            Assert.assertFalse("hiv_status should not have UI relevance in " + formPath,
                    hivStatusField.has("relevance"));
            Assert.assertEquals("harm-reduction-sober-house-enrollment-rules.yml",
                    hivStatusField.getJSONObject("calculation")
                            .getJSONObject("rules-engine")
                            .getJSONObject("ex-rules")
                            .getString("rules-file"));
        }
    }

    @Test
    public void testEnrollmentFormsCollectClientStatusBeforeScreening() throws Exception {
        for (String formPath : ENROLLMENT_FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

            Assert.assertEquals("client_status", fields.getJSONObject(0).optString("key"));

            JSONObject clientStatusField = getField(fields, "client_status");
            Assert.assertEquals(3, clientStatusField.getJSONArray("options").length());
            Assert.assertTrue(hasOption(clientStatusField, "new_client"));
            Assert.assertTrue(hasOption(clientStatusField, "existing"));
            Assert.assertTrue(hasOption(clientStatusField, "relapsed"));

            JSONObject screeningField = getField(fields, "screening_tests_done");
            Assert.assertEquals("harm-reduction-sober-house-enrollment-rules.yml",
                    screeningField.getJSONObject("relevance")
                            .getJSONObject("rules-engine")
                            .getJSONObject("ex-rules")
                            .getString("rules-file"));

            JSONObject eligibilityField = getField(fields, "sober_house_eligible");
            Assert.assertEquals("harm-reduction-sober-house-enrollment-rules.yml",
                    eligibilityField.getJSONObject("relevance")
                            .getJSONObject("rules-engine")
                            .getJSONObject("ex-rules")
                            .getString("rules-file"));
        }
    }

    private static Map<String, String> expectedFollowUpFields(JSONArray fields) throws Exception {
        Map<String, String> expected = new LinkedHashMap<>();

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String key = field.optString("key");
            if (key.endsWith("_result") && hasOption(field, "has_symptoms")) {
                expected.put(key.replaceFirst("_result$", "_treatment_after_screening"), key);
            }
        }

        return expected;
    }

    private static Set<String> actualFollowUpFields(JSONArray fields) throws Exception {
        Set<String> keys = new LinkedHashSet<>();

        for (int i = 0; i < fields.length(); i++) {
            String key = fields.getJSONObject(i).optString("key");
            if (key.endsWith("_treatment_after_screening")) {
                keys.add(key);
            }
        }

        return keys;
    }

    private static boolean hasOption(JSONObject field, String optionKey) throws Exception {
        JSONArray options = field.optJSONArray("options");
        if (options == null) {
            return false;
        }

        for (int i = 0; i < options.length(); i++) {
            if (optionKey.equals(options.getJSONObject(i).optString("key"))) {
                return true;
            }
        }

        return false;
    }

    private static Set<String> mappedColumns(JSONObject clientFields) throws Exception {
        Set<String> columns = new LinkedHashSet<>();
        JSONArray bindObjects = clientFields.getJSONArray("bindobjects");

        for (int i = 0; i < bindObjects.length(); i++) {
            JSONArray mappedColumns = bindObjects.getJSONObject(i).optJSONArray("columns");
            if (mappedColumns == null) {
                continue;
            }

            for (int j = 0; j < mappedColumns.length(); j++) {
                columns.add(mappedColumns.getJSONObject(j).optString("column_name"));
            }
        }

        return columns;
    }

    private static boolean hasField(JSONArray fields, String key) throws Exception {
        for (int i = 0; i < fields.length(); i++) {
            if (key.equals(fields.getJSONObject(i).optString("key"))) {
                return true;
            }
        }

        return false;
    }

    private static JSONObject getField(JSONArray fields, String key) throws Exception {
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            if (key.equals(field.optString("key"))) {
                return field;
            }
        }

        throw new AssertionError("Missing field: " + key);
    }

    private static JSONObject readJson(String relativePath) throws Exception {
        return new JSONObject(readText(relativePath));
    }

    private static String readText(String relativePath) throws IOException {
        return new String(Files.readAllBytes(resolvePath(relativePath)), StandardCharsets.UTF_8);
    }

    private static String getRuleBlock(String rules, String ruleName) {
        String marker = "name: " + ruleName;
        int start = rules.indexOf(marker);
        Assert.assertTrue("Missing rule: " + ruleName, start >= 0);

        int next = rules.indexOf("\n---", start);
        return next >= 0 ? rules.substring(start, next) : rules.substring(start);
    }

    private static Path resolvePath(String relativePath) {
        Path direct = Paths.get(relativePath);
        if (Files.exists(direct)) {
            return direct;
        }

        Path modulePath = Paths.get("opensrp-chw-harm-reduction").resolve(relativePath);
        if (Files.exists(modulePath)) {
            return modulePath;
        }

        throw new AssertionError("Could not resolve path: " + relativePath);
    }
}
