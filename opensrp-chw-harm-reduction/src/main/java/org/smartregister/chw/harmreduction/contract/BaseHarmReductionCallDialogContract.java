package org.smartregister.chw.harmreduction.contract;

import android.content.Context;

public interface BaseHarmReductionCallDialogContract {

    interface View {
        void setPendingCallRequest(Dialer dialer);

        Context getCurrentContext();
    }

    interface Dialer {
        void callMe();
    }
}
