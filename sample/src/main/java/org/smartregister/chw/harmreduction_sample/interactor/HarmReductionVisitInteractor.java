package org.smartregister.chw.harmreduction_sample.interactor;

import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction_sample.activity.EntryActivity;


public class HarmReductionVisitInteractor extends org.smartregister.chw.harmreduction.interactor.HarmReductionVisitInteractor {

    @Override
    public MemberObject getMemberClient(String memberID, String profileType) {
        return EntryActivity.getSampleMember();
    }
}
