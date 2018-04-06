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

import java.util.HashSet;
import java.util.Set;

public class TA_3S_1SC_3F_1AC_3P_Results extends DncTestResults {

	protected TA_3S_1SC_3F_1AC_3P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory(CalculatorConfig.getInstance().getNumBackend());
		
		RealDoublePrecision real_double_epsilon;
		RealSinglePrecision real_single_epsilon;
		RationalBigInt rational_bigint_epsilon = new RationalBigInt(1, 1000000000);

		Set<Set<ArrivalBoundMethod>> ab_sets_PBOO = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_PBOO.add(DncTestMethodSources.single_1);
		ab_sets_PBOO.add(DncTestMethodSources.single_2);
		ab_sets_PBOO.add(DncTestMethodSources.pair_1);
		
		for( Set<ArrivalBoundMethod> ab_set : ab_sets_PBOO ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(110), num_factory.create(450));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(55), num_factory.create(450));
			
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1405, 18), num_factory.create(5225, 9));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(2205, 64), num_factory.create(7825, 16));
			
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(16925, 90), num_factory.create(5225, 9));
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(5725, 64), num_factory.create(7825, 16));

			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(65), num_factory.create(1025, 3));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(295, 6), num_factory.create(262.5));
			
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1405, 27), num_factory.create(7475, 27));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(6695, 192), num_factory.create(12225, 64));
			
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(280, 3), num_factory.create(1450, 3));
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(845, 12), num_factory.create(1475, 4));

			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(170, 3), num_factory.create(300));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(1405, 27), num_factory.create(7475, 27));
			addBounds(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(85), num_factory.create(1325, 3));
		}
		
		// For some Flows, PMOO Arrival Bounding yields better cross-traffic arrivals!
		// For completeness, we need to add all bounds.
		Set<Set<ArrivalBoundMethod>> ab_sets_PMOO = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_PMOO.add(DncTestMethodSources.single_3);
		ab_sets_PMOO.add(DncTestMethodSources.pair_2);
		ab_sets_PMOO.add(DncTestMethodSources.pair_3);
		ab_sets_PMOO.add(DncTestMethodSources.triplet);
		
		for( Set<ArrivalBoundMethod> ab_set : ab_sets_PMOO ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(110), num_factory.create(450));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(55), num_factory.create(450));
			
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(72.5), num_factory.create(525));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(2205, 64), num_factory.create(7825, 16));
			
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(182.5), num_factory.create(525));
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(5725, 64), num_factory.create(7825, 16));
	
			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(65), num_factory.create(1025, 3));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(295, 6), num_factory.create(262.5));
			
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(145, 3), num_factory.create(775, 3));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(6695, 192), num_factory.create(12225, 64));
			
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(280, 3), num_factory.create(1450, 3));
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(845, 12), num_factory.create(1475, 4));
	
			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(170, 3), num_factory.create(300));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(145, 3), num_factory.create(775, 3));
			addBounds(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(85), num_factory.create(1325, 3));
		}

		// --------------------------------------------------------------------------------------------------------------
	    // Sink tree
	    // --------------------------------------------------------------------------------------------------------------
		addBounds(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(450));
		addBounds(1, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(1375));
		addBounds(2, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(1375));
		
		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			/*
			 * Observed test failures:
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		TFA delay ==> expected <1405 / 18> but was <6865839275667911 / 87960930222080>
			 * 		TFA delay ==> expected <3385 / 18> but was <16541541600096711 / 87960930222080>
			 */
			addEpsilon(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.TFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(2, Analyses.TFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		SFA backlog ==> expected <341.6666666666667> but was <341.66666666666663>
			 * 		SFA delay ==> expected <52.03703703703704> but was <52.03703703703703>
			 * 		SFA backlog ==> expected <483.3333333333333> but was <483.33333333333326>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		SFA backlog ==> expected <341.66666> but was <341.6667>
			 * 		SFA backlog ==> expected <341.6666564941406> but was <341.66668701171875>
			 * 		SFA delay ==> expected <48.333332> but was <48.333336>
			 * 		SFA backlog ==> expected <483.33334> but was <483.33337>
			 * 		SFA backlog ==> expected <483.3333435058594> but was <483.3333740234375>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA backlog ==> expected <1025 / 3> but was <3005331782587733 / 8796093022208>
			 * 		SFA delay ==> expected <145 / 3> but was <3401155968587093 / 70368744177664>
			 * 		SFA delay ==> expected <280 / 3> but was <6567749456581973 / 70368744177664>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(7e-14));
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			
			real_double_epsilon = new RealDoublePrecision(new Double(1e-14));
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			
			real_double_epsilon = new RealDoublePrecision(new Double(7e-14));
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(3e-4));
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(4e-6));
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(4.25e-5));
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(2, Analyses.SFA, ab_set, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		PMOO delay ==> expected <52.03703703703704> but was <52.03703703703703>
			 * 		PMOO backlog ==> expected <258.3333333333333> but was <258.33333333333337>
			 * 		PMOO backlog ==> expected <441.6666666666667> but was <441.66666666666663>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		PMOO backlog ==> expected <441.66666> but was <441.6667>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		PMOO delay ==> expected <170 / 3> but was <7975124340135253 / 140737488355328>
			 * 		PMOO delay ==> expected <145 / 3> but was <3401155968587093 / 70368744177664>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(7e-14));
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(4e-5));
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
		}
	}
}