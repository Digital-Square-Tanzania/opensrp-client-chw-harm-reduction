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

        if (!HarmReductionDao.hasHarmReductionVisit(memberObject.getBaseEntityId())) {
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
}
