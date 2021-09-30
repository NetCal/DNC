package org.networkcalculus.dnc.tandem.fifo.LUDBOPT;

import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.num.Num;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * @author Alexander Scheffler
 */
public class PseudoAffine {
    private Expression_LUDB latency;
    private  List<TB_LUDB> stages;
    public PseudoAffine(Expression_LUDB latency, List<TB_LUDB> stages)
    {
        this.latency = latency;
        this.stages = stages;
    }

    public Expression_LUDB getLatency(){
        return latency;
    }

    public List<TB_LUDB> getStages(){
        return stages;
    }

    /**
     * Neutral element concerning convolution.
     * @return
     */
    public static PseudoAffine createZeroDelayInfiniteBurst()
    {
        Expression_LUDB zero_latency = Expression_LUDB.createZeroNumExpression();
        Expression_LUDB infinite_burst = new Expression_LUDB(Num.getUtils(Calculator.getInstance().getNumBackend()).createPositiveInfinity());
        Expression_LUDB zero_rate = Expression_LUDB.createZeroNumExpression();
        TB_LUDB tb = new TB_LUDB(infinite_burst, zero_rate);
        return new PseudoAffine(zero_latency, Arrays.asList(tb));
    }

    public static PseudoAffine convolve(PseudoAffine sc_1, PseudoAffine sc_2)
    {
        // if sc_1 is neutral elements then return sc_2 (and vice versa)
        if(isConvNeutral(sc_1))
        {
            return sc_2;
        }

        if(isConvNeutral(sc_2))
        {
            return sc_1;
        }

        // Convolution of pseudoaffine curves: The new latency is just the addition of the latencies of the curves, the stages is the union of the stages of the curves
        Expression_LUDB latency_result = new Expression_LUDB(sc_1.getLatency(), sc_2.getLatency(), Expression_LUDB.ExpressionType.ADD);
        List<TB_LUDB> stages_result = new ArrayList<>();
        stages_result.addAll(sc_1.getStages());
        stages_result.addAll(sc_2.getStages());

        return new PseudoAffine(latency_result, stages_result);
    }

    public static boolean isConvNeutral(PseudoAffine curve)
    {
        boolean isNeutral = false;
        if(curve.getStages().size()==1)
        {
            Expression_LUDB latency = curve.getLatency();
            if(latency.getType().equals(Expression_LUDB.ExpressionType.NUMBER))
            {
                Num latency_num = latency.getNumber();
                if(latency_num.eqZero())
                {
                    // there is only one stage
                    TB_LUDB stage = curve.getStages().get(0);
                    Expression_LUDB j = stage.getJump();
                    Expression_LUDB r = stage.getRate();
                    if(j.getType().equals(Expression_LUDB.ExpressionType.NUMBER) && r.getType().equals(Expression_LUDB.ExpressionType.NUMBER))
                    {
                        Num j_num = j.getNumber();
                        Num r_num = r.getNumber();
                        if(j_num.eq(Num.getUtils(Calculator.getInstance().getNumBackend()).createPositiveInfinity()) && r_num.eqZero())
                        {
                            isNeutral =  true;
                        }
                    }
                }
            }
        }
        return isNeutral;
    }
}

