package com.telappoint.notification.common.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

final class SessionIdentifierGenerator {
    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }
}