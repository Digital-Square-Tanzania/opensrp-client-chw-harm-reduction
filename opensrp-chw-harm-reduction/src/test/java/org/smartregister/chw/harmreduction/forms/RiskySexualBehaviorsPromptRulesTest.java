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

public class RiskySexualBehaviorsPromptRulesTest {

    @Test
    public void testCondomPromptRuleRequiresSexualActivityYesAndCondomUseNo() throws Exception {
        JSONObject form = new JSONObject(readText("src/main/assets/json.form-sw/harm_reduction_risky_sexual_behaviors_condoms.json"));
        JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
        JSONObject promptField = getField(fields, "condom_education_prompt");
        String rules = readText("src/main/assets/rule/harm-reduction-risky-sexual-behaviors-condoms-relevance-rules.yml");
        String promptRuleBlock = getRuleBlock(rules, "step1_condom_education_prompt");
        String condomUseRuleBlock = getRuleBlock(rules, "step1_condom_use_during_sex");

        Assert.assertEquals("harm-reduction-risky-sexual-behaviors-condoms-relevance-rules.yml",
                promptField.getJSONObject("relevance")
                        .getJSONObject("rules-engine")
                        .getJSONObject("ex-rules")
                        .getString("rules-file"));
        Assert.assertTrue(promptRuleBlock.contains("condition: \"(step1_engaging_in_sexual_activity == 'yes' && step1_condom_use_during_sex == 'no')\""));
        Assert.assertFalse("Prompt rule should not show guidance when engaging_in_sexual_activity is no",
                promptRuleBlock.contains("step1_engaging_in_sexual_activity == 'no'"));
        Assert.assertTrue(condomUseRuleBlock.contains("condition: \"step1_engaging_in_sexual_activity == 'yes'\""));
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
