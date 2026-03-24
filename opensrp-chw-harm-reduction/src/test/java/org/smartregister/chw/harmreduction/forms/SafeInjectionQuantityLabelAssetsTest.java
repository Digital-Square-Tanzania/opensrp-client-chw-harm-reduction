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

public class SafeInjectionQuantityLabelAssetsTest {

    @Test
    public void testSwahiliSafeInjectionQuantityLabelsUseItemSpecificWording() throws Exception {
        JSONObject form = new JSONObject(readText("src/main/assets/json.form-sw/harm_reduction_safe_injection_services.json"));
        JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

        JSONObject qtySyringes = getField(fields, "qty_syringes");
        Assert.assertEquals("Idadi ya Bomba", qtySyringes.getString("hint"));
        Assert.assertEquals("Tafadhali weka idadi ya Bomba", qtySyringes.getJSONObject("v_required").getString("err"));

        JSONObject qtyAlcoholSwab = getField(fields, "qty_alcohol_swab");
        Assert.assertEquals("Idadi ya Pamba yenye kileo(swabu)", qtyAlcoholSwab.getString("hint"));
        Assert.assertEquals("Tafadhali weka idadi ya Pamba yenye kileo(swabu)",
                qtyAlcoholSwab.getJSONObject("v_required").getString("err"));

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String key = field.optString("key");
            if (!key.startsWith("qty_")) {
                continue;
            }

            String hint = field.optString("hint").toLowerCase();
            String requiredError = field.getJSONObject("v_required").getString("err").toLowerCase();
            Assert.assertFalse("Quantity hint should not use the old zana wording for " + key, hint.contains("zana"));
            Assert.assertFalse("Quantity error should not use the old zana wording for " + key, requiredError.contains("zana"));
        }
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
