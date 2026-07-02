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
        Assert.assertEquals("Idadi ya Bomba na sindano", qtySyringes.getString("hint"));
        Assert.assertEquals("Tafadhali weka idadi ya Bomba na sindano", qtySyringes.getJSONObject("v_required").getString("err"));

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

    @Test
    public void testCombinedSyringesQuantityKeepsNeedleConstraints() throws Exception {
        assertQtySyringesConstraints(
                "src/main/assets/json.form-sw/harm_reduction_safe_injection_services.json",
                "Tafadhali ingiza ingizo halali",
                "Lazima iwe 1 au zaidi",
                "Lazima iwe 21 au chini",
                "Tafadhali ingiza ingizo halali"
        );
        assertQtySyringesConstraints(
                "src/main/assets/json.form/harm_reduction_safe_injection_services.json",
                "Please enter a valid number",
                "Must be 1 or more",
                "Must be 21 or less",
                "Please enter a valid input"
        );
    }

    private static void assertQtySyringesConstraints(String formPath,
                                                     String expectedNumericError,
                                                     String expectedMinError,
                                                     String expectedMaxError,
                                                     String expectedMaxLengthError) throws Exception {
        JSONObject form = new JSONObject(readText(formPath));
        JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
        JSONObject qtySyringes = getField(fields, "qty_syringes");

        Assert.assertEquals("true", qtySyringes.getJSONObject("v_numeric").getString("value"));
        Assert.assertEquals(expectedNumericError, qtySyringes.getJSONObject("v_numeric").getString("err"));
        Assert.assertEquals("1", qtySyringes.getJSONObject("v_min").getString("value"));
        Assert.assertEquals(expectedMinError, qtySyringes.getJSONObject("v_min").getString("err"));
        Assert.assertEquals("21", qtySyringes.getJSONObject("v_max").getString("value"));
        Assert.assertEquals(expectedMaxError, qtySyringes.getJSONObject("v_max").getString("err"));
        Assert.assertEquals("2", qtySyringes.getJSONObject("v_max_length").getString("value"));
        Assert.assertEquals("true", qtySyringes.getJSONObject("v_max_length").getString("is_fixed_size"));
        Assert.assertEquals(expectedMaxLengthError, qtySyringes.getJSONObject("v_max_length").getString("err"));
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
