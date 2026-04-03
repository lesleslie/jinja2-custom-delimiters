package com.wedgwoodwebworks.jinja2customdelimiters.licensing;

import java.util.concurrent.atomic.AtomicLong;

public final class LicenseGate {

    private static final long PROMPT_INTERVAL_MS = 60_000L;
    private static final AtomicLong LAST_PROMPT_AT = new AtomicLong(0L);

    private LicenseGate() {}

    public static boolean isLicensedOrPending() {
        Boolean licensed = MarketplaceLicenseChecker.isLicensed();
        return licensed == null || licensed;
    }

    public static boolean ensureLicensed(String featureName) {
        Boolean licensed = MarketplaceLicenseChecker.isLicensed();
        if (licensed == null || licensed) {
            return true;
        }

        long now = System.currentTimeMillis();
        long lastPrompt = LAST_PROMPT_AT.get();
        if (now - lastPrompt >= PROMPT_INTERVAL_MS && LAST_PROMPT_AT.compareAndSet(lastPrompt, now)) {
            MarketplaceLicenseChecker.requestLicense(
                "A valid JetBrains Marketplace license is required to use " + featureName + "."
            );
        }
        return false;
    }
}
