/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.4 "Centaur".
 *
 * Copyright (C) 2017 Steffen Bondorf
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */
 
package unikl.disco.numbers.implementations;

import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumUtilsInterface;
import unikl.disco.numbers.extraValues.NaN;
import unikl.disco.numbers.extraValues.NegativeInfinity;
import unikl.disco.numbers.extraValues.PositiveInfinity;
import unikl.disco.numbers.implementations.RationalInt;

public class RationalIntUtils implements NumUtilsInterface {
	public Num add( Num num1, Num num2 ) {
		if( num1 instanceof NaN || num2 instanceof NaN 
				|| ( num1 instanceof PositiveInfinity && num2 instanceof NegativeInfinity ) 
				|| ( num1 instanceof NegativeInfinity && num2 instanceof PositiveInfinity ) ) {
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity ) { // other num is not negative infinity
			return new PositiveInfinity(); 
		}
		if( num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity ) { // other num is not positive infinity
			return new NegativeInfinity(); 
		}

		return RationalInt.add( (RationalInt)num1, (RationalInt)num2 );
	}

	public Num sub( Num num1, Num num2 ) {
		if( num1 instanceof NaN || num2 instanceof NaN ) {
			return new NaN();
		}
		
		if( num1 instanceof NaN || num2 instanceof NaN 
				|| ( num1 instanceof PositiveInfinity && num2 instanceof PositiveInfinity ) 
				|| ( num1 instanceof NegativeInfinity && num2 instanceof NegativeInfinity ) ) {
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity				// num2 is not positive infinity
				|| num2 instanceof NegativeInfinity ) {	// num1 is not negative infinity
			return new PositiveInfinity(); 
		}
		if( num1 instanceof NegativeInfinity				// num2 is not negative infinity
				|| num2 instanceof PositiveInfinity ) {	// num1 is not positive infinity
			return new NegativeInfinity(); 
		}
		
		return RationalInt.sub( (RationalInt)num1, (RationalInt)num2 );
	}

	public Num mult( Num num1, Num num2 ) {
		if( num1 instanceof NaN || num2 instanceof NaN ) {
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity ) {
			if ( num2.ltZero() || num2 instanceof NegativeInfinity ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num2 instanceof PositiveInfinity ) {
			if ( num1.ltZero() || num1 instanceof NegativeInfinity ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num1 instanceof NegativeInfinity ) {
			if ( num2.ltZero() || num2 instanceof NegativeInfinity ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		if( num2 instanceof NegativeInfinity ) {
			if ( num1.ltZero() || num1 instanceof NegativeInfinity ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}

		return RationalInt.mult( (RationalInt)num1, (RationalInt)num2 );
	}

	public Num div( Num num1, Num num2 ) {
		if( num1 instanceof NaN || num2 instanceof NaN 
				|| ( ( num1 instanceof PositiveInfinity || num1 instanceof NegativeInfinity ) 
						&& ( num2 instanceof PositiveInfinity || num2 instanceof NegativeInfinity ) ) ) { // two infinities in the division
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity ) { // positive infinity divided by some finite value
			if( num2.ltZero() ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num1 instanceof NegativeInfinity ) { // negative infinity divided by some finite value 
			if( num2.ltZero() ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		if( num2 instanceof PositiveInfinity || num2 instanceof NegativeInfinity ) { // finite value divided by infinity
			return new RationalInt( 0 );
		}

        if ( ((RationalInt)num2).eqZero() ) {
        	return new PositiveInfinity();
       	} else {
       		return RationalInt.div( (RationalInt)num1, (RationalInt)num2 );        		
       	}
	}

	public Num abs( Num num ) {
		if( num instanceof NaN ) {
			return new NaN();
		}
		if( num instanceof PositiveInfinity || num instanceof NegativeInfinity ) {
			return new PositiveInfinity();
		}
		
		return RationalInt.abs( (RationalInt)num );
	}

	public Num diff( Num num1, Num num2 ) {
		if( num1 instanceof NaN || num2 instanceof NaN ) { 
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity 
				|| num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity ) {
			return new PositiveInfinity();
		}
		
		return RationalInt.diff( (RationalInt)num1, (RationalInt)num2 );
	}

	public Num max( Num num1, Num num2 ) {
		if( num1 instanceof NaN || num2 instanceof NaN ) {
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity ) {
			return new PositiveInfinity();
		}
		if( num1 instanceof NegativeInfinity ) {
			return num2.copy();
		}
		if( num2 instanceof NegativeInfinity ) {
			return num1.copy();
		}
		
		return RationalInt.max( (RationalInt)num1, (RationalInt)num2 );
	}

	public Num min( Num num1, Num num2 ) {
		if( num1 instanceof NaN || num2 instanceof NaN ) {
			return new NaN();
		}
		if( num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity ) {
			return new NegativeInfinity();
		}
		if( num1 instanceof PositiveInfinity ) {
			return num2.copy();
		}
		if( num2 instanceof PositiveInfinity ) {
			return num1.copy();
		}
		
		return RationalInt.min( (RationalInt)num1, (RationalInt)num2 );
	}

	public Num negate( Num num ) {
		if( num instanceof NaN ) {
			return new NaN();
		}
		if( num instanceof PositiveInfinity ) {
			return new NegativeInfinity();
		}
		if( num instanceof NegativeInfinity ) {
			return new PositiveInfinity();
		}
		
		return RationalInt.negate( (RationalInt)num );
	}

	public boolean isFinite( Num num ) {
		if( num instanceof RationalInt ) { // Only stores finite values
			return true;
		} else {
			return false; // NaN is neither finite nor infinite
		}
	}

	public boolean isInfinite( Num num ) {
		if( ( num instanceof PositiveInfinity ) || ( num instanceof NegativeInfinity ) ) {
			return true;
		} else {
			return false; // NaN is neither finite nor infinite
		}
	}
	
	public boolean isNaN( Num num ) {
		if( num instanceof NaN ) {
			return true;
		} else {
			return false;
		}
	}
}
