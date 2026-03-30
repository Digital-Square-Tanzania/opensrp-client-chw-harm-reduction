package org.smartregister.chw.harmreduction.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.harmreduction.dao.HarmReductionDao;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.domain.VisitDetail;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;
import org.smartregister.chw.harmreduction.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HarmReductionSoberHouseClientTypeFollowupStatusActionHelper implements BaseHarmReductionVisitAction.HarmReductionVisitActionHelper {
    private static final String CLIENT_TYPE_FIELD_KEY = "client_type";
    private static final String NEW_CLIENT_OPTION_KEY = "new_client";
    private static final String NEW_ENROLLMENT_STATUS = "new";

    private final MemberObject memberObject;
    private String jsonPayload;
    private String clientType;

    public HarmReductionSoberHouseClientTypeFollowupStatusActionHelper() {
        this(null);
    }

    public HarmReductionSoberHouseClientTypeFollowupStatusActionHelper(MemberObject memberObject) {
        this.memberObject = memberObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> details) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        if (StringUtils.isBlank(jsonPayload)) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            removeNewClientOption(jsonObject);
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
            clientType = getClientTypeValue(jsonObject);
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
        return StringUtils.isNotBlank(clientType)
                ? BaseHarmReductionVisitAction.Status.COMPLETED
                : BaseHarmReductionVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHarmReductionVisitAction baseVisitAction) {
        // no-op
    }

    private void removeNewClientOption(JSONObject jsonObject) {
        if (!shouldRemoveNewClientOption()) {
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
                if (field != null && CLIENT_TYPE_FIELD_KEY.equalsIgnoreCase(field.optString(JsonFormConstants.KEY))) {
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

                        if (!NEW_CLIENT_OPTION_KEY.equalsIgnoreCase(option.optString(JsonFormConstants.KEY))) {
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

    private boolean shouldRemoveNewClientOption() {
        if (memberObject == null || StringUtils.isBlank(memberObject.getBaseEntityId())) {
            return false;
        }

        if (hasPreviousSoberHouseServiceVisit()) {
            return true;
        }

        String enrollmentClientStatus = StringUtils.trimToEmpty(getSoberHouseEnrollmentClientStatus());
        return StringUtils.isNotBlank(enrollmentClientStatus)
                && !NEW_ENROLLMENT_STATUS.equalsIgnoreCase(enrollmentClientStatus);
    }

    protected boolean hasPreviousSoberHouseServiceVisit() {
        return HarmReductionDao.hasPreviousSoberHouseServiceVisit(memberObject.getBaseEntityId());
    }

    protected String getSoberHouseEnrollmentClientStatus() {
        return HarmReductionDao.getLatestSoberHouseEnrollmentClientStatus(memberObject.getBaseEntityId());
    }

    private String getClientTypeValue(JSONObject jsonObject) {
        String value = JsonFormUtils.getValue(jsonObject, CLIENT_TYPE_FIELD_KEY);
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
            if (field != null && CLIENT_TYPE_FIELD_KEY.equalsIgnoreCase(field.optString(JsonFormConstants.KEY))) {
                return StringUtils.defaultString(field.optString(JsonFormConstants.VALUE));
            }
        }

        return "";
    }
}
