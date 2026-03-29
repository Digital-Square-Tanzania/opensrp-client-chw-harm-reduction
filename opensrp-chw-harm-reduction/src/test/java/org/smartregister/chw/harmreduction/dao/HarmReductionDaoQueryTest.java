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
}
