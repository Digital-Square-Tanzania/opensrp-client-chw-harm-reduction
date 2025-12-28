package org.smartregister.chw.harmreduction.model;

import org.json.JSONObject;
import org.smartregister.chw.harmreduction.contract.HarmReductionRegisterContract;
import org.smartregister.chw.harmreduction.util.HarmReductionJsonFormUtils;

public class BaseHarmReductionRegisterModel implements HarmReductionRegisterContract.Model {

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject jsonObject = HarmReductionJsonFormUtils.getFormAsJson(formName);
        HarmReductionJsonFormUtils.getRegistrationForm(jsonObject, entityId, currentLocationId);

        return jsonObject;
    }

}
