package unikl.disco.numbers;

import unikl.disco.nc.CalculatorConfig;
import unikl.disco.numbers.implementations.RationalBigInteger;
import unikl.disco.numbers.implementations.RealDoublePrecision;
import unikl.disco.numbers.implementations.RationalInteger;
import unikl.disco.numbers.implementations.RealSinglePrecision;
import unikl.disco.numbers.values.NaN;
import unikl.disco.numbers.values.NegativeInfinity;
import unikl.disco.numbers.values.PositiveInfinity;

@SuppressWarnings("incomplete-switch")
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
		return POSITIVE_INFINITY;
	}

	public static Num createPositiveInfinity() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDoublePrecision( Double.POSITIVE_INFINITY );
			case REAL_SINGLE_PRECISION:
				return new RealSinglePrecision( Float.POSITIVE_INFINITY );
			// non IEEE 754 floating point data types
			case RATIONAL_INTEGER:
			case RATIONAL_BIGINTEGER:
				return new PositiveInfinity();
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}
	
	public static Num getNegativeInfinity() {
		return NEGATIVE_INFINITY;
	}
	
	public static Num createNegativeInfinity() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDoublePrecision( Double.NEGATIVE_INFINITY );
			case REAL_SINGLE_PRECISION:
				return new RealSinglePrecision( Float.NEGATIVE_INFINITY );
			// non IEEE 754 floating point data types
			case RATIONAL_INTEGER:
			case RATIONAL_BIGINTEGER:
				return new NegativeInfinity();
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num getNaN() {
		return NaN;
	}
	
	public static Num createNaN() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDoublePrecision( Double.NaN );
			case REAL_SINGLE_PRECISION:
				return new RealSinglePrecision( Float.NaN );
			// non IEEE 754 floating point data types
			case RATIONAL_INTEGER:
			case RATIONAL_BIGINTEGER:
				return new NaN();
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num getZero() {
		return ZERO;
	}
	
	public static Num createZero() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDoublePrecision( 0 );
			case REAL_SINGLE_PRECISION:
				return new RealSinglePrecision( 0 );
			case RATIONAL_INTEGER:
				return new RationalInteger( 0 );
			case RATIONAL_BIGINTEGER:
				return new RationalBigInteger( 0 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}
	
	public static Num getEpsilon() {
		return EPSILON;
	}
	
	public static Num createEpsilon() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return RealDoublePrecision.createEpsilon();
			case REAL_SINGLE_PRECISION:
				return RealSinglePrecision.createEpsilon();
			case RATIONAL_INTEGER:
				return RationalInteger.createEpsilon();
			case RATIONAL_BIGINTEGER:
				return RationalBigInteger.createEpsilon();
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}
	
	public static Num createNum( double value ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDoublePrecision( value );
			case REAL_SINGLE_PRECISION:
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
			case RATIONAL_BIGINTEGER:
				return new RationalBigInteger( value );
			case RATIONAL_INTEGER:
				return new RationalInteger( value );
			default:
				throw new RuntimeException( "Undefined number representation" );
		 }
	}
	
	public static Num createNum( int num, int den ) {
		if ( den == 0 ) { // division by integer 0 throws an arithmetic exception
			throw new ArithmeticException( "/ by zero" );
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDoublePrecision( num, den );
			case REAL_SINGLE_PRECISION:
				return new RealSinglePrecision( num, den );
			case RATIONAL_INTEGER:
				return new RationalInteger( num, den );
			case RATIONAL_BIGINTEGER:
				return new RationalBigInteger( num, den );
			default:
				throw new RuntimeException( "Undefined number representation" );
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
			// either an integer of something strange
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
