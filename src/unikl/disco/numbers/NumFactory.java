package unikl.disco.numbers;

//TODO throw exceptions in subclasses to indicate that a special value (NaN, +/- infty) was computed
//TODO Use the special values only for number representations that do not have them --> i.e., not for double --> make double faster
import unikl.disco.nc.CalculatorConfig;
import unikl.disco.numbers.implementations.FractionBigInteger;
import unikl.disco.numbers.implementations.DoublePrecision;
import unikl.disco.numbers.implementations.FractionInteger;
import unikl.disco.numbers.implementations.SinglePrecision;
import unikl.disco.numbers.values.NaN;

// TODO Adapt to NumUtil's structure: First IEEE Floating Point, then those needing extra representation of special values
//TODO throw exceptions in subclasses to indicate that a special value (NaN, +/- infty) was computed
//TODO Use the special values only for number representations that do not have them --> i.e., not for double --> make double faster
public class NumFactory {
	
	protected static enum SpecialValue { POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, ZERO }

//	public static final Num POSITIVE_INFINITY = createPositiveInfinity();
//	public static final Num NEGATIVE_INFINITY = createNegativeInfinity();
	public static final Num NaN = new NaN(); // createNaN();
//	public static final Num ZERO = createZero();

//	public static void reinitialize() {
//		POSITIVE_INFINITY = createPositiveInfinity();
//		NEGATIVE_INFINITY = createNegativeInfinity();
//		NaN = createNaN();
//		ZERO = createZero();
//	}
	
	public static Num getPositiveInfinity() {
//		return POSITIVE_INFINITY;
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return FractionInteger.POSITIVE_INFINITY;
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.POSITIVE_INFINITY;
			case SINGLE_PRECISION:
				return SinglePrecision.POSITIVE_INFINITY;
			case DOUBLE_PRECISION:
			default:
				return DoublePrecision.POSITIVE_INFINITY;
		 }
	}
	
	public static Num getNegativeInfinity() {
//		return NEGATIVE_INFINITY;
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return FractionInteger.NEGATIVE_INFINITY;
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.NEGATIVE_INFINITY;
			case SINGLE_PRECISION:
				return SinglePrecision.NEGATIVE_INFINITY;
			case DOUBLE_PRECISION:
			default:
				return DoublePrecision.NEGATIVE_INFINITY;
		 }
	}

	public static Num getNaN() {
		return NaN;
//		switch ( CalculatorConfig.NUM_CLASS ) {
//			case FRACTION:
//				return NumFraction.NaN;
//			case BIG_FRACTION:
//				return NumBigFraction.NaN;
//			case SINGLE:
//				return NumSingle.NaN;
//			case DOUBLE:
//			default:
//				return NumDouble.NaN;
//		 }
	}
	
	public static Num getZero() {
//		return ZERO;
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return FractionInteger.ZERO;
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.ZERO;
			case SINGLE_PRECISION:
				return SinglePrecision.ZERO;
			case DOUBLE_PRECISION:
			default:
				return DoublePrecision.ZERO;
		 }
	}
	
	public static Num getEpsilon() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return FractionInteger.createEpsilon();
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.createEpsilon();
			case SINGLE_PRECISION:
				return SinglePrecision.createEpsilon();
			case DOUBLE_PRECISION:
			default:
				return DoublePrecision.createEpsilon();
		}
	}
	
	public static Num createNum( int num ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return new FractionInteger( num );
			case FRACTION_BIG_INTEGER:
				return new FractionBigInteger( num );
			case SINGLE_PRECISION:
				return new SinglePrecision( num );
			case DOUBLE_PRECISION:
			default:
				return new DoublePrecision( num );
		 }
	}
	
	public static Num createNum( double value ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return new FractionInteger( value );
			case FRACTION_BIG_INTEGER:
				return new FractionBigInteger( value );
			case SINGLE_PRECISION:
				return new SinglePrecision( value );
			case DOUBLE_PRECISION:
			default:
				return new DoublePrecision( value );
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
	
	public static Num createNum( int num, int den ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return new FractionInteger( num, den );
			case FRACTION_BIG_INTEGER:
				return new FractionBigInteger( num, den );
			case SINGLE_PRECISION:
				return new SinglePrecision( num, den );
			case DOUBLE_PRECISION:
			default:
				return new DoublePrecision( num, den );
		 }
	}
	
	public static Num createZero() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return FractionInteger.createZero();
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.createZero();
			case SINGLE_PRECISION:
				return SinglePrecision.createZero();
			case DOUBLE_PRECISION:
			default:
				return DoublePrecision.createZero();
		}
	}

	public static Num createPositiveInfinity() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_INTEGER:
				return FractionInteger.createPositiveInfinity();
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.createPositiveInfinity();
			case SINGLE_PRECISION:
				return SinglePrecision.createPositiveInfinity();
			case DOUBLE_PRECISION:
			default:
				return DoublePrecision.createPositiveInfinity();
		 }
	}
	
	public static Num createNegativeInfinity() {
		switch ( CalculatorConfig.NUM_CLASS ) {
		 	case FRACTION_INTEGER:
				return FractionInteger.createNegativeInfinity();
		 	case FRACTION_BIG_INTEGER:
				return FractionBigInteger.createNegativeInfinity();
			case SINGLE_PRECISION:
				return SinglePrecision.createNegativeInfinity();
			case DOUBLE_PRECISION:
			default:
				return DoublePrecision.createNegativeInfinity();
		 }
	}
	
	public static Num createNaN() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case DOUBLE_PRECISION:
				return new DoublePrecision( Double.NaN );
		 	default:
		 		return new NaN();
		 }
	}
}
