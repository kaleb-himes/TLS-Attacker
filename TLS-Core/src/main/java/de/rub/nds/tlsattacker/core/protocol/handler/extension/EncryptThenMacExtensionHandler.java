/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import static de.rub.nds.tlsattacker.core.protocol.handler.extension.ExtensionHandler.LOGGER;
import de.rub.nds.tlsattacker.core.protocol.message.extension.EncryptThenMacExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.EncryptThenMacExtensionParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.EncryptThenMacExtensionPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.EncryptThenMacExtensionSerializer;
import de.rub.nds.tlsattacker.core.workflow.TlsContext;

/**
 *
 * @author Matthias Terlinde <matthias.terlinde@rub.de>
 */
public class EncryptThenMacExtensionHandler extends ExtensionHandler<EncryptThenMacExtensionMessage> {
    public EncryptThenMacExtensionHandler(TlsContext context) {
        super(context);
    }

    @Override
    public EncryptThenMacExtensionParser getParser(byte[] message, int pointer) {
        return new EncryptThenMacExtensionParser(pointer, message);
    }

    @Override
    public EncryptThenMacExtensionPreparator getPreparator(EncryptThenMacExtensionMessage message) {
        return new EncryptThenMacExtensionPreparator(context, message);
    }

    @Override
    public EncryptThenMacExtensionSerializer getSerializer(EncryptThenMacExtensionMessage message) {
        return new EncryptThenMacExtensionSerializer(message);
    }

    @Override
    public void adjustTLSContext(EncryptThenMacExtensionMessage message) {
        context.setEncryptThenMacExtensionIsPresent(true);
        LOGGER.debug("Adjusted the tls context. The encrypt then mac extension is present.");
    }
}
