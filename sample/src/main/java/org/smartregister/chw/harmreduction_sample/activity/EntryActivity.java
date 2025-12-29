package org.smartregister.chw.harmreduction_sample.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.factory.FileSourceFactoryHelper;

import org.json.JSONArray;

import org.json.JSONObject;
import org.smartregister.chw.harmreduction.contract.BaseHarmReductionVisitContract;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.util.JsonFormUtils;
import org.smartregister.chw.harmreduction_sample.R;
import org.smartregister.view.activity.SecuredActivity;

import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

public class EntryActivity extends SecuredActivity implements View.OnClickListener, BaseHarmReductionVisitContract.VisitView {
    private static MemberObject tbleprosyMemberObject;

    public static MemberObject getSampleMember() {
        if (tbleprosyMemberObject == null) {
            tbleprosyMemberObject = new MemberObject();
            tbleprosyMemberObject.setFirstName("Glory");
            tbleprosyMemberObject.setLastName("Juma");
            tbleprosyMemberObject.setMiddleName("Ali");
            tbleprosyMemberObject.setGender("Female");
            tbleprosyMemberObject.setMartialStatus("Married");
            tbleprosyMemberObject.setAddress("Morogoro");
            tbleprosyMemberObject.setDob("1982-01-18T03:00:00.000+03:00");
            tbleprosyMemberObject.setUniqueId("3503504");
            tbleprosyMemberObject.setBaseEntityId("3503504");
            tbleprosyMemberObject.setFamilyBaseEntityId("3503504");
        }

        return tbleprosyMemberObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale("sw");
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.tbleprosy_profile).setOnClickListener(this);
        findViewById(R.id.harm_reduction_community_visit).setOnClickListener(this);
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @Override
    protected void onCreation() {
        Timber.v("onCreation");
    }

    @Override
    protected void onResumption() {
        Timber.v("onCreation");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tbleprosy_profile:
                startActivity(new Intent(this, HarmReductionRegisterActivity.class));
                break;
            case R.id.harm_reduction_community_visit:
                HarmReductionVisitActivity.startTbLeprosyVisitActivity(this, "98765", false);
                break;
            default:
                break;
        }
    }

    @SuppressLint("TimberArgCount")
    private void startForm(String formName) throws Exception {
        JSONObject jsonForm = FileSourceFactoryHelper.getFileSource("").getFormFromFile(getApplicationContext(), formName);

        String currentLocationId = "Tanzania";
        if (jsonForm != null) {

            JSONArray dataFields = jsonForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
            JSONObject clientID = JsonFormUtils.getFieldJSONObject(dataFields, "tb_client_number");
            JSONObject clientIdUkoma = JsonFormUtils.getFieldJSONObject(dataFields, "leprosy_client_number");

            if (clientID != null) {
                clientID.put("mask", "##-##-##-######-#/KK/" + Calendar.getInstance().get(Calendar.YEAR) + "/#");
            }

            if (clientIdUkoma != null) {
                clientIdUkoma.put("mask", "##-##-##-######-#/UK/" + Calendar.getInstance().get(Calendar.YEAR) + "/#");
            }

            jsonForm.getJSONObject("metadata").put("encounter_location", currentLocationId);
            Intent intent = new Intent(this, JsonWizardFormActivity.class);
            intent.putExtra("json", jsonForm.toString());

            Form form = new Form();
            form.setWizard(true);
            form.setNextLabel("Next");
            form.setPreviousLabel("Previous");
            form.setSaveLabel("Save");
            form.setHideSaveLabel(true);

            intent.putExtra("form", form);
            startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
        }
    }


    @Override
    public void onDialogOptionUpdated(String jsonString) {
        Timber.v("onDialogOptionUpdated %s", jsonString);
    }

    @Override
    public Context getMyContext() {
        return this;
    }
}
