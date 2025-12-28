package org.smartregister.chw.harmreduction.presenter;

import android.content.Context;

import androidx.annotation.Nullable;

import org.smartregister.chw.harmreduction.contract.HarmReductionProfileContract;
import org.smartregister.chw.harmreduction.domain.MemberObject;

import java.lang.ref.WeakReference;

import timber.log.Timber;


public class BaseHarmReductionProfilePresenter implements HarmReductionProfileContract.Presenter {
    protected WeakReference<HarmReductionProfileContract.View> view;
    protected MemberObject memberObject;
    protected HarmReductionProfileContract.Interactor interactor;
    protected Context context;

    public BaseHarmReductionProfilePresenter(HarmReductionProfileContract.View view, HarmReductionProfileContract.Interactor interactor, MemberObject memberObject) {
        this.view = new WeakReference<>(view);
        this.memberObject = memberObject;
        this.interactor = interactor;
    }

    @Override
    public void fillProfileData(MemberObject memberObject) {
        if (memberObject != null && getView() != null) {
            getView().setProfileViewWithData();
        }
    }

    @Override
    public void recordTbLeprosyButton(@Nullable String visitState) {
        if (getView() == null) {
            return;
        }

        if (("OVERDUE").equals(visitState) || ("DUE").equals(visitState)) {
            if (("OVERDUE").equals(visitState)) {
                getView().setOverDueColor();
            }
        } else {
            getView().hideView();
        }
    }

    @Override
    @Nullable
    public HarmReductionProfileContract.View getView() {
        if (view != null && view.get() != null)
            return view.get();

        return null;
    }

    @Override
    public void refreshProfileBottom() {
        interactor.refreshProfileInfo(memberObject, getView());
    }

    @Override
    public void saveForm(String jsonString) {
        try {
            interactor.saveRegistration(jsonString, getView());
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
