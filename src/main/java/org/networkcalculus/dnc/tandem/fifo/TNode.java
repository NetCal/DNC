package org.networkcalculus.dnc.tandem.fifo;


import java.util.ArrayList;
import java.util.List;

import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.tandem.fifo.LUDBOPT.Expression_LUDB;
import org.networkcalculus.dnc.tandem.fifo.LUDBOPT.PseudoAffine;

/**
 * Represents a nesting tree for a nested tandem.
 * @param <T>
 * @author Alexander Scheffler
 */
public class TNode<T> {

    // content of the node --- either flow or list of servers
    private T inf;
    private ServiceCurve leftover;

    // due bottom up construction of the left over service curve for the foi
    private TNode<T> parent;
    private ArrayList<TNode<T>> children;

    // current decompositions (LUDB mode)
    private PseudoAffine curr_pseudo_affine_curve;
    private List<Expression_LUDB> curr_constraints;

    public TNode(T inf, TNode parent)
    {
        this.inf = inf;
        this.parent = parent;
        children = new ArrayList<TNode<T>>();
        // neutral element concerning convolution
        leftover = Curve.getFactory().createZeroDelayInfiniteBurst();
    }

    public TNode<T> addChild(T child)
    {
        TNode<T> childNode = new TNode<T>(child, this);
        children.add(childNode);
        return childNode;
    }

    public T getInf()
    {
        return inf;
    }

    public TNode<T> getParent()
    {
        return parent;
    }

    public ArrayList<TNode<T>> getChildren()
    {
        return children;
    }

    public ServiceCurve getLeftover() {return  leftover;}

    public void setLeftover(ServiceCurve curve) {  leftover = curve;}

    public void setCurrentPseudoAffineCurveAndConstraints(PseudoAffine curr_pseudo_affine_curve, List<Expression_LUDB> curr_constraints)
    {
        this.curr_pseudo_affine_curve = curr_pseudo_affine_curve;
        this.curr_constraints = curr_constraints;
    }

    public PseudoAffine getCurrentPseudoAffineCurve()
    {
        return curr_pseudo_affine_curve;
    }

    public List<Expression_LUDB> getCurrentConstraints()
    {
        return curr_constraints;
    }

}




