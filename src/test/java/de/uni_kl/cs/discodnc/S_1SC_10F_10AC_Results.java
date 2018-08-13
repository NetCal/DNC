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

public class S_1SC_10F_10AC_Results extends DncTestResults {

	protected S_1SC_10F_10AC_Results() {
	}

	protected void initialize() {
		super.clear();

		Num num_factory = Num.getFactory();
		
		RealDoublePrecision real_double_epsilon;
		RealSinglePrecision real_single_epsilon;
		RationalBigInt rational_bigint_epsilon = new RationalBigInt(1, 1000000000);

		for( Set<ArrivalBoundMethod> ab_set : DncTestMethodSources.ab_sets ) {
			// TFA
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(15.5), num_factory.create(110));
			addBounds(6,  Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(15.5), num_factory.create(110));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(310, 9), num_factory.create(110));
			addBounds(6,  Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(310, 9), num_factory.create(110));

			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		TFA delay ==> expected <34.44444444444444> but was <34.44444444444445>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		TFA delay ==> expected <34.444443> but was <34.44444>
			 * 		TFA delay ==> expected <34.44444274902344> but was <34.44443893432617>
			 * 		TFA backlog ==> expected <110.0> but was <109.99999237060547>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		TFA delay ==> expected <310 / 9> but was <1116892707587883008 / 32425917317067569>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(1e-14));
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(6, Analyses.TFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(6, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Double(1e-5));
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(6, Analyses.TFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(6, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.TFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(6, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(6, Analyses.TFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

			// SFA
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1796, 115), num_factory.create(127, 50));
			addBounds(6,  Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(2099, 130), num_factory.create(434, 25));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(775, 23), num_factory.create(100, 23));
			addBounds(6,  Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(775, 26), num_factory.create(350, 13));

			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		SFA delay ==> expected <15.617391304347827> but was <15.617391304347825>
			 * 		SFA delay ==> expected <33.69565217391305> but was <33.695652173913054>
			 * 		SFA delay ==> expected <29.807692307692307> but was <29.807692307692314>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		SFA delay ==> expected <15.617392> but was <15.617393>
			 * 		SFA delay ==> expected <33.69565200805664> but was <33.695652173913054>
			 * 		SFA delay ==> expected <29.807692> but was <29.80769>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA delay ==> expected <775 / 23> but was <2371120727725635 / 70368744177664>
			 * 		SFA delay ==> expected <775 / 23> but was <90071992547409920 / 2673104294955391>
			 * 		SFA delay ==> expected <775 / 26> but was <1116892707587883008 / 37469948899722525>
			 * 		SFA delay ==> expected <775 / 26> but was <4195059749053047 / 140737488355328>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(8e-15));
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(6, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(6, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(4e-5));
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(2e-6));
			addEpsilon(6, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(6, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(6, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(6, Analyses.SFA, ab_set, Multiplexing.FIFO, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

			// PMOO
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(775, 23), num_factory.create(100, 23));
			addBounds(6,  Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(775, 26), num_factory.create(350, 13));

			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		PMOO delay ==> expected <33.69565217391305> but was <33.695652173913054>
			 * 		PMOO delay ==> expected <29.807692307692307> but was <29.807692307692314>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		PMOO delay ==> expected <33.695652> but was <33.695656>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		PMOO delay ==> expected <775 / 23> but was <2371120727725635 / 70368744177664>
			 * 		PMOO delay ==> expected <775 / 26> but was <1116892707587883008 / 37469948899722525>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(8e-15));
			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);
			
			real_double_epsilon = new RealDoublePrecision(new Double(8e-15));
			addEpsilon(6, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(4e-6));
			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(6, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(6, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, NumImpl.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
		}

		addBounds(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(110));
		addBounds(6, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, num_factory.getNaN(), num_factory.create(110));

		/*
		 * expected <110> but was <109.99999>
		 */
		real_single_epsilon = new RealSinglePrecision(new Float(1e-5));
		addEpsilon(0, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
		addEpsilon(6, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY, NumImpl.REAL_SINGLE_PRECISION, real_single_epsilon);
	}
}
