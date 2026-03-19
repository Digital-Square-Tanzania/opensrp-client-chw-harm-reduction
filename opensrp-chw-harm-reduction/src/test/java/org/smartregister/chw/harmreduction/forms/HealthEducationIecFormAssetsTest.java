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

public class HealthEducationIecFormAssetsTest {

    private static final List<String> IEC_FORM_PATHS = Arrays.asList(
            "src/main/assets/json.form/harm_reduction_health_education_iec.json",
            "src/main/assets/json.form-sw/harm_reduction_health_education_iec.json",
            "src/main/assets/json.form/harm_reduction_pre_mat_sessions_health_education.json",
            "src/main/assets/json.form-sw/harm_reduction_pre_mat_sessions_health_education.json"
    );

    @Test
    public void testIecFormsDefineQuantityFieldPerSelectableMaterial() throws Exception {
        for (String formPath : IEC_FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

            Map<String, String> expectedQtyFields = expectedQtyFields(fields);
            Set<String> actualQtyFields = actualQtyFields(fields);

            Assert.assertEquals("Expected one quantity field per selectable IEC material in " + formPath,
                    expectedQtyFields.keySet(), actualQtyFields);
            Assert.assertFalse("Legacy IEC count field should be removed from " + formPath,
                    hasField(fields, "iec_materials_count"));

            for (String qtyKey : expectedQtyFields.keySet()) {
                JSONObject qtyField = getField(fields, qtyKey);
                Assert.assertTrue(qtyField.getBoolean("read_only"));
                Assert.assertEquals("harm-reduction-health-education-iec-calculation-rules.yml",
                        qtyField.getJSONObject("calculation")
                                .getJSONObject("rules-engine")
                                .getJSONObject("ex-rules")
                                .getString("rules-file"));
                Assert.assertEquals("1", qtyField.getJSONObject("v_min").getString("value"));
                Assert.assertEquals("1", qtyField.getJSONObject("v_max").getString("value"));
                Assert.assertEquals("harm-reduction-health-education-iec-relevance-rules.yml",
                        qtyField.getJSONObject("relevance")
                                .getJSONObject("rules-engine")
                                .getJSONObject("ex-rules")
                                .getString("rules-file"));
            }
        }
    }

    @Test
    public void testIecQuantityRulesMatchFullHealthEducationOptions() throws Exception {
        JSONObject fullForm = readJson("src/main/assets/json.form/harm_reduction_health_education_iec.json");
        Map<String, String> expectedQtyFields = expectedQtyFields(fullForm.getJSONObject("step1").getJSONArray("fields"));
        String relevanceRules = readText("src/main/assets/rule/harm-reduction-health-education-iec-relevance-rules.yml");
        String calculationRules = readText("src/main/assets/rule/harm-reduction-health-education-iec-calculation-rules.yml");

        Assert.assertFalse(relevanceRules.contains("step1_iec_materials_count"));
        Assert.assertFalse(relevanceRules.contains("calculation ="));

        for (Map.Entry<String, String> entry : expectedQtyFields.entrySet()) {
            String qtyKey = entry.getKey();
            String optionKey = entry.getValue();

            String relevanceRuleBlock = getRuleBlock(relevanceRules, "step1_" + qtyKey);
            Assert.assertTrue("Missing relevance condition for " + qtyKey,
                    relevanceRuleBlock.contains("condition: \"step1_iec_materials_provided == 'yes' && step1_iec_materials_type != null && step1_iec_materials_type.contains('" + optionKey + "')\""));
            Assert.assertTrue("Missing relevance action for " + qtyKey,
                    relevanceRuleBlock.contains("  - \"isRelevant = true\""));

            String calculationRuleBlock = getRuleBlock(calculationRules, "step1_" + qtyKey);
            Assert.assertTrue("Missing calculation condition for " + qtyKey,
                    calculationRuleBlock.contains("condition: \"true\""));
            Assert.assertTrue("Missing calculation expression for " + qtyKey,
                    calculationRuleBlock.contains("  - \"calculation = (step1_iec_materials_provided == 'yes' && step1_iec_materials_type != null && step1_iec_materials_type.contains('" + optionKey + "')) ? '1' : '0'\""));
        }
    }

    private static Map<String, String> expectedQtyFields(JSONArray fields) throws Exception {
        JSONObject iecMaterialsType = getField(fields, "iec_materials_type");
        JSONArray options = iecMaterialsType.getJSONArray("options");
        Map<String, String> expected = new LinkedHashMap<>();

        for (int i = 0; i < options.length(); i++) {
            String optionKey = options.getJSONObject(i).getString("key");
            expected.put(optionKey.replaceFirst("^iec_", "iec_qty_"), optionKey);
        }

        return expected;
    }

    private static Set<String> actualQtyFields(JSONArray fields) throws Exception {
        Set<String> keys = new LinkedHashSet<>();

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String key = field.optString("key");
            if (key.startsWith("iec_qty_")) {
                keys.add(key);
            }
        }

        return keys;
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
