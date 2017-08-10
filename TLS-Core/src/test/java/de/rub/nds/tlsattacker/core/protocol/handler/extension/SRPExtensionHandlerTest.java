/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.SRPExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.SRPExtensionParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.SRPExtensionPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.SRPExtensionSerializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Matthias Terlinde <matthias.terlinde@rub.de>
 */
public class SRPExtensionHandlerTest {

    private final byte[] srpIdentifier = new byte[] { 0x00, 0x01, 0x02, 0x03 };
    private final int srpIdentifierLength = 4;
    private SRPExtensionHandler handler;
    private TlsContext context;

    @Before
    public void setUp() {
        context = new TlsContext();
        handler = new SRPExtensionHandler(context);
    }

    @Test
    public void testAdjustTLSContext() {
        SRPExtensionMessage msg = new SRPExtensionMessage();
        msg.setSrpIdentifier(srpIdentifier);
        msg.setSrpIdentifierLength(srpIdentifierLength);

        handler.adjustTLSContext(msg);

        assertArrayEquals(srpIdentifier, context.getSecureRemotePasswordExtensionIdentifier());
    }

    @Test
    public void testGetParser() {
        assertTrue(handler.getParser(new byte[0], 0) instanceof SRPExtensionParser);
    }

    @Test
    public void testGetPreparator() {
        assertTrue(handler.getPreparator(new SRPExtensionMessage()) instanceof SRPExtensionPreparator);
    }

    @Test
    public void testGetSerializer() {
        assertTrue(handler.getSerializer(new SRPExtensionMessage()) instanceof SRPExtensionSerializer);
    }

}