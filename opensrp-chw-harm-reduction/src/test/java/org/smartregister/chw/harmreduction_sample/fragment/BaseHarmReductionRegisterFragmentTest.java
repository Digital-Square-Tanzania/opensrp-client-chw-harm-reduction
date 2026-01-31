package org.smartregister.chw.harmreduction_sample.fragment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.harmreduction.activity.BaseHarmReductionProfileActivity;
import org.smartregister.chw.harmreduction.fragment.BaseHarmReductionRegisterFragment;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import static org.mockito.Mockito.times;

public class BaseHarmReductionRegisterFragmentTest {
    @Mock
    public BaseHarmReductionRegisterFragment baseTestRegisterFragment;

    @Mock
    public CommonPersonObjectClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = Exception.class)
    public void openProfile() throws Exception {
        Whitebox.invokeMethod(baseTestRegisterFragment, "openProfile", client);
        PowerMockito.mockStatic(BaseHarmReductionProfileActivity.class);
        BaseHarmReductionProfileActivity.startProfileActivity(null, null);
        PowerMockito.verifyStatic(BaseHarmReductionProfileActivity.class, times(1));
        BaseHarmReductionProfileActivity.startProfileActivity(null, null);

    }
}
