package org.smartregister.chw.harmreduction.actionhelper;

import static org.smartregister.client.utils.constants.JsonFormConstants.TYPE;

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
    private static final String DRUG_ADHERENCE_STATUS_CTC_FIELD_KEY = "drug_adherence_status_ctc";
    private static final String CTC_ID_FIELD_KEY = "ctc_id";
    private static final String POSITIVE_VALUE = "positive";
    private static final String YES_VALUE = "yes";
    private static final String NOT_STARTED_VALUE = "not_started";
    private static final String READ_ONLY = "read_only";
    private static final String EDITABLE = "editable";

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
        if (StringUtils.isBlank(jsonPayload) || !hasPreviousFollowUpVisit() || !hasPreviousPositiveHivVisit()) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            boolean wasUpdated = false;
            wasUpdated |= prefillLockedField(jsonObject, HIV_TESTED_FIELD_KEY, YES_VALUE);
            wasUpdated |= prefillLockedField(jsonObject, HIV_TEST_LOCATION_FIELD_KEY, getLatestPositiveHivTestLocation());
            wasUpdated |= prefillLockedField(jsonObject, HIV_RESULTS_FIELD_KEY, POSITIVE_VALUE);
            wasUpdated |= prefillLockedField(jsonObject, ENROLLED_INTO_CTC_SERVICES_FIELD_KEY, getLatestPositiveEnrolledIntoCtcServices());
            wasUpdated |= prefillLockedField(jsonObject, CTC_ID_FIELD_KEY, getLatestPositiveCtcId());
            if (wasUpdated) {
                removeOption(jsonObject, DRUG_ADHERENCE_STATUS_CTC_FIELD_KEY, NOT_STARTED_VALUE);
                moveFieldToTop(jsonObject, DRUG_ADHERENCE_STATUS_CTC_FIELD_KEY);
            }
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

    private boolean prefillLockedField(JSONObject jsonObject, String fieldKey, String value) throws JSONException {
        if (StringUtils.isBlank(value)) {
            return false;
        }

        JSONObject field = getField(jsonObject, fieldKey);
        if (field == null || StringUtils.isNotBlank(field.optString(JsonFormConstants.VALUE))) {
            return false;
        }

        field.put(JsonFormConstants.VALUE, value);
        field.put(READ_ONLY, true);
        field.put(EDITABLE, false);
        field.put(TYPE, "hidden");
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

    private void removeOption(JSONObject jsonObject, String fieldKey, String optionKey) throws JSONException {
        JSONObject field = getField(jsonObject, fieldKey);
        if (field == null) {
            return;
        }

        JSONArray options = field.optJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        if (options == null) {
            return;
        }

        JSONArray updatedOptions = new JSONArray();
        for (int i = 0; i < options.length(); i++) {
            JSONObject option = options.optJSONObject(i);
            if (option != null && !StringUtils.equalsIgnoreCase(optionKey, option.optString(JsonFormConstants.KEY))) {
                updatedOptions.put(option);
            }
        }
        field.put(JsonFormConstants.OPTIONS_FIELD_NAME, updatedOptions);
    }

    private void moveFieldToTop(JSONObject jsonObject, String fieldKey) throws JSONException {
        JSONObject stepOne = jsonObject.optJSONObject(JsonFormConstants.STEP1);
        if (stepOne == null) {
            return;
        }

        JSONArray fields = stepOne.optJSONArray(JsonFormConstants.FIELDS);
        if (fields == null) {
            return;
        }

        JSONObject targetField = null;
        JSONArray reorderedFields = new JSONArray();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.optJSONObject(i);
            if (field == null) {
                continue;
            }

            if (fieldKey.equalsIgnoreCase(field.optString(JsonFormConstants.KEY))) {
                targetField = field;
            } else {
                reorderedFields.put(field);
            }
        }

        if (targetField == null) {
            return;
        }

        JSONArray updatedFields = new JSONArray();
        updatedFields.put(targetField);
        for (int i = 0; i < reorderedFields.length(); i++) {
            updatedFields.put(reorderedFields.get(i));
        }
        stepOne.put(JsonFormConstants.FIELDS, updatedFields);
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

    protected boolean hasPreviousPositiveHivVisit() {
        return memberObject != null && HarmReductionDao.hasPreviousPositiveHivFollowUpVisit(memberObject.getBaseEntityId());
    }

    protected String getLatestPositiveHivTestLocation() {
        return memberObject == null ? "" : HarmReductionDao.getLatestPositiveHivTestLocation(memberObject.getBaseEntityId());
    }

    protected String getLatestPositiveEnrolledIntoCtcServices() {
        return memberObject == null ? "" : HarmReductionDao.getLatestPositiveEnrolledIntoCtcServices(memberObject.getBaseEntityId());
    }

    protected String getLatestPositiveCtcId() {
        return memberObject == null ? "" : HarmReductionDao.getLatestPositiveCtcId(memberObject.getBaseEntityId());
    }
}
