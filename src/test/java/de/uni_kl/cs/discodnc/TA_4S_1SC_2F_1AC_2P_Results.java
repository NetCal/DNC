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
import de.uni_kl.cs.discodnc.nc.CalculatorConfig.NumImpl;
import de.uni_kl.cs.discodnc.numbers.Num;
import de.uni_kl.cs.discodnc.numbers.implementations.RationalBigInt;
import de.uni_kl.cs.discodnc.numbers.implementations.RealDoublePrecision;
import de.uni_kl.cs.discodnc.numbers.implementations.RealSinglePrecision;

import java.util.HashSet;
import java.util.Set;

public class TA_4S_1SC_2F_1AC_2P_Results extends DncTestResults {

	protected TA_4S_1SC_2F_1AC_2P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory();
		
		RealDoublePrecision real_double_epsilon;
		RealSinglePrecision real_single_epsilon;
		RationalBigInt rational_bigint_epsilon = new RationalBigInt(1, 1000000000);

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
		

		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		TFA delay ==> expected <194.58333333333334> but was <194.58333333333331>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		TFA delay ==> expected <194.58333> but was <194.58334>
			 * 		TFA delay ==> expected <194.5833282470703> but was <194.58334350585938>
			 * 		TFA backlog ==> expected <566.6666870117188> but was <566.6666259765625>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		TFA delay ==> expected <2335 / 12> but was <34231462011426133 / 175921860444160>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(3e-14));
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(6.25e-5));
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		SFA backlog ==> expected <408.3333333333333> but was <408.33333333333326>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		SFA backlog ==> expected <408.33334> but was <408.33337>
			 * 		SFA backlog ==> expected <408.3333435058594> but was <408.3333740234375>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA backlog ==> expected <1625 / 3> but was <4764550387029333 / 8796093022208>
			 * 		SFA delay ==> expected <235 / 3> but was <5512218293917013 / 70368744177664>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(6e-14));
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(3.25e-5));
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		PMOO backlog ==> expected <333.3333333333333> but was <333.33333333333337>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		PMOO backlog ==> expected <333.33334> but was <333.3333>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		PMOO delay ==> expected <290 / 3> but was <6802311937174187 / 70368744177664>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(7e-14));
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(4e-5));
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
		}
	}
}