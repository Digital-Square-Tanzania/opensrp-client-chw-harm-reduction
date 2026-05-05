package org.smartregister.chw.harmreduction.dao;

import org.junit.Assert;
import org.junit.Test;

public class HarmReductionDaoQueryTest {

    @Test
    public void buildLatestStatusQueryShouldBuildCommunityFollowUpQuery() {
        String query = HarmReductionDao.buildLatestStatusQuery(
                "ec_harm_reduction_followup_visit",
                "follow_up_status",
                "base-id"
        );

        Assert.assertEquals(
                "SELECT follow_up_status FROM ec_harm_reduction_followup_visit WHERE is_closed = 0 AND entity_id = 'base-id' ORDER BY last_interacted_with DESC LIMIT 1",
                query
        );
    }

    @Test
    public void buildLatestStatusQueryShouldBuildSoberHouseFollowUpQuery() {
        String query = HarmReductionDao.buildLatestStatusQuery(
                "ec_harm_reduction_sober_house_services",
                "follow_up_status",
                "base-id"
        );

        Assert.assertEquals(
                "SELECT follow_up_status FROM ec_harm_reduction_sober_house_services WHERE is_closed = 0 AND entity_id = 'base-id' ORDER BY last_interacted_with DESC LIMIT 1",
                query
        );
    }

    @Test
    public void buildHasPreviousHarmReductionFollowUpVisitQueryShouldTargetCommunityFollowUpTable() {
        String query = HarmReductionDao.buildHasPreviousHarmReductionFollowUpVisitQuery("base-id");

        Assert.assertEquals(
                "SELECT count(p.entity_id) count FROM ec_harm_reduction_followup_visit p WHERE p.entity_id = 'base-id'",
                query
        );
    }

    @Test
    public void buildHasPreviousPositiveHivFollowUpVisitQueryShouldRequirePositiveHistory() {
        String query = HarmReductionDao.buildHasPreviousPositiveHivFollowUpVisitQuery("base-id");

        Assert.assertEquals(
                "SELECT count(p.entity_id) count FROM ec_harm_reduction_followup_visit p WHERE p.entity_id = 'base-id' AND p.hiv_results = 'positive'",
                query
        );
    }

    @Test
    public void buildHasPreviousSoberHouseServiceVisitQueryShouldTargetSoberHouseServiceTable() {
        String query = HarmReductionDao.buildHasPreviousSoberHouseServiceVisitQuery("base-id");

        Assert.assertEquals(
                "SELECT count(p.entity_id) count FROM ec_harm_reduction_sober_house_services p WHERE p.entity_id = 'base-id'",
                query
        );
    }

    @Test
    public void buildLatestSoberHouseEnrollmentClientStatusQueryShouldTargetEnrollmentTable() {
        String query = HarmReductionDao.buildLatestSoberHouseEnrollmentClientStatusQuery("base-id");

        Assert.assertEquals(
                "SELECT client_status FROM ec_harm_reduction_sober_house_enrollment WHERE is_closed = 0 AND base_entity_id = 'base-id' ORDER BY last_interacted_with DESC LIMIT 1",
                query
        );
    }

    @Test
    public void buildLatestRiskAssessmentClientStatusQueryShouldTargetRiskAssessmentTable() {
        String query = HarmReductionDao.buildLatestRiskAssessmentClientStatusQuery("base-id");

        Assert.assertEquals(
                "SELECT client_status FROM ec_harm_reduction_risk_assessment WHERE is_closed = 0 AND base_entity_id = 'base-id' ORDER BY last_interacted_with DESC LIMIT 1",
                query
        );
    }

    @Test
    public void buildLatestRiskAssessmentUicQueryShouldTargetRiskAssessmentTable() {
        String query = HarmReductionDao.buildLatestRiskAssessmentUicQuery("base-id");

        Assert.assertEquals(
                "SELECT uic FROM ec_harm_reduction_risk_assessment WHERE is_closed = 0 AND base_entity_id = 'base-id' ORDER BY last_interacted_with DESC LIMIT 1",
                query
        );
    }

    @Test
    public void buildLatestSoberHouseEnrollmentUicQueryShouldTargetSoberHouseEnrollmentTable() {
        String query = HarmReductionDao.buildLatestSoberHouseEnrollmentUicQuery("base-id");

        Assert.assertEquals(
                "SELECT uic_id FROM ec_harm_reduction_sober_house_enrollment WHERE is_closed = 0 AND base_entity_id = 'base-id' ORDER BY last_interacted_with DESC LIMIT 1",
                query
        );
    }

    @Test
    public void buildLatestPositiveHivFollowUpVisitFieldQueryShouldTargetPositiveFollowUpRows() {
        String query = HarmReductionDao.buildLatestPositiveHivFollowUpVisitFieldQuery("ctc_id", "base-id");

        Assert.assertEquals(
                "SELECT ctc_id FROM ec_harm_reduction_followup_visit WHERE is_closed = 0 AND entity_id = 'base-id' AND hiv_results = 'positive' AND ctc_id IS NOT NULL AND TRIM(ctc_id) != '' ORDER BY last_interacted_with DESC LIMIT 1",
                query
        );
    }

    @Test
    public void buildSoberHouseMemberQueryShouldRequireDetoxification() {
        String query = HarmReductionDao.buildSoberHouseMemberQuery("base-id");

        Assert.assertTrue(query.contains("inner join ec_harm_reduction_sober_house_enrollment sh on sh.base_entity_id = m.base_entity_id"));
        Assert.assertTrue(query.contains("where sh.is_closed = 0 AND sh.detoxification_done = 'yes' AND m.base_entity_id ='base-id'"));
    }

    @Test
    public void buildCloseCompletedMethadoneTreatmentRiskAssessmentSqlShouldCloseActiveRiskAssessment() {
        String query = HarmReductionDao.buildCloseCompletedMethadoneTreatmentRiskAssessmentSql("base-id");

        Assert.assertEquals(
                "UPDATE ec_harm_reduction_risk_assessment SET client_started_mat = 'no', is_closed = 1 WHERE base_entity_id = 'base-id' AND is_closed = 0",
                query
        );
    }

    @Test
    public void buildCloseCompletedMethadoneTreatmentRiskAssessmentSqlShouldEscapeBaseEntityId() {
        String query = HarmReductionDao.buildCloseCompletedMethadoneTreatmentRiskAssessmentSql("base'id");

        Assert.assertEquals(
                "UPDATE ec_harm_reduction_risk_assessment SET client_started_mat = 'no', is_closed = 1 WHERE base_entity_id = 'base''id' AND is_closed = 0",
                query
        );
    }

    @Test
    public void buildReassignStoppedMethadoneTreatmentRiskAssessmentSqlShouldReturnClientToCommunityServices() {
        String query = HarmReductionDao.buildReassignStoppedMethadoneTreatmentRiskAssessmentSql("base-id");

        Assert.assertEquals(
                "UPDATE ec_harm_reduction_risk_assessment SET client_started_mat = 'no', follow_up_status = 'continue_service', status = 'on_community_service' WHERE base_entity_id = 'base-id' AND is_closed = 0",
                query
        );
    }

    @Test
    public void buildReassignStoppedMethadoneTreatmentRiskAssessmentSqlShouldEscapeBaseEntityId() {
        String query = HarmReductionDao.buildReassignStoppedMethadoneTreatmentRiskAssessmentSql("base'id");

        Assert.assertEquals(
                "UPDATE ec_harm_reduction_risk_assessment SET client_started_mat = 'no', follow_up_status = 'continue_service', status = 'on_community_service' WHERE base_entity_id = 'base''id' AND is_closed = 0",
                query
        );
    }

    @Test
    public void isCompletedMethadoneTreatmentShouldHandleExactAndSerializedValues() {
        Assert.assertTrue(HarmReductionDao.isCompletedMethadoneTreatment("completed_methadone_treatment"));
        Assert.assertTrue(HarmReductionDao.isCompletedMethadoneTreatment("[completed_methadone_treatment]"));
    }

    @Test
    public void isCompletedMethadoneTreatmentShouldRejectOtherStatuses() {
        Assert.assertFalse(HarmReductionDao.isCompletedMethadoneTreatment("continuing_methadone_treatment"));
        Assert.assertFalse(HarmReductionDao.isCompletedMethadoneTreatment("stopped_using_methadone"));
        Assert.assertFalse(HarmReductionDao.isCompletedMethadoneTreatment("not_completed_methadone_treatment"));
        Assert.assertFalse(HarmReductionDao.isCompletedMethadoneTreatment(""));
        Assert.assertFalse(HarmReductionDao.isCompletedMethadoneTreatment(null));
    }

    @Test
    public void isStoppedUsingMethadoneShouldHandleExactAndSerializedValues() {
        Assert.assertTrue(HarmReductionDao.isStoppedUsingMethadone("stopped_using_methadone"));
        Assert.assertTrue(HarmReductionDao.isStoppedUsingMethadone("[stopped_using_methadone]"));
    }

    @Test
    public void isStoppedUsingMethadoneShouldRejectOtherStatuses() {
        Assert.assertFalse(HarmReductionDao.isStoppedUsingMethadone("continuing_methadone_treatment"));
        Assert.assertFalse(HarmReductionDao.isStoppedUsingMethadone("completed_methadone_treatment"));
        Assert.assertFalse(HarmReductionDao.isStoppedUsingMethadone("not_stopped_using_methadone"));
        Assert.assertFalse(HarmReductionDao.isStoppedUsingMethadone(""));
        Assert.assertFalse(HarmReductionDao.isStoppedUsingMethadone(null));
    }
}
