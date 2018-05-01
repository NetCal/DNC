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

import java.util.Set;

public class TR_7S_1SC_3F_1AC_3P_Results extends DncTestResults {

	protected TR_7S_1SC_3F_1AC_3P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory();
		
		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// TFA
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(395, 2), num_factory.create(1375));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(875, 4), num_factory.create(1375));
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(180), num_factory.create(1375));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(660), num_factory.create(1375));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2725, 4), num_factory.create(1375));
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1155, 2), num_factory.create(1375));

			// SFA
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(165), num_factory.create(1675, 2));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(165), num_factory.create(1675, 2));
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(295, 2), num_factory.create(750));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1735, 6), num_factory.create(4375, 3));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1655, 6), num_factory.create(4175, 3));
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(505, 2), num_factory.create(1275));

			// PMOO
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(355, 2), num_factory.create(900));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(375, 2), num_factory.create(950));
			addBounds(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(355, 2), num_factory.create(900));
		}

		addBounds(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(1375));
		addBounds(1, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(1375));
		addBounds(2, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(1375));
	}
}