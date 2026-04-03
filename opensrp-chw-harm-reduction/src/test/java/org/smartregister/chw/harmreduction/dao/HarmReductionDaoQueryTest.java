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
    public void buildSoberHouseMemberQueryShouldRequireDetoxification() {
        String query = HarmReductionDao.buildSoberHouseMemberQuery("base-id");

        Assert.assertTrue(query.contains("inner join ec_harm_reduction_sober_house_enrollment sh on sh.base_entity_id = m.base_entity_id"));
        Assert.assertTrue(query.contains("where sh.is_closed = 0 AND sh.detoxification_done = 'yes' AND m.base_entity_id ='base-id'"));
    }
}
