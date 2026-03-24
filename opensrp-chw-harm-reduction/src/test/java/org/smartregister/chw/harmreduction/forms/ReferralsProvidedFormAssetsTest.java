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
