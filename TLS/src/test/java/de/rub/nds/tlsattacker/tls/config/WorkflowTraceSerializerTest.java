/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.tls.config;

import de.rub.nds.tlsattacker.dtls.record.DtlsRecord;
import de.rub.nds.tlsattacker.modifiablevariable.ModifiableVariableFactory;
import de.rub.nds.tlsattacker.modifiablevariable.VariableModification;
import de.rub.nds.tlsattacker.modifiablevariable.integer.IntegerModificationFactory;
import de.rub.nds.tlsattacker.modifiablevariable.integer.ModifiableInteger;
import de.rub.nds.tlsattacker.tls.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.tls.protocol.ProtocolMessage;
import de.rub.nds.tlsattacker.tls.record.Record;
import de.rub.nds.tlsattacker.tls.workflow.TlsContext;
import de.rub.nds.tlsattacker.tls.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.tls.workflow.WorkflowConfigurationFactory;
import de.rub.nds.tlsattacker.util.RandomHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Juraj Somorovsky <juraj.somorovsky@rub.de>
 */
public class WorkflowTraceSerializerTest {

    private static Logger LOGGER = LogManager.getLogger(WorkflowTraceSerializerTest.class);

    /**
     * Test of write method, of class WorkflowTraceSerializer.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteRead() throws Exception {
	WorkflowConfigurationFactory factory = WorkflowConfigurationFactory.createInstance(new ClientCommandConfig());
	TlsContext context = factory.createFullTlsContext();

	// pick random protocol message and initialize a record with modifiable
	// variable
	List<ProtocolMessage> pms = context.getWorkflowTrace().getProtocolMessages();
	int random = RandomHelper.getRandom().nextInt(pms.size());
	List<Record> records = new LinkedList<>();
	Record r = new Record();
	ModifiableInteger mv = ModifiableVariableFactory.createIntegerModifiableVariable();
	VariableModification<Integer> iam = IntegerModificationFactory.createRandomModification();
	iam.setPostModification(IntegerModificationFactory.explicitValue(random));
	mv.setModification(iam);
	r.setLength(mv);
	records.add(r);
	pms.get(random).setRecords(records);

	ByteArrayOutputStream os = new ByteArrayOutputStream();
	WorkflowTraceSerializer.write(os, context.getWorkflowTrace());

	String serializedWorkflow = new String(os.toByteArray());

	LOGGER.debug(serializedWorkflow);

	ByteArrayInputStream bis = new ByteArrayInputStream(serializedWorkflow.getBytes());
	WorkflowTrace wt = WorkflowTraceSerializer.read(bis);

	os = new ByteArrayOutputStream();
	WorkflowTraceSerializer.write(os, wt);

	Assert.assertArrayEquals("The serialized workflows have to be equal", serializedWorkflow.getBytes(),
		os.toByteArray());
    }

    /**
     * Test of write method, of class WorkflowTraceSerializer.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteReadDtls() throws Exception {
        ClientCommandConfig ccc = new ClientCommandConfig();
        ccc.setProtocolVersion(ProtocolVersion.DTLS12);
	WorkflowConfigurationFactory factory = WorkflowConfigurationFactory.createInstance(ccc);
	TlsContext context = factory.createFullTlsContext();

	// pick random protocol message and initialize a record with modifiable
	// variable
	List<ProtocolMessage> pms = context.getWorkflowTrace().getProtocolMessages();
	int random = RandomHelper.getRandom().nextInt(pms.size());
	List<Record> records = new LinkedList<>();
	DtlsRecord r = new DtlsRecord();
	ModifiableInteger mv = ModifiableVariableFactory.createIntegerModifiableVariable();
	VariableModification<Integer> iam = IntegerModificationFactory.createRandomModification();
	iam.setPostModification(IntegerModificationFactory.explicitValue(random));
	mv.setModification(iam);
	r.setLength(mv);
	records.add(r);
	pms.get(random).setRecords(records);

	ByteArrayOutputStream os = new ByteArrayOutputStream();
	WorkflowTraceSerializer.write(os, context.getWorkflowTrace());

	String serializedWorkflow = new String(os.toByteArray());

	LOGGER.debug(serializedWorkflow);

	ByteArrayInputStream bis = new ByteArrayInputStream(serializedWorkflow.getBytes());
	WorkflowTrace wt = WorkflowTraceSerializer.read(bis);

	os = new ByteArrayOutputStream();
	WorkflowTraceSerializer.write(os, wt);

	Assert.assertArrayEquals("The serialized workflows have to be equal", serializedWorkflow.getBytes(),
		os.toByteArray());
    }
}
