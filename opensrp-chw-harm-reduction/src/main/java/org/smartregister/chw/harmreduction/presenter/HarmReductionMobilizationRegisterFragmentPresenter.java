package org.smartregister.chw.harmreduction.presenter;

import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.contract.HarmReductionRegisterFragmentContract;

public class HarmReductionMobilizationRegisterFragmentPresenter extends BaseHarmReductionRegisterFragmentPresenter {

    public HarmReductionMobilizationRegisterFragmentPresenter(HarmReductionRegisterFragmentContract.View view, HarmReductionRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainTable() {
        return Constants.TABLES.TBLEPROSY_MOBILIZATION;
    }

    @Override
    public String getMainCondition() {
        return " is_closed is 0 ";
    }

}
