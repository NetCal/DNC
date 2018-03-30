package de.uni_kl.cs.discodnc;

import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.curves.LinearSegment;
import de.uni_kl.cs.discodnc.curves.dnc.Curve_DNC;
import de.uni_kl.cs.discodnc.curves.dnc.LinearSegment_DNC;
import de.uni_kl.cs.discodnc.minplus.MinPlus;
import de.uni_kl.cs.discodnc.minplus.MinPlus_DNC;
import de.uni_kl.cs.discodnc.numbers.Num;

public enum CurveBackend_DNC implements CurveBackend {
		DNC;
	
		@Override
		public MinPlus getMinPlus() {
			return MinPlus_DNC.MIN_PLUS_DNC;
		}

		@Override
		public CurvePwAffine getCurveFactory() {
			return Curve_DNC.getFactory();
		}

		@Override
		public LinearSegment createLinearSegment(Num x, Num y, Num grad, boolean leftopen) {
			return new LinearSegment_DNC(x, y, grad, leftopen);
		}

		@Override
		public LinearSegment createHorizontalLine(double y) {
			return new LinearSegment_DNC(Num.getFactory().createZero(),
                    Num.getFactory().create(y), Num.getFactory().createZero(), false);
		}
}
