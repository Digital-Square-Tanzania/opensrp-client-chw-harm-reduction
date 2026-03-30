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
import java.util.List;

public class MarkClientStartedMatFormAssetsTest {

    private static final List<String> FORM_PATHS = Arrays.asList(
            "src/main/assets/json.form/harm_reduction_mark_the_client_has_started_mat.json",
            "src/main/assets/json.form-sw/harm_reduction_mark_the_client_has_started_mat.json"
    );

    @Test
    public void testMatConfirmationFormsKeepStatusDrivenBySharedRulesFile() throws Exception {
        for (String formPath : FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
            JSONObject statusField = getField(fields, "status");

            Assert.assertEquals("hidden", statusField.getString("type"));
            Assert.assertEquals("harm-reduction-mark-the-client-has-started-mat-rules.yml",
                    statusField.getJSONObject("calculation")
                            .getJSONObject("rules-engine")
                            .getJSONObject("ex-rules")
                            .getString("rules-file"));
        }
    }

    @Test
    public void testNoMatAnswerKeepsClientOnCommunityService() throws Exception {
        String rules = readText("src/main/assets/rule/harm-reduction-mark-the-client-has-started-mat-rules.yml");
        String ruleBlock = getRuleBlock(rules, "step1_status");

        Assert.assertTrue(ruleBlock.contains("step1_client_started_mat == 'yes' ? 'started_mat_services' : 'on_community_service'"));
        Assert.assertFalse(ruleBlock.contains("step1_client_started_mat == 'yes' ? 'started_mat_services' : '-'"));
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
