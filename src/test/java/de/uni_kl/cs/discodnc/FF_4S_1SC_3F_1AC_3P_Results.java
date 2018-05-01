/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, test_network.fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.discodnc;

import java.util.Set;

import de.uni_kl.cs.discodnc.nc.Analysis.Analyses;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.numbers.Num;

public class FF_4S_1SC_3F_1AC_3P_Results extends DncTestResults {

	protected FF_4S_1SC_3F_1AC_3P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory();

		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// TFA
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(5985, 64), num_factory.create(9425, 16));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(3965, 64), num_factory.create(9425, 16));
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(885, 16), num_factory.create(1825, 4));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(6425, 36), num_factory.create(6125, 9));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(11975, 90), num_factory.create(6125, 9));
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(685, 6), num_factory.create(1475, 3));

			// SFA
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1795, 24), num_factory.create(3125, 8));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(10715, 192), num_factory.create(18925, 64));
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(295, 6), num_factory.create(525, 2));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(875, 9), num_factory.create(4525, 9));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2095, 27), num_factory.create(10925, 27));
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(65), num_factory.create(1025, 3));

			// PMOO
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(875, 9), num_factory.create(4525, 9));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(2095, 27), num_factory.create(10925, 27));
			addBounds(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(65), num_factory.create(1025, 3));
		}
	}
}