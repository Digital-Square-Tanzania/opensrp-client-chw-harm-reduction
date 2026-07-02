package org.smartregister.chw.harmreduction.forms;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RecoveryCapitalAssessmentAftercareFormAssetsTest {

    private static final String EXPECTED_SWAHILI_LABEL = "Tathmini ya rasilimali ya upataji nafuu";
    private static final String LEGACY_SWAHILI_LABEL = "Tathmini ya rasilimali za urekebishaji";

    @Test
    public void shouldUseApprovedSwahiliRecoveryCapitalLabelInFormAndStrings() throws Exception {
        JSONObject form = readJson("src/main/assets/json.form-sw/harm_reduction_sober_house_recovery_capital_assessment_aftercare.json");

        Assert.assertEquals(EXPECTED_SWAHILI_LABEL, form.getJSONObject("step1").getString("title"));

        String strings = readText("src/main/res/values-sw/strings.xml");
        Assert.assertTrue(strings.contains(
                "<string name=\"harm_reduction_sober_house_recovery_capital_assessment_aftercare\">" +
                        EXPECTED_SWAHILI_LABEL +
                        "</string>"
        ));
        Assert.assertFalse(strings.contains(LEGACY_SWAHILI_LABEL));
    }

    private static JSONObject readJson(String relativePath) throws Exception {
        return new JSONObject(readText(relativePath));
    }

    private static String readText(String relativePath) throws Exception {
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
