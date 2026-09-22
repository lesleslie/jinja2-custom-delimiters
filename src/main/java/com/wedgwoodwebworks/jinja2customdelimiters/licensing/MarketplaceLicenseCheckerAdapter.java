package com.wedgwoodwebworks.jinja2customdelimiters.licensing;

/** Adapter: exposes {@link MarketplaceLicenseChecker}'s static methods as an instance API. */
public final class MarketplaceLicenseCheckerAdapter implements LicenseChecker {

    @Override
    public Boolean isLicensed() {
        return MarketplaceLicenseChecker.isLicensed();
    }

    @Override
    public void requestLicense(String message) {
        MarketplaceLicenseChecker.requestLicense(message);
    }
}
