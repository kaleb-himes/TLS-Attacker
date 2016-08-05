/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package Analyzer;

import Config.EvolutionaryFuzzerConfig;
import Graphs.BranchTrace;
import Graphs.CountEdge;
import Graphs.ProbeVertex;
import Result.MergeResult;
import Result.Result;
import Result.ResultContainer;
import TestVector.TestVectorSerializer;
import de.rub.nds.tlsattacker.tls.config.WorkflowTraceSerializer;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jgrapht.DirectedGraph;

/**
 * 
 * @author Robert Merget - robert.merget@rub.de
 */
public class IsGoodRule extends Rule {

    private static final Logger LOG = Logger.getLogger(IsGoodRule.class.getName());
    // BranchTrace with which other Workflows are merged
    private final BranchTrace branch;
    private EvolutionaryFuzzerConfig evoConfig;
    private int found = 0;

    public IsGoodRule(EvolutionaryFuzzerConfig evoConfig) {
	this.branch = new BranchTrace();
	this.evoConfig = evoConfig;
    }

    @Override
    public boolean applys(Result result) {
	MergeResult r = null;
	r = branch.merge(result.getBranchTrace());

	if (r != null && (r.getNewBranches() > 0 || r.getNewVertices() > 0)) {
	    LOG.log(Level.FINE, "Found a GoodTrace:{0}", r.toString());
	    return true;
	} else {
	    return false;
	}

    }

    @Override
    public void onApply(Result result) {
	found++;
	result.setGoodTrace(true);
	// It may be that we dont want to safe good Traces, for example if
	// we execute already saved Traces
	if (evoConfig.isSerialize()) {
	    File f = new File(evoConfig.getOutputFolder() + "good/" + result.getId());
	    try {
		f.createNewFile();
		TestVectorSerializer.write(f, result.getExecutedVector());
	    } catch (JAXBException | IOException E) {
		LOG.log(Level.SEVERE, "Could not write Results to Disk! Does the Fuzzer have the rights to write to "
			+ f.getAbsolutePath(), E);
	    }
	}
	result.getVector().getTrace().makeGeneric();
	ResultContainer.getInstance().addGoodVector(result.getVector());
    }

    public BranchTrace getBranchTrace() {
	return branch;
    }

    @Override
    public void onDecline(Result result) {
	result.setGoodTrace(Boolean.FALSE);
    }

    @Override
    public String report() {
	return "Vertices:" + branch.getVerticesCount() + " Edges:" + branch.getBranchCount() + " Good:" + found + "\n";
    }

}
