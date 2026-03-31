package org.smartregister.chw.harmreduction.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.domain.VisitDetail;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;
import org.smartregister.chw.harmreduction.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HarmReductionSoberHouseClientTypeFollowupStatusActionHelper implements BaseHarmReductionVisitAction.HarmReductionVisitActionHelper {
    private static final String FOLLOW_UP_STATUS_FIELD_KEY = "follow_up_status";
    private String followUpStatus;

    public HarmReductionSoberHouseClientTypeFollowupStatusActionHelper() {
        // no-op
    }

    public HarmReductionSoberHouseClientTypeFollowupStatusActionHelper(MemberObject ignoredMemberObject) {
        // no-op
    }

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
            followUpStatus = getFieldValue(jsonObject, FOLLOW_UP_STATUS_FIELD_KEY);
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
        return StringUtils.isNotBlank(followUpStatus)
                ? BaseHarmReductionVisitAction.Status.COMPLETED
                : BaseHarmReductionVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHarmReductionVisitAction baseVisitAction) {
        // no-op
    }

    private String getFieldValue(JSONObject jsonObject, String key) {
        String value = JsonFormUtils.getValue(jsonObject, key);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }

        JSONObject stepOne = jsonObject.optJSONObject(JsonFormConstants.STEP1);
        if (stepOne == null) {
            return "";
        }

        JSONArray fields = stepOne.optJSONArray(JsonFormConstants.FIELDS);
        if (fields == null) {
            return "";
        }

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.optJSONObject(i);
            if (field != null && key.equalsIgnoreCase(field.optString(JsonFormConstants.KEY))) {
                return StringUtils.defaultString(field.optString(JsonFormConstants.VALUE));
            }
        }

        return "";
    }
}
