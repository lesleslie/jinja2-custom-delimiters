package com.wedgwoodwebworks.jinja2customdelimiters.licensing;

/**
 * Abstraction over the licensing subsystem for testability.
 * Production binding: {@link MarketplaceLicenseCheckerAdapter}.
 */
public interface LicenseChecker {
    /**
     * @return {@code Boolean.TRUE} if licensed, {@code Boolean.FALSE} if verified-not-licensed,
     *         or {@code null} if the licensing subsystem has not initialized yet (first-launch).
     */
    Boolean isLicensed();

    void requestLicense(String message);
}
