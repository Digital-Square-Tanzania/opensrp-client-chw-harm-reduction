package org.smartregister.chw.harmreduction.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.harmreduction.contract.HarmReductionRegisterContract;
import org.smartregister.chw.harmreduction.util.AppExecutors;
import org.smartregister.chw.harmreduction.util.HarmReductionUtil;

public class BaseHarmReductionRegisterInteractor implements HarmReductionRegisterContract.Interactor {

    private final AppExecutors appExecutors;

    @VisibleForTesting
    BaseHarmReductionRegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseHarmReductionRegisterInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void saveRegistration(final String jsonString, final HarmReductionRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            try {
                HarmReductionUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.onRegistrationSaved());
        };
        appExecutors.diskIO().execute(runnable);
    }
}
