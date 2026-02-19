package org.smartregister.chw.harmreduction.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.chw.harmreduction.sync.intent.CloseSoberHouseMembershipIntentService;
import org.smartregister.job.BaseJob;

public class CloseSoberHouseMemberServiceJob extends BaseJob {

    public static final String TAG = "CloseSoberHouseMemberServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), CloseSoberHouseMembershipIntentService.class);
        getApplicationContext().startService(intent);
        return params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
