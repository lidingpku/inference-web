/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj;
import org.inference_web.pml.v2.*;

/**
 * Models variable mappings in Inference Web inference steps. Each binding of an inference step is
 * a mapping from a variable to a term specifying substitutions performed on the premises before the
 * application of the step's rule. For instance, substitutions may be required to unify the terms in 
 * premises in order to perform resolution. Variable mappings are used to model these bindings.
 */

public interface IWMapping extends PMLObject { 

    /** 
     * Returns the term of the variable mapping
     * @return the term of the variable mapping
     */
    public String getMapTo();

    /**
     * Sets the term of the variable mapping
     * @param newTerm the new term of the variable mapping
     */
    public void setMapTo(String newTerm);

    /**
     * Returns the variable of the variable mapping
     * @return the variable of the variable mapping
     */
    public String getMapFrom();

    /**
     * Sets the variable of the variable mapping
     * @param newVariable the new variable of the variable mapping
     */
    public void setMapFrom(String newVariable);

    public boolean equals (IWMapping mapping);
} /* END OF IWMapping */
