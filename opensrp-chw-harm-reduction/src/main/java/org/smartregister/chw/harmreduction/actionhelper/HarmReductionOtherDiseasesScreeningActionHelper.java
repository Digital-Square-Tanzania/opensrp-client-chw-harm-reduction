package org.smartregister.chw.harmreduction.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.harmreduction.domain.VisitDetail;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HarmReductionOtherDiseasesScreeningActionHelper implements BaseHarmReductionVisitAction.HarmReductionVisitActionHelper {
    private static final String HEPATITIS_BC_SCREENING_FIELD_KEY = "hepatitis_bc_screening";
    private static final String HEPATITIS_B_SCREENING_FIELD_KEY = "hepatitis_b_screening";
    private static final String HEPATITIS_C_SCREENING_FIELD_KEY = "hepatitis_c_screening";

    private String hepatitisBcScreening;
    private String hepatitisBScreening;
    private String hepatitisCScreening;

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> details) {
        // no-op
    }

    @Override
    public String getPreProcessed() {
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            hepatitisBcScreening = getFieldValue(jsonObject, HEPATITIS_BC_SCREENING_FIELD_KEY);
            hepatitisBScreening = getFieldValue(jsonObject, HEPATITIS_B_SCREENING_FIELD_KEY);
            hepatitisCScreening = getFieldValue(jsonObject, HEPATITIS_C_SCREENING_FIELD_KEY);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public BaseHarmReductionVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return null;
    }

    @Override
    public String postProcess(String jsonPayload) {
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseHarmReductionVisitAction.Status evaluateStatusOnPayload() {
        return StringUtils.isNotBlank(hepatitisBcScreening)
                || (StringUtils.isNotBlank(hepatitisBScreening) && StringUtils.isNotBlank(hepatitisCScreening))
                ? BaseHarmReductionVisitAction.Status.COMPLETED
                : BaseHarmReductionVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHarmReductionVisitAction baseVisitAction) {
        // no-op
    }

    private String getFieldValue(JSONObject jsonObject, String key) throws JSONException {
        JSONArray fields = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            if (key.equals(field.optString(JsonFormConstants.KEY))) {
                return StringUtils.defaultString(field.optString(JsonFormConstants.VALUE));
            }
        }
        return "";
    }
}
