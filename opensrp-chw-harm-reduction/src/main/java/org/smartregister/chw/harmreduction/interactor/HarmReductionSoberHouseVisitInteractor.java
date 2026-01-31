package org.smartregister.chw.harmreduction.interactor;

import static org.smartregister.chw.harmreduction.util.Constants.EVENT_TYPE.HARM_REDUCTION_SOBER_HOUSE_VISIT;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.harmreduction.R;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseClientTypeFollowupStatusActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseLinkageToOtherServicesActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseNextAppointmentDateActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseRecoveryCapitalAssessmentAftercareActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseReferralsActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseRoutineServicesActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseVitalSignCheckActionHelper;
import org.smartregister.chw.harmreduction.contract.BaseHarmReductionVisitContract;
import org.smartregister.chw.harmreduction.domain.VisitDetail;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HarmReductionSoberHouseVisitInteractor extends BaseHarmReductionVisitInteractor {
    private static final String FOLLOW_UP_STATUS_FIELD = "follow_up_status";
    private static final String CONTINUING_SERVICE_VALUE = "continuing_service";

    public HarmReductionSoberHouseVisitInteractor() {
        super(HARM_REDUCTION_SOBER_HOUSE_VISIT);
    }

    @Override
    protected void populateActionList(BaseHarmReductionVisitContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {
            try {
                evaluateClientTypeFollowupStatus(details);
                evaluateVitalSignCheck(details);
                evaluateRoutineServices(details);
                evaluateLinkageToOtherServices(details);
                evaluateReferrals(details);
                evaluateRecoveryCapitalAssessmentAftercare(details);
                evaluateNextAppointmentDate(details);
            } catch (BaseHarmReductionVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    protected String getEncounterType() {
        return HARM_REDUCTION_SOBER_HOUSE_VISIT;
    }

    private void evaluateClientTypeFollowupStatus(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionSoberHouseClientTypeFollowupStatusActionHelper actionHelper = new HarmReductionSoberHouseClientTypeFollowupStatusActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_sober_house_client_type_followup_status))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.FORMS.HARM_REDUCTION_SOBER_HOUSE_CLIENT_TYPE_FOLLOWUP_STATUS)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_sober_house_client_type_followup_status), action);
    }

    private void evaluateVitalSignCheck(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionSoberHouseVitalSignCheckActionHelper actionHelper = new HarmReductionSoberHouseVitalSignCheckActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_sober_house_vital_sign_check))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(continuingServiceValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_SOBER_HOUSE_VITAL_SIGN_CHECK)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_sober_house_vital_sign_check), action);
    }

    private void evaluateRoutineServices(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionSoberHouseRoutineServicesActionHelper actionHelper = new HarmReductionSoberHouseRoutineServicesActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_sober_house_routine_services))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(continuingServiceValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_SOBER_HOUSE_ROUTINE_SERVICES)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_sober_house_routine_services), action);
    }

    private void evaluateLinkageToOtherServices(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionSoberHouseLinkageToOtherServicesActionHelper actionHelper = new HarmReductionSoberHouseLinkageToOtherServicesActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_sober_house_linkage_to_other_services))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(continuingServiceValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_SOBER_HOUSE_LINKAGE_TO_OTHER_SERVICES)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_sober_house_linkage_to_other_services), action);
    }

    private void evaluateReferrals(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionSoberHouseReferralsActionHelper actionHelper = new HarmReductionSoberHouseReferralsActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_sober_house_referrals))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(continuingServiceValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_SOBER_HOUSE_REFERRALS)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_sober_house_referrals), action);
    }

    private void evaluateRecoveryCapitalAssessmentAftercare(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionSoberHouseRecoveryCapitalAssessmentAftercareActionHelper actionHelper = new HarmReductionSoberHouseRecoveryCapitalAssessmentAftercareActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_sober_house_recovery_capital_assessment_aftercare))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(continuingServiceValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_SOBER_HOUSE_RECOVERY_CAPITAL_ASSESSMENT_AFTERCARE)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_sober_house_recovery_capital_assessment_aftercare), action);
    }

    private void evaluateNextAppointmentDate(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionSoberHouseNextAppointmentDateActionHelper actionHelper = new HarmReductionSoberHouseNextAppointmentDateActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_sober_house_next_appointment_date))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(continuingServiceValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_SOBER_HOUSE_NEXT_APPOINTMENT_DATE)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_sober_house_next_appointment_date), action);
    }

    private BaseHarmReductionVisitAction.Validator continuingServiceValidator() {
        return new BaseHarmReductionVisitAction.Validator() {
            @Override
            public boolean isValid(String key) {
                return isContinuingService();
            }

            @Override
            public boolean isEnabled(String key) {
                return isContinuingService();
            }

            @Override
            public void onChanged(String key) {
                // no-op
            }
        };
    }

    private boolean isContinuingService() {
        String status = getFollowUpStatusValue(FOLLOW_UP_STATUS_FIELD);
        return CONTINUING_SERVICE_VALUE.equalsIgnoreCase(status);
    }

    private String getFollowUpStatusValue(String key) {
        if (context == null) {
            return null;
        }

        BaseHarmReductionVisitAction statusAction = actionList.get(
                context.getString(R.string.harm_reduction_sober_house_client_type_followup_status)
        );
        if (statusAction == null) {
            return null;
        }

        String payload = statusAction.getJsonPayload();
        if (StringUtils.isBlank(payload)) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(payload);
            return JsonFormUtils.getValue(jsonObject, key);
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }
}
