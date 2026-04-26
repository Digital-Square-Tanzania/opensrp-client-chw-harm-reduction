package org.smartregister.chw.harmreduction.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.harmreduction.contract.HarmReductionRegisterFragmentContract;

public class BaseHarmReductionRegisterFragmentPresenterTest {

    private BaseHarmReductionRegisterFragmentPresenter presenter;

    @Mock
    private HarmReductionRegisterFragmentContract.View view;

    @Mock
    private HarmReductionRegisterFragmentContract.Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new BaseHarmReductionRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void testMainConditionFallsBackToFollowUpStatusWhenDerivedFieldsAreBlank() {
        Assert.assertEquals(
                " ec_harm_reduction_risk_assessment.is_closed = 0 AND (ec_harm_reduction_risk_assessment.status = 'on_community_service' OR (IFNULL(ec_harm_reduction_risk_assessment.status, '') = '' AND IFNULL(ec_harm_reduction_risk_assessment.client_started_mat, '') <> 'yes' AND IFNULL(ec_harm_reduction_risk_assessment.follow_up_status, '') <> 'started_mat_services')) ",
                presenter.getMainCondition()
        );
    }
}
