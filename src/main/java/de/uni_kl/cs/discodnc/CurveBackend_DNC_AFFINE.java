package de.uni_kl.cs.discodnc;

import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.LinearSegment;
import de.uni_kl.cs.discodnc.curves.dnc.LinearSegment_DNC;
import de.uni_kl.cs.discodnc.curves.dnc.affine.AffineCurve_DNC;
import de.uni_kl.cs.discodnc.minplus.MinPlus;
import de.uni_kl.cs.discodnc.minplus.dnc.MinPlus_DNC;

public enum CurveBackend_DNC_AFFINE implements CurveBackend {
		DNC_AFFINE;
	
		@Override
		public MinPlus getMinPlus() {
			return MinPlus_DNC.MINPLUS_DNC;
		}

		@Override
		public Curve getCurveFactory() {
			return AffineCurve_DNC.getFactory();
		}

		@Override
		public LinearSegment.Builder getLinearSegmentFactory() {
			return LinearSegment_DNC.getBuilder();
		}
}
