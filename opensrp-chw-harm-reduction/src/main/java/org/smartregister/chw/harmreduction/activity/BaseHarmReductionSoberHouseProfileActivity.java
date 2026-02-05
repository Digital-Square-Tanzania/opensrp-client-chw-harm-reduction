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
            HarmReductionSoberHouseVisitActivity.startHarmReductionSoberHouseVisitActivity(this, memberObject.getBaseEntityId(), false);
            return;
        }
        super.onClick(view);
    }
}
