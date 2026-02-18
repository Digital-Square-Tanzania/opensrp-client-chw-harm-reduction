package org.smartregister.chw.harmreduction.util;

import static org.smartregister.util.JsonFormUtils.VALUE;
import static org.smartregister.util.Utils.getAllSharedPreferences;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.constants.Gender;
import org.smartregister.chw.harmreduction.HarmReductionLibrary;
import org.smartregister.chw.harmreduction.R;
import org.smartregister.chw.harmreduction.contract.BaseHarmReductionCallDialogContract;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.PermissionUtils;
import org.smartregister.util.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class HarmReductionUtil {

    protected static final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    protected static final DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault());

    public static void processEvent(AllSharedPreferences allSharedPreferences, Event baseEvent) throws Exception {
        if (baseEvent != null) {
            HarmReductionJsonFormUtils.tagEvent(allSharedPreferences, baseEvent);
            JSONObject eventJson = new JSONObject(HarmReductionJsonFormUtils.gson.toJson(baseEvent));

            getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, BaseRepository.TYPE_Unprocessed);
            startClientProcessing();
        }
    }

    public static void startClientProcessing() {
        try {
            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    public static ECSyncHelper getSyncHelper() {
        return HarmReductionLibrary.getInstance().getEcSyncHelper();
    }

    public static ClientProcessorForJava getClientProcessorForJava() {
        return HarmReductionLibrary.getInstance().getClientProcessorForJava();
    }

    public static Spanned fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static boolean launchDialer(final Activity activity, final BaseHarmReductionCallDialogContract.View callView, final String phoneNumber) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // set a pending call execution request
            if (callView != null) {
                callView.setPendingCallRequest(() -> HarmReductionUtil.launchDialer(activity, callView, phoneNumber));
            }

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE);

            return false;
        } else {
            if (((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number()
                    == null) {

                Timber.i("No dial application so we launch copy to clipboard...");

                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(activity.getText(R.string.copied_phone_number), phoneNumber);
                clipboard.setPrimaryClip(clip);

                CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(activity, R.style.copy_clipboard_dialog);
                copyToClipboardDialog.setContent(phoneNumber);
                copyToClipboardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                copyToClipboardDialog.show();
                Toast.makeText(activity, activity.getText(R.string.copied_phone_number), Toast.LENGTH_SHORT).show();

            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                activity.startActivity(intent);
            }
            return true;
        }
    }

    public static void saveFormEvent(final String jsonString) throws Exception {
        AllSharedPreferences allSharedPreferences = HarmReductionLibrary.getInstance().context().allSharedPreferences();
        Event baseEvent = HarmReductionJsonFormUtils.processJsonForm(allSharedPreferences, jsonString);
        if (baseEvent != null && baseEvent.getEventType().equalsIgnoreCase(Constants.EVENT_TYPE.HARM_REDUCTION_SOBER_HOUSE_ENROLLMENT)) {
            baseEvent.addObs(new Obs().withFormSubmissionField(Constants.JSON_FORM_KEY.UIC_ID).withValue(generateUICID(baseEvent.getBaseEntityId(), jsonString))
                    .withFieldCode(Constants.JSON_FORM_KEY.UIC_ID).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));
        }
        HarmReductionUtil.processEvent(allSharedPreferences, baseEvent);
    }

    public static String generateUICID(String baseEntityId, String jsonString) throws ParseException {
        CommonPersonObjectClient client = getCommonPersonObjectClient(baseEntityId);
        if (client == null) {
            return "";
        }

        String firstName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, false);
        String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, false);
        String gender = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GENDER, false);
        String dob = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        if (StringUtils.isBlank(dob)) {
            return "";
        }

        Date inputDob = inputFormat.parse(dob);
        if (inputDob == null) {
            return "";
        }

        String dobString = df.format(inputDob);
        String birthLocation = getClientBirthRegionFromForm(jsonString);

        String uicId = "";

        // UIC ID is formed by:
        // last two letters from first and last name,
        // first three letters from birth region,
        // gender marker (male=1 else 2),
        // first two digits of birth date and last two digits of birth year.
        uicId += firstName.length() > 2 ? firstName.substring(firstName.length() - 2) : firstName;
        uicId += lastName.length() > 2 ? lastName.substring(lastName.length() - 2) : lastName;
        uicId += birthLocation.length() > 3 ? birthLocation.substring(0, 3) : birthLocation;
        uicId += gender.equalsIgnoreCase(Gender.MALE.toString()) ? 1 : 2;
        uicId += dobString.length() > 2 ? dobString.substring(0, 2) : dobString;
        uicId += dobString.length() > 2 ? dobString.substring(dobString.length() - 2) : dobString;

        if (StringUtils.isNotBlank(uicId)) {
            return uicId;
        }
        return "";
    }

    private static String getClientBirthRegionFromForm(String jsonString) {
        try {
            JSONObject form = new JSONObject(jsonString);
            JSONArray fields = HarmReductionJsonFormUtils.tbleprosyFormFields(form);
            JSONObject birthRegion = HarmReductionJsonFormUtils.getFieldJSONObject(fields, "birth_region");
            if (birthRegion != null) {
                String birthRegionValue = birthRegion.getString(VALUE);
                if (StringUtils.isNotBlank(birthRegionValue)) {
                    return birthRegionValue;
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return "";
    }

    public static CommonPersonObjectClient getCommonPersonObjectClient(@NonNull String baseEntityId) {
        CommonRepository commonRepository = HarmReductionLibrary.getInstance().context().commonrepository(Constants.TABLES.FAMILY_MEMBER_TABLE);
        CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(baseEntityId);
        if (commonPersonObject == null) {
            return null;
        }

        CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        return client;
    }

    public static int getMemberProfileImageResourceIdentifier(String entityType) {
        return R.mipmap.ic_member;
    }

    public static String getGenderTranslated(Context context, String gender) {
        if (gender.equalsIgnoreCase(Gender.MALE.toString())) {
            return context.getResources().getString(R.string.male);
        } else if (gender.equalsIgnoreCase(Gender.FEMALE.toString())) {
            return context.getResources().getString(R.string.female);
        }
        return "";
    }

    protected static Event getCloseHarmReductionEvent(String jsonString,
                                                      String baseEntityId) {

        Event closeTbLeprosyEvent = new Gson().
                fromJson(jsonString, Event.class);

        closeTbLeprosyEvent.setEntityType(Constants.TABLES.HARM_REDUCTION_RISK_ASSESSMENT);
        closeTbLeprosyEvent.setEventType(Constants.EVENT_TYPE.CLOSE_HARM_REDUCTION_SERVICE);
        closeTbLeprosyEvent.setBaseEntityId(baseEntityId);
        closeTbLeprosyEvent.setFormSubmissionId(JsonFormUtils.
                generateRandomUUIDString());
        closeTbLeprosyEvent.setEventDate(new Date());
        return closeTbLeprosyEvent;
    }

    public static void closeHarmReductionService(String baseEntityId) {
        AllSharedPreferences allSharedPreferences = HarmReductionLibrary.
                getInstance().
                context().
                allSharedPreferences();
        Event closeTbLeprosyEvent = getCloseHarmReductionEvent(new JSONObject().
                        toString(),
                baseEntityId);

        try {
            NCUtils.addEvent(allSharedPreferences, closeTbLeprosyEvent);
            NCUtils.startClientProcessing();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
