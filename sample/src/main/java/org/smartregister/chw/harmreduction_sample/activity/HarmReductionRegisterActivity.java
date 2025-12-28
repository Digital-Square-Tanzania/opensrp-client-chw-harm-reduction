package org.smartregister.chw.harmreduction_sample.activity;

import android.view.MenuItem;

import org.smartregister.chw.harmreduction.activity.BaseHarmReductionRegisterActivity;

public class HarmReductionRegisterActivity extends BaseHarmReductionRegisterActivity {

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        if(bottomNavigationView != null){
            MenuItem clients = bottomNavigationView.getMenu().findItem(org.smartregister.R.id.action_clients);
            if (clients != null) {
                clients.setTitle("");
            }

            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_search);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_library);
        }
    }

}