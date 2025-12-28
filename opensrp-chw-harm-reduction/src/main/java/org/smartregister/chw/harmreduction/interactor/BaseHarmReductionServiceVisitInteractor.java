package org.smartregister.chw.harmreduction.interactor;


import android.content.Context;

import androidx.annotation.VisibleForTesting;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.harmreduction.R;
import org.smartregister.chw.harmreduction.HarmReductionLibrary;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionInvestigationActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSampleActionHelper;
import org.smartregister.chw.harmreduction.actionhelper.HarmReductionSourceActionHelper;
import org.smartregister.chw.harmreduction.contract.BaseHarmReductionVisitContract;
import org.smartregister.chw.harmreduction.domain.VisitDetail;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;
import org.smartregister.chw.harmreduction.util.AppExecutors;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class BaseHarmReductionServiceVisitInteractor extends BaseHarmReductionVisitInteractor {

    protected BaseHarmReductionVisitContract.InteractorCallBack callBack;

    String visitType;
    private final LinkedHashMap<String, BaseHarmReductionVisitAction> actionList;
    protected AppExecutors appExecutors;
    private ECSyncHelper syncHelper;
    private Context mContext;
    private HarmReductionInvestigationActionHelper contactTbInvestigationHelper;


    @VisibleForTesting
    public BaseHarmReductionServiceVisitInteractor(AppExecutors appExecutors, HarmReductionLibrary HarmReductionLibrary, ECSyncHelper syncHelper) {
        this.appExecutors = appExecutors;
        this.mContext = HarmReductionLibrary.getInstance().context().applicationContext();
        this.syncHelper = syncHelper;
        this.actionList = new LinkedHashMap<>();
    }

    public BaseHarmReductionServiceVisitInteractor(String visitType) {
        this(new AppExecutors(), HarmReductionLibrary.getInstance(), HarmReductionLibrary.getInstance().getEcSyncHelper());
        this.visitType = visitType;
    }

    @Override
    protected String getCurrentVisitType() {
        if (StringUtils.isNotBlank(visitType)) {
            return visitType;
        }
        return super.getCurrentVisitType();
    }

    /**
     * this method is used to list all actions
     * @param callBack
     */
    @Override
    protected void populateActionList(BaseHarmReductionVisitContract.InteractorCallBack callBack) {
        this.callBack = callBack;
        final Runnable runnable = () -> {
            try {
                evaluateTbLeprosySource(details);
                evaluateContactTbInvestigation(details);
                evaluateContactLeprosyInvestigation(details);
                evaluateTbLeprosySample(details);

            } catch (BaseHarmReductionVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    /**
     * this action deals Type of Contact (Njia ya kuchunguza Kifua Kikuu)
     * @param details
     * @throws BaseHarmReductionVisitAction.ValidationException
     */
    private void evaluateTbLeprosySource(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {

        HarmReductionSourceActionHelper actionHelper = new HarmReductionSourceActionHelper(mContext, memberObject);
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.tbleprosy_source))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HarmReduction_FOLLOWUP_FORMS.TBLEPROSY_INDEX_CLIENT_DETAILS_SOURCE)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_source), action);

    }

    /**
     * this action deals with TB Investigation (Uchunguzi wa Kifua Kikuu)
     * @param details
     * @throws BaseHarmReductionVisitAction.ValidationException
     */
    private void evaluateContactTbInvestigation(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {

        HarmReductionInvestigationActionHelper actionHelper = new HarmReductionInvestigationActionHelper(mContext, memberObject);
        contactTbInvestigationHelper = actionHelper;
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.tbleprosy_contact_tb_investigation))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(getSourceContactTypeValidator("tb"))
                .withFormName(Constants.HarmReduction_FOLLOWUP_FORMS.TBLEPROSY_CONTACT_TB_INVESTIGATION)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_contact_tb_investigation), action);
    }

    /**
     * this action deals with Leprosy Investigation (Uchunguzi wa Ukoma)
     * @param details
     * @throws BaseHarmReductionVisitAction.ValidationException
     */
    private void evaluateContactLeprosyInvestigation(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {

        HarmReductionInvestigationActionHelper actionHelper = new HarmReductionInvestigationActionHelper(mContext, memberObject);
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.tbleprosy_contact_leprosy_investigation))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(getSourceContactTypeValidator("leprosy"))
                .withFormName(Constants.HarmReduction_FOLLOWUP_FORMS.TBLEPROSY_CONTACT_LEPROSY_INVESTIGATION)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_contact_leprosy_investigation), action);
    }

    /**
     * this action deals with sample collection (kuchukua sampuli)
     * @param details
     * @throws BaseHarmReductionVisitAction.ValidationException
     */
    private void evaluateTbLeprosySample(Map<String, List<VisitDetail>> details) throws BaseHarmReductionVisitAction.ValidationException {

        HarmReductionSampleActionHelper actionHelper = new HarmReductionSampleActionHelper(mContext, memberObject, contactTbInvestigationHelper);
        BaseHarmReductionVisitAction action = getBuilder(context.getString(R.string.tbleprosy_sample))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withValidator(getSampleEligibilityValidator(actionHelper))
                .withFormName(Constants.HarmReduction_FOLLOWUP_FORMS.TBLEPROSY_SAMPLE)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_sample), action);
    }

    private BaseHarmReductionVisitAction.Validator getSourceContactTypeValidator(final String requiredType) {
        return new BaseHarmReductionVisitAction.Validator() {
            @Override
            public boolean isValid(String key) {
                return isSourceContactTypeSelected(requiredType);
            }

            @Override
            public boolean isEnabled(String key) {
                return isValid(key);
            }

            @Override
            public void onChanged(String key) {
                // UI refresh handles visibility changes when data updates
            }
        };
    }

    private boolean isSourceContactTypeSelected(String requiredType) {
        if (StringUtils.isBlank(requiredType) || context == null) {
            return false;
        }

        BaseHarmReductionVisitAction sourceAction = actionList.get(context.getString(R.string.tbleprosy_source));
        if (sourceAction == null) {
            return false;
        }

        String payload = sourceAction.getJsonPayload();
        if (StringUtils.isBlank(payload)) {
            return false;
        }

        try {
            JSONObject jsonObject = new JSONObject(payload);
            Set<String> contactTypes = extractContactTypes(jsonObject);

            String normalizedRequiredType = normalizeContactType(requiredType);
            if (StringUtils.isBlank(normalizedRequiredType)) {
                normalizedRequiredType = requiredType.toLowerCase(Locale.ENGLISH);
            }

            return contactTypes.contains(normalizedRequiredType);
        } catch (Exception e) {
            Timber.e(e);
        }

        return false;
    }

    private Set<String> extractContactTypes(JSONObject jsonObject) {
        Set<String> normalizedTypes = new HashSet<>();
        if (jsonObject == null) {
            return normalizedTypes;
        }

        String[] candidateKeys = new String[]{
                "index_case_condition_types",
                "relationship_to_index_client"
        };

        for (String key : candidateKeys) {
            List<String> selections = extractSelections(jsonObject, key);
            for (String selection : selections) {
                if (StringUtils.isBlank(selection)) {
                    continue;
                }

                String normalizedSelection = selection.trim().toLowerCase(Locale.ENGLISH);
                normalizedTypes.add(normalizedSelection);

                String normalizedContactType = normalizeContactType(selection);
                if (StringUtils.isNotBlank(normalizedContactType)) {
                    normalizedTypes.add(normalizedContactType);
                }
            }
        }

        return normalizedTypes;
    }

    private String normalizeContactType(String rawValue) {
        if (StringUtils.isBlank(rawValue)) {
            return null;
        }

        String normalized = rawValue.trim().toLowerCase(Locale.ENGLISH);
        switch (normalized) {
            case "tb":
            case "high_tb_burden_area":
                return "tb";
            case "leprosy":
                return "leprosy";
            default:
                return null;
        }
    }

    private List<String> extractSelections(JSONObject jsonObject, String key) {
        List<String> selections = new ArrayList<>();
        if (jsonObject == null || StringUtils.isBlank(key)) {
            return selections;
        }

        try {
            JSONObject fieldObject = FormUtils.getFieldFromForm(jsonObject, key);
            if (fieldObject == null || !fieldObject.has(JsonFormConstants.VALUE)) {
                return selections;
            }

            Object rawValue = fieldObject.get(JsonFormConstants.VALUE);
            if (rawValue instanceof JSONArray) {
                JSONArray array = (JSONArray) rawValue;
                for (int i = 0; i < array.length(); i++) {
                    String value = array.optString(i);
                    if (StringUtils.isNotBlank(value)) {
                        selections.add(value.trim());
                    }
                }
            } else if (rawValue instanceof String) {
                String value = (String) rawValue;
                if (StringUtils.isNotBlank(value)) {
                    String[] tokens = value.split("[,\\s]+");
                    for (String token : tokens) {
                        if (StringUtils.isNotBlank(token)) {
                            selections.add(token.trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return selections;
    }

    private BaseHarmReductionVisitAction.Validator getSampleEligibilityValidator(final HarmReductionSampleActionHelper sampleActionHelper) {
        return new BaseHarmReductionVisitAction.Validator() {
            @Override
            public boolean isValid(String key) {
                return sampleActionHelper != null
                        && isSourceContactTypeSelected("tb")
                        && sampleActionHelper.isEligibleForSampleCollection();
            }

            @Override
            public boolean isEnabled(String key) {
                return isValid(key);
            }

            @Override
            public void onChanged(String key) {
                // No-op
            }
        };
    }

    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.TBLEPROSY_CONTACT_VISIT;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.TBLEPROSY_SERVICES;
    }




}
