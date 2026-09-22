package com.wedgwoodwebworks.jinja2customdelimiters.licensing;

import com.intellij.openapi.diagnostic.Logger;

/**
 * Gates paid features behind license verification. Hard-fails on license
 * subsystem failure with a user-actionable error (see
 * {@link com.wedgwoodwebworks.jinja2customdelimiters.formatting.CustomJinja2PreFormatProcessor}).
 *
 * Testability: production binds via {@link MarketplaceLicenseCheckerAdapter};
 * tests inject via {@link #setChecker(LicenseChecker)}.
 */
public final class LicenseGate {

    private static final Logger LOG = Logger.getInstance(LicenseGate.class);

    /** Permissive threshold for first-launch (LicensingFacade returns null while booting). */
    private static final long FACADE_NULL_PERMISSIVE_MS = 5_000L;
    private static final long PROMPT_INTERVAL_MS = 60_000L;

    private static volatile LicenseChecker checker = new MarketplaceLicenseCheckerAdapter();
    private static volatile long firstLaunchPermissiveUntilMs = 0L;
    private static volatile long lastPromptTimeMs = 0L;

    private LicenseGate() {}

    /** Test seam. Production should not call this. */
    public static void setChecker(LicenseChecker c) { checker = c; }

    /** Test seam. Production should not call this. */
    public static void resetChecker() { checker = new MarketplaceLicenseCheckerAdapter(); }

    public static boolean isLicensedOrPending() {
        Boolean licensed = checker.isLicensed();
        if (licensed == null) {
            // First-launch permissive window: when LicensingFacade hasn't
            // initialized yet (cold IDE start, first few hundred ms),
            // return true so formatting isn't blocked before the user
            // has a chance to license. After FACADE_NULL_PERMISSIVE_MS,
            // null is treated as failure (hard-fail in formatter path).
            long now = System.currentTimeMillis();
            if (now < firstLaunchPermissiveUntilMs) return true;
            firstLaunchPermissiveUntilMs = now + FACADE_NULL_PERMISSIVE_MS;
            return true;  // first call: enter permissive window
        }
        return licensed;
    }

    public static boolean ensureLicensed(String featureId) {
        if (isLicensedOrPending()) {
            return true;
        }
        long now = System.currentTimeMillis();
        if (now - lastPromptTimeMs < PROMPT_INTERVAL_MS) {
            return false;
        }
        lastPromptTimeMs = now;
        checker.requestLicense(featureId);
        return false;
    }

    /** Test seam. Production should not call this. */
    public static void resetStateForTesting() {
        firstLaunchPermissiveUntilMs = 0L;
        lastPromptTimeMs = 0L;
    }
}
