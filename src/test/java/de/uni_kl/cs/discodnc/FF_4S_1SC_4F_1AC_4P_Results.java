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

import de.uni_kl.cs.discodnc.nc.Analysis.Analyses;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.numbers.Num;

import java.util.HashSet;
import java.util.Set;

public class FF_4S_1SC_4F_1AC_4P_Results extends DncTestResults {

	protected FF_4S_1SC_4F_1AC_4P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory();

		Set<Set<ArrivalBoundMethod>> ab_sets_PBOO = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_PBOO.add(DncTestMethodSources.single_1);
		ab_sets_PBOO.add(DncTestMethodSources.single_2);
		ab_sets_PBOO.add(DncTestMethodSources.pair_1);
		ab_sets_PBOO.add(DncTestMethodSources.pair_2);
		ab_sets_PBOO.add(DncTestMethodSources.pair_3);
		ab_sets_PBOO.add(DncTestMethodSources.triplet);
		
		for( Set<ArrivalBoundMethod> ab_set : ab_sets_PBOO ) {
			// TFA
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(3735, 32), num_factory.create(975));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(77.5), num_factory.create(975));
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(1875, 32), num_factory.create(3975, 8));
			addBounds(3, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(845, 8), num_factory.create(975));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1370, 3), num_factory.create(1400));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(395), num_factory.create(1400));
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1105, 6), num_factory.create(2075, 3));
			addBounds(3, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(462.5), num_factory.create(1400));

			// SFA
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1525, 16), num_factory.create(7825, 16));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(575, 8), num_factory.create(2975, 8));
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1695, 32), num_factory.create(8875, 32));
			addBounds(3, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1405, 16), num_factory.create(7225, 16));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(580, 3), num_factory.create(5875, 6));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(345, 2), num_factory.create(875));
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1625, 18), num_factory.create(4175, 9));
			addBounds(3, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(560, 3), num_factory.create(5675, 6));

			// PMOO
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(650, 3), num_factory.create(6575, 6));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(345, 2), num_factory.create(875));
			addBounds(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(305, 3), num_factory.create(3125, 6));
			addBounds(3, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(1145, 6), num_factory.create(2900, 3));
		}
		
		// For some Flows, PMOO Arrival Bounding yields worse cross-traffic arrivals!
		// For completeness, we need to add all bounds.
		Set<ArrivalBoundMethod> ab_set = DncTestMethodSources.single_3;
		
		// TFA
		addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(3735, 32), num_factory.create(975));
		addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(77.5), num_factory.create(975));
		addBounds(2, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(1875, 32), num_factory.create(3975, 8));
		addBounds(3, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(845, 8), num_factory.create(975));
		addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2765, 6), num_factory.create(8525, 6));
		addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2395, 6), num_factory.create(8525, 6));
		addBounds(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1105, 6), num_factory.create(2075, 3));
		addBounds(3, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1400, 3), num_factory.create(8525, 6));

		// SFA
		addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1525, 16), num_factory.create(7825, 16));
		addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(575, 8), num_factory.create(2975, 8));
		addBounds(2, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1695, 32), num_factory.create(8875, 32));
		addBounds(3, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1405, 16), num_factory.create(7225, 16));
		addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2345, 12), num_factory.create(11875, 12));
		addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2095, 12), num_factory.create(10625, 12));
		addBounds(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1625, 18), num_factory.create(4175, 9));
		addBounds(3, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(560, 3), num_factory.create(5675, 6));

		// PMOO
		addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(875, 4), num_factory.create(4425, 4));
		addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(2095, 12), num_factory.create(10625, 12));
		addBounds(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(305, 3), num_factory.create(3125, 6));
		addBounds(3, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(1145, 6), num_factory.create(2900, 3));
	}
}