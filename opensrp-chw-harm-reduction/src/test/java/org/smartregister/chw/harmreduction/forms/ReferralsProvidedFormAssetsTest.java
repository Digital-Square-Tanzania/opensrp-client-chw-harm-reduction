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

public class ReferralsProvidedFormAssetsTest {

    @Test
    public void testSwahiliReferralsFormUsesReferralsLabel() throws Exception {
        JSONObject form = new JSONObject(readText("src/main/assets/json.form-sw/harm_reduction_referrals_provided.json"));
        JSONObject step1 = form.getJSONObject("step1");
        JSONArray fields = step1.getJSONArray("fields");
        JSONObject referralsField = fields.getJSONObject(0);
        String swStrings = readText("src/main/res/values-sw/strings.xml");

        Assert.assertEquals("Rufaa zilizotolewa", step1.getString("title"));
        Assert.assertEquals("referrals_provided", referralsField.getString("key"));
        Assert.assertEquals("Rufaa zilizotolewa", referralsField.getString("label"));
        Assert.assertTrue(swStrings.contains("<string name=\"harm_reduction_referrals_provided\">Rufaa zilizotolewa</string>"));

        Assert.assertFalse("The Swahili referrals assets should not use the old Methadone title",
                readText("src/main/assets/json.form-sw/harm_reduction_referrals_provided.json").contains("Tiba ya Methadone"));
    }

    @Test
    public void testReferralsFormOnlyContainsReferralOptions() throws Exception {
        JSONObject form = new JSONObject(readText("src/main/assets/json.form-sw/harm_reduction_referrals_provided.json"));
        JSONArray options = form.getJSONObject("step1")
                .getJSONArray("fields")
                .getJSONObject(0)
                .getJSONArray("options");

        Assert.assertEquals(Arrays.asList(
                "hiv_aids",
                "epidemic_diseases",
                "communicable_diseases",
                "tb_leprosy",
                "ntds",
                "family_planning",
                "reproductive_health",
                "prep",
                "hepatitis_bc",
                "hiv_testing",
                "wounds_abscess_treatment",
                "other",
                "none"
        ), getOptionKeys(options));
    }

    @Test
    public void testReferralsFormDoesNotContainMovedLinkageOptions() throws Exception {
        JSONObject form = new JSONObject(readText("src/main/assets/json.form/harm_reduction_referrals_provided.json"));
        List<String> optionKeys = getOptionKeys(form.getJSONObject("step1")
                .getJSONArray("fields")
                .getJSONObject(0)
                .getJSONArray("options"));

        for (String movedOption : Arrays.asList(
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
                "income_generating"
        )) {
            Assert.assertFalse(optionKeys.contains(movedOption));
        }
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
