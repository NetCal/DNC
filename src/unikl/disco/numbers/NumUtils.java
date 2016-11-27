package unikl.disco.numbers;

import unikl.disco.nc.CalculatorConfig;

public class NumUtils {

	public static Num abs( Num num ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return NumFraction.abs( (NumFraction)num );
			case BIG_FRACTION:
				return NumBigFraction.abs( (NumBigFraction)num );
			case SINGLE:
				 return NumSingle.abs( (NumSingle)num );
			case DOUBLE:
			default:
				return NumDouble.abs( (NumDouble)num );
		}
	}

	public static Num add( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return NumFraction.add( (NumFraction)num1, (NumFraction)num2 );
			case BIG_FRACTION:
				 return NumBigFraction.add( (NumBigFraction)num1, (NumBigFraction)num2 );
			case SINGLE:
				 return NumSingle.add( (NumSingle)num1, (NumSingle)num2 );
			case DOUBLE:
			default:
				return NumDouble.add( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num diff( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return NumFraction.diff( (NumFraction)num1, (NumFraction)num2 );
			case BIG_FRACTION:
				return NumBigFraction.diff( (NumBigFraction)num1, (NumBigFraction)num2 );
			case SINGLE:
				 return NumSingle.diff( (NumSingle)num1, (NumSingle)num2 );
			case DOUBLE:
			default:
				return NumDouble.diff( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num div( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return NumFraction.div( (NumFraction)num1, (NumFraction)num2 );
			case BIG_FRACTION:
				return NumBigFraction.div( (NumBigFraction)num1, (NumBigFraction)num2 );
			case SINGLE:
				 return NumSingle.div( (NumSingle)num1, (NumSingle)num2 );
			case DOUBLE:
			default:
				return NumDouble.div( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num max( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return NumFraction.max( (NumFraction)num1, (NumFraction)num2 );
			case BIG_FRACTION:
				return NumBigFraction.max( (NumBigFraction)num1, (NumBigFraction)num2 );
			case SINGLE:
				 return NumSingle.max( (NumSingle)num1, (NumSingle)num2 );
			case DOUBLE:
			default:
				return NumDouble.max( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num min( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return NumFraction.min( (NumFraction)num1, (NumFraction)num2 );
			case BIG_FRACTION:
				return NumBigFraction.min( (NumBigFraction)num1, (NumBigFraction)num2 );
			case SINGLE:
				 return NumSingle.min( (NumSingle)num1, (NumSingle)num2 );
			case DOUBLE:
			default:
				return NumDouble.min( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num mult( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return NumFraction.mult( (NumFraction)num1, (NumFraction)num2 );
			case BIG_FRACTION:
				return NumBigFraction.mult( (NumBigFraction)num1, (NumBigFraction)num2 );
			case SINGLE:
				 return NumSingle.mult( (NumSingle)num1, (NumSingle)num2 );
			case DOUBLE:
			default:
				return NumDouble.mult( (NumDouble)num1, (NumDouble)num2 );
		}
	}

	public static Num negate( Num num ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return NumFraction.negate( (NumFraction)num );
			case BIG_FRACTION:
				return NumBigFraction.negate( (NumBigFraction)num );
			case SINGLE:
				 return NumSingle.negate( (NumSingle)num );
			case DOUBLE:
			default:
				return NumDouble.negate( (NumDouble)num );
		}
	}

	public static Num sub( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION:
				return NumFraction.sub( (NumFraction)num1, (NumFraction)num2 );
			case BIG_FRACTION:
				return NumBigFraction.sub( (NumBigFraction)num1, (NumBigFraction)num2 );
			case SINGLE:
				 return NumSingle.sub( (NumSingle)num1, (NumSingle)num2 );
			case DOUBLE:
			default:
				return NumDouble.sub( (NumDouble)num1, (NumDouble)num2 );
		}
	}

}
