package unikl.disco.numbers;

/**
 * 
 * The IEEE 754 floating point data types double and single provide infinity values and NaN.
 * For other data types, i.e., rational numbers based on the Apache Commons Math's Fraction classes,
 * we need to emulate the floating point behavior. 
 * 
 */
import unikl.disco.nc.CalculatorConfig;
import unikl.disco.nc.CalculatorConfig.NumClass;
import unikl.disco.numbers.implementations.FractionBigInteger;
import unikl.disco.numbers.implementations.DoublePrecision;
import unikl.disco.numbers.implementations.FractionInteger;
import unikl.disco.numbers.implementations.SinglePrecision;
import unikl.disco.numbers.values.NaN;
import unikl.disco.numbers.values.NegativeInfinity;
import unikl.disco.numbers.values.PositiveInfinity;

public class NumUtils {
	public static Num add( Num num1, Num num2 ) {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return DoublePrecision.add( (DoublePrecision)num1, (DoublePrecision)num2 );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return SinglePrecision.add( (SinglePrecision)num1, (SinglePrecision)num2 );
		}
		
		if( num1.isNaN() || num2.isNaN() 
				|| ( num1.isPosInfty() && num2.isNegInfty() ) 
				|| ( num1.isNegInfty() && num2.isPosInfty() ) ) {
			return new NaN();
		}
		if( num1.isPosInfty() || num2.isPosInfty() ) { // other num is not negative infinity
			return new PositiveInfinity(); 
		}
		if( num1.isNegInfty() || num2.isNegInfty() ) { // other num is not positive infinity
			return new NegativeInfinity(); 
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_BIG_INTEGER:
				 return FractionBigInteger.add( (FractionBigInteger)num1, (FractionBigInteger)num2 );
			case FRACTION_INTEGER:
			default:
				return FractionInteger.add( (FractionInteger)num1, (FractionInteger)num2 );
		}
	}

	public static Num sub( Num num1, Num num2 ) {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return DoublePrecision.sub( (DoublePrecision)num1, (DoublePrecision)num2 );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return SinglePrecision.sub( (SinglePrecision)num1, (SinglePrecision)num2 );
		}
		
		if( num1.isNaN() || num2.isNaN() ) {
			return new NaN();
		}
		
		if( num1.isNaN() || num2.isNaN() 
				|| ( num1.isPosInfty() && num2.isPosInfty() ) 
				|| ( num1.isNegInfty() && num2.isNegInfty() ) ) {
			return new NaN();
		}
		if( num1.isPosInfty()				// num2 is not positive infinity
				|| num2.isNegInfty() ) {	// num1 is not negative infinity
			return new PositiveInfinity(); 
		}
		if( num1.isNegInfty()				// num2 is not negative infinity
				|| num2.isPosInfty() ) {	// num1 is not positive infinity
			return new NegativeInfinity(); 
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.sub( (FractionBigInteger)num1, (FractionBigInteger)num2 );
			default:
			case FRACTION_INTEGER:
				return FractionInteger.sub( (FractionInteger)num1, (FractionInteger)num2 );
		}
	}

	public static Num mult( Num num1, Num num2 ) {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return DoublePrecision.mult( (DoublePrecision)num1, (DoublePrecision)num2 );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return SinglePrecision.mult( (SinglePrecision)num1, (SinglePrecision)num2 );
		}
		
		if( num1.isNaN() || num2.isNaN() ) {
			return new NaN();
		}
		if( num1.isPosInfty() ) {
			if ( num2.less( NumFactory.getZero() ) || num2.isNegInfty() ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num2.isPosInfty() ) {
			if ( num1.less( NumFactory.getZero() ) || num1.isNegInfty() ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num1.isNegInfty() ) {
			if ( num2.less( NumFactory.getZero() ) || num2.isNegInfty() ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		if( num2.isNegInfty() ) {
			if ( num1.less( NumFactory.getZero() ) || num1.isNegInfty() ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.mult( (FractionBigInteger)num1, (FractionBigInteger)num2 );
			case FRACTION_INTEGER:
			default:
				return FractionInteger.mult( (FractionInteger)num1, (FractionInteger)num2 );
		}
	}

	public static Num div( Num num1, Num num2 ) {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return DoublePrecision.div( (DoublePrecision)num1, (DoublePrecision)num2 );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return SinglePrecision.div( (SinglePrecision)num1, (SinglePrecision)num2 );
		}

		if( num1.isNaN() || num2.isNaN() 
				|| ( ( num1.isPosInfty() || num1.isNegInfty() ) && ( num2.isPosInfty() || num2.isNegInfty() ) ) ) { // two infinities in the division
			return new NaN();
		}
		if( num1.isPosInfty() ) { // positive infinity divided by some finite value
			if( num2.less( NumFactory.getZero() ) ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num1.isNegInfty() ) { // negative infinity divided by some finite value 
			if( num2.less( NumFactory.getZero() ) ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		if( num2.isPosInfty() || num2.isNegInfty() ) { // finite value divided by infinity
			return NumFactory.createZero();
		}

		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_BIG_INTEGER:
		        if ( ((FractionBigInteger)num2).isZero() ) {
		        	return new PositiveInfinity();
		       	} else {
					return FractionBigInteger.div( (FractionBigInteger)num1, (FractionBigInteger)num2 );     		
		       	}
			case FRACTION_INTEGER:
			default:
		        if ( ((FractionInteger)num2).isZero() ) {
		        	return new PositiveInfinity();
		       	} else {
		       		return FractionInteger.div( (FractionInteger)num1, (FractionInteger)num2 );        		
		       	}
		}
	}

	public static Num abs( Num num ) {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return DoublePrecision.abs( (DoublePrecision)num );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return SinglePrecision.abs( (SinglePrecision)num );
		}
		
		if( num.isNaN() ) {
			return new NaN();
		}
		if( num.isPosInfty() || num.isNegInfty() ) {
			return new PositiveInfinity();
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.abs( (FractionBigInteger)num );
			case FRACTION_INTEGER:
			default:
				return FractionInteger.abs( (FractionInteger)num );
		}
	}

	public static Num diff( Num num1, Num num2 ) {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return DoublePrecision.diff( (DoublePrecision)num1, (DoublePrecision)num2 );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return SinglePrecision.diff( (SinglePrecision)num1, (SinglePrecision)num2 );
		}
		
		if( num1.isNaN() || num2.isNaN() ) { 
			return new NaN();
		}
		if( num1.isPosInfty() || num2.isPosInfty() 
				|| num1.isNegInfty() || num2.isNegInfty() ) {
			return new PositiveInfinity();
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.diff( (FractionBigInteger)num1, (FractionBigInteger)num2 );
			default:
			case FRACTION_INTEGER:
				return FractionInteger.diff( (FractionInteger)num1, (FractionInteger)num2 );
		}
	}

	public static Num max( Num num1, Num num2 ) {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return DoublePrecision.max( (DoublePrecision)num1, (DoublePrecision)num2 );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return SinglePrecision.max( (SinglePrecision)num1, (SinglePrecision)num2 );
		}
		
		if( num1.isNaN() || num2.isNaN() ) {
			return new NaN();
		}
		if( num1.isPosInfty() || num2.isPosInfty() ) {
			return new PositiveInfinity();
		}
		if( num1.isNegInfty() ) {
			return num2.copy();
		}
		if( num2.isNegInfty() ) {
			return num1.copy();
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.max( (FractionBigInteger)num1, (FractionBigInteger)num2 );
			case FRACTION_INTEGER:
			default:
				return FractionInteger.max( (FractionInteger)num1, (FractionInteger)num2 );
		}
	}

	public static Num min( Num num1, Num num2 ) {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return DoublePrecision.min( (DoublePrecision)num1, (DoublePrecision)num2 );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return SinglePrecision.min( (SinglePrecision)num1, (SinglePrecision)num2 );
		}
		
		if( num1.isNaN() || num2.isNaN() ) {
			return new NaN();
		}
		if( num1.isNegInfty() || num2.isNegInfty() ) {
			return new NegativeInfinity();
		}
		if( num1.isPosInfty() ) {
			return num2.copy();
		}
		if( num2.isPosInfty() ) {
			return num1.copy();
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.min( (FractionBigInteger)num1, (FractionBigInteger)num2 );
			case FRACTION_INTEGER:
			default:
				return FractionInteger.min( (FractionInteger)num1, (FractionInteger)num2 );
		}
	}

	public static Num negate( Num num ) {
		if( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE_PRECISION ) {
			return SinglePrecision.negate( (SinglePrecision)num );
		}
		if( CalculatorConfig.NUM_CLASS == NumClass.SINGLE_PRECISION ) {
			return DoublePrecision.negate( (DoublePrecision)num );
		}
		
		if( num.isNaN() ) {
			return new NaN();
		}
		if( num.isPosInfty() ) {
			return new NegativeInfinity();
		}
		if( num.isNegInfty() ) {
			return new PositiveInfinity();
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case FRACTION_BIG_INTEGER:
				return FractionBigInteger.negate( (FractionBigInteger)num );
			case FRACTION_INTEGER:
			default:
				return FractionInteger.negate( (FractionInteger)num );
		}
	}
}
