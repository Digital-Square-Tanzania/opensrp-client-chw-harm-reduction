package org.smartregister.chw.harmreduction.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.harmreduction.domain.VisitDetail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HarmReductionJsonFormUtilsTest {

    @Test
    public void testPopulateFormRestoresMultiSelectListFromStoredKeys() throws Exception {
        JSONObject form = new JSONObject()
                .put("count", "1")
                .put("step1", new JSONObject().put("fields", new JSONArray().put(
                        new JSONObject()
                                .put("key", "health_education_provided")
                                .put("type", "multi_select_list")
                                .put("options", new JSONArray()
                                        .put(option("hiv_aids", "HIV and AIDS"))
                                        .put(option("tb_leprosy", "TB and leprosy"))
                                        .put(option("nutrition", "Nutrition")))
                )));

        VisitDetail detail = new VisitDetail();
        detail.setDetails("hiv_aids, tb_leprosy");
        detail.setHumanReadable("HIV and AIDS, TB and leprosy");
        Map<String, List<VisitDetail>> details = new HashMap<>();
        details.put("health_education_provided", Collections.singletonList(detail));

        HarmReductionJsonFormUtils.populateForm(form, details);

        JSONObject field = form.getJSONObject("step1").getJSONArray("fields").getJSONObject(0);
        JSONArray selectedOptions = new JSONArray(field.getString("value"));
        Assert.assertEquals(2, selectedOptions.length());
        Assert.assertEquals("hiv_aids", selectedOptions.getJSONObject(0).getString("key"));
        Assert.assertEquals("tb_leprosy", selectedOptions.getJSONObject(1).getString("key"));
    }

    private static JSONObject option(String key, String text) throws Exception {
        return new JSONObject().put("key", key).put("text", text);
    }
}
