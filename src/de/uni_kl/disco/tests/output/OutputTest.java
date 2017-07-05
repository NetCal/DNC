/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera"
 *
 * Copyright (C) 2015 - 2017 Steffen Bondorf
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package de.uni_kl.disco.tests.output;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_kl.disco.nc.AnalysisConfig;
import de.uni_kl.disco.nc.CalculatorConfig;
import de.uni_kl.disco.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.disco.nc.AnalysisConfig.GammaFlag;
import de.uni_kl.disco.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.disco.nc.analyses.PmooAnalysis;
import de.uni_kl.disco.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.disco.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.disco.network.Flow;
import de.uni_kl.disco.network.Network;
import de.uni_kl.disco.network.NetworkFactory;
import de.uni_kl.disco.numbers.Num;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.assertEquals;

public class OutputTest {
    private static Network network_given;

    private static Network network_saved;
    private static String saved_network_path = "src/unikl/disco/tests/output/";
    private static String saved_network_package = "unikl.disco.tests.output";
    private static String saved_network_name = "OutputTestNetworkSaved";

    @SuppressFBWarnings(value = {"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "DE_MIGHT_IGNORE"},
            justification = "Returned File object is only used for deletion. Just ignore exception raised of there is no file to delete.")
    @BeforeClass
    public static void createNetworks() throws Exception {
        File saved_network_java = new File(saved_network_path + saved_network_name + ".java");

        try {
            saved_network_java.delete();
            new File(saved_network_path + saved_network_name + ".class").delete();
        } catch (Exception e) {
            // Ignore that there were no files to delete.
        }

        try {
            // Instantiate the given network and save it.
            network_given = new OutputTestNetwork().getNetwork();
            network_given.saveAs(saved_network_path, saved_network_name + ".java", saved_network_package);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        // Compile the saved network factory, load the class and instantiate it.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, saved_network_java.getPath());

        // Load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File(saved_network_path).toURI().toURL()});

        try {
            Class<?> cls = Class.forName(saved_network_package + "." + saved_network_name, true, classLoader);
            Object instance = cls.newInstance();

            NetworkFactory network_newly_compiled = (NetworkFactory) instance;
            network_saved = network_newly_compiled.getNetwork();

            classLoader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Test
    public void tests() throws Exception {
        // Analysis configurations
        CalculatorConfig.disableAllChecks();

        AnalysisConfig base_config = new AnalysisConfig();

        base_config.setUseTbrlConvolution(true);
        base_config.setUseTbrlDeconvolution(true);

        base_config.setUseGamma(GammaFlag.GLOBALLY_OFF);
        base_config.setUseExtraGamma(GammaFlag.GLOBALLY_OFF);

        base_config.setMultiplexingDiscipline(MuxDiscipline.GLOBAL_ARBITRARY);


        AnalysisConfig pboo_concat_config = base_config.copy();
        pboo_concat_config.setArrivalBoundMethod(ArrivalBoundMethod.PBOO_CONCATENATION);

        AnalysisConfig pboo_perHop_config = base_config.copy();
        pboo_perHop_config.setArrivalBoundMethod(ArrivalBoundMethod.PBOO_PER_HOP);

        AnalysisConfig pmoo_concat_config = base_config.copy();
        pmoo_concat_config.setArrivalBoundMethod(ArrivalBoundMethod.PMOO);


        // Given Network
        // TFA + aggrPBOO-AB
        TotalFlowAnalysis tfa_analysis_1 = new TotalFlowAnalysis(network_given, pboo_concat_config);
        // SFA + aggrPBOO-AB
        SeparateFlowAnalysis sfa_analysis_1 = new SeparateFlowAnalysis(network_given, pboo_perHop_config);
        // PMOO + aggrPMOO-AB
        PmooAnalysis pmoo_analysis_1 = new PmooAnalysis(network_given, pmoo_concat_config);

        // Saved Network:
        // TFA + aggrPBOO-AB
        TotalFlowAnalysis tfa_analysis_2 = new TotalFlowAnalysis(network_saved, pboo_concat_config);
        // SFA + aggrPBOO-AB
        SeparateFlowAnalysis sfa_analysis_2 = new SeparateFlowAnalysis(network_saved, pboo_concat_config);
        // PMOO + aggrPMOO-AB
        PmooAnalysis pmoo_analysis_2 = new PmooAnalysis(network_saved, pmoo_concat_config);


        // Run the analyses
        Num[] d_tfa_given_network = new Num[network_given.numFlows()];
        Num[] d_sfa_given_network = new Num[network_given.numFlows()];
        Num[] d_pmoo_given_network = new Num[network_given.numFlows()];

        int index_id;
        for (Flow flow_of_interest : network_given.getFlows()) {
            index_id = Integer.parseInt(flow_of_interest.getAlias().substring(1));

            // TFAs
            tfa_analysis_1.performAnalysis(flow_of_interest);
            d_tfa_given_network[index_id] = tfa_analysis_1.getDelayBound();

            // SFAs
            sfa_analysis_1.performAnalysis(flow_of_interest);
            d_sfa_given_network[index_id] = sfa_analysis_1.getDelayBound();

            // PMOOs
            pmoo_analysis_1.performAnalysis(flow_of_interest);
            d_pmoo_given_network[index_id] = pmoo_analysis_1.getDelayBound();
        }


        Num[] d_tfa_saved_network = new Num[network_saved.numFlows()];
        Num[] d_sfa_saved_network = new Num[network_saved.numFlows()];
        Num[] d_pmoo_saved_network = new Num[network_saved.numFlows()];

        for (Flow flow_of_interest : network_saved.getFlows()) {
            index_id = Integer.parseInt(flow_of_interest.getAlias().substring(1));

            // TFAs
            tfa_analysis_2.performAnalysis(flow_of_interest);
            d_tfa_saved_network[index_id] = tfa_analysis_2.getDelayBound();

            // SFAs
            sfa_analysis_2.performAnalysis(flow_of_interest);
            d_sfa_saved_network[index_id] = sfa_analysis_2.getDelayBound();

            // PMOOs
            pmoo_analysis_2.performAnalysis(flow_of_interest);
            d_pmoo_saved_network[index_id] = pmoo_analysis_2.getDelayBound();
        }

        for (int i = 0; i < network_saved.numFlows(); i++) {
            assertEquals("TFA delay, flow " + Integer.toString(i), d_tfa_given_network[i], d_tfa_saved_network[i]);
            assertEquals("SFA delay, flow " + Integer.toString(i), d_sfa_given_network[i], d_sfa_saved_network[i]);
            assertEquals("PMOO delay, flow " + Integer.toString(i), d_pmoo_given_network[i], d_pmoo_saved_network[i]);
        }
    }

    @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", justification = "Returned File object is only used for deletion.")
    @After
    public void restoreDummyTestNetworkSaved() throws Exception {
        new File(saved_network_path + saved_network_name + ".java").delete();
        new File(saved_network_path + saved_network_name + ".class").delete();
    }
}