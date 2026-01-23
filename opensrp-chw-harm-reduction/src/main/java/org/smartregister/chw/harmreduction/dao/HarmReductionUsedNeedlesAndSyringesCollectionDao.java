package org.smartregister.chw.harmreduction.dao;

import android.annotation.SuppressLint;

import org.smartregister.chw.harmreduction.model.HarmReductionUsedNeedlesAndSyringesCollectionModel;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.util.DBConstants;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class HarmReductionUsedNeedlesAndSyringesCollectionDao extends AbstractDao {

    public static void updateData(String baseEntityID,
                                  String dateOfCollection,
                                  String maskaniName,
                                  String collectionSiteGps,
                                  String otherCollection,
                                  String fixedBins,
                                  String totalSafetyBoxesCollected,
                                  String nameOfOw) {
        String sql = "INSERT INTO " + Constants.TABLES.HARM_REDUCTION_SAFETY_BOX_COLLECTION +
                "           (id, date_of_collection, maskani_name, collection_site_gps, other_collection, fixed_bins, total_safety_boxes_collected, name_of_ow) " +
                "           VALUES (" +
                "                   '" + baseEntityID + "', " +
                "                   '" + dateOfCollection + "', " +
                "                   '" + maskaniName + "', " +
                "                   '" + collectionSiteGps + "', " +
                "                   '" + otherCollection + "', " +
                "                   '" + fixedBins + "', " +
                "                   '" + totalSafetyBoxesCollected + "', " +
                "                   '" + nameOfOw + "') " +
                " ON CONFLICT (id) DO UPDATE SET date_of_collection = EXCLUDED.date_of_collection, " +
                "                               maskani_name = EXCLUDED.maskani_name, " +
                "                               collection_site_gps = EXCLUDED.collection_site_gps, " +
                "                               other_collection = EXCLUDED.other_collection, " +
                "                               fixed_bins = EXCLUDED.fixed_bins, " +
                "                               total_safety_boxes_collected = EXCLUDED.total_safety_boxes_collected, " +
                "                               name_of_ow = EXCLUDED.name_of_ow;";

        updateDB(sql);
    }

    public static List<HarmReductionUsedNeedlesAndSyringesCollectionModel> getCollectionSessions() {
        String sql = "SELECT *,  substr(date_of_collection, 7, 4)||'-'|| " +
                "                substr(date_of_collection, 4,2)||'-'|| " +
                "                substr(date_of_collection, 1,2) as orderDate FROM " +
                Constants.TABLES.HARM_REDUCTION_SAFETY_BOX_COLLECTION + " ORDER BY julianday(orderDate) DESC";

        @SuppressLint("Range") DataMap<HarmReductionUsedNeedlesAndSyringesCollectionModel> dataMap = cursor -> {
            HarmReductionUsedNeedlesAndSyringesCollectionModel collectionModel = new HarmReductionUsedNeedlesAndSyringesCollectionModel();
            collectionModel.setCollectionId(cursor.getString(cursor.getColumnIndex(DBConstants.KEY.BASE_ENTITY_ID)));
            collectionModel.setCollectionDate(cursor.getString(cursor.getColumnIndex(DBConstants.KEY.DATE_OF_COLLECTION)));

            String totalSafetyBoxesCollected = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.TOTAL_SAFETY_BOXES_COLLECTED));
            if (totalSafetyBoxesCollected == null || totalSafetyBoxesCollected.isEmpty()) {
                totalSafetyBoxesCollected = computeTotalSafetyBoxes(
                        cursor.getString(cursor.getColumnIndex(DBConstants.KEY.OTHER_COLLECTION)),
                        cursor.getString(cursor.getColumnIndex(DBConstants.KEY.FIXED_BINS))
                );
            }
            collectionModel.setTotalSafetyBoxesCollected(totalSafetyBoxesCollected);
            return collectionModel;
        };

        List<HarmReductionUsedNeedlesAndSyringesCollectionModel> res = readData(sql, dataMap);
        if (res == null || res.isEmpty()) {
            return null;
        }
        return res;
    }

    private static String computeTotalSafetyBoxes(String otherCollection, String fixedBins) {
        try {
            int sum = Integer.parseInt(defaultZero(otherCollection)) + Integer.parseInt(defaultZero(fixedBins));
            return String.valueOf(sum);
        } catch (Exception e) {
            return null;
        }
    }

    private static String defaultZero(String value) {
        return value == null || value.isEmpty() ? "0" : value;
    }
}
