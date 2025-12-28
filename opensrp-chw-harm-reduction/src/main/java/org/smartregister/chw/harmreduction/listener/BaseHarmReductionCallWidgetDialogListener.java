package org.smartregister.chw.harmreduction.listener;


import android.view.View;

import org.smartregister.chw.harmreduction.R;
import org.smartregister.chw.harmreduction.fragment.BaseHarmReductionCallDialogFragment;

public class BaseHarmReductionCallWidgetDialogListener implements View.OnClickListener {

    private BaseHarmReductionCallDialogFragment callDialogFragment;

    public BaseHarmReductionCallWidgetDialogListener(BaseHarmReductionCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tbleprosy_call_close) {
            callDialogFragment.dismiss();
        }
    }
}
