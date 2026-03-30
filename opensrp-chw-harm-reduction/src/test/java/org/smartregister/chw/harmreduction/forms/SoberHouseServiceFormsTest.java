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

public class SoberHouseServiceFormsTest {

    @Test
    public void testSwahiliClientTypeFormUsesMpokeaHudumaLabel() throws Exception {
        JSONObject form = readJson("src/main/assets/json.form-sw/harm_reduction_sober_house_client_type_followup_status.json");
        JSONObject clientTypeField = getField(form.getJSONObject("step1").getJSONArray("fields"), "client_type");

        Assert.assertEquals("Aina ya mpokea huduma", clientTypeField.getString("label"));
    }

    @Test
    public void testSoberHouseFollowUpFormRequiresContinuationStatusAndDiscontinuedReason() throws Exception {
        JSONObject englishForm = readJson("src/main/assets/json.form/harm_reduction_sober_house_client_type_followup_status.json");
        JSONObject swahiliForm = readJson("src/main/assets/json.form-sw/harm_reduction_sober_house_client_type_followup_status.json");

        Assert.assertEquals("Harm Reduction Sober House Follow-up Status", englishForm.getJSONObject("step1").getString("title"));
        Assert.assertEquals("Hali ya Ufuatiliaji", swahiliForm.getJSONObject("step1").getString("title"));

        JSONObject englishContinuationField = getField(englishForm.getJSONObject("step1").getJSONArray("fields"), "service_continuation_status");
        JSONObject swahiliContinuationField = getField(swahiliForm.getJSONObject("step1").getJSONArray("fields"), "service_continuation_status");
        JSONObject englishReasonField = getField(englishForm.getJSONObject("step1").getJSONArray("fields"), "discontinued_reason");
        JSONObject swahiliReasonField = getField(swahiliForm.getJSONObject("step1").getJSONArray("fields"), "discontinued_reason");
        JSONObject englishFollowUpStatus = getField(englishForm.getJSONObject("step1").getJSONArray("fields"), "follow_up_status");
        JSONObject swahiliFollowUpStatus = getField(swahiliForm.getJSONObject("step1").getJSONArray("fields"), "follow_up_status");

        Assert.assertEquals("Follow-up status", englishContinuationField.getString("label"));
        Assert.assertEquals("Hali ya Ufuatiliaji", swahiliContinuationField.getString("label"));
        Assert.assertEquals(2, englishContinuationField.getJSONArray("options").length());
        Assert.assertEquals(2, swahiliContinuationField.getJSONArray("options").length());
        Assert.assertEquals(4, englishReasonField.getJSONArray("options").length());
        Assert.assertEquals(4, swahiliReasonField.getJSONArray("options").length());
        Assert.assertEquals("hidden", englishFollowUpStatus.getString("type"));
        Assert.assertEquals("hidden", swahiliFollowUpStatus.getString("type"));
    }

    @Test
    public void testSoberHouseFollowUpRulesDriveReasonVisibilityAndStoredStatus() throws Exception {
        String rules = readText("src/main/assets/rule/harm-reduction-sober-house-client-type-followup-status-rules.yml");

        String continuationRule = getRuleBlock(rules, "step1_service_continuation_status");
        Assert.assertTrue(continuationRule.contains("step1_client_type == 'returning_client'"));

        String reasonRule = getRuleBlock(rules, "step1_discontinued_reason");
        Assert.assertTrue(reasonRule.contains("step1_client_type == 'returning_client' && step1_service_continuation_status == 'discontinued_service'"));

        String storedStatusRule = getRuleBlock(rules, "step1_follow_up_status");
        Assert.assertTrue(storedStatusRule.contains("step1_service_continuation_status == 'continuing_service' ? 'continuing_service' : step1_discontinued_reason"));
    }

    @Test
    public void testVitalServiceFormsUseUpdatedTitles() throws Exception {
        JSONObject englishForm = readJson("src/main/assets/json.form/harm_reduction_sober_house_vitals.json");
        JSONObject swahiliForm = readJson("src/main/assets/json.form-sw/harm_reduction_sober_house_vitals.json");

        Assert.assertEquals("Testing Services", englishForm.getJSONObject("step1").getString("title"));
        Assert.assertEquals("Huduma za Vipimo", swahiliForm.getJSONObject("step1").getString("title"));
    }

    @Test
    public void testLifeSkillsParticipationMovedToSeparateForms() throws Exception {
        JSONObject englishRoutineForm = readJson("src/main/assets/json.form/harm_reduction_sober_house_routine_services.json");
        JSONObject swahiliRoutineForm = readJson("src/main/assets/json.form-sw/harm_reduction_sober_house_routine_services.json");
        JSONObject englishLifeSkillsForm = readJson("src/main/assets/json.form/harm_reduction_sober_house_life_skills_participation.json");
        JSONObject swahiliLifeSkillsForm = readJson("src/main/assets/json.form-sw/harm_reduction_sober_house_life_skills_participation.json");

        Assert.assertFalse(hasField(englishRoutineForm.getJSONObject("step1").getJSONArray("fields"), "life_skills_participation"));
        Assert.assertFalse(hasField(swahiliRoutineForm.getJSONObject("step1").getJSONArray("fields"), "life_skills_participation"));

        JSONObject englishLifeSkillsField = getField(englishLifeSkillsForm.getJSONObject("step1").getJSONArray("fields"), "life_skills_participation");
        JSONObject swahiliLifeSkillsField = getField(swahiliLifeSkillsForm.getJSONObject("step1").getJSONArray("fields"), "life_skills_participation");

        Assert.assertEquals("Life Skills Participation", englishLifeSkillsForm.getJSONObject("step1").getString("title"));
        Assert.assertEquals("Ushirika katika stadi za maisha", swahiliLifeSkillsForm.getJSONObject("step1").getString("title"));
        Assert.assertEquals("Life Skills Participation", englishLifeSkillsField.getString("label"));
        Assert.assertEquals("Ushirika katika stadi za maisha", swahiliLifeSkillsField.getString("label"));
        Assert.assertEquals(12, englishLifeSkillsField.getJSONArray("options").length());
        Assert.assertEquals(12, swahiliLifeSkillsField.getJSONArray("options").length());
    }

    private static boolean hasField(JSONArray fields, String key) throws Exception {
        for (int i = 0; i < fields.length(); i++) {
            if (key.equals(fields.getJSONObject(i).optString("key"))) {
                return true;
            }
        }

        return false;
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

    private static JSONObject readJson(String relativePath) throws Exception {
        return new JSONObject(readText(relativePath));
    }

    private static String getRuleBlock(String rules, String ruleName) {
        String marker = "name: " + ruleName;
        int start = rules.indexOf(marker);
        Assert.assertTrue("Missing rule: " + ruleName, start >= 0);

        int next = rules.indexOf("\n---", start);
        return next >= 0 ? rules.substring(start, next) : rules.substring(start);
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
