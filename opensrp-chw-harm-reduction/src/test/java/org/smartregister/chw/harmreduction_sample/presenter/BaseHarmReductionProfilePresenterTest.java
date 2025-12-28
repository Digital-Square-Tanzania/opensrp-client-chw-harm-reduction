package org.smartregister.chw.harmreduction_sample.presenter;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.chw.harmreduction.contract.HarmReductionProfileContract;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.presenter.BaseHarmReductionProfilePresenter;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class BaseHarmReductionProfilePresenterTest {

    @Mock
    private HarmReductionProfileContract.View view = Mockito.mock(HarmReductionProfileContract.View.class);

    @Mock
    private HarmReductionProfileContract.Interactor interactor = Mockito.mock(HarmReductionProfileContract.Interactor.class);

    @Mock
    private MemberObject tbleprosyMemberObject = new MemberObject();

    private BaseHarmReductionProfilePresenter profilePresenter = new BaseHarmReductionProfilePresenter(view, interactor, tbleprosyMemberObject);


    @Test
    public void fillProfileDataCallsSetProfileViewWithDataWhenPassedMemberObject() {
        profilePresenter.fillProfileData(tbleprosyMemberObject);
        verify(view).setProfileViewWithData();
    }

    @Test
    public void fillProfileDataDoesntCallsSetProfileViewWithDataIfMemberObjectEmpty() {
        profilePresenter.fillProfileData(null);
        verify(view, never()).setProfileViewWithData();
    }

    @Test
    public void malariaTestDatePeriodIsLessThanSeven() {
        profilePresenter.recordTbLeprosyButton("");
        verify(view).hideView();
    }

    @Test
    public void malariaTestDatePeriodIsMoreThanFourteen() {
        profilePresenter.recordTbLeprosyButton("EXPIRED");
        verify(view).hideView();
    }

    @Test
    public void refreshProfileBottom() {
        profilePresenter.refreshProfileBottom();
        verify(interactor).refreshProfileInfo(tbleprosyMemberObject, profilePresenter.getView());
    }

    @Test
    public void saveForm() {
        profilePresenter.saveForm(null);
        verify(interactor).saveRegistration(null, view);
    }
}
