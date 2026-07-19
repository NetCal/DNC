package org.networkcalculus.dnc.sanitychecknew.checks;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.sanitychecknew.AbstractSanityCheck;
import org.networkcalculus.dnc.sanitychecknew.Sanity_Check;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.TotalFlowAnalysis;
import org.networkcalculus.dnc.measurements.networks.Network5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Check5 extends AbstractSanityCheck implements Sanity_Check {

    private final Network5 network5;

    public Check5() {
        this.network5 = new Network5();
    }

    public static void main(String[] args) {
        Check5 check = new Check5();

        try {
            check.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        ServerGraph sg = this.network5.createServerGraph();
        AnalysisConfig configuration_arb = new AnalysisConfig();
        configuration_arb.enforceMultiplexing(AnalysisConfig.MultiplexingEnforcement.GLOBAL_ARBITRARY);
        AnalysisConfig configuration_fifo = new AnalysisConfig();
        configuration_fifo.enforceMultiplexing(AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO);

        StringBuilder output = new StringBuilder();

        List<Flow> flows = new ArrayList<>(sg.getFlows());
        flows.sort(Comparator.comparing(Flow::getId));

        for (Flow flow_of_interest : flows) {
            output.append("Flow of interest: ").append(flow_of_interest.toString()).append("\n\n");

            // --- Total Flow Analysis ARB ---
            output.append("--- Total Flow Analysis ARB---\n");
            TotalFlowAnalysis tfa = new TotalFlowAnalysis(sg, configuration_arb);
            try {
                tfa.performAnalysis(flow_of_interest);
                output.append("delay bound     : ").append(tfa.getDelayBound().toDouble()).append("\n");
                output.append("     per server : ").append(tfa.getServerDelayBoundMapString()).append("\n");
                output.append("backlog bound   : ").append(tfa.getBacklogBound().toDouble()).append("\n");
                output.append("     per server : ").append(tfa.getServerBacklogBoundMapString()).append("\n");
                output.append("alpha per server: ").append(tfa.getServerAlphasMapString()).append("\n");
            } catch (Exception e) {
                output.append("TFA analysis failed with: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
            output.append("\n");

            // --- Total Flow Analysis FIFO ---
            output.append("--- Total Flow Analysis FIFO---\n");
            TotalFlowAnalysis tfa_fifo = new TotalFlowAnalysis(sg, configuration_fifo);
            try {
                tfa_fifo.performAnalysis(flow_of_interest);
                output.append("delay bound     : ").append(tfa_fifo.getDelayBound().toDouble()).append("\n");
                output.append("     per server : ").append(tfa_fifo.getServerDelayBoundMapString()).append("\n");
                output.append("backlog bound   : ").append(tfa_fifo.getBacklogBound().toDouble()).append("\n");
                output.append("     per server : ").append(tfa_fifo.getServerBacklogBoundMapString()).append("\n");
                output.append("alpha per server: ").append(tfa_fifo.getServerAlphasMapString()).append("\n");
            } catch (Exception e) {
                output.append("TFA analysis failed with: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
            output.append("\n");

            // --- Separated Flow Analysis ARB ---
            output.append("--- Separated Flow Analysis ARB ---\n");
            SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(sg, configuration_arb);
            try {
                sfa.performAnalysis(flow_of_interest);
                output.append("e2e SFA SCs     : ").append(sfa.getLeftOverServiceCurves()).append("\n");
                output.append("     per server : ").append(sfa.getServerLeftOverBetasMapString()).append("\n");
                output.append("xtx per server  : ").append(sfa.getServerAlphasMapString()).append("\n");
                output.append("delay bound     : ").append(sfa.getDelayBound().toDouble()).append("\n");
                output.append("backlog bound   : ").append(sfa.getBacklogBound().toDouble()).append("\n");
            } catch (Exception e) {
                output.append("SFA analysis failed with: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
            output.append("\n");

            // --- Separated Flow Analysis FIFO ---
            output.append("--- Separated Flow Analysis FIFO ---\n");
            SeparateFlowAnalysis sfa_fifo = new SeparateFlowAnalysis(sg, configuration_fifo);
            try {
                sfa_fifo.performAnalysis(flow_of_interest);
                output.append("e2e SFA SCs     : ").append(sfa_fifo.getLeftOverServiceCurves()).append("\n");
                output.append("     per server : ").append(sfa_fifo.getServerLeftOverBetasMapString()).append("\n");
                output.append("xtx per server  : ").append(sfa_fifo.getServerAlphasMapString()).append("\n");
                output.append("delay bound     : ").append(sfa_fifo.getDelayBound().toDouble()).append("\n");
                output.append("backlog bound   : ").append(sfa_fifo.getBacklogBound().toDouble()).append("\n");
            } catch (Exception e) {
                output.append("SFA analysis failed with: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
            output.append("\n\n");
        }

        try {
            writeResultToFile("Check5_results", output.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}