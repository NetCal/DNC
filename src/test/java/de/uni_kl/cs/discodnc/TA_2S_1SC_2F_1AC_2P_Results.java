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
import de.uni_kl.cs.discodnc.numbers.implementations.RealDoublePrecision;
import de.uni_kl.cs.discodnc.numbers.implementations.RealSinglePrecision;

import java.util.Set;

public class TA_2S_1SC_2F_1AC_2P_Results extends DncTestResults {

	protected TA_2S_1SC_2F_1AC_2P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory(CalculatorConfig.getInstance().getNumBackend());
		
		RealDoublePrecision real_double_epsilon;
		RealSinglePrecision real_single_epsilon;
		RationalBigInt rational_bigint_epsilon = new RationalBigInt(1, 1000000000);
		
		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(305, 4), num_factory.create(350));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(48.75), num_factory.create(350));
			
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(55), num_factory.create(350));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(55, 2), num_factory.create(350));

			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(50), num_factory.create(800, 3));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(515, 12), num_factory.create(925, 4));
			
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(110, 3), num_factory.create(200));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(335, 12), num_factory.create(625, 4));
			
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		SFA delay ==> expected <50.0> but was <49.99999999999999>
			 * 		SFA backlog ==> expected <266.6666666666667> but was <266.66666666666663>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		SFA delay ==> expected <50> but was <50.000004>
			 * 		SFA backlog ==> expected <266.6666564941406> but was <266.66668701171875>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA delay ==> expected <50> but was <7036874417766399 / 140737488355328>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(6e-14));
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(3.25e-5));
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(50), num_factory.create(800, 3));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(110, 3), num_factory.create(200));
			
			/*
			 * Observed test failures:
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		PMOO backlog ==> expected <800 / 3> but was <4691249611844267 / 17592186044416>
			 * 		PMOO delay ==> expected <110 / 3> but was <5160374573028693 / 140737488355328>
			 */
			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
		}

		// --------------------------------------------------------------------------------------------------------------
	    // Sink tree
	    // --------------------------------------------------------------------------------------------------------------
		addBounds(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(350));
		addBounds(1, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(350));
	}
}