package org.smartregister.chw.harmreduction.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.harmreduction.contract.HarmReductionProfileContract;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.util.AppExecutors;
import org.smartregister.chw.harmreduction.util.HarmReductionUtil;
import org.smartregister.domain.AlertStatus;

public class BaseHarmReductionProfileInteractor implements HarmReductionProfileContract.Interactor {
    protected AppExecutors appExecutors;

    @VisibleForTesting
    BaseHarmReductionProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseHarmReductionProfileInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void refreshProfileInfo(MemberObject memberObject, HarmReductionProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshFamilyStatus(AlertStatus.normal);
            callback.refreshMedicalHistory(true);
        });
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final String jsonString, final HarmReductionProfileContract.InteractorCallBack callback) {

        Runnable runnable = () -> {
            try {
                HarmReductionUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        appExecutors.diskIO().execute(runnable);
    }
}
