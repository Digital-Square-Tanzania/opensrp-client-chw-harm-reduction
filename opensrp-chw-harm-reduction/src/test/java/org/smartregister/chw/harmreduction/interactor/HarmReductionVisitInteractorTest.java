package org.smartregister.chw.harmreduction.interactor;

import org.junit.Assert;
import org.junit.Test;

public class HarmReductionVisitInteractorTest {

    @Test
    public void testShouldContinueCommunityServices() {
        Assert.assertTrue(HarmReductionVisitInteractor.shouldContinueCommunityServices("continue_service"));
        Assert.assertTrue(HarmReductionVisitInteractor.shouldContinueCommunityServices(" Continue_Service "));
        Assert.assertFalse(HarmReductionVisitInteractor.shouldContinueCommunityServices("started_mat_services"));
        Assert.assertFalse(HarmReductionVisitInteractor.shouldContinueCommunityServices("lost"));
        Assert.assertFalse(HarmReductionVisitInteractor.shouldContinueCommunityServices(""));
    }
}
