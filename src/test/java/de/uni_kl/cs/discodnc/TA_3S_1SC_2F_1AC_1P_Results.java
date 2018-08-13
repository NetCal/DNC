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

import java.util.Set;

public class TA_3S_1SC_2F_1AC_1P_Results extends DncTestResults {

	protected TA_3S_1SC_2F_1AC_1P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory();
		
		RealDoublePrecision real_double_epsilon;
		RealSinglePrecision real_single_epsilon;
		RationalBigInt rational_bigint_epsilon = new RationalBigInt(1, 1000000000);
		
		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(195), num_factory.create(650));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(97.5), num_factory.create(650));
			
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(195), num_factory.create(650));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(97.5), num_factory.create(650));

			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(320, 3), num_factory.create(550));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(965, 12), num_factory.create(1675, 4));
			
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(320, 3), num_factory.create(550));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(965, 12), num_factory.create(1675, 4));
			
			/*
			 * Observed test failures:
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA delay ==> expected <320 / 3> but was <7505999378950827 / 70368744177664>
			 * 		SFA delay ==> expected <965 / 12> but was <5658819844287147 / 70368744177664>
			 */
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(250, 3), num_factory.create(1300, 3));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(250, 3), num_factory.create(1300, 3));
			
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		PMOO delay ==> expected <83.33333333333333> but was <83.33333333333334>
			 * 		PMOO backlog ==> expected <433.3333333333333> but was <433.33333333333337>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		PMOO delay ==> expected <83.333336> but was <83.33333>
			 * 		PMOO delay ==> expected <83.33333587646484> but was <83.33332824707031>
			 * 		PMOO backlog ==> expected <433.3333435058594> but was <433.33331298828125>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		PMOO delay ==> expected <250 / 3> but was <2932031007402667 / 35184372088832>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(7e-14));
			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(3.25e-5));
			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
		}

		// --------------------------------------------------------------------------------------------------------------
	    // Sink tree
	    // --------------------------------------------------------------------------------------------------------------
		addBounds(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(650));
		addBounds(1, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(650));
	}
}