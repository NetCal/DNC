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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.discodnc;

import de.uni_kl.cs.discodnc.nc.Analysis.Analyses;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.numbers.Num;

import java.util.HashSet;
import java.util.Set;

public class TA_4S_1SC_2F_1AC_2P_Results extends DncTestResults {

	protected TA_4S_1SC_2F_1AC_2P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory();

		Set<Set<ArrivalBoundMethod>> ab_sets_PBOO = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_PBOO.add(DncTestMethodSources.single_1);
		ab_sets_PBOO.add(DncTestMethodSources.single_2);
		ab_sets_PBOO.add(DncTestMethodSources.pair_1);
		
		for( Set<ArrivalBoundMethod> ab_set : ab_sets_PBOO ) {
			// TFA
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(7985, 64), num_factory.create(550));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(65), num_factory.create(550));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2335, 12), num_factory.create(1700, 3));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(130), num_factory.create(550));

			// SFA
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(535, 6), num_factory.create(925, 2));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(355, 6), num_factory.create(625, 2));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(105), num_factory.create(1625, 3));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(235, 3), num_factory.create(1225, 3));

			// PMOO
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(290, 3), num_factory.create(500));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(190, 3), num_factory.create(1000, 3));
		}
		
		// For some Flows, PMOO Arrival Bounding yields better cross-traffic arrivals!
		// For completeness, we need to add all bounds.
		Set<Set<ArrivalBoundMethod>> ab_sets_PMOO = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_PMOO.add(DncTestMethodSources.single_3);
		ab_sets_PMOO.add(DncTestMethodSources.pair_2);
		ab_sets_PMOO.add(DncTestMethodSources.pair_3);
		ab_sets_PMOO.add(DncTestMethodSources.triplet);
		
		for( Set<ArrivalBoundMethod> ab_set : ab_sets_PMOO ) {
			// TFA
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(7985, 64), num_factory.create(550));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(65), num_factory.create(550));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(765, 4), num_factory.create(550));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(130), num_factory.create(550));

			// SFA
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(535, 6), num_factory.create(925, 2));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(355, 6), num_factory.create(625, 2));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(105), num_factory.create(1625, 3));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(235, 3), num_factory.create(1225, 3));

			// PMOO
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(290, 3), num_factory.create(500));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(190, 3), num_factory.create(1000, 3));
		}

		addBounds(1, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(550));
	}
}