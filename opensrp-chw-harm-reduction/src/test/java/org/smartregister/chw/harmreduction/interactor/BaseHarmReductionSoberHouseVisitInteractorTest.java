package org.smartregister.chw.harmreduction.interactor;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class BaseHarmReductionSoberHouseVisitInteractorTest {

    @Test
    public void testRecoveryCapitalActionHiddenBeforeThreeMonthsFromEnrollment() {
        boolean visible = BaseHarmReductionSoberHouseVisitInteractor
                .isVisitAtLeastThreeCalendarMonthsFromEnrollment(
                        "2025-01-15",
                        new DateTime(2025, 4, 14, 12, 0)
                );

        Assert.assertFalse(visible);
    }

    @Test
    public void testRecoveryCapitalActionVisibleAtExactlyThreeMonthsFromEnrollment() {
        boolean visible = BaseHarmReductionSoberHouseVisitInteractor
                .isVisitAtLeastThreeCalendarMonthsFromEnrollment(
                        "2025-01-15",
                        new DateTime(2025, 4, 15, 0, 0)
                );

        Assert.assertTrue(visible);
    }

    @Test
    public void testRecoveryCapitalActionVisibleAfterThreeMonthsFromEnrollment() {
        boolean visible = BaseHarmReductionSoberHouseVisitInteractor
                .isVisitAtLeastThreeCalendarMonthsFromEnrollment(
                        "2025-01-15",
                        new DateTime(2025, 4, 16, 9, 30)
                );

        Assert.assertTrue(visible);
    }

    @Test
    public void testRecoveryCapitalActionHiddenWhenEnrollmentDateMissing() {
        boolean visible = BaseHarmReductionSoberHouseVisitInteractor
                .isVisitAtLeastThreeCalendarMonthsFromEnrollment(
                        "",
                        new DateTime(2025, 4, 16, 9, 30)
                );

        Assert.assertFalse(visible);
    }

    @Test
    public void testNextAppointmentHiddenWhenRecoveryActionVisibleAndPassedYes() {
        boolean shouldHide = BaseHarmReductionSoberHouseVisitInteractor
                .shouldHideNextAppointmentDateAction(true, "yes");

        Assert.assertTrue(shouldHide);
    }

    @Test
    public void testNextAppointmentHiddenWhenRecoveryActionVisibleAndPassedYesCaseInsensitive() {
        boolean shouldHide = BaseHarmReductionSoberHouseVisitInteractor
                .shouldHideNextAppointmentDateAction(true, "YES");

        Assert.assertTrue(shouldHide);
    }

    @Test
    public void testNextAppointmentVisibleWhenRecoveryActionHiddenEvenIfPassedYes() {
        boolean shouldHide = BaseHarmReductionSoberHouseVisitInteractor
                .shouldHideNextAppointmentDateAction(false, "yes");

        Assert.assertFalse(shouldHide);
    }

    @Test
    public void testNextAppointmentVisibleWhenRecoveryActionVisibleAndPassedNo() {
        boolean shouldHide = BaseHarmReductionSoberHouseVisitInteractor
                .shouldHideNextAppointmentDateAction(true, "no");

        Assert.assertFalse(shouldHide);
    }

    @Test
    public void testContinuingServicesAllowedForReturningClientOnlyWhenContinuingStatusSelected() {
        Assert.assertTrue(BaseHarmReductionSoberHouseVisitInteractor
                .shouldContinueSoberHouseServices("continuing_service", "returning_client"));
        Assert.assertFalse(BaseHarmReductionSoberHouseVisitInteractor
                .shouldContinueSoberHouseServices("absconded", "returning_client"));
        Assert.assertFalse(BaseHarmReductionSoberHouseVisitInteractor
                .shouldContinueSoberHouseServices("other", "returning_client"));
    }

    @Test
    public void testContinuingServicesAllowedForEnrollmentClientTypesWithoutReturningStatus() {
        Assert.assertTrue(BaseHarmReductionSoberHouseVisitInteractor
                .shouldContinueSoberHouseServices("", "new_client"));
        Assert.assertTrue(BaseHarmReductionSoberHouseVisitInteractor
                .shouldContinueSoberHouseServices("", "relapsed_client"));
        Assert.assertTrue(BaseHarmReductionSoberHouseVisitInteractor
                .shouldContinueSoberHouseServices("", "migrant_client"));
    }
}
