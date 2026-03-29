package org.smartregister.chw.harmreduction.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.domain.VisitDetail;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;
import org.smartregister.chw.harmreduction.util.JsonFormUtils;
import org.smartregister.chw.harmreduction.dao.HarmReductionDao;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HarmReductionClientStatusActionHelper implements BaseHarmReductionVisitAction.HarmReductionVisitActionHelper {
    private static final String GLOBAL = "global";
    private static final String CLIENT_STATUS_FIELD_KEY = "client_status";
    private static final String CLIENT_STATUS_NEW_OPTION = "new";
    private static final String PREGNANCY_BREASTFEEDING_STATUS_FIELD_KEY = "pregnancy_breastfeeding_status";
    private static final String PREGNANT_FIELD_VALUE = "pregnant";
    private static final String NOT_PREGNANT_FIELD_VALUE = "not_pregnant";
    private static final String YES = "yes";
    private static final String NO = "no";

    protected MemberObject memberObject;
    protected String clientStatus;
    private String jsonPayload;

    public HarmReductionClientStatusActionHelper(MemberObject memberObject) {
        this.memberObject = memberObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> details) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONObject global = jsonObject.optJSONObject(GLOBAL);
            if (global == null) {
                global = new JSONObject();
                jsonObject.put(GLOBAL, global);
            }
            global.put("sex", memberObject.getGender().toLowerCase());
            removeNewClientStatusOption(jsonObject);
            prefillPregnancyBreastfeedingStatus(jsonObject);
            return jsonObject.toString();
        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            clientStatus = JsonFormUtils.getValue(jsonObject, "client_status");
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
        return StringUtils.isNotBlank(clientStatus)
                ? BaseHarmReductionVisitAction.Status.COMPLETED
                : BaseHarmReductionVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHarmReductionVisitAction baseVisitAction) {
        // no-op
    }

    private void removeNewClientStatusOption(JSONObject jsonObject) {
        if (memberObject == null || StringUtils.isBlank(memberObject.getBaseEntityId())) {
            return;
        }

        if (!hasPreviousFollowUpVisit()) {
            return;
        }

        try {
            JSONObject stepOne = jsonObject.optJSONObject(JsonFormConstants.STEP1);
            if (stepOne == null) {
                return;
            }

            JSONArray fields = stepOne.optJSONArray(JsonFormConstants.FIELDS);
            if (fields == null) {
                return;
            }

            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.optJSONObject(i);
                if (field != null && CLIENT_STATUS_FIELD_KEY.equalsIgnoreCase(field.optString(JsonFormConstants.KEY))) {
                    JSONArray options = field.optJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    if (options == null) {
                        return;
                    }

                    JSONArray filteredOptions = new JSONArray();
                    for (int j = 0; j < options.length(); j++) {
                        JSONObject option = options.optJSONObject(j);
                        if (option == null) {
                            continue;
                        }
                        if (!CLIENT_STATUS_NEW_OPTION.equalsIgnoreCase(option.optString(JsonFormConstants.KEY))) {
                            filteredOptions.put(option);
                        }
                    }
                    field.put(JsonFormConstants.OPTIONS_FIELD_NAME, filteredOptions);
                    return;
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void prefillPregnancyBreastfeedingStatus(JSONObject jsonObject) {
        if (memberObject == null
                || StringUtils.isBlank(memberObject.getBaseEntityId())
                || !StringUtils.equalsIgnoreCase(memberObject.getGender(), "female")
                || hasPreviousFollowUpVisit()) {
            return;
        }

        try {
            JSONObject stepOne = jsonObject.optJSONObject(JsonFormConstants.STEP1);
            if (stepOne == null) {
                return;
            }

            JSONArray fields = stepOne.optJSONArray(JsonFormConstants.FIELDS);
            if (fields == null) {
                return;
            }

            JSONObject field = null;
            for (int i = 0; i < fields.length(); i++) {
                JSONObject currentField = fields.optJSONObject(i);
                if (currentField != null && PREGNANCY_BREASTFEEDING_STATUS_FIELD_KEY.equalsIgnoreCase(currentField.optString(JsonFormConstants.KEY))) {
                    field = currentField;
                    break;
                }
            }

            if (field == null || StringUtils.isNotBlank(field.optString(JsonFormConstants.VALUE))) {
                return;
            }

            String riskAssessmentPregnancyStatus = getRiskAssessmentPregnancyStatus();
            if (YES.equalsIgnoreCase(riskAssessmentPregnancyStatus)) {
                field.put(JsonFormConstants.VALUE, PREGNANT_FIELD_VALUE);
            } else if (NO.equalsIgnoreCase(riskAssessmentPregnancyStatus)) {
                field.put(JsonFormConstants.VALUE, NOT_PREGNANT_FIELD_VALUE);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    protected boolean hasPreviousFollowUpVisit() {
        return memberObject != null && HarmReductionDao.hasPreviousHarmReductionFollowUpVisit(memberObject.getBaseEntityId());
    }

    protected String getRiskAssessmentPregnancyStatus() {
        return memberObject == null ? "" : HarmReductionDao.getRiskAssessmentPregnancyStatus(memberObject.getBaseEntityId());
    }
}
