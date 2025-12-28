package org.smartregister.chw.harmreduction_sample.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.harmreduction.contract.HarmReductionRegisterFragmentContract;
import org.smartregister.chw.harmreduction.presenter.BaseHarmReductionRegisterFragmentPresenter;
import org.smartregister.chw.harmreduction.util.Constants;
import org.smartregister.chw.harmreduction.util.DBConstants;
import org.smartregister.configurableviews.model.View;

import java.util.Set;
import java.util.TreeSet;

public class BaseHarmReductionRegisterFragmentPresenterTest {
    @Mock
    protected HarmReductionRegisterFragmentContract.View view;

    @Mock
    protected HarmReductionRegisterFragmentContract.Model model;

    private BaseHarmReductionRegisterFragmentPresenter baseHarmReductionRegisterFragmentPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        baseHarmReductionRegisterFragmentPresenter = new BaseHarmReductionRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void assertNotNull() {
        Assert.assertNotNull(baseHarmReductionRegisterFragmentPresenter);
    }

    @Test
    public void getMainCondition() {
        Assert.assertEquals(" ec_tbleprosy_screening.is_closed = 0 ", baseHarmReductionRegisterFragmentPresenter.getMainCondition());
    }

    @Test
    public void getDueFilterCondition() {
        Assert.assertEquals(" (cast( julianday(STRFTIME('%Y-%m-%d', datetime('now'))) -  julianday(IFNULL(SUBSTR(tbleprosy_test_date,7,4)|| '-' || SUBSTR(tbleprosy_test_date,4,2) || '-' || SUBSTR(tbleprosy_test_date,1,2),'')) as integer) between 7 and 14) ", baseHarmReductionRegisterFragmentPresenter.getDueFilterCondition());
    }

    @Test
    public void getDefaultSortQuery() {
        Assert.assertEquals(Constants.TABLES.TBLEPROSY_SCREENING + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ", baseHarmReductionRegisterFragmentPresenter.getDefaultSortQuery());
    }

    @Test
    public void getMainTable() {
        Assert.assertEquals(Constants.TABLES.TBLEPROSY_SCREENING, baseHarmReductionRegisterFragmentPresenter.getMainTable());
    }

    @Test
    public void initializeQueries() {
        Set<View> visibleColumns = new TreeSet<>();
        baseHarmReductionRegisterFragmentPresenter.initializeQueries(null);
        Mockito.doNothing().when(view).initializeQueryParams(Constants.TABLES.TBLEPROSY_SCREENING, null, null);
        Mockito.verify(view).initializeQueryParams(Constants.TABLES.TBLEPROSY_SCREENING, null, null);
        Mockito.verify(view).initializeAdapter(visibleColumns);
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
    }

}