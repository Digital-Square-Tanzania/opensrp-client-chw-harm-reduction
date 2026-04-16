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

public class SafetyBoxCollectionFormAssetsTest {

    @Test
    public void testEnglishNeedleAndSyringeCollectionFormUsesRevisedFields() throws Exception {
        JSONObject form = readJson("src/main/assets/json.form/harm_reduction_safety_box_collection.json");
        JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

        Assert.assertEquals("Used Needle and Syringes Collection", form.getJSONObject("step1").getString("title"));
        Assert.assertEquals(
                "Number of used Needles and Syringes collected",
                getField(fields, "number_of_used_needles_and_syringes_collected").getString("hint")
        );
        Assert.assertEquals(
                "Issues/Challenges related to collection of used syringes",
                getField(fields, "issues_challenges_related_to_collection_of_used_needles_and_syringes").getString("hint")
        );

        assertMissingField(fields, "collection_site_gps");
        assertMissingField(fields, "other_collection");
        assertMissingField(fields, "fixed_bins");
        assertMissingField(fields, "total_safety_boxes_collected");
        assertMissingField(fields, "name_of_ow");
    }

    @Test
    public void testSwahiliNeedleAndSyringeCollectionFormUsesRevisedFields() throws Exception {
        JSONObject form = readJson("src/main/assets/json.form-sw/harm_reduction_safety_box_collection.json");
        JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

        Assert.assertEquals("Ukusanyaji wa sindano na mabomba yaliyotumika", form.getJSONObject("step1").getString("title"));
        Assert.assertEquals(
                "Idadi ya sindano na mabomba yaliyotumika yaliyokusanywa",
                getField(fields, "number_of_used_needles_and_syringes_collected").getString("hint")
        );
        Assert.assertEquals(
                "Changamoto zinazohusiana na ukusanyaji wa sindano na mabomba yaliyotumika",
                getField(fields, "issues_challenges_related_to_collection_of_used_needles_and_syringes").getString("hint")
        );

        assertMissingField(fields, "collection_site_gps");
        assertMissingField(fields, "other_collection");
        assertMissingField(fields, "fixed_bins");
        assertMissingField(fields, "total_safety_boxes_collected");
        assertMissingField(fields, "name_of_ow");
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

    private static void assertMissingField(JSONArray fields, String key) throws Exception {
        for (int i = 0; i < fields.length(); i++) {
            if (key.equals(fields.getJSONObject(i).optString("key"))) {
                throw new AssertionError("Did not expect field: " + key);
            }
        }
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
