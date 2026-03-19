package org.smartregister.chw.harmreduction.assets;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class SwahiliFormTitleLocalizationTest {

    @Test
    public void swahiliIssue658FormTitlesRemainLocalized() throws Exception {
        Path assetsDir = resolveSwahiliAssetsDir();
        Map<String, String> expectedTitles = new LinkedHashMap<>();
        expectedTitles.put("harm_reduction_client_status_visit.json", "Hali ya mteja");
        expectedTitles.put("harm_reduction_health_education_iec.json", "Elimu ya Afya");
        expectedTitles.put("harm_reduction_safe_injection_services.json", "Huduma ya vifaa vya kujidunga salama");
        expectedTitles.put("harm_reduction_risky_sexual_behaviors_condoms.json", "Tabia hatarishi za ngono");
        expectedTitles.put("harm_reduction_hiv_infection_status.json", "Hali ya maambukizi ya VVU");
        expectedTitles.put("harm_reduction_other_diseases_screening.json", "Uchunguzi wa magonjwa mengine");
        expectedTitles.put("harm_reduction_referrals_provided.json", "Rufaa zilizotolewa");
        expectedTitles.put("roc_consent_joining_mat_services.json", "Ridhaa ya kujiunga na huduma za MAT");

        for (Map.Entry<String, String> entry : expectedTitles.entrySet()) {
            JSONObject form = new JSONObject(new String(Files.readAllBytes(assetsDir.resolve(entry.getKey())), StandardCharsets.UTF_8));
            String actualTitle = form.getJSONObject("step1").getString("title");
            Assert.assertEquals("Unexpected title in " + entry.getKey(), entry.getValue(), actualTitle);
        }
    }

    private Path resolveSwahiliAssetsDir() {
        Path moduleRelative = Paths.get("src", "main", "assets", "json.form-sw");
        if (Files.isDirectory(moduleRelative)) {
            return moduleRelative;
        }

        Path repoRelative = Paths.get("opensrp-chw-harm-reduction", "src", "main", "assets", "json.form-sw");
        if (Files.isDirectory(repoRelative)) {
            return repoRelative;
        }

        throw new IllegalStateException("Could not locate src/main/assets/json.form-sw");
    }
}
