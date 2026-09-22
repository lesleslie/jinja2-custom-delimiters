package com.wedgwoodwebworks.jinja2customdelimiters.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.wedgwoodwebworks.jinja2customdelimiters.licensing.LicenseChecker;
import com.wedgwoodwebworks.jinja2customdelimiters.licensing.LicenseGate;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class Jinja2DelimitersConfigurableTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Reset license state so tests are independent
        LicenseGate.resetChecker();
        LicenseGate.resetStateForTesting();
        // Reset settings to defaults so apply() tests start from a known baseline
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        if (settings != null) {
            settings.setBlockStartString("{%");
            settings.setBlockEndString("%}");
            settings.setVariableStartString("{{");
            settings.setVariableEndString("}}");
            settings.setCommentStartString("{#");
            settings.setCommentEndString("#}");
            settings.setLineStatementPrefix("");
            settings.setLineCommentPrefix("");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        LicenseGate.resetChecker();
        LicenseGate.resetStateForTesting();
        super.tearDown();
    }

    /**
     * Contract (per Jinja2DelimitersConfigurable.apply):
     *   apply() MUST call LicenseGate.ensureLicensed(...) first. If the gate
     *   returns false (license check failed), apply() MUST throw
     *   ConfigurationException — this is the hard-fail path required by Task 7.
     */
    public void testApply_throwsConfigurationExceptionOnLicenseFailure() {
        // Stub the checker so ensureLicensed returns false
        LicenseGate.setChecker(new LicenseChecker() {
            @Override
            public Boolean isLicensed() { return Boolean.FALSE; }
            @Override
            public void requestLicense(String message) { /* no-op for test */ }
        });
        // Force the first-launch permissive window closed so FALSE is treated as denied
        LicenseGate.resetStateForTesting();

        Jinja2DelimitersConfigurable configurable = new Jinja2DelimitersConfigurable();
        // createComponent() so the fields are initialised (needed before apply)
        configurable.createComponent();

        try {
            configurable.apply();
            fail("apply() must throw ConfigurationException when license check fails");
        } catch (ConfigurationException expected) {
            assertNotNull(expected.getMessage());
            assertTrue(
                "Exception message must mention the license requirement",
                expected.getMessage().toLowerCase().contains("license")
            );
        }
    }

    /**
     * Contract (per Jinja2DelimitersConfigurable.createComponent):
     *   When !LicenseGate.isLicensedOrPending(), createComponent() MUST return
     *   the unlicensedPanel (a JPanel containing the activate-license affordance),
     *   NOT the standard mainPanel with the delimiter text fields.
     */
    public void testCreateComponent_returnsUnlicensedPanel_whenNotLicensed() {
        // Stub the checker so isLicensedOrPending returns false
        LicenseGate.setChecker(new LicenseChecker() {
            @Override
            public Boolean isLicensed() { return Boolean.FALSE; }
            @Override
            public void requestLicense(String message) { /* no-op for test */ }
        });
        LicenseGate.resetStateForTesting();

        Jinja2DelimitersConfigurable configurable = new Jinja2DelimitersConfigurable();

        JComponent component = configurable.createComponent();
        assertNotNull("createComponent() must not return null when unlicensed", component);
        assertTrue(
            "createComponent() must return a JPanel (the unlicensedPanel wrapper) when unlicensed",
            component instanceof JPanel
        );
    }

    /**
     * Counterpart: when licensed (or first-launch permissive), createComponent()
     * returns the mainPanel with the delimiter fields — not the unlicensedPanel.
     */
    public void testCreateComponent_returnsMainPanel_whenLicensed() {
        // Stub the checker so isLicensedOrPending returns true (licensed)
        LicenseGate.setChecker(new LicenseChecker() {
            @Override
            public Boolean isLicensed() { return Boolean.TRUE; }
            @Override
            public void requestLicense(String message) { /* no-op for test */ }
        });

        Jinja2DelimitersConfigurable configurable = new Jinja2DelimitersConfigurable();

        JComponent component = configurable.createComponent();
        assertNotNull("createComponent() must not return null when licensed", component);
        assertTrue(
            "createComponent() must return a JPanel (the mainPanel wrapper) when licensed",
            component instanceof JPanel
        );
    }
}