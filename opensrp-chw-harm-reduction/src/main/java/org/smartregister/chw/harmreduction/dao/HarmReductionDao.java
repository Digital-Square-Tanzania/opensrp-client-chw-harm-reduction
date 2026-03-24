package org.smartregister.chw.harmreduction.dao;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.smartregister.chw.harmreduction.domain.MemberObject;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class HarmReductionDao extends AbstractDao {
    private static final String SOBER_HOUSE_SERVICES_TABLE = "ec_harm_reduction_sober_house_services";
    private static final String FOLLOW_UP_STATUS_COLUMN = "follow_up_status";
    private static final String CLIENT_STATUS_COLUMN = "client_status";
    private static final String CLIENT_DECEASED_STATUS = "client_deceased";
    private static final String YES_VALUE = "yes";
    private static final DateTimeFormatter SQL_DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter[] SUPPORTED_EVENT_DATE_FORMATS = new DateTimeFormatter[]{
            ISODateTimeFormat.dateTimeParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S"),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormat.forPattern("yyyy-MM-dd")
    };

    private static final SimpleDateFormat df = new SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
    );

    public static class SoberHouseAutoCloseSummary {
        private final int affectedClients;
        private final int serviceRowsUpdated;
        private final int enrollmentRowsUpdated;

        public SoberHouseAutoCloseSummary(int affectedClients, int serviceRowsUpdated, int enrollmentRowsUpdated) {
            this.affectedClients = affectedClients;
            this.serviceRowsUpdated = serviceRowsUpdated;
            this.enrollmentRowsUpdated = enrollmentRowsUpdated;
        }

        public int getAffectedClients() {
            return affectedClients;
        }

        public int getServiceRowsUpdated() {
            return serviceRowsUpdated;
        }

        public int getEnrollmentRowsUpdated() {
            return enrollmentRowsUpdated;
        }
    }

    @VisibleForTesting
    static class SoberHouseClosureTrigger {
        private final String entityId;
        private final String eventDate;

        SoberHouseClosureTrigger(String entityId, String eventDate) {
            this.entityId = entityId;
            this.eventDate = eventDate;
        }
    }

    private static final DataMap<MemberObject> memberObjectMap = cursor -> {

        MemberObject memberObject = new MemberObject();

        memberObject.setFirstName(getCursorValue(cursor, "first_name", ""));
        memberObject.setMiddleName(getCursorValue(cursor, "middle_name", ""));
        memberObject.setLastName(getCursorValue(cursor, "last_name", ""));
        memberObject.setAddress(getCursorValue(cursor, "village_town"));
        memberObject.setGender(getCursorValue(cursor, "gender"));
        memberObject.setMartialStatus(getCursorValue(cursor, "marital_status"));
        memberObject.setUniqueId(getCursorValue(cursor, "unique_id", ""));
        memberObject.setDob(getCursorValue(cursor, "dob"));
        memberObject.setFamilyBaseEntityId(getCursorValue(cursor, "family_base_entity_id", ""));
        memberObject.setRelationalId(getCursorValue(cursor, "relational_id", ""));
        memberObject.setPrimaryCareGiver(getCursorValue(cursor, "primary_caregiver"));
        memberObject.setFamilyName(getCursorValue(cursor, "family_name", ""));
        memberObject.setPhoneNumber(getCursorValue(cursor, "phone_number", ""));
        memberObject.setTbLeprosyTestDate(getCursorValueAsDate(cursor, "tbleprosy_test_date", df));
        memberObject.setBaseEntityId(getCursorValue(cursor, "base_entity_id", ""));
        memberObject.setFamilyHead(getCursorValue(cursor, "family_head", ""));
        memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "pcg_phone_number", ""));
        memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "family_head_phone_number", ""));
        memberObject.setEnrollmentDate(getCursorValue(cursor,
                "enrollment_date",
                ""));

        String familyHeadName = getCursorValue(cursor, "family_head_first_name", "") + " "
                + getCursorValue(cursor, "family_head_middle_name", "");

        familyHeadName =
                (familyHeadName.trim() + " " + getCursorValue(cursor, "family_head_last_name", "")).trim();
        memberObject.setFamilyHeadName(familyHeadName);

        String familyPcgName = getCursorValue(cursor, "pcg_first_name", "") + " "
                + getCursorValue(cursor, "pcg_middle_name", "");

        familyPcgName =
                (familyPcgName.trim() + " " + getCursorValue(cursor, "pcg_last_name", "")).trim();
        memberObject.setPrimaryCareGiverName(familyPcgName);

        return memberObject;
    };

    public static String getRegistrationStatus(String baseEntityId) {
        String status = getClientStatusFromTable(Constants.TABLES.HARM_REDUCTION_RISK_ASSESSMENT, baseEntityId);
        return StringUtils.defaultString(status);
    }

    private static String getClientStatusFromTable(String tableName, String baseEntityId) {
        String sql = "SELECT status FROM " + tableName + " WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "status");
        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty()) {
            return res.get(0);
        }
        return null;
    }

    public static boolean hasHarmReductionVisit(String baseEntityId) {
        String sql = "SELECT count(p.entity_id) count FROM " + Constants.TABLES.HARM_REDUCTION_FOLLOWUP_VISIT + " p " +
                " WHERE p.entity_id = '" + baseEntityId + "'";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1) {
            return false;
        }
        Integer count = res.get(0);
        return count != null && count > 0;
    }

    public static String getLatestCommunityFollowUpStatus(String baseEntityId) {
        String followUpStatus = getLatestStatusFromTable(Constants.TABLES.HARM_REDUCTION_FOLLOWUP_VISIT, FOLLOW_UP_STATUS_COLUMN, baseEntityId);
        if (StringUtils.isBlank(followUpStatus)) {
            return getLatestStatusFromTable(Constants.TABLES.HARM_REDUCTION_FOLLOWUP_VISIT, CLIENT_STATUS_COLUMN, baseEntityId);
        }
        return followUpStatus;
    }

    public static String getLatestSoberHouseFollowUpStatus(String baseEntityId) {
        return getLatestStatusFromTable(SOBER_HOUSE_SERVICES_TABLE, FOLLOW_UP_STATUS_COLUMN, baseEntityId);
    }

    public static boolean isCommunityClientDeceased(String baseEntityId) {
        return isDeceasedFollowUpStatus(getLatestCommunityFollowUpStatus(baseEntityId));
    }

    public static boolean isSoberHouseClientDeceased(String baseEntityId) {
        return isDeceasedFollowUpStatus(getLatestSoberHouseFollowUpStatus(baseEntityId));
    }

    private static String getLatestStatusFromTable(String tableName, String statusColumn, String baseEntityId) {
        if (StringUtils.isBlank(baseEntityId) || StringUtils.isBlank(tableName) || StringUtils.isBlank(statusColumn)) {
            return "";
        }

        String sql = buildLatestStatusQuery(tableName, statusColumn, baseEntityId);

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, statusColumn, "");
        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty()) {
            return StringUtils.defaultString(res.get(0));
        }
        return "";
    }

    @VisibleForTesting
    static boolean isDeceasedFollowUpStatus(String followUpStatus) {
        String normalizedStatus = StringUtils.trimToEmpty(followUpStatus).toLowerCase(Locale.ENGLISH);
        return CLIENT_DECEASED_STATUS.equals(normalizedStatus);
    }

    @VisibleForTesting
    static String buildLatestStatusQuery(String tableName, String statusColumn, String baseEntityId) {
        if (StringUtils.isBlank(baseEntityId) || StringUtils.isBlank(tableName) || StringUtils.isBlank(statusColumn)) {
            return "";
        }

        return "SELECT " + statusColumn + " FROM " + tableName +
                " WHERE is_closed = 0 AND entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
    }

    public static String getEnrollmentDate(String baseEntityId) {
        String sql = "SELECT enrollment_date FROM ec_tbleprosy_screening p " +
                " WHERE p.base_entity_id = '" + baseEntityId + "' ORDER BY enrollment_date DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "enrollment_date");

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getSoberHouseEnrollmentEventDate(String baseEntityId) {
        String sql = "SELECT event_date FROM " + Constants.TABLES.HARM_REDUCTION_SOBER_HOUSE_ENROLLMENT +
                " WHERE base_entity_id = '" + baseEntityId + "' ORDER BY event_date DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "event_date");
        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static SoberHouseAutoCloseSummary autoCloseSoberHouseRecordsAfterRecoveryCapitalPass() {
        List<SoberHouseClosureTrigger> triggers = loadRecoveryCapitalPassTriggers();
        Map<String, DateTime> closeThresholdByClient = findEarliestCloseThresholdByClient(triggers);

        int affectedClients = 0;
        int serviceRowsUpdated = 0;
        int enrollmentRowsUpdated = 0;

        for (Map.Entry<String, DateTime> entry : closeThresholdByClient.entrySet()) {
            String clientId = entry.getKey();
            DateTime closeThreshold = entry.getValue();

            int openServiceRows = countOpenServiceRowsToClose(clientId, closeThreshold);
            int openEnrollmentRows = countOpenEnrollmentRowsToClose(clientId, closeThreshold);

            if (openServiceRows > 0) {
                updateDB(buildCloseServicesSql(clientId, closeThreshold));
            }

            if (openEnrollmentRows > 0) {
                updateDB(buildCloseEnrollmentSql(clientId, closeThreshold));
            }

            if (openServiceRows > 0 || openEnrollmentRows > 0) {
                affectedClients++;
            }
            serviceRowsUpdated += openServiceRows;
            enrollmentRowsUpdated += openEnrollmentRows;
        }

        return new SoberHouseAutoCloseSummary(affectedClients, serviceRowsUpdated, enrollmentRowsUpdated);
    }

    public static String getLastInteractedWithMatConsentFollowUpVisit(String baseEntityId) {
        String sql = "SELECT last_interacted_with FROM " + Constants.TABLES.HARM_REDUCTION_FOLLOWUP_VISIT +
                " WHERE entity_id = '" + baseEntityId + "' AND " +
                Constants.FORMS.ROC_CONSENT_JOINING_MAT_SERVICES + " = 'yes' " +
                "ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "last_interacted_with");
        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return null;
    }

    public static int getVisitNumber(String baseEntityID) {
        String sql = "SELECT visit_number  FROM ec_tbleprosy_follow_up_visit WHERE entity_id='" + baseEntityID + "' ORDER BY visit_number DESC LIMIT 1";
        DataMap<Integer> map = cursor -> getCursorIntValue(cursor, "visit_number");
        List<Integer> res = readData(sql, map);

        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0) + 1;
        } else
            return 0;
    }

    public static boolean isRegisteredForTbLeprosy(String baseEntityID) {
        String sql = "SELECT count(p.base_entity_id) count FROM ec_tbleprosy_screening p " +
                "WHERE p.base_entity_id = '" + baseEntityID + "' AND p.is_closed = 0 ";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    public static Integer getTbLeprosyFamilyMembersCount(String familyBaseEntityId) {
        String sql = "SELECT count(emc.base_entity_id) count FROM ec_tbleprosy_screening emc " +
                "INNER Join ec_family_member fm on fm.base_entity_id = emc.base_entity_id " +
                "WHERE fm.relational_id = '" + familyBaseEntityId + "' AND fm.is_closed = 0 " +
                "AND emc.is_closed = 0 AND emc.tbleprosy = 1";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.isEmpty())
            return 0;
        return res.get(0);
    }

    public static MemberObject getMember(String baseEntityID) {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "f.base_entity_id as family_base_entity_id, " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.marital_status , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join " + Constants.TABLES.HARM_REDUCTION_RISK_ASSESSMENT + " mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 AND m.base_entity_id ='" + baseEntityID + "' ";
        List<MemberObject> res = readData(sql, memberObjectMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static MemberObject getSoberHouseMember(String baseEntityID) {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "f.base_entity_id as family_base_entity_id, " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.marital_status , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "sh.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join " + Constants.TABLES.HARM_REDUCTION_SOBER_HOUSE_ENROLLMENT + " sh on sh.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where sh.is_closed = 0 AND m.base_entity_id ='" + baseEntityID + "' ";
        List<MemberObject> res = readData(sql, memberObjectMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static String getMemberSex(String baseEntityID) {
        String sql = "select " +
                "gender " +
                "from ec_family_member m " +
                "where base_entity_id = '" + baseEntityID + "' ";

        DataMap<String> map = cursor -> getCursorValue(cursor, "gender");
        List<String> res = readData(sql, map);

        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        } else
            return "";

    }

    public static String getRocConsentForJoiningMatServices(String baseEntityID) {
        String sql = "select roc_consent_joining_mat_services from " + Constants.TABLES.HARM_REDUCTION_RISK_ASSESSMENT +
                " where base_entity_id = '" + baseEntityID + "' ";

        DataMap<String> map = cursor -> getCursorValue(cursor, "roc_consent_joining_mat_services");
        List<String> res = readData(sql, map);

        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        } else
            return "";

    }

    public static String getRocMatPreSession(String baseEntityID) {
        String sql = "select roc_mat_pre_session from " + Constants.TABLES.HARM_REDUCTION_RISK_ASSESSMENT +
                " where base_entity_id = '" + baseEntityID + "' ";

        DataMap<String> map = cursor -> getCursorValue(cursor, "roc_mat_pre_session");
        List<String> res = readData(sql, map);

        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        } else
            return "";

    }


    public static String getVisitDateForRocConsentForJoiningMatServices(String baseEntityID) {
        String sql = "select dateCreated from " + Constants.TABLES.HARM_REDUCTION_FOLLOWUP_VISIT +
                " where entity_id = '" + baseEntityID + "' AND roc_consent_joining_mat_services = 'yes' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> map = cursor -> getCursorValue(cursor, "dateCreated");
        List<String> res = readData(sql, map);

        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        } else
            return "";

    }

    public static MemberObject getContact(String baseEntityID) {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "f.base_entity_id as family_base_entity_id, " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.marital_status , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join ec_tbleprosy_contacts mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 AND m.base_entity_id ='" + baseEntityID + "' ";
        List<MemberObject> res = readData(sql, memberObjectMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static List<MemberObject> getMembers() {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "f.base_entity_id as family_base_entity_id, " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.marital_status , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join " + Constants.TABLES.HARM_REDUCTION_RISK_ASSESSMENT + " mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 ";

        return readData(sql, memberObjectMap);
    }

    private static List<SoberHouseClosureTrigger> loadRecoveryCapitalPassTriggers() {
        String sql = "SELECT entity_id, event_date FROM " + SOBER_HOUSE_SERVICES_TABLE +
                " WHERE LOWER(recovery_capital_passed) = '" + YES_VALUE + "'" +
                " AND entity_id IS NOT NULL AND TRIM(entity_id) <> ''" +
                " AND event_date IS NOT NULL AND TRIM(event_date) <> ''";

        DataMap<SoberHouseClosureTrigger> dataMap = cursor -> new SoberHouseClosureTrigger(
                getCursorValue(cursor, "entity_id"),
                getCursorValue(cursor, "event_date")
        );

        List<SoberHouseClosureTrigger> rows = readData(sql, dataMap);
        return rows == null ? new ArrayList<>() : rows;
    }

    @VisibleForTesting
    static Map<String, DateTime> findEarliestCloseThresholdByClient(List<SoberHouseClosureTrigger> triggers) {
        Map<String, DateTime> closeThresholdByClient = new LinkedHashMap<>();
        if (triggers == null || triggers.isEmpty()) {
            return closeThresholdByClient;
        }

        for (SoberHouseClosureTrigger trigger : triggers) {
            if (trigger == null || StringUtils.isBlank(trigger.entityId)) {
                continue;
            }

            DateTime triggerDate = parseEventDate(trigger.eventDate);
            if (triggerDate == null) {
                continue;
            }

            DateTime current = closeThresholdByClient.get(trigger.entityId);
            if (current == null || triggerDate.isBefore(current)) {
                closeThresholdByClient.put(trigger.entityId, triggerDate);
            }
        }

        return closeThresholdByClient;
    }

    @VisibleForTesting
    static DateTime parseEventDate(String eventDate) {
        if (StringUtils.isBlank(eventDate)) {
            return null;
        }

        String normalized = eventDate.trim();
        try {
            return new DateTime(Long.parseLong(normalized));
        } catch (NumberFormatException e) {
            // no-op
        }

        for (DateTimeFormatter formatter : SUPPORTED_EVENT_DATE_FORMATS) {
            try {
                return formatter.parseDateTime(normalized);
            } catch (IllegalArgumentException e) {
                // try next supported format
            }
        }

        return null;
    }

    @VisibleForTesting
    static boolean isEventDateOnOrBeforeCloseThreshold(String eventDate, DateTime closeThreshold) {
        DateTime parsedEventDate = parseEventDate(eventDate);
        return parsedEventDate != null && closeThreshold != null && !parsedEventDate.isAfter(closeThreshold);
    }

    @VisibleForTesting
    static boolean shouldCloseOpenRecord(boolean isClosed, String eventDate, DateTime closeThreshold) {
        return !isClosed && isEventDateOnOrBeforeCloseThreshold(eventDate, closeThreshold);
    }

    private static int countOpenServiceRowsToClose(String clientId, DateTime closeThreshold) {
        String sql = "SELECT count(*) count FROM " + SOBER_HOUSE_SERVICES_TABLE +
                " WHERE entity_id = '" + escapeSqlValue(clientId) + "'" +
                " AND (is_closed = 0 OR is_closed IS NULL)" +
                " AND event_date IS NOT NULL AND TRIM(event_date) <> ''" +
                " AND julianday(event_date) <= julianday('" + toSqlDateTime(closeThreshold) + "')";
        return countRows(sql);
    }

    private static int countOpenEnrollmentRowsToClose(String clientId, DateTime closeThreshold) {
        String sql = "SELECT count(*) count FROM " + Constants.TABLES.HARM_REDUCTION_SOBER_HOUSE_ENROLLMENT +
                " WHERE base_entity_id = '" + escapeSqlValue(clientId) + "'" +
                " AND (is_closed = 0 OR is_closed IS NULL)" +
                " AND event_date IS NOT NULL AND TRIM(event_date) <> ''" +
                " AND julianday(event_date) <= julianday('" + toSqlDateTime(closeThreshold) + "')";
        return countRows(sql);
    }

    private static String buildCloseServicesSql(String clientId, DateTime closeThreshold) {
        return "UPDATE " + SOBER_HOUSE_SERVICES_TABLE +
                " SET is_closed = 1" +
                " WHERE entity_id = '" + escapeSqlValue(clientId) + "'" +
                " AND (is_closed = 0 OR is_closed IS NULL)" +
                " AND event_date IS NOT NULL AND TRIM(event_date) <> ''" +
                " AND julianday(event_date) <= julianday('" + toSqlDateTime(closeThreshold) + "')";
    }

    private static String buildCloseEnrollmentSql(String clientId, DateTime closeThreshold) {
        return "UPDATE " + Constants.TABLES.HARM_REDUCTION_SOBER_HOUSE_ENROLLMENT +
                " SET is_closed = 1" +
                " WHERE base_entity_id = '" + escapeSqlValue(clientId) + "'" +
                " AND (is_closed = 0 OR is_closed IS NULL)" +
                " AND event_date IS NOT NULL AND TRIM(event_date) <> ''" +
                " AND julianday(event_date) <= julianday('" + toSqlDateTime(closeThreshold) + "')";
    }

    private static int countRows(String sql) {
        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");
        List<Integer> rows = readData(sql, dataMap);
        if (rows == null || rows.isEmpty() || rows.get(0) == null) {
            return 0;
        }
        return rows.get(0);
    }

    private static String toSqlDateTime(DateTime dateTime) {
        return SQL_DATE_TIME_FORMAT.print(dateTime.withZone(DateTimeZone.UTC));
    }

    private static String escapeSqlValue(String value) {
        return StringUtils.defaultString(value).replace("'", "''");
    }

    public boolean hasStartedMat(String baseEntityId) {
        if (StringUtils.isBlank(baseEntityId)) {
            return false;
        }

        try {
            String sql = "SELECT client_started_mat FROM " + Constants.TABLES.HARM_REDUCTION_RISK_ASSESSMENT +
                    " WHERE base_entity_id = '" + baseEntityId + "' AND is_closed = 0 " +
                    "ORDER BY last_interacted_with DESC LIMIT 1";

            DataMap<String> dataMap = cursor -> getCursorValue(cursor, "client_started_mat");
            List<String> res = readData(sql, dataMap);
            if (res != null && !res.isEmpty() && res.get(0) != null) {
                return res.get(0).equalsIgnoreCase("yes");
            }
            return false;
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
    }

}
