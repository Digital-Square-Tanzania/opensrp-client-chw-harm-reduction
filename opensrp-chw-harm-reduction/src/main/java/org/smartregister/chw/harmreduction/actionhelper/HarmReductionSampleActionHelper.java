package org.smartregister.chw.harmreduction.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.domain.VisitDetail;
import org.smartregister.chw.harmreduction.model.BaseHarmReductionVisitAction;
import org.smartregister.chw.harmreduction.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HarmReductionSampleActionHelper implements BaseHarmReductionVisitAction.HarmReductionVisitActionHelper {
    protected String jsonPayload;

    protected String sampleCollection;
    protected String observation;

    protected String baseEntityId;

    protected Context context;

    protected MemberObject memberObject;
    private HarmReductionInvestigationActionHelper tbInvestigationActionHelper;


    public HarmReductionSampleActionHelper(Context context, MemberObject memberObject, HarmReductionInvestigationActionHelper tbInvestigationActionHelper) {
        this.context = context;
        this.memberObject = memberObject;
        this.tbInvestigationActionHelper = tbInvestigationActionHelper;
        this.observation = tbInvestigationActionHelper != null ? tbInvestigationActionHelper.getScreeningStatus() : null;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONObject global = jsonObject.getJSONObject("global");
            global.put("observation", getCurrentScreeningStatus());
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
            sampleCollection = JsonFormUtils.getValue(jsonObject, "has_sample_been_collected");


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


        if(StringUtils.isNotBlank(sampleCollection)){
            return BaseHarmReductionVisitAction.Status.COMPLETED;
        }
        return BaseHarmReductionVisitAction.Status.PENDING;

    }

    @Override
    public void onPayloadReceived(BaseHarmReductionVisitAction baseTbLeprosyVisitAction) {
        Timber.v("onPayloadReceived");
    }

    public boolean isEligibleForSampleCollection() {
        return tbInvestigationActionHelper != null && tbInvestigationActionHelper.isTbPresumptive();
    }

    private String getCurrentScreeningStatus() {
        if (tbInvestigationActionHelper != null) {
            String status = tbInvestigationActionHelper.getScreeningStatus();
            if (StringUtils.isNotBlank(status)) {
                return status;
            }
        }
        return StringUtils.defaultString(observation);
    }

    public void setTbInvestigationActionHelper(HarmReductionInvestigationActionHelper tbInvestigationActionHelper) {
        this.tbInvestigationActionHelper = tbInvestigationActionHelper;
        this.observation = tbInvestigationActionHelper != null ? tbInvestigationActionHelper.getScreeningStatus() : null;
    }
}
