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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HivInfectionStatusFormAssetsTest {

    private static final List<String> FORM_PATHS = Arrays.asList(
            "src/main/assets/json.form/harm_reduction_hiv_infection_status.json",
            "src/main/assets/json.form-sw/harm_reduction_hiv_infection_status.json"
    );

    @Test
    public void testHivInfectionStatusFormsAskAboutCtcEnrollmentBeforeAdherenceAndCtcId() throws Exception {
        for (String formPath : FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

            JSONObject ctcEnrollmentField = getField(fields, "enrolled_into_ctc_services");
            JSONObject adherenceField = getField(fields, "drug_adherence_status_ctc");
            JSONObject ctcIdField = getField(fields, "ctc_id");

            Assert.assertTrue("CTC enrollment question should be placed before adherence in " + formPath,
                    indexOf(fields, "enrolled_into_ctc_services") < indexOf(fields, "drug_adherence_status_ctc"));
            Assert.assertTrue("Adherence question should be placed before CTC ID in " + formPath,
                    indexOf(fields, "drug_adherence_status_ctc") < indexOf(fields, "ctc_id"));
            Assert.assertEquals("native_radio", ctcEnrollmentField.getString("type"));
            Assert.assertEquals("native_radio", adherenceField.getString("type"));
            Assert.assertEquals(2, ctcEnrollmentField.getJSONArray("options").length());
            Assert.assertTrue(hasOption(ctcEnrollmentField, "yes"));
            Assert.assertTrue(hasOption(ctcEnrollmentField, "no"));
            Assert.assertEquals("mask_edit_text", ctcIdField.getString("type"));
            Assert.assertEquals("harm-reduction-hiv-infection-status-relevance-rules.yml",
                    ctcEnrollmentField.getJSONObject("relevance")
                            .getJSONObject("rules-engine")
                            .getJSONObject("ex-rules")
                            .getString("rules-file"));
            Assert.assertEquals("harm-reduction-hiv-infection-status-relevance-rules.yml",
                    ctcIdField.getJSONObject("relevance")
                            .getJSONObject("rules-engine")
                            .getJSONObject("ex-rules")
                            .getString("rules-file"));
        }
    }

    @Test
    public void testHivInfectionStatusRulesAndFollowUpVisitMappingsIncludeCtcFields() throws Exception {
        String rules = readText("src/main/assets/rule/harm-reduction-hiv-infection-status-relevance-rules.yml");
        JSONObject clientFields = readJson("src/main/assets/ec_client_fields.json");
        Set<String> followUpVisitColumns = mappedColumnsForBindObject(clientFields, "ec_harm_reduction_followup_visit");

        String ctcEnrollmentRule = getRuleBlock(rules, "step1_enrolled_into_ctc_services");
        Assert.assertTrue(ctcEnrollmentRule.contains("step1_hiv_tested == 'yes'"));
        Assert.assertTrue(ctcEnrollmentRule.contains("step1_hiv_results == 'positive'"));

        String adherenceRule = getRuleBlock(rules, "step1_drug_adherence_status_ctc");
        Assert.assertTrue(adherenceRule.contains("step1_hiv_tested == 'yes'"));
        Assert.assertTrue(adherenceRule.contains("step1_hiv_results == 'positive'"));
        Assert.assertTrue(adherenceRule.contains("step1_enrolled_into_ctc_services == 'yes'"));

        String ctcIdRule = getRuleBlock(rules, "step1_ctc_id");
        Assert.assertTrue(ctcIdRule.contains("step1_hiv_tested == 'yes'"));
        Assert.assertTrue(ctcIdRule.contains("step1_hiv_results == 'positive'"));
        Assert.assertTrue(ctcIdRule.contains("step1_enrolled_into_ctc_services == 'yes'"));

        Assert.assertTrue(followUpVisitColumns.contains("enrolled_into_ctc_services"));
        Assert.assertTrue(followUpVisitColumns.contains("ctc_id"));
    }

    private static JSONObject readJson(String relativePath) throws Exception {
        return new JSONObject(readText(relativePath));
    }

    private static String readText(String relativePath) throws IOException {
        return new String(Files.readAllBytes(resolvePath(relativePath)), StandardCharsets.UTF_8);
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

    private static int indexOf(JSONArray fields, String key) throws Exception {
        for (int i = 0; i < fields.length(); i++) {
            if (key.equals(fields.getJSONObject(i).optString("key"))) {
                return i;
            }
        }

        throw new AssertionError("Missing field: " + key);
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

    private static Set<String> mappedColumnsForBindObject(JSONObject clientFields, String bindObjectName) throws Exception {
        JSONArray bindObjects = clientFields.getJSONArray("bindobjects");

        for (int i = 0; i < bindObjects.length(); i++) {
            JSONObject bindObject = bindObjects.getJSONObject(i);
            if (!bindObjectName.equals(bindObject.optString("name"))) {
                continue;
            }

            Set<String> columns = new LinkedHashSet<>();
            JSONArray mappedColumns = bindObject.optJSONArray("columns");
            if (mappedColumns == null) {
                return columns;
            }

            for (int j = 0; j < mappedColumns.length(); j++) {
                columns.add(mappedColumns.getJSONObject(j).optString("column_name"));
            }

            return columns;
        }

        throw new AssertionError("Missing bind object: " + bindObjectName);
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
