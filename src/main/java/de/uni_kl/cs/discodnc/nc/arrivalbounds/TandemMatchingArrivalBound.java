package de.uni_kl.cs.discodnc.nc.arrivalbounds;

// TODO: Rebuild based on the newest PmooArrivalBound class.
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.uni_kl.cs.discodnc.nc.ArrivalBoundDispatch;
import de.uni_kl.cs.discodnc.nc.AbstractArrivalBound;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig;
import de.uni_kl.cs.discodnc.nc.ArrivalBound;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.GammaFlag;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.discodnc.nc.analyses.TandemMatchingAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.bounds.LeftOverService;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Link;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.Path;
import de.uni_kl.cs.discodnc.network.Server;
import de.uni_kl.cs.discodnc.curves.MaxServiceCurve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.misc.SetUtils;
import de.uni_kl.cs.discodnc.numbers.Num;
import de.uni_kl.cs.discodnc.minplus.MinPlus;

public class TandemMatchingArrivalBound extends AbstractArrivalBound implements ArrivalBound {
	private static TandemMatchingArrivalBound instance = new TandemMatchingArrivalBound();

	private TandemMatchingArrivalBound() {
	}

	public TandemMatchingArrivalBound( Network network, AnalysisConfig configuration ) {
		this.network = network;
		this.configuration = configuration;
	}

	public static TandemMatchingArrivalBound getInstance() {
		return instance;
	}
	
	public Set<ArrivalCurve> computeArrivalBound( Link link, Flow flow_of_interest ) throws Exception {
		return computeArrivalBound( link, network.getFlows( link ), flow_of_interest );
	}
	
	/**
	 * 
	 * Bound the arrivals of flows on the given link,
	 * i.e., the output from link.getSource().
	 * 
	 * Therefore, the backlog bound of link.getSource() is
	 * a bound on the arrival burstiness.
	 * 
	 */
	public Set<ArrivalCurve> computeArrivalBound( Link link, Set<Flow> f_xfcaller, Flow flow_of_interest ) throws Exception {
		Set<ArrivalCurve> alphas_xfcaller = new HashSet<ArrivalCurve>( Collections.singleton( CurvePwAffine.getFactory().createZeroArrivals() ) );
		if ( f_xfcaller == null || f_xfcaller.isEmpty() )
		{
			return alphas_xfcaller;
		}
		
		// Get the common sub-path of f_xfcaller flows crossing the given link
		Server to = link.getDest();
		Set<Flow> f_to = network.getFlows( to );
		Set<Flow> f_xfcaller_to = SetUtils.getIntersection( f_to, f_xfcaller );
		f_xfcaller_to.remove( flow_of_interest );
		if ( f_xfcaller_to.isEmpty() ) {
			return alphas_xfcaller;
		}
		
		if( configuration.multiplexingDiscipline() == MuxDiscipline.GLOBAL_FIFO 
				|| ( configuration.multiplexingDiscipline() == MuxDiscipline.SERVER_LOCAL & link.getSource().multiplexingDiscipline() == Multiplexing.FIFO ) )
		{
			throw new Exception( "Tandem matching arrival bounding is not available for FIFO multiplexing nodes" );
		}
		
		Server from = network.findSplittingServer( to, f_xfcaller_to );
		Path common_subpath;
		Set<ServiceCurve> betas_loxfcaller_subpath = new HashSet<ServiceCurve>();
		ServiceCurve null_service = CurvePwAffine.getFactory().createZeroService();
		
		if ( from.equals( to ) ) { // Shortcut if the common subpath only consists of a single hop
			common_subpath = new Path( to );

			Set<Flow> f_xxfcaller = network.getFlows( link );
			f_xxfcaller.removeAll( f_xfcaller );
			f_xxfcaller.remove( flow_of_interest );
			Set<ArrivalCurve> alphas_xxfcaller = ArrivalBoundDispatch.computeArrivalBounds( network, configuration, to, f_xxfcaller, flow_of_interest );
			
			for( ServiceCurve beta_loxfcaller_subpath : LeftOverService.arbMux( to.getServiceCurve(), alphas_xxfcaller ) ) {
				if( !beta_loxfcaller_subpath.equals( null_service ) ) {
					betas_loxfcaller_subpath.add( beta_loxfcaller_subpath ); // Adding to the set, not adding up the curves
				}
			}
		} else {
			common_subpath = f_xfcaller_to.iterator().next().getPath().getSubPath( from, link.getSource() );

			TandemMatchingAnalysis cut = new TandemMatchingAnalysis( network, configuration );
			betas_loxfcaller_subpath = cut.getServiceCurves( flow_of_interest, common_subpath, f_xfcaller_to );
		}
		
		// Check if there's any service left on this path. Signaled by at least one service curve in this set
		if( betas_loxfcaller_subpath.isEmpty() ) {
			System.out.println( "No service left over during TMA arrival bounding!" );
			alphas_xfcaller.clear();
			alphas_xfcaller.add( CurvePwAffine.getFactory().createArrivalCurve( CurvePwAffine.getFactory().createZeroDelayInfiniteBurst() ) );
			return alphas_xfcaller;
		}
		
		// Get arrival bound at the splitting point:
		// We need to know the arrival bound of f_xfcaller at the server 'from', i.e., at the above sub-path's source
		// in order to deconvolve it with beta_loxfcaller_subpath to get the arrival bound of the sub-path
		// Note that flows f_xfcaller that originate in 'from' are covered by this call of computeArrivalBound
		Set<ArrivalCurve> alpha_xfcaller_from = ArrivalBoundDispatch.computeArrivalBounds( network, configuration, from, f_xfcaller, flow_of_interest );
		
		// Convolve to get the bound.
		// See "Improving Performance Bounds in Feed-Forward Networks by Paying Multiplexing Only Once", Lemma 2
		if( configuration.useGamma() != GammaFlag.GLOBALLY_OFF  )
		{
			MaxServiceCurve gamma = common_subpath.getGamma();
			alphas_xfcaller = MinPlus.deconvolve_almostConcCs_SCs( MinPlus.convolve_ACs_MSC( alpha_xfcaller_from, gamma ), betas_loxfcaller_subpath );
		}
		else
		{
			alphas_xfcaller = MinPlus.deconvolve( alpha_xfcaller_from, betas_loxfcaller_subpath, configuration.tbrlDeconvolution() );
		}
		
		if( configuration.useExtraGamma() != GammaFlag.GLOBALLY_OFF )
		{
			MaxServiceCurve extra_gamma = common_subpath.getExtraGamma();
			alphas_xfcaller = MinPlus.convolve_ACs_EGamma( alphas_xfcaller, extra_gamma );
		}// The following code only works for token buckets and rate latencies
		if( !configuration.tbrlConvolution()
				|| !configuration.tbrlDeconvolution() ) {
			return alphas_xfcaller;
		}
		
		if( configuration.abConsiderTFANodeBacklog() ) {
			Server last_hop_xtx = link.getSource();
			TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, configuration ); // If we reach this point, then the configuration includes CUT_PMOO arrival bounding which should be best for TFA as well.
			tfa.deriveBoundsAtServer( last_hop_xtx );
			
			Set<Num> tfa_backlog_bounds = tfa.getServerBacklogBoundMap().get( last_hop_xtx );
			Num tfa_backlog_bound_min = Num.getFactory().getPositiveInfinity();
			
			for( Num tfa_backlog_bound : tfa_backlog_bounds ) {
				if( tfa_backlog_bound.leq( tfa_backlog_bound_min ) ) {
					tfa_backlog_bound_min = tfa_backlog_bound;
				}
			}
			
			// Reduce the burst
			for( ArrivalCurve alpha_xfcaller : alphas_xfcaller ) {
				if( alpha_xfcaller.getBurst().gt( tfa_backlog_bound_min ) ) {

//					System.out.println( "flows " + Integer.toString( network.getFlows( last_hop_xtx ).size() ) );
//					System.out.println( "backlog   " + Double.toString( tfa_backlog_bound_min.doubleValue() ) );
//					System.out.println( "flows " + Integer.toString( f_xfcaller.size() ));
//					System.out.println( "burst out " + Double.toString( alpha_xfcaller.getBurst().doubleValue() ) );
//					System.out.println( "burst in  " + Double.toString( alpha_xfcaller_from.iterator().next().getBurst().doubleValue() ) );
//					System.out.println( Double.toString( alpha_xfcaller.getBurst().doubleValue() - tfa_backlog_bound_min.doubleValue() ) );
//					System.out.println( Integer.toString( common_subpath.numServers() ) );
//					System.out.println();
					
//					System.out.println( "TFA B could improve the PBOO arrival bound!" );
//					System.out.println( link.toString() + " --- " + f_xfcaller.toString() );
//					System.out.println( "Arrival bound burstiness of " + alpha_xfcaller.getBurst().toString() + " will be reduced to " + tfa_backlog_bound_min.toString() );
					alpha_xfcaller.getSegment( 1 ).setY( tfa_backlog_bound_min ); // if the burst is >0 then there are at least two segments and the second holds the burst as its y-axis value
				}
			}
			
			// Further alternatives
			//
//			// Chang's Lemma 1.4.2.
//			// Input ac burst
//			Num burstiness_arrivals_at_from = alpha_xfcaller_from.iterator().next().getBurst();
//			Num input_burst_plus_backlog_bound = Num.add( tfa_backlog_bound_min, burstiness_arrivals_at_from );
//			
//			// Should be a single iteration loop due to convolution of alternatives
//			for( ArrivalCurve alpha_xfcaller : alphas_xfcaller ) {
//				
//				if( alpha_xfcaller.getBurst().greater( input_burst_plus_backlog_bound ) ) {
//					ab_calls_improved++;
//					burst_reduction += ( alpha_xfcaller.getBurst().doubleValue() - input_burst_plus_backlog_bound.doubleValue() );
//					alpha_xfcaller.getSegment( 1 ).setY( input_burst_plus_backlog_bound ); // if the burst is >0 then there are at least two segments and the second holds the burst as its y-axis value
//				}
//			}
//			
//			// Arrival Bound for all flows at the last server (link's src) + Deconvolve
//			ArrivalCurve ab_all = computeArrivalBounds( network, configuration, link.getSource(), network.getFlows( link.getSource() ), Flow.NULL_FLOW ).iterator().next();
//			ab_all = Deconvolution.deconvolve( ab_all, link.getSource().getServiceCurve() );
//			return new HashSet<ArrivalCurve>( Collections.singleton( ArrivalCurve.min( alphas_xfcaller.iterator().next(), ab_all ) ) );
		}
		
		return alphas_xfcaller;
	}
}
