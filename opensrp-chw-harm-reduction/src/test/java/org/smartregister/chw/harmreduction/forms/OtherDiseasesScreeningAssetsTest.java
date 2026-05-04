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

public class OtherDiseasesScreeningAssetsTest {

    @Test
    public void testOtherDiseasesScreeningUsesSeparateHepatitisFields() throws Exception {
        assertHepatitisFields(
                "src/main/assets/json.form/harm_reduction_other_diseases_screening.json",
                "Hepatitis B Screening",
                "Hepatitis C Screening"
        );
        assertHepatitisFields(
                "src/main/assets/json.form-sw/harm_reduction_other_diseases_screening.json",
                "Uchunguzi wa Homa ya Ini B",
                "Uchunguzi wa Homa ya Ini C"
        );
    }

    @Test
    public void testEcClientFieldsMapSeparateHepatitisFields() throws Exception {
        JSONObject file = new JSONObject(readText("src/main/assets/ec_client_fields.json"));
        JSONObject bindObject = findBindObject(file.getJSONArray("bindobjects"), "ec_harm_reduction_followup_visit");
        JSONArray columns = bindObject.getJSONArray("columns");

        Assert.assertTrue(hasColumn(columns, "hepatitis_bc_screening"));
        Assert.assertTrue(hasColumn(columns, "hepatitis_b_screening"));
        Assert.assertTrue(hasColumn(columns, "hepatitis_c_screening"));
    }

    private static void assertHepatitisFields(String formPath, String expectedHepatitisBLabel, String expectedHepatitisCLabel) throws Exception {
        JSONObject form = new JSONObject(readText(formPath));
        JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

        Assert.assertFalse(hasField(fields, "hepatitis_bc_screening"));
        assertHepatitisField(getField(fields, "hepatitis_b_screening"), expectedHepatitisBLabel);
        assertHepatitisField(getField(fields, "hepatitis_c_screening"), expectedHepatitisCLabel);
    }

    private static void assertHepatitisField(JSONObject field, String expectedLabel) throws Exception {
        Assert.assertEquals("native_radio", field.getString("type"));
        Assert.assertEquals(expectedLabel, field.getString("label"));
        Assert.assertEquals("true", field.getJSONObject("v_required").getString("value"));
        Assert.assertEquals(new LinkedHashSet<>(Arrays.asList(
                "has_symptoms",
                "no_symptoms",
                "not_screened",
                "undergoing_treatment"
        )), optionKeys(field.getJSONArray("options")));
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

    private static boolean hasField(JSONArray fields, String key) throws Exception {
        for (int i = 0; i < fields.length(); i++) {
            if (key.equals(fields.getJSONObject(i).optString("key"))) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> optionKeys(JSONArray options) throws Exception {
        Set<String> keys = new LinkedHashSet<>();
        for (int i = 0; i < options.length(); i++) {
            keys.add(options.getJSONObject(i).getString("key"));
        }
        return keys;
    }

    private static JSONObject findBindObject(JSONArray bindObjects, String name) throws Exception {
        for (int i = 0; i < bindObjects.length(); i++) {
            JSONObject bindObject = bindObjects.getJSONObject(i);
            if (name.equals(bindObject.optString("name"))) {
                return bindObject;
            }
        }

        throw new AssertionError("Missing bind object: " + name);
    }

    private static boolean hasColumn(JSONArray columns, String columnName) throws Exception {
        for (int i = 0; i < columns.length(); i++) {
            if (columnName.equals(columns.getJSONObject(i).optString("column_name"))) {
                return true;
            }
        }
        return false;
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
