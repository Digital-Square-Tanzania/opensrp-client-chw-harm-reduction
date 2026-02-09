package org.smartregister.chw.harmreduction.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.harmreduction.interactor.BaseHarmReductionSoberHouseVisitInteractor;
import org.smartregister.chw.harmreduction.presenter.BaseHarmReductionSoberHouseVisitPresenter;
import org.smartregister.chw.harmreduction.util.Constants;

public class BaseHarmReductionSoberHouseVisitActivity extends BaseVisitActivity {

    public static void startHarmReductionSoberHouseVisitActivity(Activity activity, String baseEntityId, Boolean editMode) {
        Intent intent = new Intent(activity, BaseHarmReductionSoberHouseVisitActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.EDIT_MODE, editMode);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.PROFILE_TYPE, Constants.PROFILE_TYPES.HARM_REDUCTION_PROFILE);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void registerPresenter() {
        presenter = new BaseHarmReductionSoberHouseVisitPresenter(memberObject, this, new BaseHarmReductionSoberHouseVisitInteractor());
    }
}
