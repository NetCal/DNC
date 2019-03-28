package org.networkcalculus.dnc.algebra.disco;

public class MinPlus_Disco_Configuration {
	private static MinPlus_Disco_Configuration instance = new MinPlus_Disco_Configuration();

	public static MinPlus_Disco_Configuration getInstance() {
		return instance;
	}

	private boolean DECONVOLUTION_CHECKS = false;
	private boolean MAX_SERVICE_CURVE_CHECKS = false;

	public void disableAllChecks() {
		DECONVOLUTION_CHECKS = false;
		MAX_SERVICE_CURVE_CHECKS = false;
	}

	public void enableAllChecks() {
		DECONVOLUTION_CHECKS = true;
		MAX_SERVICE_CURVE_CHECKS = true;
	}

	public boolean exec_deconvolution_checks() {
		return DECONVOLUTION_CHECKS;
	}

	public boolean exec_max_service_curve_checks() {
		return MAX_SERVICE_CURVE_CHECKS;
	}

	@Override
	public String toString() {
		StringBuffer config_str = new StringBuffer();

		if (exec_deconvolution_checks()) {
			config_str.append("deconv checks");
			config_str.append(", ");
		}
		if (exec_max_service_curve_checks()) {
			config_str.append("MSC checks");
			config_str.append(", ");
		}

		if( config_str.length() == 0 ) {
			config_str.append("All Disco min-plus-algebra checks are disabled");
		} else {
			config_str.delete(config_str.lastIndexOf(","), config_str.length());
		}
		
		return config_str.toString();
	}
}
