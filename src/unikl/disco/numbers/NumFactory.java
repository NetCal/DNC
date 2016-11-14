package unikl.disco.numbers;

import unikl.disco.nc.CalculatorConfig;
import unikl.disco.nc.CalculatorConfig.NumClass;
import unikl.disco.numbers.Num.SpecialValue;

public class NumFactory {
	
	public static Num getPositiveInfinity() {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return NumFraction.POSITIVE_INFINITY;
			 case DOUBLE:
			 default:
				 return NumDouble.POSITIVE_INFINITY;
		 }
	}
	
	public static Num getNegativeInfinity() {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return NumFraction.NEGATIVE_INFINITY;
			 case DOUBLE:
			 default:
				 return NumDouble.NEGATIVE_INFINITY;
		 }
	}

	public static Num getNaN() {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return NumFraction.NaN;
			 case DOUBLE:
			 default:
				 return NumDouble.NaN;
		 }
	}
	
	public static Num getZero() {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return NumFraction.ZERO;
			 case DOUBLE:
			 default:
				 return NumDouble.ZERO;
		 }
	}
	
	public static Num getEpsilon() {
		switch (CalculatorConfig.NUM_CLASS) {
			case FRACTION:
				return NumFraction.createEpsilon();
			case DOUBLE:
			default:
				return NumDouble.createEpsilon();
		}
	}
	
	public static Num createNum( int num ) {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return new NumFraction( num );
			 case DOUBLE:
			 default:
				 return new NumDouble( num );
		 }
	}
	
	public static Num createNum( double value ) {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return new NumFraction( value );
			 case DOUBLE:
			 default:
				 return new NumDouble( value );
		 }
	}
	
	public static Num createNum( String num_str ) throws Exception {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return new NumFraction( num_str );
			 case DOUBLE:
			 default:
				 return new NumDouble( num_str );
		 }
	}
	
	public static Num createNum( int num, int den ) {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return new NumFraction( num, den );
			 case DOUBLE:
			 default:
				 return new NumDouble( num, den );
		 }
	}
	
	// In contrast to the other factory methods, this one does not strictly follow the flag in CalculatorConfig.
	// The instance of the given object has higher priority in determining the actual num class to use.
	public static Num createNum( Num num ) {
		if ( CalculatorConfig.NUM_CLASS == NumClass.DOUBLE
				|| num instanceof NumDouble ) {
			return new NumDouble( num.doubleValue() );
		} else { // if ( CalculatorConfig.NUM_CLASS == NumClass.FRACTION && num instanceof NumFraction )
			return new NumFraction( (NumFraction)num );
		}
	}
	
	public static Num createZero() {
		switch (CalculatorConfig.NUM_CLASS) {
			case FRACTION:
				return new NumFraction( SpecialValue.ZERO );
			case DOUBLE:
			default:
				return new NumDouble( SpecialValue.ZERO );
		}
	}

	public static Num createPositiveInfinity() {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return new NumFraction( SpecialValue.POSITIVE_INFINITY );
			 case DOUBLE:
			 default:
				 return new NumDouble( SpecialValue.POSITIVE_INFINITY );
		 }
	}
	
	public static Num createNegativeInfinity() {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return new NumFraction( SpecialValue.NEGATIVE_INFINITY );
			 case DOUBLE:
			 default:
				 return new NumDouble( SpecialValue.NEGATIVE_INFINITY );
		 }
	}
	
	public static Num createNaN() {
		 switch (CalculatorConfig.NUM_CLASS) {
			 case FRACTION:
				 return new NumFraction( SpecialValue.NaN );
			 case DOUBLE:
			 default:
				 return new NumDouble( SpecialValue.NaN );
		 }
	}
}
