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

public class SafetyBoxCollectionFormAssetsTest {

    private static final List<String> FORM_PATHS = Arrays.asList(
            "src/main/assets/json.form/harm_reduction_safety_box_collection.json",
            "src/main/assets/json.form-sw/harm_reduction_safety_box_collection.json"
    );

    @Test
    public void safetyBoxCollectionFormsUseFreeTextForCollectionChallenges() throws Exception {
        for (String formPath : FORM_PATHS) {
            JSONObject form = readJson(formPath);
            JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
            JSONObject challengesField = getField(fields, "issues_challenges_related_to_collection_of_used_needles_and_syringes");

            Assert.assertEquals("edit_text", challengesField.getString("type"));
            Assert.assertFalse(challengesField.has("edit_type"));
            Assert.assertFalse(hasField(fields, "number_of_safety_boxes_collected_used_syringes"));
            Assert.assertFalse(hasField(fields, "total_safety_boxes_collected"));
            Assert.assertFalse(hasField(fields, "name_of_ow"));
        }
    }

    private static JSONObject readJson(String relativePath) throws Exception {
        return new JSONObject(readText(relativePath));
    }

    private static String readText(String relativePath) throws IOException {
        return new String(Files.readAllBytes(resolvePath(relativePath)), StandardCharsets.UTF_8);
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
            JSONObject field = fields.getJSONObject(i);
            if (key.equals(field.optString("key"))) {
                return true;
            }
        }
        return false;
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
