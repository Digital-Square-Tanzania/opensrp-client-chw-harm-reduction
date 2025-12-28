package org.smartregister.chw.harmreduction.actionhelper;

import static com.vijay.jsonwizard.constants.JsonFormConstants.EDITABLE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.READ_ONLY;

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

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HarmReductionSourceActionHelper implements BaseHarmReductionVisitAction.HarmReductionVisitActionHelper {

    protected String typeOfPatientRelationship;

    protected String jsonPayload;


    protected String baseEntityId;

    protected Context context;

    protected MemberObject memberObject;


    public HarmReductionSourceActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
        this.baseEntityId = memberObject != null ? memberObject.getBaseEntityId() : null;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            JSONArray fields = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
            JSONObject tbClientNumber = JsonFormUtils.getFieldJSONObject(fields, "tb_client_number");
            if (tbClientNumber != null) {
                tbClientNumber.put("mask", "############-#/KK/" + Calendar.getInstance().get(Calendar.YEAR) + "/###");
            }

            JSONObject leprosyClientNumber = JsonFormUtils.getFieldJSONObject(fields, "leprosy_client_number");
            if (leprosyClientNumber != null) {
                leprosyClientNumber.put("mask", "############-#/UK/" + Calendar.getInstance().get(Calendar.YEAR) + "/###");
            }

            JSONObject indexCaseConditionTypes = JsonFormUtils.getFieldJSONObject(fields, "index_case_condition_types");
            JSONObject doYouKnowClientsNumber = JsonFormUtils.getFieldJSONObject(fields, "do_you_know_clients_number");

            String contactBaseEntityId = baseEntityId;
            if (StringUtils.isBlank(contactBaseEntityId) && memberObject != null) {
                contactBaseEntityId = memberObject.getBaseEntityId();
            }

            String indexClientId = HarmReductionDao.getIndexClientIdForContact(contactBaseEntityId);
            if (StringUtils.isNotBlank(indexClientId)) {
                HarmReductionDao.ClientNumberInfo clientNumberInfo = HarmReductionDao.getIndexClientNumbers(indexClientId);
                if (clientNumberInfo != null) {
                    String tbNumber = clientNumberInfo.getTbClientNumber();
                    String leprosyNumber = clientNumberInfo.getLeprosyClientNumber();

                    if (StringUtils.isNotBlank(tbNumber)) {
                        selectCheckBoxValue(indexCaseConditionTypes, "tb");
                        if (doYouKnowClientsNumber != null) {
                            doYouKnowClientsNumber.put(JsonFormConstants.VALUE, "yes");
                            doYouKnowClientsNumber.put(EDITABLE, true);
                            doYouKnowClientsNumber.put(READ_ONLY,true);
                        }
                        if (tbClientNumber != null) {
                            tbClientNumber.put(JsonFormConstants.VALUE, tbNumber);
                            tbClientNumber.put(EDITABLE, true);
                            tbClientNumber.put(READ_ONLY,true);
                        }
                    } else if (StringUtils.isNotBlank(leprosyNumber)) {
                        selectCheckBoxValue(indexCaseConditionTypes, "leprosy");
                        if (doYouKnowClientsNumber != null) {
                            doYouKnowClientsNumber.put(JsonFormConstants.VALUE, "yes");
                            doYouKnowClientsNumber.put(EDITABLE, true);
                            doYouKnowClientsNumber.put(READ_ONLY,true);
                        }
                        if (leprosyClientNumber != null) {
                            leprosyClientNumber.put(JsonFormConstants.VALUE, leprosyNumber);
                            leprosyClientNumber.put(EDITABLE, true);
                            leprosyClientNumber.put(READ_ONLY,true);
                        }
                    }
                }
            }

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            typeOfPatientRelationship = JsonFormUtils.getValue(jsonObject, "contact_lives_with_patient_type");
            if (StringUtils.isBlank(typeOfPatientRelationship)) {
                typeOfPatientRelationship = JsonFormUtils.getValue(jsonObject, "anaishi_karibu_na_mgonjwa");
            }
            if (StringUtils.isBlank(typeOfPatientRelationship)) {
                typeOfPatientRelationship = JsonFormUtils.getValue(jsonObject, "relationship_to_index_client");
            }
            if (StringUtils.isBlank(typeOfPatientRelationship)) {
                typeOfPatientRelationship = JsonFormUtils.getValue(jsonObject, "aina_ya_ukaribu_na_mgonjwa");
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
        if(StringUtils.isNotBlank(typeOfPatientRelationship)){
            return BaseHarmReductionVisitAction.Status.COMPLETED;
        }
        return BaseHarmReductionVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHarmReductionVisitAction baseTbLeprosyVisitAction) {
        Timber.v("onPayloadReceived");
    }

    private void selectCheckBoxValue(JSONObject checkBoxField, String optionKey) throws JSONException {
        if (checkBoxField == null || StringUtils.isBlank(optionKey)) {
            return;
        }

        JSONArray options = checkBoxField.optJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        if (options == null) {
            return;
        }

        JSONArray selectedValues = new JSONArray();
        for (int i = 0; i < options.length(); i++) {
            JSONObject option = options.getJSONObject(i);
            if (StringUtils.equalsIgnoreCase(option.optString(JsonFormConstants.KEY), optionKey)) {
                option.put(JsonFormConstants.VALUE, true);
                selectedValues.put(option.optString(JsonFormConstants.KEY));
                break;
            }
        }
        checkBoxField.put(JsonFormConstants.VALUE, selectedValues);
    }
}
