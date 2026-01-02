package org.smartregister.chw.harmreduction.interactor;

import org.smartregister.chw.harmreduction.R;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionClientStatusActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionConsentJoiningMatActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionHealthEducationActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionHivInfectionStatusActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionOtherDiseasesScreeningActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionReferralsProvidedActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionRiskySexualBehaviorsActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSafeInjectionServicesActionHelper;
import org.smartregister.chw.harmreduction.contract.BaseHarmReductionVisitContract;
import org.smartregister.chw.harmreduction.domain.VisitDetail;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HarmReductionVisitInteractor extends BaseHarmReductionVisitInteractor {

    private static final String VISIT_TYPE = "Harm Reduction Community Visit";
    private static final String CLIENT_STATUS_FIELD = "client_status";
    private static final String CLIENT_STATUS_NEW = "new";
    private static final String CLIENT_STATUS_CONTINUE_SERVICE = "continue_service";
    private static final String IS_IDU_FIELD = "is_idu";
    private static final String YES_VALUE = "yes";

    public HarmReductionVisitInteractor() {
        super(VISIT_TYPE);
    }

    @Override
    protected void populateActionList(BaseHarmReductionVisitContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {
            try {
                evaluateClientStatus(details);
                evaluateHealthEducation(details);
                evaluateSafeInjectionServices(details);
                evaluateRiskySexualBehaviors(details);
                evaluateHivInfectionStatus(details);
                evaluateOtherDiseasesScreening(details);
                evaluateReferralsProvided(details);
                evaluateConsentJoiningMat(details);
            } catch (BaseHarmReductionVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    protected String getEncounterType() {
        return VISIT_TYPE;
    }

    private void evaluateClientStatus(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionClientStatusActionHelper actionHelper = new HarmReductionClientStatusActionHelper(memberObject);
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_client_status))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.FORMS.HARM_REDUCTION_CLIENT_STATUS)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_client_status), action);
    }

    private void evaluateHealthEducation(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionHealthEducationActionHelper actionHelper = new HarmReductionHealthEducationActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_health_education))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(otherActionsVisibilityValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_HEALTH_EDUCATION)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_health_education), action);
    }

    private void evaluateSafeInjectionServices(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionSafeInjectionServicesActionHelper actionHelper = new HarmReductionSafeInjectionServicesActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_safe_injection_services))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(safeInjectionVisibilityValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_SAFE_INJECTION_SERVICES)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_safe_injection_services), action);
    }

    private void evaluateRiskySexualBehaviors(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionRiskySexualBehaviorsActionHelper actionHelper = new HarmReductionRiskySexualBehaviorsActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_risky_sexual_behaviors))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(otherActionsVisibilityValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_RISKY_SEXUAL_BEHAVIORS)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_risky_sexual_behaviors), action);
    }

    private void evaluateHivInfectionStatus(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionHivInfectionStatusActionHelper actionHelper = new HarmReductionHivInfectionStatusActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_hiv_infection_status))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(otherActionsVisibilityValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_HIV_INFECTION_STATUS)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_hiv_infection_status), action);
    }

    private void evaluateOtherDiseasesScreening(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionOtherDiseasesScreeningActionHelper actionHelper = new HarmReductionOtherDiseasesScreeningActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_other_diseases_screening))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(otherActionsVisibilityValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_OTHER_DISEASES_SCREENING)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_other_diseases_screening), action);
    }

    private void evaluateReferralsProvided(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionReferralsProvidedActionHelper actionHelper = new HarmReductionReferralsProvidedActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_referrals_provided))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(otherActionsVisibilityValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_REFERRALS_PROVIDED)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_referrals_provided), action);
    }

    private void evaluateConsentJoiningMat(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionConsentJoiningMatActionHelper actionHelper = new HarmReductionConsentJoiningMatActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_consent_joining_mat))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(otherActionsVisibilityValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_CONSENT_JOINING_MAT)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_consent_joining_mat), action);
    }

    private BaseHarmReductionVisitAction.Validator otherActionsVisibilityValidator() {
        return new BaseHarmReductionVisitAction.Validator() {
            @Override
            public boolean isValid(String key) {
                return isClientStatusEligible();
            }

            @Override
            public boolean isEnabled(String key) {
                return isClientStatusEligible();
            }

            @Override
            public void onChanged(String key) {
                // no-op
            }
        };
    }

    private boolean isClientStatusEligible() {
        String status = getClientStatusValue(CLIENT_STATUS_FIELD);
        return CLIENT_STATUS_NEW.equalsIgnoreCase(status)
                || CLIENT_STATUS_CONTINUE_SERVICE.equalsIgnoreCase(status);
    }

    private BaseHarmReductionVisitAction.Validator safeInjectionVisibilityValidator() {
        return new BaseHarmReductionVisitAction.Validator() {
            @Override
            public boolean isValid(String key) {
                return isClientIdu();
            }

            @Override
            public boolean isEnabled(String key) {
                return isClientIdu();
            }

            @Override
            public void onChanged(String key) {
                // no-op
            }
        };
    }

    private boolean isClientIdu() {
        String isIdu = getClientStatusValue(IS_IDU_FIELD);
        return YES_VALUE.equalsIgnoreCase(isIdu);
    }

    private String getClientStatusValue(String key) {
        if (context == null) {
            return null;
        }

        BaseHarmReductionVisitAction clientStatusAction = actionList.get(
                context.getString(R.string.harm_reduction_client_status)
        );
        if (clientStatusAction == null) {
            return null;
        }

        String payload = clientStatusAction.getJsonPayload();
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
