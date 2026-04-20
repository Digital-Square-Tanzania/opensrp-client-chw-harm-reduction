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

public class RiskAssessmentFormAssetsTest {

    private static final List<String> FORM_PATHS = Arrays.asList(
            "src/main/assets/json.form/harm_reduction_risk_assessment.json",
            "src/main/assets/json.form-sw/harm_reduction_risk_assessment.json"
    );

    @Test
    public void testRiskAssessmentFormsFilterSubstancesPerSelectedRoute() throws Exception {
        for (String formPath : FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

            Assert.assertEquals("hidden", getField(fields, "substances_used_injecting").getString("type"));
            Assert.assertEquals("hidden", getField(fields, "substances_used_non_injecting").getString("type"));

            Assert.assertEquals(new LinkedHashSet<>(Arrays.asList(
                    "heroine",
                    "cocaine",
                    "valium",
                    "methamphetamine",
                    "tramadol",
                    "other"
            )), optionKeys(getField(fields, "substances_used_injecting_route")));

            Assert.assertEquals(new LinkedHashSet<>(Arrays.asList(
                    "heroine",
                    "marijuana",
                    "cocaine",
                    "methamphetamine",
                    "tobacco",
                    "other"
            )), optionKeys(getField(fields, "substances_used_smoking_route")));

            Assert.assertEquals(new LinkedHashSet<>(Arrays.asList(
                    "alcohol",
                    "valium",
                    "tramadol",
                    "mirungi",
                    "tobacco",
                    "other"
            )), optionKeys(getField(fields, "substances_used_drinking_route")));

            Assert.assertEquals(new LinkedHashSet<>(Arrays.asList(
                    "heroine",
                    "cocaine",
                    "valium",
                    "methamphetamine",
                    "tramadol",
                    "other"
            )), optionKeys(getField(fields, "substances_used_sniffing_route")));
        }
    }

    @Test
    public void testRiskAssessmentRulesAggregateStoredSubstanceValues() throws Exception {
        String rules = readText("src/main/assets/rule/harm-reduction-risk-assessment-relevance-rules.yml");

        String injectingRouteRule = getRuleBlock(rules, "step1_substances_used_injecting_route");
        Assert.assertTrue(injectingRouteRule.contains("step1_route_of_substance_use.contains('injecting')"));

        String injectingCalculationRule = getRuleBlock(rules, "step1_substances_used_injecting");
        Assert.assertTrue(injectingCalculationRule.contains("step1_substances_used_injecting_route"));

        String smokingRouteRule = getRuleBlock(rules, "step1_substances_used_smoking_route");
        Assert.assertTrue(smokingRouteRule.contains("step1_route_of_substance_use.contains('smoking')"));

        String drinkingRouteRule = getRuleBlock(rules, "step1_substances_used_drinking_route");
        Assert.assertTrue(drinkingRouteRule.contains("step1_route_of_substance_use.contains('drinking')"));

        String sniffingRouteRule = getRuleBlock(rules, "step1_substances_used_sniffing_route");
        Assert.assertTrue(sniffingRouteRule.contains("step1_route_of_substance_use.contains('sniffing')"));

        String nonInjectingCalculationRule = getRuleBlock(rules, "step1_substances_used_non_injecting");
        Assert.assertTrue(nonInjectingCalculationRule.contains("step1_substances_used_smoking_route"));
        Assert.assertTrue(nonInjectingCalculationRule.contains("step1_substances_used_drinking_route"));
        Assert.assertTrue(nonInjectingCalculationRule.contains("step1_substances_used_sniffing_route"));

        String injectingOtherRule = getRuleBlock(rules, "step1_substances_used_injecting_other_specify");
        Assert.assertTrue(injectingOtherRule.contains("step1_substances_used_injecting.contains('other')"));

        String nonInjectingOtherRule = getRuleBlock(rules, "step1_substances_used_non_injecting_other_specify");
        Assert.assertTrue(nonInjectingOtherRule.contains("step1_substances_used_non_injecting.contains('other')"));
    }

    private static Set<String> optionKeys(JSONObject field) throws Exception {
        Set<String> optionKeys = new LinkedHashSet<>();
        JSONArray options = field.getJSONArray("options");

        for (int i = 0; i < options.length(); i++) {
            optionKeys.add(options.getJSONObject(i).optString("key"));
        }

        return optionKeys;
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
        String marker = "name: " + ruleName + "\n";
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
