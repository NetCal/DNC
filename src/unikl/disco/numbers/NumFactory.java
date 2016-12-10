package unikl.disco.numbers;

import unikl.disco.nc.CalculatorConfig;
import unikl.disco.nc.CalculatorConfig.NumClass;
import unikl.disco.numbers.implementations.RationalBigInteger;
import unikl.disco.numbers.implementations.RealDoublePrecision;
import unikl.disco.numbers.implementations.RationalInteger;
import unikl.disco.numbers.implementations.RealSinglePrecision;
import unikl.disco.numbers.values.NaN;
import unikl.disco.numbers.values.NegativeInfinity;
import unikl.disco.numbers.values.PositiveInfinity;

public class NumFactory {
	private static Num POSITIVE_INFINITY = createPositiveInfinity();
	private static Num NEGATIVE_INFINITY = createNegativeInfinity();
	private static Num NaN = createNaN();
	private static Num ZERO = createZero();
	private static Num EPSILON = createEpsilon();
	
	/**
	 * 
	 * Required to call if the number representation is changed.
	 * 
	 */
	public static void createSingletons() {
		POSITIVE_INFINITY = createPositiveInfinity();
		NEGATIVE_INFINITY = createNegativeInfinity();
		NaN = createNaN();
		ZERO = createZero();
		EPSILON = createEpsilon();
	}
	
	public static Num getPositiveInfinity() {
//		return createPositiveInfinity();
		return POSITIVE_INFINITY;
	}

	public static Num createPositiveInfinity() {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return new RealDoublePrecision( Double.POSITIVE_INFINITY );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return new RealSinglePrecision( Float.POSITIVE_INFINITY );
		}
		
		// non IEEE 754 floating point data types 
		return new PositiveInfinity();
	}
	
	public static Num getNegativeInfinity() {
//		return createNegativeInfinity();
		return NEGATIVE_INFINITY;
	}
	
	public static Num createNegativeInfinity() {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return new RealDoublePrecision( Double.NEGATIVE_INFINITY );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return new RealSinglePrecision( Float.NEGATIVE_INFINITY );
		}
		
		// non IEEE 754 floating point data types
		return new NegativeInfinity();
	}

	public static Num getNaN() {
//		return createNaN();
		return NaN;
	}
	
	public static Num createNaN() {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return new RealDoublePrecision( Double.NaN );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return new RealSinglePrecision( Float.NaN );
		}
		
		// non IEEE 754 floating point data types 
		return new NaN();
	}

	public static Num getZero() {
//		return createZero();
		return ZERO;
	}
	
	public static Num createZero() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return new RationalInteger( 0 );
			case FRACTION_BIG_INTEGER:
				return new RationalBigInteger( 0 );
			case SINGLE_PRECISION:
				return new RealSinglePrecision( 0 );
			case DOUBLE_PRECISION:
			default:
				return new RealDoublePrecision( 0 );
		}
	}
	
	public static Num getEpsilon() {
//		return createEpsilon();
		return EPSILON;
	}
	
	public static Num createEpsilon() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return RationalInteger.createEpsilon();
			case FRACTION_BIG_INTEGER:
				return RationalBigInteger.createEpsilon();
			case SINGLE_PRECISION:
				return RealSinglePrecision.createEpsilon();
			case DOUBLE_PRECISION:
			default:
				return RealDoublePrecision.createEpsilon();
		}
	}
	
	public static Num createNum( double value ) {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return new RealDoublePrecision( value );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return new RealSinglePrecision( value );
		}
		
		// non IEEE 754 floating point data types
		if( value == Double.POSITIVE_INFINITY ) {
			return createPositiveInfinity();
		}
		if( value == Double.NEGATIVE_INFINITY ) {
			return createNegativeInfinity();
		}
		if( value == Double.NaN ) {
			return createNaN();
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_BIG_INTEGER:
				return new RationalBigInteger( value );
			case FRACTION_INTEGER:
			default:
				return new RationalInteger( value );
		 }
	}
	
	public static Num createNum( int num, int den ) {
		if ( den == 0 ) { // division by integer 0 throws an arithmetic exception
			throw new ArithmeticException( "/ by zero" );
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return new RationalInteger( num, den );
			case FRACTION_BIG_INTEGER:
				return new RationalBigInteger( num, den );
			case SINGLE_PRECISION:
				return new RealSinglePrecision( num, den );
			case DOUBLE_PRECISION:
			default:
				return new RealDoublePrecision( num, den );
		 }
	}
	
	public static Num createNum( String num_str ) throws Exception {
		if( num_str.equals( "Infinity" ) ) {
			return createPositiveInfinity();
		}
		if( num_str.equals( "-Infinity" ) ) {
			return createNegativeInfinity();
		}
		if( num_str.equals( "NaN" ) || num_str.equals( "NA" ) ) {
			return createNaN();
		}
		
		boolean fraction_indicator = num_str.contains( " / " );
		boolean double_based = num_str.contains( "." );
		
		if ( fraction_indicator && double_based ) {
			throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString() 
									+ ": " + num_str );
		}
		
		try {
			// Either an integer of something strange
			if ( !fraction_indicator && !double_based ) {
				return createNum( Integer.parseInt( num_str ) );
			}
			
			if ( fraction_indicator ) {
				String[] num_den = num_str.split( " / " ); // ["num","den"]
				if( num_den.length != 2 ) {
					throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString() 
											+ ": " + num_str );
				}
				
				int den = Integer.parseInt( num_den[1] );
				if( den != 0 ) {
					return createNum( Integer.parseInt( num_den[0] ), den );
				} else {
					return createNaN();
				}
			}
			
			if ( double_based ) {
				return createNum( Double.parseDouble( num_str ) );
			}
		} catch (Exception e) {
			throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString()
									+ ": " + num_str );
		}
		
		// This code should not be reachable because all the operations above either succeed such that we can return a number
		// of raise an exception of some kind. Yet, Java does not get this and thus complains if there's no "finalizing statement". 
		throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString()
								+ ": " + num_str );
	}
}
