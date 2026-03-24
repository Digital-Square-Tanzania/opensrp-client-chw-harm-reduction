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

public class MatFollowUpFormAssetsTest {

    private static final String[][] ENGLISH_EXPECTED_OPTIONS = {
            {"drug_education", "Drug education"},
            {"drug_use_related_diseases", "Education on diseases associated with drug use"},
            {"epidemic_diseases", "Education on epidemic diseases"},
            {"behavior_change", "Behavior change education"},
            {"treatment_adherence", "Good treatment adherence education"},
            {"legal_support_mat_procedures", "Education on legal support and MAT procedures"},
            {"overdose", "Education on drug overdose"},
            {"drug_use_and_women", "Education on drug use and women"},
            {"reproductive_health_services", "Education on reproductive health services"},
            {"correct_condom_use", "Education on correct condom use"},
            {"opioid_addiction_treatment_use", "Education on correct use of opioid addiction treatment (Methadone, Buprenorphine, Suboxone)"},
            {"life_and_job_skills", "Life and job skills education"},
            {"mental_health", "Mental health education"},
            {"injection_related_diseases", "Education on injection-related diseases"}
    };

    private static final String[][] SWAHILI_EXPECTED_OPTIONS = {
            {"drug_education", "Elimu ya Dawa za Kulevya"},
            {"drug_use_related_diseases", "Elimu ya Magonjwa Yanayoambatana na Matumizi ya Dawa za Kulevya"},
            {"epidemic_diseases", "Elimu ya Magonjwa ya Mlipuko"},
            {"behavior_change", "Elimu ya Mabadiliko ya Tabia"},
            {"treatment_adherence", "Elimu ya Ufuasi Mzuri wa Tiba"},
            {"legal_support_mat_procedures", "Elimu ya Msaada wa Kisheria na Taratibu za MAT"},
            {"overdose", "Elimu ya Uzidishaji wa Kiwango cha Dawa za Kulevya (Overdose)"},
            {"drug_use_and_women", "Elimu ya Matumizi ya Dawa za Kulevya na Wanawake"},
            {"reproductive_health_services", "Elimu ya Huduma za Afya ya Uzazi"},
            {"correct_condom_use", "Elimu ya Matumizi Sahihi ya Kondomu"},
            {"opioid_addiction_treatment_use", "Elimu ya Matumizi Sahihi ya Matibabu ya Uraibu Afyuni (Methadone, Buprenorphine, Suboxone)"},
            {"life_and_job_skills", "Elimu ya Stadi za Maisha na Stadi za Kazi"},
            {"mental_health", "Elimu ya Afya ya Akili"},
            {"injection_related_diseases", "Elimu ya Magonjwa Yatokanayo na Ujidunga"}
    };

    @Test
    public void testEnglishMatFollowUpOptionsMatchApprovedList() throws Exception {
        assertOptions("src/main/assets/json.form/harm_reduction_mat_followup.json", ENGLISH_EXPECTED_OPTIONS);
    }

    @Test
    public void testSwahiliMatFollowUpOptionsMatchApprovedList() throws Exception {
        assertOptions("src/main/assets/json.form-sw/harm_reduction_mat_followup.json", SWAHILI_EXPECTED_OPTIONS);
    }

    private static void assertOptions(String relativePath, String[][] expectedOptions) throws Exception {
        JSONObject form = new JSONObject(readText(relativePath));
        JSONArray options = form.getJSONObject("step1")
                .getJSONArray("fields")
                .getJSONObject(0)
                .getJSONArray("options");

        Assert.assertEquals("Unexpected option count in " + relativePath, expectedOptions.length, options.length());

        for (int i = 0; i < expectedOptions.length; i++) {
            JSONObject option = options.getJSONObject(i);
            Assert.assertEquals("Unexpected key at index " + i + " in " + relativePath,
                    expectedOptions[i][0], option.getString("key"));
            Assert.assertEquals("Unexpected text at index " + i + " in " + relativePath,
                    expectedOptions[i][1], option.getString("text"));
            Assert.assertEquals("Expected openmrs_entity_id to match key at index " + i + " in " + relativePath,
                    expectedOptions[i][0], option.getString("openmrs_entity_id"));
        }
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
