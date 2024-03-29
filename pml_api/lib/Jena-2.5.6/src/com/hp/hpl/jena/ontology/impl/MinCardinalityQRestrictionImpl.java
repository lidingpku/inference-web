/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       ian.dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            08-Sep-2003
 * Filename           $RCSfile: MinCardinalityQRestrictionImpl.java,v $
 * Revision           $Revision: 1.8 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2008/01/02 12:08:03 $
 *               by   $Author: andy_seaborne $
 *
 * (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 *****************************************************************************/

// Package
///////////////
package com.hp.hpl.jena.ontology.impl;


// Imports
///////////////
import com.hp.hpl.jena.enhanced.*;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.ontology.*;


/**
 * <p>
 * Implementation of the min qualified cardinality restriction
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: MinCardinalityQRestrictionImpl.java,v 1.8 2008/01/02 12:08:03 andy_seaborne Exp $
 */
public class MinCardinalityQRestrictionImpl 
    extends QualifiedRestrictionImpl
    implements MinCardinalityQRestriction
{
    // Constants
    //////////////////////////////////

    // Static variables
    //////////////////////////////////
    
    /**
     * A factory for generating QualifiedRestriction facets from nodes in enhanced graphs.
     * Note: should not be invoked directly by user code: use 
     * {@link com.hp.hpl.jena.rdf.model.RDFNode#as as()} instead.
     */
    public static Implementation factory = new Implementation() {
        public EnhNode wrap( Node n, EnhGraph eg ) { 
            if (canWrap( n, eg )) {
                return new MinCardinalityQRestrictionImpl( n, eg );
            }
            else {
                throw new ConversionException( "Cannot convert node " + n + " to MinCardinalityQRestriction");
            } 
        }
            
        public boolean canWrap( Node node, EnhGraph eg ) 
            { return isMinCardinalityQRestriction( node, eg ); }
    };
    
    public static boolean isMinCardinalityQRestriction( Node node, EnhGraph eg ) 
        {
        // node will support being a QualifiedRestriction facet if it has rdf:type owl:Restriction or equivalent
        Profile profile = (eg instanceof OntModel) ? ((OntModel) eg).getProfile() : null;
        return (profile != null)  &&  profile.isSupported( node, eg, MinCardinalityQRestriction.class );
        }
    
    public boolean isValid()
        { return super.isValid() && isMinCardinalityQRestriction( asNode(), getGraph() ); }
    
    // Instance variables
    //////////////////////////////////

    /**
     * <p>
     * Construct a qualified restriction node represented by the given node in the given graph.
     * </p>
     * 
     * @param n The node that represents the resource
     * @param g The enh graph that contains n
     */
    public MinCardinalityQRestrictionImpl( Node n, EnhGraph g ) {
        super( n, g );
    }


    // Constructors
    //////////////////////////////////

    // External signature methods
    //////////////////////////////////

    /**
     * <p>Assert that this restriction restricts the property to have the given
     * min cardinality. Any existing statements for <code>cardinalityQ</code>
     * will be removed.</p>
     * @param cardinality The cardinality of the restricted property
     * @exception OntProfileException If the {@link Profile#MIN_CARDINALITY_Q()} property is not supported in the current language profile.   
     */ 
    public void setMinCardinalityQ( int cardinality ) {
        setPropertyValue( getProfile().MIN_CARDINALITY_Q(), "MIN_CARDINALITY_Q", getModel().createTypedLiteral( cardinality ) );
    }

    /**
     * <p>Answer the cardinality of the restricted property.</p>
     * @return The cardinality of the restricted property
     * @exception OntProfileException If the {@link Profile#MIN_CARDINALITY_Q()} property is not supported in the current language profile.   
     */ 
    public int getMinCardinalityQ() {
        return objectAsInt( getProfile().MIN_CARDINALITY_Q(), "MIN_CARDINALITY_Q" );
    }

    /**
     * <p>Answer true if this property restriction has the given cardinality.</p>
     * @param cardinality The cardinality to test against 
     * @return True if the given cardinality is the cardinality of the restricted property in this restriction
     * @exception OntProfileException If the {@link Profile#MIN_CARDINALITY_Q()} property is not supported in the current language profile.   
     */
    public boolean hasMinCardinalityQ( int cardinality ) {
        return hasPropertyValue( getProfile().MIN_CARDINALITY_Q(), "MIN_CARDINALITY_Q", getModel().createTypedLiteral( cardinality ) );
    }
    
    /**
     * <p>Remove the statement that this restriction has the given cardinality 
     * for the restricted property.  If this statement
     * is not true of the current model, nothing happens.</p>
     * @param cardinality A cardinality value to be removed from this restriction
     * @exception OntProfileException If the {@link Profile#MIN_CARDINALITY_Q()} property is not supported in the current language profile.   
     */
    public void removeMinCardinalityQ( int cardinality ) {
        removePropertyValue( getProfile().MIN_CARDINALITY_Q(), "MIN_CARDINALITY_Q", getModel().createTypedLiteral( cardinality ) );
    }
    

    // Internal implementation methods
    //////////////////////////////////

    //==============================================================================
    // Inner class definitions
    //==============================================================================

}


/*
 *  (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
