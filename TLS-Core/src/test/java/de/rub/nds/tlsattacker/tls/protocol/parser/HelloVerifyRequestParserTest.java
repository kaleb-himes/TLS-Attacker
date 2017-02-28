/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.tls.protocol.parser;

import de.rub.nds.tlsattacker.tls.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.tls.protocol.message.HelloVerifyRequestMessage;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Merget - robert.merget@rub.de
 */
@RunWith(Parameterized.class)
public class HelloVerifyRequestParserTest {

    @Parameterized.Parameters
    public static Collection<Object[]> generateData() {
        return Arrays
                .asList(new Object[][]{
                    {},
                    {}});
    }
    private byte[] message;
    private int start;
    private byte[] expectedPart;

    private HandshakeMessageType type;
    private int length;

    private byte[] protocolVersion;
    private byte cookieLength;
    private byte[] cookie;

    public HelloVerifyRequestParserTest(byte[] message, int start, byte[] expectedPart, HandshakeMessageType type, int length, byte[] protocolVersion, byte cookieLength, byte[] cookie) {
        this.message = message;
        this.start = start;
        this.expectedPart = expectedPart;
        this.type = type;
        this.length = length;
        this.protocolVersion = protocolVersion;
        this.cookieLength = cookieLength;
        this.cookie = cookie;
    }

    /**
     * Test of parse method, of class HelloVerifyRequestParser.
     */
    @Test
    public void testParse() {
        HelloVerifyRequestParser parser = new HelloVerifyRequestParser(start, message);
        HelloVerifyRequestMessage msg = parser.parse();
        assertArrayEquals(expectedPart, msg.getCompleteResultingMessage().getValue());
        assertTrue(msg.getLength().getValue() == length);
        assertTrue(msg.getType().getValue() == type.getValue());
        assertArrayEquals(protocolVersion, msg.getProtocolVersion().getValue());
        assertArrayEquals(cookie, msg.getCookie().getValue());
        assertTrue(cookieLength == msg.getCookieLength().getValue());
    }
}
