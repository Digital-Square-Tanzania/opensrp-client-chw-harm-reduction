package org.smartregister.chw.harmreduction.interactor;

import static org.smartregister.chw.harmreduction.util.Constants.EVENT_TYPE.HARM_REDUCTION_SOBER_HOUSE_VISIT;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.harmreduction.R;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseClientTypeFollowupStatusActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseLifeSkillsParticipationActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseLinkageToOtherServicesActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseNextAppointmentDateActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseRecoveryCapitalAssessmentAftercareActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseReferralsActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseRoutineServicesActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSoberHouseVitalSignCheckActionHelper;
import org.smartregister.chw.harmreduction.contract.BaseHarmReductionVisitContract;
import org.smartregister.chw.harmreduction.dao.HarmReductionDao;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.domain.VisitDetail;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class BaseHarmReductionSoberHouseVisitInteractor extends BaseHarmReductionVisitInteractor {
    private static final int RECOVERY_CAPITAL_ASSESSMENT_DELAY_MONTHS = 3;
    private static final String FOLLOW_UP_STATUS_FIELD = "follow_up_status";
    private static final String CLIENT_TYPE = "client_type";
    private static final String CONTINUING_SERVICE_VALUE = "continuing_service";
    private static final String NEW_CLIENT_VALUE = "new_client";
    private static final String RELAPSED_CLIENT_VALUE = "relapsed_client";
    private static final String MIGRANT_CLIENT_VALUE = "migrant_client";
    private static final String RECOVERY_CAPITAL_PASSED_FIELD = "recovery_capital_passed";
    private static final String YES_VALUE = "yes";

    public BaseHarmReductionSoberHouseVisitInteractor() {
        super(HARM_REDUCTION_SOBER_HOUSE_VISIT);
    }

    @Override
    protected void populateActionList(BaseHarmReductionVisitContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {
            try {
                evaluateClientTypeFollowupStatus(details);
                evaluateVitalSignCheck(details);
                evaluateRoutineServices(details);
                evaluateLifeSkillsParticipation(details);
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

    private void evaluateLifeSkillsParticipation(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {
        HarmReductionSoberHouseLifeSkillsParticipationActionHelper actionHelper = new HarmReductionSoberHouseLifeSkillsParticipationActionHelper();
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.harm_reduction_sober_house_life_skills_participation))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(continuingServiceValidator())
                .withFormName(Constants.FORMS.HARM_REDUCTION_SOBER_HOUSE_LIFE_SKILLS_PARTICIPATION)
                .build();
        actionList.put(context.getString(R.string.harm_reduction_sober_house_life_skills_participation), action);
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
                .withValidator(recoveryCapitalAssessmentAftercareValidator())
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
                .withValidator(nextAppointmentDateValidator())
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

    private BaseHarmReductionVisitAction.Validator recoveryCapitalAssessmentAftercareValidator() {
        return new BaseHarmReductionVisitAction.Validator() {
            @Override
            public boolean isValid(String key) {
                return isRecoveryCapitalAssessmentAftercareVisible();
            }

            @Override
            public boolean isEnabled(String key) {
                return isRecoveryCapitalAssessmentAftercareVisible();
            }

            @Override
            public void onChanged(String key) {
                // no-op
            }
        };
    }

    private BaseHarmReductionVisitAction.Validator nextAppointmentDateValidator() {
        return new BaseHarmReductionVisitAction.Validator() {
            @Override
            public boolean isValid(String key) {
                return isNextAppointmentDateVisible();
            }

            @Override
            public boolean isEnabled(String key) {
                return isNextAppointmentDateVisible();
            }

            @Override
            public void onChanged(String key) {
                // no-op
            }
        };
    }

    private boolean isContinuingService() {
        String status = getFollowUpStatusValue(FOLLOW_UP_STATUS_FIELD);
        String clientType = getFollowUpStatusValue(CLIENT_TYPE);
        return CONTINUING_SERVICE_VALUE.equalsIgnoreCase(status) ||
                NEW_CLIENT_VALUE.equalsIgnoreCase(clientType) ||
                RELAPSED_CLIENT_VALUE.equalsIgnoreCase(clientType) ||
                MIGRANT_CLIENT_VALUE.equalsIgnoreCase(clientType);
    }

    private boolean isNextAppointmentDateVisible() {
        if (!isContinuingService()) {
            return false;
        }

        return !shouldHideNextAppointmentDateAction(
                isRecoveryCapitalAssessmentAftercareVisible(),
                getRecoveryCapitalPassedValue()
        );
    }

    @VisibleForTesting
    static boolean shouldHideNextAppointmentDateAction(boolean isRecoveryCapitalAssessmentAftercareVisible,
                                                       String recoveryCapitalPassedValue) {
        return isRecoveryCapitalAssessmentAftercareVisible &&
                YES_VALUE.equalsIgnoreCase(StringUtils.trimToEmpty(recoveryCapitalPassedValue));
    }

    private boolean isRecoveryCapitalAssessmentAftercareVisible() {
        if (!isContinuingService() || memberObject == null || StringUtils.isBlank(memberObject.getBaseEntityId())) {
            return false;
        }

        String enrollmentEventDate = HarmReductionDao.getSoberHouseEnrollmentEventDate(memberObject.getBaseEntityId());
        return isVisitAtLeastThreeCalendarMonthsFromEnrollment(enrollmentEventDate, getCurrentVisitDateTime());
    }

    @VisibleForTesting
    DateTime getCurrentVisitDateTime() {
        return DateTime.now();
    }

    @VisibleForTesting
    static boolean isVisitAtLeastThreeCalendarMonthsFromEnrollment(String enrollmentEventDate, DateTime visitDateTime) {
        if (visitDateTime == null) {
            return false;
        }

        LocalDate enrollmentDate = parseEnrollmentEventDate(enrollmentEventDate);
        if (enrollmentDate == null) {
            return false;
        }

        LocalDate eligibleDate = enrollmentDate.plusMonths(RECOVERY_CAPITAL_ASSESSMENT_DELAY_MONTHS);
        return !visitDateTime.toLocalDate().isBefore(eligibleDate);
    }

    @VisibleForTesting
    static LocalDate parseEnrollmentEventDate(String enrollmentEventDate) {
        if (StringUtils.isBlank(enrollmentEventDate)) {
            return null;
        }

        String normalizedDate = enrollmentEventDate.trim();
        try {
            return new DateTime(Long.parseLong(normalizedDate)).toLocalDate();
        } catch (NumberFormatException e) {
            // no-op
        }

        DateTimeFormatter[] formatters = {
                ISODateTimeFormat.dateTimeParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S"),
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormat.forPattern("yyyy-MM-dd")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return formatter.parseDateTime(normalizedDate).toLocalDate();
            } catch (IllegalArgumentException e) {
                // try next supported format
            }
        }

        return null;
    }

    private String getFollowUpStatusValue(String key) {
        return getActionValue(
                context == null ? null : context.getString(R.string.harm_reduction_sober_house_client_type_followup_status),
                key
        );
    }

    private String getRecoveryCapitalPassedValue() {
        return getActionValue(
                context == null ? null : context.getString(R.string.harm_reduction_sober_house_recovery_capital_assessment_aftercare),
                RECOVERY_CAPITAL_PASSED_FIELD
        );
    }

    private String getActionValue(String actionTitle, String key) {
        if (context == null) {
            return null;
        }

        BaseHarmReductionVisitAction statusAction = actionList.get(actionTitle);
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

    /**
     * Default if profile type is not provided is TbLeprosy/PrEP member
     *
     * @param memberID    unique identifier for the user
     * @param profileType profile type being used
     * @return MemberObject wrapper for the user's data
     */
    @Override
    public MemberObject getMemberClient(String memberID, String profileType) {
        return HarmReductionDao.getSoberHouseMember(memberID);
    }

}
