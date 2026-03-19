package org.smartregister.chw.harmreduction.util;

import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class HarmReductionJsonFormUtilsTest {

    @Test
    public void addNumberingToTargetMultiSelectListsNumbersOnlyTargetedLists() throws Exception {
        JSONObject jsonForm = new JSONObject()
                .put("count", "1")
                .put("step1", new JSONObject()
                        .put("fields", new JSONArray()
                                .put(new JSONObject()
                                        .put("key", "health_education_provided")
                                        .put("type", "multi_select_list")
                                        .put("options", new JSONArray()
                                                .put(new JSONObject().put("text", "HIV and AIDS"))
                                                .put(new JSONObject().put("text", "Mental Health"))))
                                .put(new JSONObject()
                                        .put("key", "health_education_provided")
                                        .put("type", "check_box")
                                        .put("options", new JSONArray()
                                                .put(new JSONObject().put("text", "Should stay the same"))))
                                .put(new JSONObject()
                                        .put("key", "education_delivery_method")
                                        .put("type", "multi_select_list")
                                        .put("options", new JSONArray()
                                                .put(new JSONObject().put("text", "Group session"))))));

        HarmReductionJsonFormUtils.addNumberingToTargetMultiSelectLists(jsonForm);
        HarmReductionJsonFormUtils.addNumberingToTargetMultiSelectLists(jsonForm);

        JSONArray fields = jsonForm.getJSONObject("step1").getJSONArray("fields");
        JSONArray numberedOptions = fields.getJSONObject(0).getJSONArray("options");
        JSONArray checkboxOptions = fields.getJSONObject(1).getJSONArray("options");
        JSONArray untouchedOptions = fields.getJSONObject(2).getJSONArray("options");

        assertEquals("1. HIV and AIDS", numberedOptions.getJSONObject(0).getString("text"));
        assertEquals("2. Mental Health", numberedOptions.getJSONObject(1).getString("text"));
        assertEquals("Should stay the same", checkboxOptions.getJSONObject(0).getString("text"));
        assertEquals("Group session", untouchedOptions.getJSONObject(0).getString("text"));
    }
}
