package org.smartregister.chw.harmreduction.sync.intent;

import android.app.IntentService;
import android.content.Intent;

import org.smartregister.chw.harmreduction.dao.HarmReductionDao;

import timber.log.Timber;

public class CloseSoberHouseMembershipIntentService extends IntentService {

    private static final String TAG = CloseSoberHouseMembershipIntentService.class.getSimpleName();

    public CloseSoberHouseMembershipIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            HarmReductionDao.SoberHouseAutoCloseSummary summary = HarmReductionDao.autoCloseSoberHouseRecordsAfterRecoveryCapitalPass();
            if (summary != null) {
                Timber.i(
                        "Sober house auto-close completed. affectedClients=%d, serviceRowsUpdated=%d, enrollmentRowsUpdated=%d",
                        summary.getAffectedClients(),
                        summary.getServiceRowsUpdated(),
                        summary.getEnrollmentRowsUpdated()
                );
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
