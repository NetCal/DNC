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

public class S_1SC_10F_10AC_Results extends DncTestResults {

	protected S_1SC_10F_10AC_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory();

		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// TFA
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(15.5), num_factory.create(110));
			addBounds(6,  Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(15.5), num_factory.create(110));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(310, 9), num_factory.create(110));
			addBounds(6,  Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(310, 9), num_factory.create(110));

			// SFA
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1796, 115), num_factory.create(127, 50));
			addBounds(6,  Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(2099, 130), num_factory.create(434, 25));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(775, 23), num_factory.create(100, 23));
			addBounds(6,  Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(775, 26), num_factory.create(350, 13));

			// PMOO
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(775, 23), num_factory.create(100, 23));
			addBounds(6,  Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(775, 26), num_factory.create(350, 13));
		}

		addBounds(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(110));
		addBounds(6, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(110));
	}
}
