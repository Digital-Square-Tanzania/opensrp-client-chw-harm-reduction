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
import java.util.Set;

public class SafeInjectionQuestionLabelAssetsTest {

    @Test
    public void testSwahiliSafeInjectionQuestionUsesAinaYaVifaa() throws Exception {
        JSONObject form = new JSONObject(readText("src/main/assets/json.form-sw/harm_reduction_safe_injection_services.json"));
        JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
        JSONObject safeInjectionToolsField = fields.getJSONObject(0);
        String label = safeInjectionToolsField.getString("label");

        Assert.assertEquals("safe_injection_tools", safeInjectionToolsField.getString("key"));
        Assert.assertEquals("Huduma za sindano salama zinazotolewa - Aina ya vifaa", label);
        Assert.assertFalse("Question label should not use the old zana wording", label.contains("Aina ya zana"));
    }

    @Test
    public void testSafeInjectionToolsIncludeNotGivenOption() throws Exception {
        assertSafeInjectionToolOptions(
                "src/main/assets/json.form/harm_reduction_safe_injection_services.json",
                "Not given"
        );
        assertSafeInjectionToolOptions(
                "src/main/assets/json.form-sw/harm_reduction_safe_injection_services.json",
                "Hajapewa"
        );
    }

    private static void assertSafeInjectionToolOptions(String formPath, String expectedNotGivenText) throws Exception {
        JSONObject form = new JSONObject(readText(formPath));
        JSONArray options = form.getJSONObject("step1")
                .getJSONArray("fields")
                .getJSONObject(0)
                .getJSONArray("options");

        Assert.assertEquals(new LinkedHashSet<>(Arrays.asList(
                "syringes",
                "alcohol_swab",
                "dry_cotton",
                "plaster",
                "sterile_water",
                "needles",
                "not_given"
        )), optionKeys(options));
        Assert.assertEquals(expectedNotGivenText, optionText(options, "not_given"));
    }

    private static Set<String> optionKeys(JSONArray options) throws Exception {
        Set<String> keys = new LinkedHashSet<>();
        for (int i = 0; i < options.length(); i++) {
            keys.add(options.getJSONObject(i).getString("key"));
        }
        return keys;
    }

    private static String optionText(JSONArray options, String key) throws Exception {
        for (int i = 0; i < options.length(); i++) {
            JSONObject option = options.getJSONObject(i);
            if (key.equals(option.getString("key"))) {
                return option.getString("text");
            }
        }

        throw new AssertionError("Missing option: " + key);
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
