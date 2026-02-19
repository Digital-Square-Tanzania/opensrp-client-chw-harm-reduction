package org.smartregister.chw.harmreduction.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.smartregister.chw.harmreduction.HarmReductionLibrary;
import org.smartregister.chw.harmreduction.R;
import org.smartregister.chw.harmreduction.dao.HarmReductionDao;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.domain.Visit;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.util.HarmReductionVisitsUtil;

import timber.log.Timber;

public abstract class BaseHarmReductionSoberHouseProfileActivity extends BaseHarmReductionProfileActivity {

    public static void startProfileActivity(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, BaseHarmReductionSoberHouseProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }

    @Override
    protected MemberObject getMemberObject(String baseEntityId) {
        MemberObject member = HarmReductionDao.getSoberHouseMember(baseEntityId);
        if (member != null) {
            return member;
        }

        return HarmReductionDao.getContact(baseEntityId);
    }

    @Override
    protected Visit getServiceVisit() {
        return HarmReductionLibrary.getInstance()
                .visitRepository()
                .getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.HARM_REDUCTION_SOBER_HOUSE_VISIT);
    }

    @Override
    protected void setupButtons() {
        super.setupButtons();
        textViewRecordHarmReductionVisit.setVisibility(View.GONE);
        textViewRecordSoberHouseVisit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textview_record_sober_house_visit) {
            openFollowupVisit();
            return;
        }
        super.onClick(view);
    }

    @Override
    public void refreshMedicalHistory(boolean hasHistory) {
        showProgressBar(false);
        Visit lastVisit = HarmReductionLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.HARM_REDUCTION_SOBER_HOUSE_VISIT);
        rlLastVisit.setVisibility(lastVisit != null ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            profilePresenter.saveForm(data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON));
            try {
                Visit lastVisit = HarmReductionLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.HARM_REDUCTION_SOBER_HOUSE_VISIT);
                HarmReductionVisitsUtil.manualProcessVisit(lastVisit);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
