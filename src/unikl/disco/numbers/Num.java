/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.6 "Hydra".
 *
 * Copyright (C) 2014 - 2016 Steffen Bondorf
 *
 * disco | Distributed Computer Systems Lab
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
 
package unikl.disco.numbers;

import unikl.disco.nc.CalculatorConfig;

/**
 * 
 * Class emulating multiple dispatch (Java only offers single dispatch) 
 * in order to allow the user to switch between available number representations. 
 *
 * @author Steffen Bondorf
 *
 */
public abstract class Num {
	protected static enum SpecialValue { POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, ZERO };

	protected boolean isNaN;
	protected boolean isPosInfty;
	protected boolean isNegInfty;

	private static CalculatorConfig.NumClass numClassToCall( Num num ) {
		if( num instanceof NumDouble ) {
			return CalculatorConfig.NumClass.DOUBLE;
		} else {
			return CalculatorConfig.NumClass.FRACTION;
		}
	}
	
	private static CalculatorConfig.NumClass numClassToCall( Num num1, Num num2 ) {
		// If there's at least one double involved, go for NumDouble.
		// In case the other is a Fraction based Num, that works because NumDouble internally works with the .doubleValue() result. 
		if( num1 instanceof NumDouble || num2 instanceof NumDouble ) {
			return CalculatorConfig.NumClass.DOUBLE;
		} else {
			return CalculatorConfig.NumClass.FRACTION;
		}
	}
	
	public static Num add( Num num1, Num num2 ) {
		if( num1.isNaN || num2.isNaN ) {
			return NumFactory.createNaN();
		}
		// Prevent overflow exception when adding integer based number representations like Fraction
		if( num1.isPosInfty || num2.isPosInfty ) {
			return NumFactory.createPositiveInfinity();
		}
		if( num1.isNegInfty || num2.isNegInfty ) {
			return NumFactory.createNegativeInfinity();
		}
		
		switch ( numClassToCall( num1, num2 ) ) {
			case FRACTION:
				return NumFraction.add( (NumFraction)num1, (NumFraction)num2 );
			case DOUBLE:
			default:
				return NumDouble.add( (NumDouble)num1, (NumDouble)num2 );
		}
	}
	
	public static Num sub( Num num1, Num num2 ) {
		if( num1.isNaN || num2.isNaN ) {
			return NumFactory.createNaN();
		}
		// Prevent overflow exception when adding integer based number representations like Fraction
		if( num1.isPosInfty || num2.isPosInfty ) {
			return NumFactory.createPositiveInfinity();
		}
		if( num1.isNegInfty || num2.isNegInfty ) {
			return NumFactory.createNegativeInfinity();
		}
		
		switch ( numClassToCall( num1, num2 ) ) {
			case FRACTION:
				return NumFraction.sub( (NumFraction)num1, (NumFraction)num2 );
			case DOUBLE:
			default:
				return NumDouble.sub( (NumDouble)num1, (NumDouble)num2 );
		}
	}
	
	public static Num mult( Num num1, Num num2 ) {
		if( num1.isNaN || num2.isNaN ) {
			return NumFactory.createNaN();
		}
		if( num1.isPosInfty || num2.isPosInfty ) {
			return NumFactory.createPositiveInfinity();
		}
		if( num1.isNegInfty || num2.isNegInfty ) {
			return NumFactory.createNegativeInfinity();
		}
		
		switch ( numClassToCall( num1, num2 ) ) {
			case FRACTION:
				return NumFraction.mult( (NumFraction)num1, (NumFraction)num2 );
			case DOUBLE:
			default:
				return NumDouble.mult( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num div( Num num1, Num num2 ) {
		if( num1.isNaN || num2.isNaN ) {
			return NumFactory.createNaN();
		}
		
		// Integer based number representations use Integer.MAX_VALUE to signal infinity so special treatment is necessary when dividing
		if( num1.isPosInfty ) {
			return NumFactory.createPositiveInfinity();
		}
		if( num2.isPosInfty ) {
			return NumFactory.createZero();
		}
		if( num1.isNegInfty ) {
			return NumFactory.createNegativeInfinity();
		}
		if( num2.isNegInfty ) {
			return NumFactory.createZero();
		}
		
		switch ( numClassToCall( num1, num2 ) ) {
			case FRACTION:
				return NumFraction.div( (NumFraction)num1, (NumFraction)num2 );
			case DOUBLE:
			default:
				return NumDouble.div( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num diff( Num num1, Num num2 ) {
		if( num1.isNaN || num2.isNaN ) {
			return NumFactory.createNaN();
		}
		
		if( num1.isPosInfty || num1.isNegInfty 
				 || num2.isPosInfty || num2.isNegInfty ) {
			return NumFactory.createPositiveInfinity();
		}
		
		switch ( numClassToCall( num1, num2 ) ) {
			case FRACTION:
				return NumFraction.diff( (NumFraction)num1, (NumFraction)num2 );
			case DOUBLE:
			default:
				return NumDouble.diff( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num max( Num num1, Num num2 ) {
		if( num1.isNaN || num2.isNaN ) {
			return NumFactory.createNaN();
		}
		if( num1.isPosInfty ) {
			return num1;
		}
		if( num1.isNegInfty ) {
			return num2;
		}
		
		switch ( numClassToCall( num1, num2 ) ) {
			case FRACTION:
				return NumFraction.max( (NumFraction)num1, (NumFraction)num2 );
			case DOUBLE:
			default:
				return NumDouble.max( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num min( Num num1, Num num2 ) {
		if( num1.isNaN || num2.isNaN ) {
			return NumFactory.createNaN();
		}
		if( num1.isPosInfty ) {
			return num2;
		}
		if( num1.isNegInfty ) {
			return num1;
		}
		
		switch ( numClassToCall( num1, num2 ) ) {
			case FRACTION:
				return NumFraction.min( (NumFraction)num1, (NumFraction)num2 );
			case DOUBLE:
			default:
				return NumDouble.min( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num abs( Num num ) {
		if ( num.isNaN ) {
    		return NumFactory.createNaN();
    	}
    	if ( num.isPosInfty ) {
    		return NumFactory.createPositiveInfinity();
    	}
    	if ( num.isNegInfty ) {
			return NumFactory.createNegativeInfinity();
		}
	    
		switch ( numClassToCall( num ) ) {
			case FRACTION:
				return NumFraction.abs( (NumFraction)num );
			case DOUBLE:
			default:
				return NumDouble.abs( (NumDouble)num );
		}
	}

	public static Num negate( Num num ) {
		if ( num.isNaN ) {
    		return NumFactory.createNaN();
    	}
    	if ( num.isPosInfty ) {
    		return NumFactory.createNegativeInfinity();
    	}
    	if ( num.isNegInfty ) {
			return NumFactory.createPositiveInfinity();
		}

		switch ( numClassToCall( num ) ) {
			case FRACTION:
				return NumFraction.negate( (NumFraction)num );
			case DOUBLE:
			default:
				return NumDouble.negate( (NumDouble)num );
		}
	}

	public abstract void add( Num num2 );
	
	public abstract void sub( Num num2 );
	
	public abstract void mult( Num num2 );
	
	public abstract void div( Num num2 );

	public abstract boolean equals( double num2 );
	// TODO
//	public abstract boolean equals0();

	public abstract boolean greater( Num num2 );
//	public abstract boolean greater0();

	public abstract boolean ge( Num num2 );
//	public abstract boolean ge0();

	public abstract boolean less( Num num2 );
//	public abstract boolean less0();

	public abstract boolean le( Num num2 );
//	public abstract boolean le0();
	
	public abstract double doubleValue();

	public abstract Num copy();
	
	protected static Num parse( String num_str ) throws Exception {
		boolean fraction_indicator = num_str.contains( " / " );
		boolean double_based = num_str.contains( "." );
		
		if ( fraction_indicator && double_based ) {
			throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString() );
		}
		
		try {
			// Either an integer of something strange
			if ( !fraction_indicator && !double_based ) {
				return NumFactory.createNum( Integer.parseInt( num_str ) );
			}
			
			if ( fraction_indicator ) {
				String[] num_den = num_str.split( " / " ); // ["num","den"]
				if( num_den.length != 2 ) {
					throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString() );
				}
				
				int den = Integer.parseInt( num_den[1] );
				if( den != 0 ) {
					return NumFactory.createNum( Integer.parseInt( num_den[0] ), den );
				} else {
					return NumFactory.createNaN();
				}
			}
			
			if ( double_based ) {
				return NumFactory.createNum( Double.parseDouble( num_str ) );
			}
		} catch (Exception e) {
			throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString() );
		}
		
		// This code should not be reachable because all the operations above either succeed such that we can return a number
		// of raise an exception of some kind. Yet, Java does not get this and thus complains if there's no "finalizing statement". 
		throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString() );
	}
}
