package org.kimios.test.pax;

import org.junit.Assert;
import org.junit.Test;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.AuthenticationSource;

import java.util.List;

public class ProvisioningTest extends KimiosTest {
    @Test
    public void testProvisioning() throws Exception {
        // Check that the features are installed
        // assertFeatureInstalled("pax-jdbc-postgresql", "4.0.4");
        // assertFeatureInstalled("hibernate", "4.3.6.Final");
        // assertFeatureInstalled("jpa", "2.2.0");

        // Check that the bundles are installed
        // assertBundleInstalled("ippon-osgi-sample-services");
        // assertBundleInstalled("ippon-osgi-sample-command");

        Assert.assertEquals("1", "1");
        Assert.assertNotNull("securityController not null", securityController);

        Assert.assertNotNull("administrationController", administrationController);
        List<AuthenticationSource> authenticationSourceList = this.securityController.getAuthenticationSources();
        Assert.assertNotNull(authenticationSourceList);
        Assert.assertTrue(authenticationSourceList.size() > 0);

        Session session = this.securityController.startSession("admin", "kimios");
        Assert.assertNotNull("admin session not null", session);
    }
}
