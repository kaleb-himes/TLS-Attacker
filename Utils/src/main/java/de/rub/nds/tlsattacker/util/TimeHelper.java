/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.util;

/**
 *
 * @author Robert Merget - robert.merget@rub.de
 */
public class TimeHelper {
    private static TimeProvider provider;

    public static long getTime() {
        if (provider == null) {
            provider = new RealTimeProvider();
        }
        return provider.getTime();
    }

    public static void setProvider(TimeProvider provider) {
        TimeHelper.provider = provider;
    }

}
