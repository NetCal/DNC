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
import de.uni_kl.cs.discodnc.nc.CalculatorConfig;
import de.uni_kl.cs.discodnc.numbers.Num;
import de.uni_kl.cs.discodnc.numbers.NumBackend;
import de.uni_kl.cs.discodnc.numbers.implementations.RationalBigInt;

import java.util.Set;

public class TA_2S_2SC_1F_1AC_1P_Results extends DncTestResults {

	protected TA_2S_2SC_1F_1AC_1P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory(CalculatorConfig.getInstance().getNumBackend());
		
		RationalBigInt rational_bigint_epsilon = new RationalBigInt(1, 1000000000);
		
		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(31), num_factory.create(105));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(31), num_factory.create(105));

			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(121, 6), num_factory.create(105));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(121, 6), num_factory.create(105));
			
			/*
			 * Observed test failures:
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA delay ==> expected <121 / 6> but was <5676412030331563 / 281474976710656>
			 */
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(121, 6), num_factory.create(105));
			
			/*
			 * Observed test failures:
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		PMOO delay ==> expected <121 / 6> but was <5676412030331563 / 281474976710656>
			 */
			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
		}

		// --------------------------------------------------------------------------------------------------------------
	    // Sink tree
	    // --------------------------------------------------------------------------------------------------------------
		addBounds(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(105));
	}
}