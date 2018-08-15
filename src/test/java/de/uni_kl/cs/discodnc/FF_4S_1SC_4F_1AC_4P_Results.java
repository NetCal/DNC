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
 * Foundation, Inc., 51 Franklin Street, test_network.fifth Floor, Boston, MA 02110-1301 USA.
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

public class FF_4S_1SC_4F_1AC_4P_Results extends DncTestResults {

	protected FF_4S_1SC_4F_1AC_4P_Results() {
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
		ab_sets_PBOO.add(DncTestMethodSources.pair_2);
		ab_sets_PBOO.add(DncTestMethodSources.pair_3);
		ab_sets_PBOO.add(DncTestMethodSources.triplet);
		
		for( Set<ArrivalBoundMethod> ab_set : ab_sets_PBOO ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1370, 3), num_factory.create(1400));
			addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(3735, 32), num_factory.create(975));
			
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(395), num_factory.create(1400));
			addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(77.5), num_factory.create(975));
			
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1105, 6), num_factory.create(2075, 3));
			addBounds(2, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(1875, 32), num_factory.create(3975, 8));
			
			addBounds(3, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(462.5), num_factory.create(1400));
			addBounds(3, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(845, 8), num_factory.create(975));

			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(580, 3), num_factory.create(5875, 6));
			addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1525, 16), num_factory.create(7825, 16));
			
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(345, 2), num_factory.create(875));
			addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(575, 8), num_factory.create(2975, 8));
			
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1625, 18), num_factory.create(4175, 9));
			addBounds(2, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1695, 32), num_factory.create(8875, 32));
			
			addBounds(3, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(560, 3), num_factory.create(5675, 6));
			addBounds(3, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1405, 16), num_factory.create(7225, 16));

			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(650, 3), num_factory.create(6575, 6));
			addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(345, 2), num_factory.create(875));
			addBounds(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(305, 3), num_factory.create(3125, 6));
			addBounds(3, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(1145, 6), num_factory.create(2900, 3));
		}
		
		// For some Flows, PMOO Arrival Bounding yields worse cross-traffic arrivals!
		// For completeness, we need to add all bounds.
		Set<ArrivalBoundMethod> ab_set = DncTestMethodSources.single_3;
		
		// --------------------------------------------------------------------------------------------------------------
	    // TFA
	    // --------------------------------------------------------------------------------------------------------------
		addBounds(0, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2765, 6), num_factory.create(8525, 6));
		addBounds(0, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(3735, 32), num_factory.create(975));
		
		addBounds(1, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2395, 6), num_factory.create(8525, 6));
		addBounds(1, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(77.5), num_factory.create(975));
		
		addBounds(2, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1105, 6), num_factory.create(2075, 3));
		addBounds(2, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(1875, 32), num_factory.create(3975, 8));
		
		addBounds(3, Analyses.TFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1400, 3), num_factory.create(8525, 6));
		addBounds(3, Analyses.TFA, ab_set, Multiplexing.FIFO, num_factory.create(845, 8), num_factory.create(975));

		// --------------------------------------------------------------------------------------------------------------
	    // SFA
	    // --------------------------------------------------------------------------------------------------------------
		addBounds(0, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2345, 12), num_factory.create(11875, 12));
		addBounds(0, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1525, 16), num_factory.create(7825, 16));
		
		addBounds(1, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(2095, 12), num_factory.create(10625, 12));
		addBounds(1, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(575, 8), num_factory.create(2975, 8));
		
		addBounds(2, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(1625, 18), num_factory.create(4175, 9));
		addBounds(2, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1695, 32), num_factory.create(8875, 32));
		
		addBounds(3, Analyses.SFA, ab_set, Multiplexing.ARBITRARY, num_factory.create(560, 3), num_factory.create(5675, 6));
		addBounds(3, Analyses.SFA, ab_set, Multiplexing.FIFO, num_factory.create(1405, 16), num_factory.create(7225, 16));

		// --------------------------------------------------------------------------------------------------------------
	    // PMOO
	    // --------------------------------------------------------------------------------------------------------------
		addBounds(0, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(875, 4), num_factory.create(4425, 4));
		addBounds(1, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(2095, 12), num_factory.create(10625, 12));
		addBounds(2, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(305, 3), num_factory.create(3125, 6));
		addBounds(3, Analyses.PMOO, ab_set, Multiplexing.ARBITRARY, num_factory.create(1145, 6), num_factory.create(2900, 3));
		
		for( Set<ArrivalBoundMethod> ab_set_epsilon : DncTestMethodSources.ab_sets ) {
			// --------------------------------------------------------------------------------------------------------------
		    // TFA
		    // --------------------------------------------------------------------------------------------------------------
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		TFA delay ==> expected <399.1666666666667> but was <399.16666666666663>
			 * 		TFA delay ==> expected <184.16666666666666> but was <184.16666666666669>
			 * 		TFA backlog ==> expected <691.6666666666666> but was <691.6666666666667>
			 * 		TFA delay ==> expected <466.6666666666667> but was <466.66666666666663>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		TFA delay ==> expected <399.16666> but was <399.1667>
			 * 		TFA delay ==> expected <184.16667> but was <184.16666>
			 * 		TFA delay ==> expected <184.1666717529297> but was <184.16665649414062>
			 * 		TFA backlog ==> expected <691.6666870117188> but was <691.6666259765625>
			 * 		TFA delay ==> expected <466.66666> but was <466.6667>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		TFA delay ==> expected <1370 / 3> but was <80337649602833067 / 175921860444160>
			 * 		TFA delay ==> expected <2765 / 6> but was <16214131470936747 / 35184372088832>
			 * 		TFA delay ==> expected <2395 / 6> but was <4388883914205867 / 10995116277760>
			 * 		TFA delay ==> expected <1105 / 6> but was <32398942631799467 / 175921860444160>
			 * 		TFA delay ==> expected <1400 / 3> but was <5131054262954667 / 10995116277760>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(4e-13));
			addEpsilon(1, Analyses.TFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(1, Analyses.TFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_double_epsilon = new RealDoublePrecision(new Double(2e-13));
			addEpsilon(2, Analyses.TFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(2, Analyses.TFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			
			real_double_epsilon = new RealDoublePrecision(new Double(7e-14));
			addEpsilon(3, Analyses.TFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(3, Analyses.TFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(4e-5));
			addEpsilon(1, Analyses.TFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(1, Analyses.TFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(6.25e-5));
			addEpsilon(2, Analyses.TFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(2, Analyses.TFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(4e-5));
			addEpsilon(3, Analyses.TFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(3, Analyses.TFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.TFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.TFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(1, Analyses.TFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.TFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(2, Analyses.TFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(2, Analyses.TFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(3, Analyses.TFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(3, Analyses.TFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);

			// --------------------------------------------------------------------------------------------------------------
		    // SFA
		    // --------------------------------------------------------------------------------------------------------------
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		SFA delay ==> expected <193.33333333333334> but was <193.33333333333331>
			 * 		SFA backlog ==> expected <979.1666666666666> but was <979.1666666666665>
			 * 		SFA delay ==> expected <174.58333333333334> but was <174.58333333333331>
			 * 		SFA backlog ==> expected <885.4166666666666> but was <885.4166666666665>
			 * 		SFA delay ==> expected <90.27777777777777> but was <90.27777777777779>
			 * 		SFA delay ==> expected <186.66666666666666> but was <186.66666666666669>
			 * 		SFA backlog ==> expected <945.8333333333334> but was <945.8333333333333>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		SFA backlog ==> expected <979.1667> but was <979.1666>
			 * 		SFA delay ==> expected <174.58333> but was <174.58334>
			 * 		SFA delay ==> expected <174.5833282470703> but was <174.58334350585938>
			 * 		SFA backlog ==> expected <885.4166870117188> but was <885.416748046875>
			 * 		SFA delay ==> expected <90.27778> but was <90.27777>
			 * 		SFA backlog ==> expected <463.8888854980469> but was <463.88885498046875>
			 * 		SFA delay ==> expected <186.66667> but was <186.66666>
			 * 		SFA delay ==> expected <186.6666717529297> but was <186.66665649414062>
			 * 		SFA backlog ==> expected <945.8333129882812> but was <945.833251953125>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		SFA delay ==> expected <580 / 3> but was <3401155968587093 / 17592186044416>
			 * 		SFA delay ==> expected <2095 / 12> but was <6142604960508587 / 35184372088832>
			 * 		SFA delay ==> expected <1625 / 18> but was <6352733849372445 / 70368744177664>
			 * 		SFA delay ==> expected <560 / 3> but was <3283874728290987 / 17592186044416>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(1.25e-13));
			addEpsilon(0, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_double_epsilon = new RealDoublePrecision(new Double(1.25e-13));
			addEpsilon(1, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_double_epsilon = new RealDoublePrecision(new Double(2e-14));
			addEpsilon(2, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(2, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_double_epsilon = new RealDoublePrecision(new Double(1.25e-13));
			addEpsilon(3, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);
			addEpsilon(3, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(1e-3));
			addEpsilon(0, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(6.25e-3));
			addEpsilon(1, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(3.25e-5));
			addEpsilon(2, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(2, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(6.25e-5));
			addEpsilon(3, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);
			addEpsilon(3, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(0, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(1, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(2, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(2, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			addEpsilon(3, Analyses.SFA, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(3, Analyses.SFA, ab_set_epsilon, Multiplexing.FIFO, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			
			// --------------------------------------------------------------------------------------------------------------
		    // PMOO
		    // --------------------------------------------------------------------------------------------------------------
			/*
			 * Observed test failures:
			 * 
			 * Real Double: Epsilon set to ignore
			 * 		PMOO delay ==> expected <174.58333333333334> but was <174.58333333333331>
			 * 		PMOO backlog ==> expected <885.4166666666666> but was <885.4166666666665>
			 * 		PMOO delay ==> expected <190.83333333333334> but was <190.83333333333331>
			 * 		PMOO backlog ==> expected <966.6666666666666> but was <966.6666666666665>
			 * 
			 * Real Single: Epsilon set to ignore
			 * 		PMOO delay ==> expected <216.66667> but was <216.66666>
			 * 		PMOO delay ==> expected <216.6666717529297> but was <216.66665649414062>
			 * 		PMOO backlog ==> expected <1095.8333740234375> but was <1095.833251953125>
			 * 		PMOO delay ==> expected <174.58333> but was <174.58334>
			 * 		PMOO delay ==> expected <174.5833282470703> but was <174.58334350585938>
			 * 		PMOO backlog ==> expected <885.4166870117188> but was <885.416748046875>
			 * 		PMOO delay ==> expected <190.83333> but was <190.83334>
			 * 		PMOO backlog ==> expected <966.6667> but was <966.6666>
			 * 
			 * Rational BigInteger: Epsilon set to ignore
			 * 		PMOO delay ==> expected <650 / 3> but was <19058201548117333 / 87960930222080>
			 * 		PMOO delay ==> expected <2095 / 12> but was <6142604960508587 / 35184372088832>
			 * 		PMOO delay ==> expected <305 / 3> but was <7154155658062507 / 70368744177664>
			 * 		PMOO delay ==> expected <305 / 3> but was <17885389145156267 / 175921860444160>
			 * 		PMOO delay ==> expected <1145 / 6> but was <6714351006952107 / 35184372088832>
			 */
			real_double_epsilon = new RealDoublePrecision(new Double(1.25e-13));
			addEpsilon(1, Analyses.PMOO, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_double_epsilon = new RealDoublePrecision(new Double(1.25e-13));
			addEpsilon(3, Analyses.PMOO, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_DOUBLE_PRECISION, real_double_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(1.25e-4));
			addEpsilon(0, Analyses.PMOO, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(6.25e-4));
			addEpsilon(1, Analyses.PMOO, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			real_single_epsilon = new RealSinglePrecision(new Float(1.25e-4));
			addEpsilon(3, Analyses.PMOO, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.REAL_SINGLE_PRECISION, real_single_epsilon);

			addEpsilon(0, Analyses.PMOO, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(1, Analyses.PMOO, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(2, Analyses.PMOO, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
			addEpsilon(3, Analyses.PMOO, ab_set_epsilon, Multiplexing.ARBITRARY, NumBackend.RATIONAL_BIGINTEGER, rational_bigint_epsilon);
		}
	}
}