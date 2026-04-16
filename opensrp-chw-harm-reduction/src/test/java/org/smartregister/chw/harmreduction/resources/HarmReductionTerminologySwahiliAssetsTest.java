package org.smartregister.chw.harmreduction.resources;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class HarmReductionTerminologySwahiliAssetsTest {

    private static final List<String> AFFECTED_FILES = Arrays.asList(
            "src/main/assets/json.form-sw/harm_reduction_mark_the_client_has_started_mat.json",
            "src/main/assets/json.form-sw/harm_reduction_client_status_visit.json",
            "src/main/assets/json.form-sw/harm_reduction_sober_house_enrollment.json",
            "src/main/assets/json.form-sw/harm_reduction_mat_followup.json",
            "src/main/assets/json.form-sw/harm_reduction_risky_sexual_behaviors_condoms.json",
            "src/main/assets/json.form-sw/harm_reduction_risk_assessment.json",
            "src/main/assets/json.form-sw/harm_reduction_sober_house_linkage_to_other_services.json",
            "src/main/assets/json.form-sw/harm_reduction_register_existing_client.json",
            "src/main/assets/json.form-sw/harm_reduction_sober_house_detoxification.json",
            "src/main/assets/json.form-sw/harm_reduction_hiv_infection_status.json",
            "src/main/res/values-sw/strings.xml"
    );

    @Test
    public void shouldUseUpdatedClientTerminologyInAffectedSwahiliAssets() throws Exception {
        for (String relativePath : AFFECTED_FILES) {
            String content = readText(relativePath).toLowerCase();

            Assert.assertFalse("Found deprecated singular client term in " + relativePath, content.contains("mteja"));
            Assert.assertFalse("Found deprecated plural client term in " + relativePath, content.contains("wateja"));
            Assert.assertTrue(
                    "Missing updated client terminology in " + relativePath,
                    content.contains("mpokea huduma") || content.contains("wapokea huduma")
            );
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
