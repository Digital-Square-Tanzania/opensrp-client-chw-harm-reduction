package org.smartregister.chw.harmreduction.presenter;

import org.smartregister.chw.harmreduction.contract.BaseHarmReductionVisitContract;
import org.smartregister.chw.harmreduction.domain.MemberObject;

public class HarmReductionSoberHouseVisitPresenter extends BaseHarmReductionVisitPresenter {

    public HarmReductionSoberHouseVisitPresenter(MemberObject memberObject, BaseHarmReductionVisitContract.View view,
                                                 BaseHarmReductionVisitContract.Interactor interactor) {
        super(memberObject, view, interactor);
    }
}
