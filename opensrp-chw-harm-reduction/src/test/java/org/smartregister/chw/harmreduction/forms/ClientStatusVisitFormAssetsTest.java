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

public class ClientStatusVisitFormAssetsTest {

    private static final List<String> FORM_PATHS = Arrays.asList(
            "src/main/assets/json.form/harm_reduction_client_status_visit.json",
            "src/main/assets/json.form-sw/harm_reduction_client_status_visit.json"
    );

    @Test
    public void testClientStatusVisitFormsCollectFollowUpStatus() throws Exception {
        Set<String> expectedOptions = new LinkedHashSet<>(Arrays.asList(
                "continue_service",
                "started_mat_services",
                "lost",
                "moved",
                "discontinued_service",
                "overdose",
                "client_deceased"
        ));

        for (String formPath : FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

            Assert.assertFalse(hasField(fields, "client_status"));
            Assert.assertTrue(hasField(fields, "follow_up_status"));

            JSONObject followUpStatusField = getField(fields, "follow_up_status");
            Assert.assertEquals(expectedOptions, optionKeys(followUpStatusField));
        }
    }

    @Test
    public void testClientStatusVisitRulesUseFollowUpStatus() throws Exception {
        String rules = readText("src/main/assets/rule/harm-reduction-client-status-visit-relevance-rules.yml");

        Assert.assertFalse(rules.contains("step1_client_status"));

        String deathRule = getRuleBlock(rules, "step1_cause_of_death");
        Assert.assertTrue(deathRule.contains("step1_follow_up_status == 'client_deceased'"));

        String overdoseRule = getRuleBlock(rules, "step1_overdose_reason");
        Assert.assertTrue(overdoseRule.contains("step1_follow_up_status == 'overdose'"));

        String serviceRule = getRuleBlock(rules, "step1_substance_use_methods");
        Assert.assertTrue(serviceRule.contains("step1_follow_up_status == 'continue_service'"));
    }

    @Test
    public void testCommunityFollowUpMappingPersistsFollowUpStatusConcept() throws Exception {
        JSONObject clientFields = readJson("src/main/assets/ec_client_fields.json");
        JSONArray bindObjects = clientFields.getJSONArray("bindobjects");

        JSONObject followupVisitTable = null;
        for (int i = 0; i < bindObjects.length(); i++) {
            JSONObject bindObject = bindObjects.getJSONObject(i);
            if ("ec_harm_reduction_followup_visit".equals(bindObject.optString("name"))) {
                followupVisitTable = bindObject;
                break;
            }
        }

        Assert.assertNotNull(followupVisitTable);

        JSONObject followUpStatusMapping = getColumn(followupVisitTable.getJSONArray("columns"), "follow_up_status");
        Assert.assertEquals(
                "follow_up_status",
                followUpStatusMapping.getJSONObject("json_mapping").getString("concept")
        );
    }

    private static JSONObject getColumn(JSONArray columns, String columnName) throws Exception {
        for (int i = 0; i < columns.length(); i++) {
            JSONObject column = columns.getJSONObject(i);
            if (columnName.equals(column.optString("column_name"))) {
                return column;
            }
        }

        throw new AssertionError("Missing column: " + columnName);
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
