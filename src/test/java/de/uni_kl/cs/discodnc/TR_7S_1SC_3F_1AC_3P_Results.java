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

public class TR_7S_1SC_3F_1AC_3P_Results extends DncTestResults {

	protected TR_7S_1SC_3F_1AC_3P_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory();
		
		RealDoublePrecision real_double_epsilon;
		RealSinglePrecision real_single_epsilon;
		RationalBigInt rational_bigint_epsilon = new RationalBigInt(1, 1000000000);
		
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
			
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		SFA delay ==> expected <289.1666666666667> but was <289.16666666666663>
			 * 		SFA backlog ==> expected <1458.3333333333333> but was <1458.333333333333>
			 * 		SFA backlog ==> expected <1391.6666666666667> but was <1391.6666666666665>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		SFA delay ==> expected <289.16666> but was <289.1667>
			 * 		SFA backlog ==> expected <1458.3333740234375> but was <1458.33349609375>
			 * 		SFA backlog ==> expected <1391.6666> but was <1391.6667>
			 * 		SFA backlog ==> expected <1391.6666259765625> but was <1391.666748046875>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA delay ==> expected <1735 / 6> but was <2543536898921813 / 8796093022208>
			 * 		SFA delay ==> expected <1655 / 6> but was <4852511317251413 / 17592186044416>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(3e-13));
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			
			real_double_epsilon = new RealDoublePrecision(new Double(2.5e-13));
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(1.3e-4)); // Set to prevent backlog bound test failure.
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(1.25e-4));
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

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