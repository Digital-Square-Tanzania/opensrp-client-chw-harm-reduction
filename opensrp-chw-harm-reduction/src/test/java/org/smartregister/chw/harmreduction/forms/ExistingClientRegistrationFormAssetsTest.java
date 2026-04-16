package org.smartregister.chw.harmreduction.forms;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.util.Constants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ExistingClientRegistrationFormAssetsTest {

    private static final List<String> FORM_PATHS = Arrays.asList(
            "src/main/assets/json.form/harm_reduction_register_existing_client.json",
            "src/main/assets/json.form-sw/harm_reduction_register_existing_client.json"
    );

    @Test
    public void testExistingClientRegistrationFormsPersistToRiskAssessmentTable() throws Exception {
        for (String formPath : FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

            Assert.assertEquals(Constants.EVENT_TYPE.HARM_REDUCTION_RISK_ASSESSMENT, form.getString("encounter_type"));
            Assert.assertTrue(hasField(fields, "roc_group_type"));
            Assert.assertTrue(hasField(fields, "uic"));
            Assert.assertTrue(hasField(fields, "follow_up_status"));
            Assert.assertTrue(hasField(fields, "service_start_date"));
            Assert.assertTrue(hasField(fields, "maskani_name"));

            JSONObject rocGroupType = getField(fields, "roc_group_type");
            Assert.assertEquals("hidden", rocGroupType.getString("type"));

            JSONObject followUpStatus = getField(fields, "follow_up_status");
            Assert.assertEquals(new LinkedHashSet<>(Arrays.asList(
                    "continue_service",
                    "started_mat_services",
                    "lost",
                    "moved",
                    "discontinued_service",
                    "overdose",
                    "client_deceased"
            )), optionKeys(followUpStatus));

            JSONObject causeOfDeath = getField(fields, "cause_of_death");
            Assert.assertEquals(new LinkedHashSet<>(Arrays.asList(
                    "accident",
                    "overdose",
                    "unknown",
                    "other"
            )), optionKeys(causeOfDeath));
        }
    }

    @Test
    public void testExistingClientRegistrationDerivedFieldsAndMappingsStayInSync() throws Exception {
        JSONObject clientFields = readJson("src/main/assets/ec_client_fields.json");
        Set<String> mappedColumns = mappedColumns(clientFields);
        String rules = readText("src/main/assets/rule/harm-reduction-register-existing-client-rules.yml");

        Assert.assertTrue(mappedColumns.contains("uic"));
        Assert.assertTrue(mappedColumns.contains("service_start_date"));
        Assert.assertTrue(mappedColumns.contains("roc_group_type"));
        Assert.assertTrue(mappedColumns.contains("follow_up_status"));

        String statusRule = getRuleBlock(rules, "step1_status");
        Assert.assertTrue(statusRule.contains("calculation = step1_follow_up_status"));

        String startedMatRule = getRuleBlock(rules, "step1_client_started_mat");
        Assert.assertTrue(startedMatRule.contains("step1_follow_up_status == 'started_mat_services' ? 'yes' : 'no'"));

        for (String formPath : FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

            JSONObject statusField = getField(fields, "status");
            Assert.assertEquals("hidden", statusField.getString("type"));
            Assert.assertEquals("harm-reduction-register-existing-client-rules.yml",
                    statusField.getJSONObject("calculation")
                            .getJSONObject("rules-engine")
                            .getJSONObject("ex-rules")
                            .getString("rules-file"));

            JSONObject clientStartedMatField = getField(fields, "client_started_mat");
            Assert.assertEquals("hidden", clientStartedMatField.getString("type"));
            Assert.assertEquals("harm-reduction-register-existing-client-rules.yml",
                    clientStartedMatField.getJSONObject("calculation")
                            .getJSONObject("rules-engine")
                            .getJSONObject("ex-rules")
                            .getString("rules-file"));
        }
    }

    private static Set<String> mappedColumns(JSONObject clientFields) throws Exception {
        Set<String> columns = new LinkedHashSet<>();
        JSONArray bindObjects = clientFields.getJSONArray("bindobjects");

        for (int i = 0; i < bindObjects.length(); i++) {
            JSONArray tableColumns = bindObjects.getJSONObject(i).optJSONArray("columns");
            if (tableColumns == null) {
                continue;
            }

            for (int j = 0; j < tableColumns.length(); j++) {
                columns.add(tableColumns.getJSONObject(j).optString("column_name"));
            }
        }

        return columns;
    }

    private static Set<String> optionKeys(JSONObject field) throws Exception {
        Set<String> optionKeys = new LinkedHashSet<>();
        JSONArray options = field.getJSONArray("options");

        for (int i = 0; i < options.length(); i++) {
            optionKeys.add(options.getJSONObject(i).optString("key"));
        }

        return optionKeys;
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

    private static String getRuleBlock(String rules, String ruleName) {
        String marker = "name: " + ruleName;
        int start = rules.indexOf(marker);
        Assert.assertTrue("Missing rule: " + ruleName, start >= 0);

        int next = rules.indexOf("\n---", start);
        return next >= 0 ? rules.substring(start, next) : rules.substring(start);
    }

    private static JSONObject readJson(String relativePath) throws Exception {
        return new JSONObject(readText(relativePath));
    }

    private static String readText(String relativePath) throws IOException {
        return new String(Files.readAllBytes(resolvePath(relativePath)), StandardCharsets.UTF_8);
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
