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

public class HarmReductionHivInfectionStatusActionHelper implements BaseHarmReductionVisitAction.HarmReductionVisitActionHelper {
    private static final String HIV_TESTED_FIELD_KEY = "hiv_tested";
    private static final String HIV_TEST_LOCATION_FIELD_KEY = "hiv_test_location";
    private static final String HIV_RESULTS_FIELD_KEY = "hiv_results";
    private static final String ENROLLED_INTO_CTC_SERVICES_FIELD_KEY = "enrolled_into_ctc_services";
    private static final String CTC_ID_FIELD_KEY = "ctc_id";

    protected MemberObject memberObject;
    private String jsonPayload;
    private String hivTested;

    public HarmReductionHivInfectionStatusActionHelper() {
        // no-op
    }

    public HarmReductionHivInfectionStatusActionHelper(MemberObject memberObject) {
        this.memberObject = memberObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> details) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        if (StringUtils.isBlank(jsonPayload) || !hasPreviousFollowUpVisit()) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            boolean wasUpdated = false;
            wasUpdated |= prefillField(jsonObject, HIV_TESTED_FIELD_KEY, getLatestHivTested());
            wasUpdated |= prefillField(jsonObject, HIV_TEST_LOCATION_FIELD_KEY, getLatestHivTestLocation());
            wasUpdated |= prefillField(jsonObject, HIV_RESULTS_FIELD_KEY, getLatestHivResults());
            wasUpdated |= prefillField(jsonObject, ENROLLED_INTO_CTC_SERVICES_FIELD_KEY, getLatestEnrolledIntoCtcServices());
            wasUpdated |= prefillField(jsonObject, CTC_ID_FIELD_KEY, getLatestCtcId());
            return wasUpdated ? jsonObject.toString() : null;
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            hivTested = JsonFormUtils.getValue(jsonObject, HIV_TESTED_FIELD_KEY);
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
        return StringUtils.isNotBlank(hivTested)
                ? BaseHarmReductionVisitAction.Status.COMPLETED
                : BaseHarmReductionVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHarmReductionVisitAction baseVisitAction) {
        // no-op
    }

    private boolean prefillField(JSONObject jsonObject, String fieldKey, String value) throws JSONException {
        if (StringUtils.isBlank(value)) {
            return false;
        }

        JSONObject field = getField(jsonObject, fieldKey);
        if (field == null || StringUtils.isNotBlank(field.optString(JsonFormConstants.VALUE))) {
            return false;
        }

        field.put(JsonFormConstants.VALUE, value);
        JSONArray options = field.optJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        if (options != null) {
            for (int i = 0; i < options.length(); i++) {
                JSONObject option = options.optJSONObject(i);
                if (option == null) {
                    continue;
                }
                option.put(JsonFormConstants.VALUE,
                        StringUtils.equalsIgnoreCase(value, option.optString(JsonFormConstants.KEY)));
            }
        }
        return true;
    }

    private JSONObject getField(JSONObject jsonObject, String key) {
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
            if (field != null && key.equalsIgnoreCase(field.optString(JsonFormConstants.KEY))) {
                return field;
            }
        }

        return null;
    }

    protected boolean hasPreviousFollowUpVisit() {
        return memberObject != null && HarmReductionDao.hasPreviousHarmReductionFollowUpVisit(memberObject.getBaseEntityId());
    }

    protected String getLatestHivTested() {
        return memberObject == null ? "" : HarmReductionDao.getLatestHivTested(memberObject.getBaseEntityId());
    }

    protected String getLatestHivTestLocation() {
        return memberObject == null ? "" : HarmReductionDao.getLatestHivTestLocation(memberObject.getBaseEntityId());
    }

    protected String getLatestHivResults() {
        return memberObject == null ? "" : HarmReductionDao.getLatestHivResults(memberObject.getBaseEntityId());
    }

    protected String getLatestEnrolledIntoCtcServices() {
        return memberObject == null ? "" : HarmReductionDao.getLatestEnrolledIntoCtcServices(memberObject.getBaseEntityId());
    }

    protected String getLatestCtcId() {
        return memberObject == null ? "" : HarmReductionDao.getLatestCtcId(memberObject.getBaseEntityId());
    }
}
