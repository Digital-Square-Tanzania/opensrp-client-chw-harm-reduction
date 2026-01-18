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

public class HarmReductionRiskySexualBehaviorsActionHelper implements BaseHarmReductionVisitAction.HarmReductionVisitActionHelper {

    private static final String ENGAGING_IN_SEXUAL_ACTIVITY_KEY = "engaging_in_sexual_activity";
    private static final String YES_VALUE = "yes";
    private static final String READ_ONLY = "read_only";
    private static final String EDITABLE = "editable";

    private final UnsafeSexProvider unsafeSexProvider;
    private String jsonPayload;

    public HarmReductionRiskySexualBehaviorsActionHelper(UnsafeSexProvider unsafeSexProvider) {
        this.unsafeSexProvider = unsafeSexProvider;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> details) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        boolean unsafeSexSelected = unsafeSexProvider != null && unsafeSexProvider.isUnsafeSexSelected();
        if (!unsafeSexSelected || StringUtils.isBlank(jsonPayload)) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONObject stepOne = jsonObject.optJSONObject(JsonFormConstants.STEP1);
            if (stepOne == null) {
                return null;
            }

            JSONArray fields = stepOne.optJSONArray(JsonFormConstants.FIELDS);
            if (fields == null) {
                return null;
            }

            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.optJSONObject(i);
                if (field != null && ENGAGING_IN_SEXUAL_ACTIVITY_KEY.equalsIgnoreCase(field.optString(JsonFormConstants.KEY))) {
                    field.put(JsonFormConstants.VALUE, YES_VALUE);
                    JSONArray options = field.optJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    if (options != null) {
                        for (int j = 0; j < options.length(); j++) {
                            JSONObject option = options.optJSONObject(j);
                            if (option == null) {
                                continue;
                            }
                            boolean isYesOption = YES_VALUE.equalsIgnoreCase(option.optString(JsonFormConstants.KEY));
                            option.put(JsonFormConstants.VALUE, isYesOption);
                        }
                    }
                    field.put(READ_ONLY, true);
                    field.put(EDITABLE, false);
                    return jsonObject.toString();
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        this.jsonPayload = jsonPayload;
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
        return StringUtils.isNotBlank(jsonPayload)
                ? BaseHarmReductionVisitAction.Status.COMPLETED
                : BaseHarmReductionVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHarmReductionVisitAction baseVisitAction) {
        // no-op
    }

    public interface UnsafeSexProvider {
        boolean isUnsafeSexSelected();
    }
}
