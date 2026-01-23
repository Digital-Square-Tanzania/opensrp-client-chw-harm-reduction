package org.smartregister.chw.harmreduction.presenter;

import org.smartregister.chw.harmreduction.contract.HarmReductionRegisterFragmentContract;
import org.smartregister.chw.harmreduction.util.Constants;

public class HarmReductionUsedNeedlesAndSyringesCollectionRegisterFragmentPresenter extends BaseHarmReductionRegisterFragmentPresenter {

    public HarmReductionUsedNeedlesAndSyringesCollectionRegisterFragmentPresenter(HarmReductionRegisterFragmentContract.View view, HarmReductionRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainTable() {
        return Constants.TABLES.HARM_REDUCTION_SAFETY_BOX_COLLECTION;
    }

    @Override
    public String getMainCondition() {
        return " is_closed is 0 ";
    }
}
