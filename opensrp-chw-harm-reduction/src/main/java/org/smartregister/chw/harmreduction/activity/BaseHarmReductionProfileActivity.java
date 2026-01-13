package org.smartregister.chw.harmreduction.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.chw.harmreduction.HarmReductionLibrary;
import org.smartregister.chw.harmreduction.R;
import org.smartregister.chw.harmreduction.contract.HarmReductionProfileContract;
import org.smartregister.chw.harmreduction.custom_views.BaseHarmReductionFloatingMenu;
import org.smartregister.chw.harmreduction.dao.HarmReductionDao;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.domain.Visit;
import org.smartregister.chw.harmreduction.interactor.BaseHarmReductionProfileInteractor;
import org.smartregister.chw.harmreduction.presenter.BaseHarmReductionProfilePresenter;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.util.HarmReductionUtil;
import org.smartregister.chw.harmreduction.util.HarmReductionVisitsUtil;
import org.smartregister.domain.AlertStatus;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;


public abstract class BaseHarmReductionProfileActivity extends BaseProfileActivity implements HarmReductionProfileContract.View, HarmReductionProfileContract.InteractorCallBack {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
    protected MemberObject memberObject;
    protected HarmReductionProfileContract.Presenter profilePresenter;
    protected CircleImageView imageView;
    protected TextView textViewName;
    protected TextView textViewGender;
    protected TextView textViewLocation;
    protected TextView textViewUniqueID;
    protected TextView textViewRecordTbLeprosy;
    protected TextView textViewRecordSoberHouseVisit;
    protected TextView textViewContinueTbLeprosy;
    protected TextView manualProcessVisit;
    protected TextView textview_positive_date;
    protected View view_last_visit_row;
    protected View view_most_due_overdue_row;
    protected View view_family_row;
    protected View view_positive_date_row;
    protected RelativeLayout rlLastVisit;
    protected RelativeLayout rlObservationResults;
    protected RelativeLayout rlUpcomingServices;
    protected RelativeLayout rlFamilyServicesDue;
    protected RelativeLayout visitStatus;
    protected RelativeLayout visitInProgress;
    protected ImageView imageViewCross;
    protected TextView textViewUndo;
    protected RelativeLayout rlTbLeprosyPositiveDate;
    protected TextView textViewVisitDone;
    protected RelativeLayout visitDone;
    protected TextView textViewVisitDoneEdit;
    protected String profileType;
    protected BaseHarmReductionFloatingMenu baseTbLeprosyFloatingMenu;
    protected CustomFontTextView ivViewHistoryArrow;
    private TextView tvUpComingServices;
    private TextView tvFamilyStatus;
    private ProgressBar progressBar;

    public static void startProfileActivity(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, BaseHarmReductionProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }

    public abstract void openObservationResults();

    public abstract void openHarmReductionContactRegister();

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_harm_reduction_profile);
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        String baseEntityId = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID);
        profileType = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.PROFILE_TYPE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }

        toolbar.setNavigationOnClickListener(v -> BaseHarmReductionProfileActivity.this.finish());
        appBarLayout = this.findViewById(R.id.collapsing_toolbar_appbarlayout);
        if (Build.VERSION.SDK_INT >= 21) {
            appBarLayout.setOutlineProvider(null);
        }

        textViewName = findViewById(R.id.textview_name);
        textViewGender = findViewById(R.id.textview_gender);
        textViewLocation = findViewById(R.id.textview_address);
        textViewUniqueID = findViewById(R.id.textview_id);
        view_last_visit_row = findViewById(R.id.view_last_visit_row);
        view_most_due_overdue_row = findViewById(R.id.view_most_due_overdue_row);
        view_family_row = findViewById(R.id.view_family_row);
        view_positive_date_row = findViewById(R.id.view_positive_date_row);
        imageViewCross = findViewById(R.id.tick_image);
        tvUpComingServices = findViewById(R.id.textview_name_due);
        tvFamilyStatus = findViewById(R.id.textview_family_has);
        textview_positive_date = findViewById(R.id.textview_positive_date);
        rlLastVisit = findViewById(R.id.rlLastVisit);
        rlObservationResults = findViewById(R.id.rlObservationResults);
        rlUpcomingServices = findViewById(R.id.rlUpcomingServices);
        rlFamilyServicesDue = findViewById(R.id.rlFamilyServicesDue);
        rlTbLeprosyPositiveDate = findViewById(R.id.rlTbLeprosyPositiveDate);
        textViewVisitDone = findViewById(R.id.textview_visit_done);
        visitStatus = findViewById(R.id.record_visit_not_done_bar);
        visitDone = findViewById(R.id.visit_done_bar);
        visitInProgress = findViewById(R.id.record_visit_in_progress);
        progressBar = findViewById(R.id.progress_bar);
        textViewVisitDoneEdit = findViewById(R.id.textview_edit);
        textViewRecordTbLeprosy = findViewById(R.id.textview_record_harm_reduction_community_visit);
        textViewRecordSoberHouseVisit = findViewById(R.id.textview_record_sober_house_visit);
        textViewContinueTbLeprosy = findViewById(R.id.textview_continue);
        manualProcessVisit = findViewById(R.id.textview_manual_process);
        textViewUndo = findViewById(R.id.textview_undo);
        imageView = findViewById(R.id.imageview_profile);

        ivViewHistoryArrow = findViewById(R.id.ivViewHistoryArrow);


        ivViewHistoryArrow.setOnClickListener(this);
        textViewVisitDoneEdit.setOnClickListener(this);
        rlLastVisit.setOnClickListener(this);
        rlObservationResults.setOnClickListener(this);
        rlUpcomingServices.setOnClickListener(this);
        rlFamilyServicesDue.setOnClickListener(this);
        rlTbLeprosyPositiveDate.setOnClickListener(this);
        textViewRecordTbLeprosy.setOnClickListener(this);
        textViewRecordSoberHouseVisit.setOnClickListener(this);
        textViewContinueTbLeprosy.setOnClickListener(this);
        manualProcessVisit.setOnClickListener(this);
        textViewUndo.setOnClickListener(this);

        imageRenderHelper = new ImageRenderHelper(this);
        memberObject = getMemberObject(baseEntityId);
        initializePresenter();
        profilePresenter.fillProfileData(memberObject);
        setupViews();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setupViews();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        setupViews();
    }

    @Override
    protected void setupViews() {
        initializeFloatingMenu();
        setupButtons();
    }

    protected void setupButtons() {
        rlLastVisit.setVisibility(View.GONE);
    }

    protected Visit getServiceVisit() {
        return HarmReductionLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.HARM_REDUCTION_FOLLOW_UP_VISIT);
    }


    protected void processTbLeprosyService() {
        findViewById(R.id.family_tbleprosy_head).setVisibility(View.VISIBLE);
    }


    protected MemberObject getMemberObject(String baseEntityId) {
        MemberObject member = HarmReductionDao.getMember(baseEntityId);
        if (member != null) {
            return member;
        }

        return HarmReductionDao.getContact(baseEntityId);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.title_layout) {
            onBackPressed();
        } else if (id == R.id.rlLastVisit) {
            this.openMedicalHistory();
        } else if (id == R.id.rlUpcomingServices) {
            this.openUpcomingService();
        } else if (id == R.id.rlFamilyServicesDue) {
            this.openFamilyDueServices();
        } else if (id == R.id.textview_record_harm_reduction_community_visit) {
            if (textViewRecordTbLeprosy.getText().equals(getString(R.string.record_harm_reduction_community_visit))) {
                this.openRecordClientVisit();
            } else if (textViewRecordTbLeprosy.getText().equals(getString(R.string.record_mat_client_followup_visit))) {
                this.openRecordTbContactVisit();
            } else if (textViewRecordTbLeprosy.getText().equals(getString(R.string.record_harm_reduction_client_followup_visit))) {
                this.openFollowupVisit();
            } else if (textViewRecordTbLeprosy.getText().equals(getString(R.string.record_observation_results))) {
                this.openObservationResults();
            } else {
                Toast.makeText(getApplicationContext(), "No click", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.textview_continue) {
            this.continueContactVisit();
        }
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        profilePresenter = new BaseHarmReductionProfilePresenter(this, new BaseHarmReductionProfileInteractor(), memberObject);
        fetchProfileData();
        profilePresenter.refreshProfileBottom();
    }

    public void initializeFloatingMenu() {
        if (StringUtils.isNotBlank(memberObject.getPhoneNumber())) {
            baseTbLeprosyFloatingMenu = new BaseHarmReductionFloatingMenu(this, memberObject);
            baseTbLeprosyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
            LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            addContentView(baseTbLeprosyFloatingMenu, linearLayoutParams);
        }
    }


    @Override
    public void hideView() {
        //Implement later
    }

    @Override
    public void openFollowupVisit() {
        //Implement in application
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void setProfileViewWithData() {
        int age = new Period(new DateTime(memberObject.getAge()), new DateTime()).getYears();
        textViewName.setText(String.format("%s %s %s, %d", memberObject.getFirstName(), memberObject.getMiddleName(), memberObject.getLastName(), age));
        textViewGender.setText(HarmReductionUtil.getGenderTranslated(this, memberObject.getGender()));
        textViewLocation.setText(memberObject.getAddress());
        textViewUniqueID.setText(memberObject.getUniqueId());

        if (StringUtils.isNotBlank(memberObject.getPrimaryCareGiver()) && memberObject.getPrimaryCareGiver().equals(memberObject.getBaseEntityId())) {
            findViewById(R.id.primary_tbleprosy_caregiver).setVisibility(View.GONE);
        }

        if (memberObject.getTbLeprosyTestDate() != null) {
            textview_positive_date.setText(getString(R.string.harm_reduction_positive) + " " + formatTime(memberObject.getTbLeprosyTestDate()));
        }
    }

    @Override
    public void setOverDueColor() {
        textViewRecordTbLeprosy.setBackground(getResources().getDrawable(R.drawable.record_btn_selector_overdue));

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        //fetch profile data
    }

    @Override
    public void showProgressBar(boolean status) {
        progressBar.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void refreshMedicalHistory(boolean hasHistory) {
        showProgressBar(false);
        Visit lastVisit = HarmReductionLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.HARM_REDUCTION_FOLLOW_UP_VISIT);
        rlLastVisit.setVisibility(lastVisit != null ? View.VISIBLE : View.GONE);
    }

    @Override
    public void refreshFamilyStatus(AlertStatus status) {
        showProgressBar(false);
        if (status == AlertStatus.complete) {
            setFamilyStatus(getString(R.string.family_has_nothing_due));
        } else if (status == AlertStatus.normal) {
            setFamilyStatus(getString(R.string.family_has_services_due));
        } else if (status == AlertStatus.urgent) {
            tvFamilyStatus.setText(HarmReductionUtil.fromHtml(getString(R.string.family_has_service_overdue)));
        }
    }

    private void setFamilyStatus(String familyStatus) {
        view_family_row.setVisibility(View.VISIBLE);
        rlFamilyServicesDue.setVisibility(View.GONE);
        tvFamilyStatus.setText(familyStatus);
    }

    @Override
    public void openMedicalHistory() {
        //implementation here

    }

    @Override
    public void openUpcomingService() {
        //implement
    }

    @Override
    public void openFamilyDueServices() {
        //implement
    }

    @Nullable
    private String formatTime(Date dateTime) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            return formatter.format(dateTime);
        } catch (Exception e) {
            Timber.d(e);
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            profilePresenter.saveForm(data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON));
            try {
                Visit lastVisit = HarmReductionLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.HARM_REDUCTION_FOLLOW_UP_VISIT);
                HarmReductionVisitsUtil.manualProcessVisit(lastVisit);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    protected boolean isVisitOnProgress(Visit visit) {
        return visit != null && !visit.getProcessed();
    }
}
