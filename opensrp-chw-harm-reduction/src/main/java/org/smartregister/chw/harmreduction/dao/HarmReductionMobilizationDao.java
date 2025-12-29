package org.smartregister.chw.harmreduction.dao;


import android.annotation.SuppressLint;

import org.smartregister.chw.harmreduction.model.HarmReductionMobilizationModel;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.util.DBConstants;
import org.smartregister.dao.AbstractDao;

import java.util.List;


public class HarmReductionMobilizationDao extends AbstractDao {

    public static void updateData(String baseEntityID, String mobilization_date, String female_clients_reached, String male_clients_reached) {
        String sql = "INSERT INTO ec_tbleprosy_mobilization " +
                "           (id, mobilization_date, female_clients_reached, male_clients_reached) " +
                "           VALUES (" +
                "                   '" + baseEntityID + "', " +
                "                   '" + mobilization_date + "', " +
                "                   '" + female_clients_reached + "', " +
                "                   '" + male_clients_reached + "') " +
                " ON CONFLICT (id) DO UPDATE SET mobilization_date = EXCLUDED.mobilization_date, " +
                "                               female_clients_reached = EXCLUDED.female_clients_reached, " +
                "                               male_clients_reached = EXCLUDED.male_clients_reached;";

        updateDB(sql);
    }
    private static String computeSessionParticipants(String femaleParticipants, String maleParticipants) {
        int sum = Integer.parseInt(femaleParticipants) + Integer.parseInt(maleParticipants);
        return String.valueOf(sum);
    }

    private static String computeCondomsIssued(String femaleCondoms, String maleCondoms) {
        int sum = Integer.parseInt(femaleCondoms) + Integer.parseInt(maleCondoms);
        return String.valueOf(sum);
    }


}
