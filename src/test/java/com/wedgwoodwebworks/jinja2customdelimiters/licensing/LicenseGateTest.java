package com.wedgwoodwebworks.jinja2customdelimiters.licensing;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LicenseGateTest {

    private LicenseChecker originalChecker;

    @Before
    public void setUp() {
        originalChecker = null;  // captured by LicenseGate#getChecker if needed
        LicenseGate.resetStateForTesting();
    }

    @After
    public void tearDown() {
        LicenseGate.resetChecker();
        LicenseGate.resetStateForTesting();
    }

    @Test
    public void isLicensedOrPending_returnsTrue_whenCheckerReturnsNull() {
        // First-launch permissive: null from checker is treated as pending
        // for the first 5s. This protects cold-start UX.
        LicenseGate.setChecker(new LicenseChecker() {
            @Override public Boolean isLicensed() { return null; }
            @Override public void requestLicense(String m) {}
        });
        assertTrue("null from checker must be permissive during first-launch window",
            LicenseGate.isLicensedOrPending());
    }

    @Test
    public void isLicensedOrPending_returnsTrue_whenCheckerReturnsTrue() {
        LicenseGate.setChecker(new LicenseChecker() {
            @Override public Boolean isLicensed() { return true; }
            @Override public void requestLicense(String m) {}
        });
        assertTrue(LicenseGate.isLicensedOrPending());
    }

    @Test
    public void isLicensedOrPending_returnsFalse_whenCheckerReturnsFalse() {
        LicenseGate.setChecker(new LicenseChecker() {
            @Override public Boolean isLicensed() { return false; }
            @Override public void requestLicense(String m) {}
        });
        assertFalse("false from checker must propagate as not-licensed",
            LicenseGate.isLicensedOrPending());
    }

    @Test
    public void ensureLicensed_returnsFalse_whenCheckerReturnsFalse() {
        LicenseGate.setChecker(new LicenseChecker() {
            @Override public Boolean isLicensed() { return false; }
            @Override public void requestLicense(String m) {}
        });
        assertFalse(LicenseGate.ensureLicensed("test"));
    }

    @Test
    public void ensureLicensed_debounces_promptWithin60s() {
        final int[] callCount = {0};
        LicenseGate.setChecker(new LicenseChecker() {
            @Override public Boolean isLicensed() { return false; }
            @Override public void requestLicense(String m) { callCount[0]++; }
        });
        LicenseGate.ensureLicensed("test");
        LicenseGate.ensureLicensed("test");
        assertEquals("Second prompt within 60s must be debounced",
            1, callCount[0]);
    }
}
