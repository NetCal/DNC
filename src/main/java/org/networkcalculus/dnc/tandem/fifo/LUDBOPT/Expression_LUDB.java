package org.networkcalculus.dnc.tandem.fifo.LUDBOPT;

import org.apache.commons.math3.util.Pair;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.num.Num;

import java.util.*;

/**
 * @author Alexander Scheffler
 */
public class Expression_LUDB {
    private Num n_const;

    // In case this Expression object is of type VAR, we have to set an id for it
    private int id = -1;


    private Expression_LUDB left;
    private Expression_LUDB right;


    // VAR being the FIFO parameter
    // GEQ in context of constraints
    public enum ExpressionType {
        VAR, NUMBER, ADD, SUB, MULT, DIV, GEQ
    }

    Expression_LUDB.ExpressionType selected_type;


    public Expression_LUDB(Num n) {
        this.n_const = n;
        selected_type = ExpressionType.NUMBER;
    }


    // id needs to be unique
    public Expression_LUDB(int id) {
        selected_type = ExpressionType.VAR;
        this.id = id;
    }

    public Expression_LUDB(Expression_LUDB left, Expression_LUDB right, ExpressionType type) {
        this.left = left;
        this.right = right;
        selected_type = type;
    }

    public ExpressionType getType() {
        return selected_type;
    }

    public static Expression_LUDB createZeroNumExpression() {
        return new Expression_LUDB(Num.getUtils(Calculator.getInstance().getNumBackend()).createZero());
    }

    /**
     * @return the respective number in case the type is NUMBER. Otherwise null is returned.
     */
    public Num getNumber() {
        return n_const;
    }

    @Override
    public String toString() {
        String to_return = "";
        switch (selected_type) {
            case VAR:
                to_return = "s_" + id;
                break;
            case NUMBER:
                to_return = n_const.toString();
                break;
            case ADD:
                to_return = left.toString() + " + " + right.toString();
                break;
            case SUB:
                to_return = left.toString() + " - [" + right.toString()+ "]";
                break;
            case MULT:
                to_return = "[" + left.toString() + "] * [" + right.toString() + "]";
                break;
            case DIV:
                to_return = "[" + left.toString() + "] / [" + right.toString() + "]";
                break;
            case GEQ:
                to_return = left.toString() + " >= " + right.toString();
                break;

        }
        return to_return;
    }


    public Pair<Num, Map<Integer, Num>> simplifyExpression() throws Exception {
        Num constant = null;
        Map<Integer, Num> map_to_return = new HashMap();
        Num zero = Num.getUtils(Calculator.getInstance().getNumBackend()).createZero();
        Num minus_one = Num.getUtils(Calculator.getInstance().getNumBackend()).create(-1);

        Pair<Num, Map<Integer, Num>> pair_left;
        Pair<Num, Map<Integer, Num>> pair_right;
        Num const_left;
        Num const_right;
        Map<Integer, Num> coeffs_left;
        Map<Integer, Num> coeffs_right;
        ArrayList<Integer> keys_union;



        switch (selected_type) {
            case VAR:
                constant =  zero;
                map_to_return.put(id,Num.getUtils(Calculator.getInstance().getNumBackend()).create(1) );
                break;
            case NUMBER:
                constant = n_const;
                // and empty map
                break;
            case ADD:
                pair_left = left.simplifyExpression() ;
                pair_right = right.simplifyExpression();
                const_left = pair_left.getFirst();
                coeffs_left = pair_left.getSecond();
                const_right = pair_right.getFirst();
                coeffs_right = pair_right.getSecond();
                constant = Num.getUtils(Calculator.getInstance().getNumBackend()).add(const_left, const_right);

                keys_union = new ArrayList();
                keys_union.addAll(coeffs_left.keySet());
                keys_union.addAll(coeffs_right.keySet());

                for(int i : keys_union)
                {
                    boolean left_has_key = coeffs_left.containsKey(i);
                    boolean right_has_key = coeffs_right.containsKey(i);
                    if(left_has_key && right_has_key)
                    {
                        map_to_return.put(i, Num.getUtils(Calculator.getInstance().getNumBackend()).add(coeffs_left.get(i), coeffs_right.get(i)));
                    }

                    if(!left_has_key && right_has_key)
                    {
                        map_to_return.put(i,  coeffs_right.get(i));
                    }

                    if(left_has_key && !right_has_key)
                    {
                        map_to_return.put(i,  coeffs_left.get(i));
                    }

                    // no other case since i is in the union of keys
                }
                break;
            case SUB:
                pair_left = left.simplifyExpression() ;
                pair_right = right.simplifyExpression();
                const_left = pair_left.getFirst();
                coeffs_left = pair_left.getSecond();
                const_right = pair_right.getFirst();
                coeffs_right = pair_right.getSecond();
                constant = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(const_left, const_right);

                keys_union = new ArrayList();
                keys_union.addAll(coeffs_left.keySet());
                keys_union.addAll(coeffs_right.keySet());

                for(int i : keys_union)
                {
                    boolean left_has_key = coeffs_left.containsKey(i);
                    boolean right_has_key = coeffs_right.containsKey(i);
                    if(left_has_key && right_has_key)
                    {
                        map_to_return.put(i, Num.getUtils(Calculator.getInstance().getNumBackend()).sub(coeffs_left.get(i), coeffs_right.get(i)));
                    }

                    if(!left_has_key && right_has_key)
                    {
                        map_to_return.put(i,  Num.getUtils(Calculator.getInstance().getNumBackend()).mult(minus_one, coeffs_right.get(i)));
                    }

                    if(left_has_key && !right_has_key)
                    {
                        map_to_return.put(i,  coeffs_left.get(i));
                    }

                    // no other case since i is in the union of keys
                }
                break;
            case MULT:
                pair_left = left.simplifyExpression() ;
                pair_right = right.simplifyExpression();
                const_left = pair_left.getFirst();
                coeffs_left = pair_left.getSecond();
                const_right = pair_right.getFirst();
                coeffs_right = pair_right.getSecond();
                constant = Num.getUtils(Calculator.getInstance().getNumBackend()).mult(const_left, const_right);

                keys_union = new ArrayList();
                keys_union.addAll(coeffs_left.keySet());
                keys_union.addAll(coeffs_right.keySet());

                for(int i : keys_union) {
                    boolean left_has_key = coeffs_left.containsKey(i);
                    boolean right_has_key = coeffs_right.containsKey(i);

                    if (left_has_key && right_has_key) {
                        throw new Exception("Nonlinear!");
                    }


                    if (!left_has_key && right_has_key) {
                        map_to_return.put(i, Num.getUtils(Calculator.getInstance().getNumBackend()).mult(const_left, coeffs_right.get(i)));
                    }

                    if (left_has_key && !right_has_key) {
                        map_to_return.put(i, Num.getUtils(Calculator.getInstance().getNumBackend()).mult(coeffs_left.get(i), const_right));
                    }

                    // no other case since i is in the union of keys
                }
                break;
            case DIV:

                pair_left = left.simplifyExpression() ;
                pair_right = right.simplifyExpression();
                const_left = pair_left.getFirst();
                coeffs_left = pair_left.getSecond();
                const_right = pair_right.getFirst();
                coeffs_right = pair_right.getSecond();

                if(const_right.eqZero())
                {
                    throw new Exception("Div by zero!");
                }

                constant = Num.getUtils(Calculator.getInstance().getNumBackend()).div(const_left, const_right);

                keys_union = new ArrayList();
                keys_union.addAll(coeffs_left.keySet());
                keys_union.addAll(coeffs_right.keySet());

                for(int i : keys_union) {
                    boolean left_has_key = coeffs_left.containsKey(i);
                    boolean right_has_key = coeffs_right.containsKey(i);

                    if (left_has_key && right_has_key) {
                        throw new Exception("Nonlinear!");
                    }


                    if (!left_has_key && right_has_key) {
                        if ( coeffs_right.get(i).eqZero()) {
                            throw new Exception("Div by zero");
                        }
                        map_to_return.put(i, Num.getUtils(Calculator.getInstance().getNumBackend()).div(const_left, coeffs_right.get(i)));
                    }

                    if (left_has_key && !right_has_key) {
                        map_to_return.put(i, Num.getUtils(Calculator.getInstance().getNumBackend()).div(coeffs_left.get(i), const_right));
                    }

                    // no other case since i is in the union of keys
                }
                break;
            default:
                throw new Exception("Faulty Expression!");
        }
        // filter out map entries with coefficient of zero
        Set<Integer> entries_to_delete_keys = new HashSet<>();
        if(map_to_return.containsValue(zero))
        {
            for(int i : map_to_return.keySet())
            {
                if (map_to_return.get(i).eqZero())
                {
                    entries_to_delete_keys.add(i);
                }
            }
        }

        for(int i : entries_to_delete_keys)
        {
            map_to_return.remove(i);
        }

        return new Pair<>(constant, map_to_return);
    }

    public Pair<Pair, Pair> simplifyConstraint() throws Exception {
        // Assumes GEQ
        Pair<Num, Map<Integer, Num>>  pair_left;
        Pair<Num, Map<Integer, Num>>  pair_right;

        switch (selected_type) {
            case GEQ:
                pair_left = left.simplifyExpression();
                pair_right = right.simplifyExpression();
                break;
            default:
                throw new Exception("Faulty Expression!");
        }
        return new Pair<>(pair_left, pair_right);
    }
}
