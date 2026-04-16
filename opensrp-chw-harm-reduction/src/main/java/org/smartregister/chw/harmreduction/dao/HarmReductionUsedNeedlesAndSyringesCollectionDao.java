package org.smartregister.chw.harmreduction.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;

import org.smartregister.chw.harmreduction.model.HarmReductionUsedNeedlesAndSyringesCollectionModel;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.util.DBConstants;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class HarmReductionUsedNeedlesAndSyringesCollectionDao extends AbstractDao {

    public static void updateData(String baseEntityID,
                                  String dateOfCollection,
                                  String maskaniName,
                                  String numberOfUsedNeedlesAndSyringesCollected,
                                  String issuesChallengesRelatedToCollectionOfUsedNeedlesAndSyringes) {
        String sql = "INSERT INTO " + Constants.TABLES.HARM_REDUCTION_SAFETY_BOX_COLLECTION +
                "           (id, base_entity_id, date_of_collection, maskani_name, number_of_used_needles_and_syringes_collected, issues_challenges_related_to_collection_of_used_needles_and_syringes) " +
                "           VALUES (" +
                "                   '" + baseEntityID + "', " +
                "                   '" + baseEntityID + "', " +
                "                   '" + dateOfCollection + "', " +
                "                   '" + maskaniName + "', " +
                "                   '" + numberOfUsedNeedlesAndSyringesCollected + "', " +
                "                   '" + issuesChallengesRelatedToCollectionOfUsedNeedlesAndSyringes + "') " +
                " ON CONFLICT (id) DO UPDATE SET date_of_collection = EXCLUDED.date_of_collection, " +
                "                               maskani_name = EXCLUDED.maskani_name, " +
                "                               number_of_used_needles_and_syringes_collected = EXCLUDED.number_of_used_needles_and_syringes_collected, " +
                "                               issues_challenges_related_to_collection_of_used_needles_and_syringes = EXCLUDED.issues_challenges_related_to_collection_of_used_needles_and_syringes;";

        updateDB(sql);
    }

    public static List<HarmReductionUsedNeedlesAndSyringesCollectionModel> getCollectionSessions() {
        String sql = "SELECT *,  substr(date_of_collection, 7, 4)||'-'|| " +
                "                substr(date_of_collection, 4,2)||'-'|| " +
                "                substr(date_of_collection, 1,2) as orderDate FROM " +
                Constants.TABLES.HARM_REDUCTION_SAFETY_BOX_COLLECTION + " ORDER BY julianday(orderDate) DESC";

        @SuppressLint("Range") DataMap<HarmReductionUsedNeedlesAndSyringesCollectionModel> dataMap = cursor -> {
            HarmReductionUsedNeedlesAndSyringesCollectionModel collectionModel = new HarmReductionUsedNeedlesAndSyringesCollectionModel();
            collectionModel.setCollectionId(getColumnValue(cursor, DBConstants.KEY.BASE_ENTITY_ID));
            collectionModel.setCollectionDate(getColumnValue(cursor, DBConstants.KEY.DATE_OF_COLLECTION));
            collectionModel.setUsedNeedlesAndSyringesCollected(resolveCollectedNeedlesAndSyringesValue(
                    getColumnValue(cursor, DBConstants.KEY.NUMBER_OF_USED_NEEDLES_AND_SYRINGES_COLLECTED),
                    getColumnValue(cursor, DBConstants.KEY.TOTAL_SAFETY_BOXES_COLLECTED),
                    getColumnValue(cursor, DBConstants.KEY.OTHER_COLLECTION),
                    getColumnValue(cursor, DBConstants.KEY.FIXED_BINS)
            ));
            return collectionModel;
        };

        List<HarmReductionUsedNeedlesAndSyringesCollectionModel> res = readData(sql, dataMap);
        if (res == null || res.isEmpty()) {
            return null;
        }
        return res;
    }

    static String resolveCollectedNeedlesAndSyringesValue(String currentCount,
                                                          String legacyTotalSafetyBoxesCollected,
                                                          String otherCollection,
                                                          String fixedBins) {
        if (hasValue(currentCount)) {
            return currentCount;
        }

        if (hasValue(legacyTotalSafetyBoxesCollected)) {
            return legacyTotalSafetyBoxesCollected;
        }

        return computeLegacyTotalSafetyBoxes(otherCollection, fixedBins);
    }

    private static String computeLegacyTotalSafetyBoxes(String otherCollection, String fixedBins) {
        try {
            int sum = Integer.parseInt(defaultZero(otherCollection)) + Integer.parseInt(defaultZero(fixedBins));
            return String.valueOf(sum);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getColumnValue(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getString(index) : null;
    }

    private static boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String defaultZero(String value) {
        return value == null || value.isEmpty() ? "0" : value;
    }
}
