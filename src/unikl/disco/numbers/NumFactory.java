package unikl.disco.numbers;

import unikl.disco.nc.CalculatorConfig;

public class NumFactory {
	
	protected static enum SpecialValue { POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, ZERO }

	public static Num getPositiveInfinity() {
		 switch ( CalculatorConfig.NUM_CLASS ) {
		 case FRACTION:
			 return NumFraction.POSITIVE_INFINITY;
			 case BIG_FRACTION:
				 return NumBigFraction.POSITIVE_INFINITY;
			 case SINGLE:
				 return NumSingle.POSITIVE_INFINITY;
			 case DOUBLE:
			 default:
				 return NumDouble.POSITIVE_INFINITY;
		 }
	}
	
	public static Num getNegativeInfinity() {
		 switch ( CalculatorConfig.NUM_CLASS ) {
			 case FRACTION:
				 return NumFraction.NEGATIVE_INFINITY;
			 case BIG_FRACTION:
				 return NumBigFraction.NEGATIVE_INFINITY;
			 case SINGLE:
				 return NumSingle.NEGATIVE_INFINITY;
			 case DOUBLE:
			 default:
				 return NumDouble.NEGATIVE_INFINITY;
		 }
	}

	public static Num getNaN() {
		 switch ( CalculatorConfig.NUM_CLASS ) {
			 case FRACTION:
				 return NumFraction.NaN;
			 case BIG_FRACTION:
				 return NumBigFraction.NaN;
			 case SINGLE:
				 return NumSingle.NaN;
			 case DOUBLE:
			 default:
				 return NumDouble.NaN;
		 }
	}
	
	public static Num getZero() {
		 switch ( CalculatorConfig.NUM_CLASS ) {
			 case FRACTION:
				 return NumFraction.ZERO;
			 case BIG_FRACTION:
				 return NumBigFraction.ZERO;
			 case SINGLE:
				 return NumSingle.ZERO;
			 case DOUBLE:
			 default:
				 return NumDouble.ZERO;
		 }
	}
	
	public static Num getEpsilon() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return NumFraction.createEpsilon();
			 case BIG_FRACTION:
				 return NumBigFraction.createEpsilon();
			 case SINGLE:
				 return NumSingle.createEpsilon();
			case DOUBLE:
			default:
				return NumDouble.createEpsilon();
		}
	}
	
	public static Num createNum( int num ) {
		 switch ( CalculatorConfig.NUM_CLASS ) {
			 case FRACTION:
				 return new NumFraction( num );
			 case BIG_FRACTION:
				 return new NumBigFraction( num );
			 case SINGLE:
				 return new NumSingle( num );
			 case DOUBLE:
			 default:
				 return new NumDouble( num );
		 }
	}
	
	public static Num createNum( double value ) {
		 switch ( CalculatorConfig.NUM_CLASS ) {
			 case FRACTION:
				 return new NumFraction( value );
			 case BIG_FRACTION:
				 return new NumBigFraction( value );
			 case SINGLE:
				 return new NumSingle( value );
			 case DOUBLE:
			 default:
				 return new NumDouble( value );
		 }
	}
	
	public static Num createNum( String num_str ) throws Exception {
		 switch ( CalculatorConfig.NUM_CLASS ) {
			 case FRACTION:
				 return new NumFraction( num_str );
			 case BIG_FRACTION:
				 return new NumBigFraction( num_str );
			 case SINGLE:
				 return new NumSingle( num_str );
			 case DOUBLE:
			 default:
				 return new NumDouble( num_str );
		 }
	}
	
	public static Num createNum( int num, int den ) {
		 switch ( CalculatorConfig.NUM_CLASS ) {
			 case FRACTION:
				 return new NumFraction( num, den );
			 case BIG_FRACTION:
				 return new NumBigFraction( num, den );
			 case SINGLE:
				 return new NumSingle( num, den );
			 case DOUBLE:
			 default:
				 return new NumDouble( num, den );
		 }
	}
	
	public static Num createZero() {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return new NumFraction( SpecialValue.ZERO );
			 case BIG_FRACTION:
				 return new NumBigFraction( SpecialValue.ZERO );
			case SINGLE:
				 return new NumSingle( SpecialValue.ZERO );
			case DOUBLE:
			default:
				return new NumDouble( SpecialValue.ZERO );
		}
	}

	public static Num createPositiveInfinity() {
		 switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				 return new NumFraction( SpecialValue.POSITIVE_INFINITY );
			case BIG_FRACTION:
				 return new NumBigFraction( SpecialValue.POSITIVE_INFINITY );
			case SINGLE:
				 return new NumSingle( SpecialValue.POSITIVE_INFINITY );
			case DOUBLE:
			default:
				return new NumDouble( SpecialValue.POSITIVE_INFINITY );
		 }
	}
	
	public static Num createNegativeInfinity() {
		 switch ( CalculatorConfig.NUM_CLASS ) {
		 	case FRACTION:
				 return new NumFraction( SpecialValue.NEGATIVE_INFINITY );
		 	case BIG_FRACTION:
				 return new NumBigFraction( SpecialValue.NEGATIVE_INFINITY );
			case SINGLE:
				 return new NumSingle( SpecialValue.NEGATIVE_INFINITY );
			case DOUBLE:
			default:
				return new NumDouble( SpecialValue.NEGATIVE_INFINITY );
		 }
	}
	
	public static Num createNaN() {
		 switch ( CalculatorConfig.NUM_CLASS ) {
		 	case FRACTION:
		 		return new NumFraction( SpecialValue.NaN );
		 	case BIG_FRACTION:
				 return new NumBigFraction( SpecialValue.NaN );
			case SINGLE:
				 return new NumSingle( SpecialValue.NaN );
		 	case DOUBLE:
		 	default:
		 		return new NumDouble( SpecialValue.NaN );
		 }
	}

	protected static Num parse( String num_str ) throws Exception {
		boolean fraction_indicator = num_str.contains( " / " );
		boolean double_based = num_str.contains( "." );
		
		if ( fraction_indicator && double_based ) {
			throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString() );
		}
		
		try {
			// Either an integer of something strange
			if ( !fraction_indicator && !double_based ) {
				return createNum( Integer.parseInt( num_str ) );
			}
			
			if ( fraction_indicator ) {
				String[] num_den = num_str.split( " / " ); // ["num","den"]
				if( num_den.length != 2 ) {
					throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString() );
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
			throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString() );
		}
		
		// This code should not be reachable because all the operations above either succeed such that we can return a number
		// of raise an exception of some kind. Yet, Java does not get this and thus complains if there's no "finalizing statement". 
		throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.NUM_CLASS.toString() );
	}
}
