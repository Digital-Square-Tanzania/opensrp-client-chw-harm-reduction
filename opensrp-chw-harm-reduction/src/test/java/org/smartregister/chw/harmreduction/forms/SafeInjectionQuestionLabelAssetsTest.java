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
