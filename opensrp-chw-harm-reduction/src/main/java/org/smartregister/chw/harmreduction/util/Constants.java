package org.smartregister.chw.harmreduction.util;

public interface Constants {

    int REQUEST_CODE_GET_JSON = 2244;
    String ENCOUNTER_TYPE = "encounter_type";
    String STEP_ONE = "step1";
    String HARM_REDUCTION_VISIT_GROUP = "harm_reduction_visit_group";


    interface JSON_FORM_EXTRA {
        String JSON = "json";
        String ENCOUNTER_TYPE = "encounter_type";
        String EVENT_TYPE = "eventType";
    }

    interface EVENT_TYPE {
        String HARM_REDUCTION_RISK_ASSESSMENT = "Harm Reduction Risk Assessment";
        String HARM_REDUCTION_FOLLOW_UP_VISIT =  "Harm Reduction Follow-up Visit";
        String VOID_EVENT = "Void Event";
        String CLOSE_HARM_REDUCTION_SERVICE = "Close Harm Reduction Service";
        String HARM_REDUCTION_USED_NEEDLES_AND_SYRINGES_COLLECTION = "Harm Reduction Used Needle and Syringes Collection";
    }

    interface FORMS {
        String HARM_REDUCTION_CLIENT_STATUS_VISIT = "harm_reduction_client_status_visit";
        String HARM_REDUCTION_CLIENT_STATUS = "harm_reduction_client_status_visit";
        String HARM_REDUCTION_HEALTH_EDUCATION_IEC = "harm_reduction_health_education_iec";
        String HARM_REDUCTION_HEALTH_EDUCATION = "harm_reduction_health_education_iec";
        String HARM_REDUCTION_HIV_INFECTION_STATUS = "harm_reduction_hiv_infection_status";
        String HARM_REDUCTION_MAT_FOLLOWUP = "harm_reduction_mat_followup";
        String HARM_REDUCTION_OTHER_DISEASES_SCREENING = "harm_reduction_other_diseases_screening";
        String HARM_REDUCTION_REFERRALS_PROVIDED = "harm_reduction_referrals_provided";
        String HARM_REDUCTION_REGISTER_EXISTING_CLIENT = "harm_reduction_register_existing_client";
        String HARM_REDUCTION_RISK_ASSESSMENT = "harm_reduction_risk_assessment";
        String HARM_REDUCTION_RISKY_SEXUAL_BEHAVIORS_CONDOMS = "harm_reduction_risky_sexual_behaviors_condoms";
        String HARM_REDUCTION_RISKY_SEXUAL_BEHAVIORS = "harm_reduction_risky_sexual_behaviors_condoms";
        String HARM_REDUCTION_SAFE_INJECTION_SERVICES = "harm_reduction_safe_injection_services";
        String HARM_REDUCTION_SOBER_HOUSE_DETOXIFICATION = "harm_reduction_sober_house_detoxification";
        String HARM_REDUCTION_SOBER_HOUSE_ENROLLMENT_ELIGIBILITY = "harm_reduction_sober_house_enrollment_eligibility";
        String HARM_REDUCTION_SOBER_HOUSE_FACILITY_REFERRAL_PROVIDED = "harm_reduction_sober_house_facility_referral_provided";
        String HARM_REDUCTION_SOBER_HOUSE_FOLLOWUP_STATUS = "harm_reduction_sober_house_followup_status";
        String HARM_REDUCTION_SOBER_HOUSE_LINKAGE_SERVICES_PROVIDED = "harm_reduction_sober_house_linkage_services_provided";
        String HARM_REDUCTION_SOBER_HOUSE_RECOVERY_CAPITAL_FOLLOWUP = "harm_reduction_sober_house_recovery_capital_followup";
        String HARM_REDUCTION_SOBER_HOUSE_ROC_PROFILE = "harm_reduction_sober_house_roc_profile";
        String HARM_REDUCTION_SOBER_HOUSE_ROUTINE_SERVICES = "harm_reduction_sober_house_routine_services";
        String ROC_CONSENT_JOINING_MAT_SERVICES = "roc_consent_joining_mat_services";
        String HARM_REDUCTION_CONSENT_JOINING_MAT = "roc_consent_joining_mat_services";
        String HARM_REDUCTION_PRE_MAT_SESSIONS_HEALTH_EDUCATION = "harm_reduction_pre_mat_sessions_health_education";
        String HARM_REDUCTION_MARK_CLIENT_HAS_STARTED_MAT = "harm_reduction_mark_the_client_has_started_mat";


    }

    interface TABLES {
        String HARM_REDUCTION_RISK_ASSESSMENT = "ec_harm_reduction_risk_assessment";
        String HARM_REDUCTION_FOLLOWUP_VISIT = "ec_harm_reduction_followup_visit";
    }

    interface ACTIVITY_PAYLOAD {
        String BASE_ENTITY_ID = "BASE_ENTITY_ID";
        String FAMILY_BASE_ENTITY_ID = "FAMILY_BASE_ENTITY_ID";
        String HARM_REDUCTION_FORM_NAME = "HARM_REDUCTION_FORM_NAME";
        String MEMBER_PROFILE_OBJECT = "MemberObject";
        String EDIT_MODE = "editMode";
        String PROFILE_TYPE = "profile_type";

    }

    interface ACTIVITY_PAYLOAD_TYPE {
        String REGISTRATION = "REGISTRATION";
        String FOLLOW_UP_VISIT = "FOLLOW_UP_VISIT";
    }

    interface CONFIGURATION {
        String HARM_REDUCTION_ENROLLMENT = "harm_reduction_risk_assessment";
    }

    interface HARMREDUCTION_MEMBER_OBJECT {
        String MEMBER_OBJECT = "memberObject";
    }

    interface PROFILE_TYPES {
        String HARM_REDUCTION_PROFILE = "harm_reduction_profile";
    }


}
