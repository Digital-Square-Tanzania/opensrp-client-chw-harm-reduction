package org.smartregister.chw.harmreduction_sample.interactor;

import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.interactor.BaseHarmReductionVisitInteractor;
import org.smartregister.chw.harmreduction_sample.activity.EntryActivity;


public class HarmReductionServiceVisitInteractor extends BaseHarmReductionVisitInteractor {
    public HarmReductionServiceVisitInteractor(String visitType) {
        super(visitType);
    }

    @Override
    public MemberObject getMemberClient(String memberID, String profileType) {
        return EntryActivity.getSampleMember();
    }
}
