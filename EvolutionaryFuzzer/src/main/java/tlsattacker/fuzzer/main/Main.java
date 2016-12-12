/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package tlsattacker.fuzzer.main;

import com.beust.jcommander.JCommander;
import de.rub.nds.tlsattacker.tls.config.GeneralConfig;
import de.rub.nds.tlsattacker.tls.constants.ConnectionEnd;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.graph.DirectedMultigraph;
import tlsattacker.fuzzer.agent.AgentFactory;
import tlsattacker.fuzzer.calibration.TimeoutCalibrator;
import tlsattacker.fuzzer.config.CalibrationConfig;
import tlsattacker.fuzzer.config.EvolutionaryFuzzerConfig;
import tlsattacker.fuzzer.config.ExecuteFaultyConfig;
import tlsattacker.fuzzer.config.ServerConfig;
import tlsattacker.fuzzer.config.TestCrashesConfig;
import tlsattacker.fuzzer.config.TraceTypesConfig;
import tlsattacker.fuzzer.controller.Controller;
import tlsattacker.fuzzer.controller.ControllerFactory;
import tlsattacker.fuzzer.exceptions.FuzzerConfigurationException;
import tlsattacker.fuzzer.exceptions.IllegalAgentException;
import tlsattacker.fuzzer.exceptions.IllegalAnalyzerException;
import tlsattacker.fuzzer.exceptions.IllegalCertificateMutatorException;
import tlsattacker.fuzzer.exceptions.IllegalControllerException;
import tlsattacker.fuzzer.exceptions.IllegalMutatorException;
import tlsattacker.fuzzer.executor.SingleTLSExecutor;
import tlsattacker.fuzzer.mutator.certificate.FixedCertificateMutator;
import tlsattacker.fuzzer.result.TestVectorResult;
import tlsattacker.fuzzer.server.ServerManager;
import tlsattacker.fuzzer.server.ServerSerializer;
import tlsattacker.fuzzer.server.TLSServer;
import tlsattacker.fuzzer.testvector.TestVector;
import tlsattacker.fuzzer.testvector.TestVectorSerializer;
import tlsattacker.fuzzer.workflow.MessageFlow;
import tlsattacker.fuzzer.workflow.WorkflowGraphBuilder;
import tlsattacker.fuzzer.workflow.WorkflowTraceType;
import tlsattacker.fuzzer.workflow.WorkflowTraceTypeManager;
import weka.core.Utils;

/**
 * Tha main class which parses the commands passed to the fuzzer and starts it
 *
 * @author Robert Merget - robert.merget@rub.de
 */
public class Main {

    /**
     * Main function which Starts the fuzzer
     *
     * @param args
     *            Arguments which are parsed
     */
    public static void main(String args[]) throws IllegalAgentException, FuzzerConfigurationException {
        LOG.log(Level.FINE, Utils.arrayToString(args));
        GeneralConfig generalConfig = new GeneralConfig();

        EvolutionaryFuzzerConfig evoConfig = new EvolutionaryFuzzerConfig();
        ServerConfig serverConfig = new ServerConfig();
        TraceTypesConfig traceTypesConfig = new TraceTypesConfig();
        ExecuteFaultyConfig faultyConfig = new ExecuteFaultyConfig();
        CalibrationConfig calibrationConfig = new CalibrationConfig();
        TestCrashesConfig testCrashedConfig = new TestCrashesConfig();
        JCommander jc = new JCommander(generalConfig);
        jc.addCommand(EvolutionaryFuzzerConfig.ATTACK_COMMAND, evoConfig);
        jc.addCommand("tracetypes", traceTypesConfig);
        jc.addCommand("execute-faulty", faultyConfig);
        jc.addCommand("new-server", serverConfig);
        jc.addCommand("calibrate", calibrationConfig);
        jc.addCommand("test-certificates", evoConfig);
        jc.addCommand("test-crashes", testCrashedConfig);
        try {
            jc.parse(args);
            if (generalConfig.isHelp() || jc.getParsedCommand() == null) {
                jc.usage();
                return;
            }
        } catch (Exception E) {
            LOG.log(Level.FINE, E.getLocalizedMessage(), E);
            jc.usage();
            return;
        }

        switch (jc.getParsedCommand()) {
            case EvolutionaryFuzzerConfig.ATTACK_COMMAND:
                try {
                    ServerManager serverManager = ServerManager.getInstance();
                    serverManager.init(evoConfig);
                    Controller controller = ControllerFactory.getController(evoConfig);
                    controller.startFuzzer();
                    controller.startInterface();
                } catch (IllegalCertificateMutatorException ex) {
                    LOG.info("Unknown Certificate Mutator. Aborting...");
                } catch (IllegalMutatorException ex) {
                    LOG.info("Unknown Mutator. Aborting...");
                } catch (IllegalAnalyzerException ex) {
                    LOG.info("Unknown Analyzer. Aborting...");
                } catch (IllegalControllerException ex) {
                    LOG.info("Unknown Controller. Aborting...");
                }
                break;
            case "tracetypes":
                File f = new File(traceTypesConfig.getTraceTypesFolder());
                if (f.exists() && f.isDirectory()) {
                    List<TestVector> vectors = TestVectorSerializer.readFolder(f);

                    LOG.log(Level.INFO, "Fininshed reading.");
                    Set<WorkflowTraceType> set = WorkflowTraceTypeManager.generateCleanTypeList(vectors,
                            ConnectionEnd.CLIENT);
                    LOG.log(Level.INFO, "Found {0} different TraceTypes", set.size());
                    DirectedMultigraph<Integer, MessageFlow> graph = WorkflowGraphBuilder.generateWorkflowGraph(set);
                    LOG.log(Level.INFO, "Printing out graph in .DOT format.");
                    String dotFormat = WorkflowGraphBuilder.generateDOTGraph(set);
                    LOG.log(Level.INFO, dotFormat);
                } else {
                    LOG.log(Level.INFO, "The Specified Folder does not exist or is not a Folder:{0}",
                            f.getAbsolutePath());
                }
                break;
            case "execute-faulty":
                ServerManager manager = ServerManager.getInstance();
                manager.init(faultyConfig);
                f = new File(faultyConfig.getOutputFaultyFolder());
                List<TestVector> vectors = TestVectorSerializer.readFolder(f);
                for (TestVector vector : vectors) {
                    LOG.log(Level.INFO, "Trace:{0}", vector.getTrace().getName());
                    vector.getTrace().reset();
                    vector.getTrace().makeGeneric();
                    TLSServer server = ServerManager.getInstance().getFreeServer();
                    SingleTLSExecutor executor = new SingleTLSExecutor(faultyConfig, vector, server);
                    FutureTask<TestVectorResult> futureTask = new FutureTask<>(executor);
                    Thread t = new Thread(futureTask);
                    t.start();
                }
                break;
            case "new-server":
                TLSServer server = new TLSServer(null, serverConfig.getIp(), serverConfig.getPort(),
                        serverConfig.getStartcommand(), serverConfig.getAccept(), serverConfig.getKillCommand(),
                        serverConfig.getMayorVersion(), serverConfig.getMinorVersion());
                {
                    try {
                        ServerSerializer.write(server, new File(serverConfig.getOutput()));
                        LOG.log(Level.INFO, "Wrote Server to:{0}", new File(serverConfig.getOutput()).getAbsolutePath());
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Could not write Server to file!", ex);
                    }
                }
                break;
            case "calibrate":
                TimeoutCalibrator calibrator = new TimeoutCalibrator(calibrationConfig);
                calibrator.setGainFactor(calibrationConfig.getGain());
                calibrator.setLimit(calibrationConfig.getTimeoutLimit());
                ServerManager.getInstance().init(calibrationConfig);
                int timeout = calibrator.calibrateTimeout();
                LOG.log(Level.INFO, "Recommended Timeout for this Server is:{0}", timeout);
                break;
            case "test-certificates":
                ServerManager.getInstance().init(calibrationConfig);
                FixedCertificateMutator mutator = new FixedCertificateMutator(calibrationConfig);
                mutator.selfTest();
                break;
            case "test-crashes":
                manager = ServerManager.getInstance();
                manager.init(testCrashedConfig);
                f = new File(testCrashedConfig.getCrashFolder());
                vectors = TestVectorSerializer.readFolder(f);
                for (TestVector vector : vectors) {
                    LOG.log(Level.INFO, "Trace:{0}", vector.getTrace().getName());
                    for (int i = 0; i < testCrashedConfig.getExecuteNumber(); i++) {
                        vector.getTrace().reset();
                        vector.getTrace().makeGeneric();
                        server = ServerManager.getInstance().getFreeServer();
                        SingleTLSExecutor executor = new SingleTLSExecutor(testCrashedConfig, vector, server);
                        FutureTask<TestVectorResult> futureTask = new FutureTask<>(executor);
                        Thread t = new Thread(futureTask);
                        t.start();
                    }
                }
                break;
            default:
                jc.usage();
        }

    }

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
}
