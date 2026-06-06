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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinkageToOtherServicesFormAssetsTest {

    @Test
    public void testSwahiliLinkageFormUsesExpectedLabels() throws Exception {
        JSONObject form = new JSONObject(readText("src/main/assets/json.form-sw/harm_reduction_linkage_to_other_services.json"));
        JSONObject step1 = form.getJSONObject("step1");
        JSONArray fields = step1.getJSONArray("fields");

        Assert.assertEquals("Huduma za Muunganiko", step1.getString("title"));
        Assert.assertEquals("linkage_to_other_services_provided", fields.getJSONObject(0).getString("key"));
        Assert.assertEquals("Je, mpokea huduma ameunganishwa katika huduma za muunganiko?", fields.getJSONObject(0).getString("label"));
        Assert.assertEquals("Huduma alizounganishwa", fields.getJSONObject(1).getString("label"));
        Assert.assertEquals("harm-reduction-linkage-to-other-services-rules.yml",
                fields.getJSONObject(1)
                        .getJSONObject("relevance")
                        .getJSONObject("rules-engine")
                        .getJSONObject("ex-rules")
                        .getString("rules-file"));
    }

    @Test
    public void testLinkageFormContainsMovedOptions() throws Exception {
        JSONObject form = new JSONObject(readText("src/main/assets/json.form/harm_reduction_linkage_to_other_services.json"));
        JSONArray options = form.getJSONObject("step1")
                .getJSONArray("fields")
                .getJSONObject(1)
                .getJSONArray("options");

        Assert.assertEquals(Arrays.asList(
                "mental_health",
                "substance_abuse",
                "nutrition",
                "gbv_vac",
                "psychological_assistance",
                "rights_vulnerable",
                "health_services_advocacy",
                "stis_stds",
                "overdose_management",
                "legal_issues",
                "safe_injection",
                "methadone_services",
                "sober_house",
                "income_generating",
                "other",
                "none"
        ), getOptionKeys(options));
    }

    private static List<String> getOptionKeys(JSONArray options) throws Exception {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < options.length(); i++) {
            keys.add(options.getJSONObject(i).getString("key"));
        }
        return keys;
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
