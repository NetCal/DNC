package org.networkcalculus.dnc.tandem.fifo.LUDBOPT;

/**
 * @author Alexander Scheffler
 */
public class TB_LUDB {
    private Expression_LUDB b;
    private Expression_LUDB r;
    public TB_LUDB(Expression_LUDB jump, Expression_LUDB rate)
    {
        b = jump;
        r = rate;
    }

    public Expression_LUDB getJump(){
        return b;
    }

    public Expression_LUDB getRate(){
        return r;
    }
}
