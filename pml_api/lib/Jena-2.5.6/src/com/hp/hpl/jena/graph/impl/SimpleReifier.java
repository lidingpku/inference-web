/*
  (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: SimpleReifier.java,v 1.55 2008/02/11 09:32:33 chris-dollin Exp $
*/

package com.hp.hpl.jena.graph.impl;

/**
	@author kers
<p>
    A base-level implementation of Reifier, intended to be straightforward
    and obvious. It fails this test nowadays ...
*/

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.shared.*;
import com.hp.hpl.jena.util.iterator.*;

import com.hp.hpl.jena.vocabulary.RDF;

public class SimpleReifier implements Reifier
    {
    protected final GraphBase parent;
    protected final boolean intercepting;
    protected final boolean concealing;
    protected final ReificationStyle style;
    
    protected ReifierFragmentsMap fragmentsMap;
    protected ReifierTripleMap tripleMap;
    
    protected boolean closed = false;
        
    /** 
        construct a simple reifier that is bound to the parent graph .
        
        @param parent the Graph which we're reifiying for
        @param style the reification style to use
    */
    public SimpleReifier( GraphBase parent, ReificationStyle style )
        { this( parent, new SimpleReifierTripleMap(), new SimpleReifierFragmentsMap(), style ); }
    
    public SimpleReifier
        ( GraphBase parent, ReifierTripleMap tm, ReifierFragmentsMap fm, ReificationStyle style )
        {
        this.parent = parent;
        this.fragmentsMap = fm;
        this.tripleMap = tm;
        this.intercepting = style.intercepts();
        this.concealing = style.conceals();
        this.style = style; 
        }
        
    public ReificationStyle getStyle()
        { return style; }
            
    /** return the parent graph we are bound to */
    public Graph getParentGraph()
        { return parent; }
        
    /** return the triple bound to _n_ */
    public Triple getTriple( Node n )        
        { return tripleMap.getTriple( n ); }
        
    /** true iff there is a triple bound to _n_ */
    public boolean hasTriple( Node n )
    	{ return getTriple( n ) != null; }
        
    /** */
    public ExtendedIterator allNodes()
        { return tripleMap.tagIterator(); }
        
    public ExtendedIterator allNodes( Triple t )
        { return tripleMap.tagIterator( t ); }

    public void clear()
        {
        fragmentsMap.clear();
        tripleMap.clear();
        }
    
    /** 
        reifiy <code>toReify</code> with tag <code>tag</code>. If a different triple is 
        already reified under <code>tag</code>, throw an AlreadyReifiedException.
    */
    public Node reifyAs( Node tag, Triple toReify )
    	{
        Triple existing = (Triple) tripleMap.getTriple( tag );
        if (existing != null)
            { if (!toReify.equals( existing )) throw new AlreadyReifiedException( tag ); }
        else
            reifyNewTriple( tag, toReify );
        if (concealing == false) graphAddQuad( parent, tag, toReify );
        return tag; 
    	}
        
    /**
         Reify <code>toReify</code> under <code>tag</code>; there is no existing
         complete reification. This code goes around the houses by adding the
         fragments one-by-one and then seeing if that made a complete reification.
         Perhaps there's a better way, but I couldn't see it. 
    */
    protected void reifyNewTriple( Node tag, Triple toReify )
        {
        if (fragmentsMap.hasFragments( tag ))           
            {
            graphAddQuad( parent, tag, toReify );
            if (tripleMap.getTriple( tag ) == null) throw new CannotReifyException( tag );
            }
        else
            tripleMap.putTriple( tag, toReify );
        }

    /**
        If n is bound to the triple t, remove that triple. If we're not concealing reification 
        quadlets, we need to remove them from the parent graph too.
    */    	
    public void remove( Node n, Triple t )
        {
        Triple x = (Triple) tripleMap.getTriple( n );
        if (t.equals( x )) 
            { tripleMap.removeTriple( n, t ); 
            if (!concealing) parentRemoveQuad( n, t ); }
        }

    public void remove( Triple t )
        { tripleMap.removeTriple( t ); }
            
    public boolean hasTriple( Triple t )
        { return tripleMap.hasTriple( t ); }
          
    public boolean handledAdd( Triple fragment )
        {
        if (intercepting)
            {
            ReifierFragmentHandler s = fragmentsMap.getFragmentHandler( fragment );  
            if (s == null)
                return false;
            else     
                {
                addFragment( s, fragment );
                return true;
                }
            }
        else
            return false;
        }

    /**
         Add <code>fragment</code> to the fragments already present. This may
         create a complete triple, or over-specify.
     * @param s
     * @param fragment
     */
    protected void addFragment( ReifierFragmentHandler s, Triple fragment )
        {
        Node tag = fragment.getSubject(), object = fragment.getObject();
        Triple reified = tripleMap.getTriple( tag );
        if (reified == null)
            updateFragments( s, fragment, tag, object );
        else if (s.clashedWith( tag, object, reified )) 
            tripleMap.removeTriple( tag, reified );
        }

    /**
     * @param s
     * @param fragment
     * @param tag
     * @param object
     */
    private void updateFragments( ReifierFragmentHandler s, Triple fragment, Node tag, Node object )
        {
        Triple t = s.reifyIfCompleteQuad( fragment, tag, object );
        if (t instanceof Triple) tripleMap.putTriple( tag, t );
        }

    public boolean handledRemove( Triple fragment )
        {
        if (intercepting)
            {
            ReifierFragmentHandler s = fragmentsMap.getFragmentHandler( fragment );  
            if (s == null)
                return false;
            else     
                {
                removeFragment( s, fragment );
                return true;
                }
            }
        else
            return false;
        }
                  
    /**
     * @param s
     * @param fragment
     */
    private void removeFragment( ReifierFragmentHandler s, Triple fragment )
        {
        Node tag = fragment.getSubject();
        Triple already = tripleMap.getTriple( tag );
        Triple complete = s.removeFragment( tag, already, fragment );
        if (complete == null)
            tripleMap.removeTriple( tag );
        else
            tripleMap.putTriple( tag, complete );
        }        
    
    public ExtendedIterator find( TripleMatch m )
        {
        return matchesReification( m ) 
            ? tripleMap.find( m ).andThen( fragmentsMap.find( m ) ) 
            : NullIterator.instance; 
        }
    
    /**
        Answer true iff <code>m</code> might match a reification triple.
    */
    private boolean matchesReification( TripleMatch m )
        {
        Node predicate = m.asTriple().getPredicate();
        return 
            !predicate.isConcrete()
            || predicate.equals( RDF.Nodes.subject ) 
            || predicate.equals( RDF.Nodes.predicate ) 
            || predicate.equals( RDF.Nodes.object )
            || predicate.equals( RDF.Nodes.type ) && matchesStatement( m.asTriple().getObject() )
            ;
        }

    private boolean matchesStatement( Node x )
        { return !x.isConcrete() || x.equals( RDF.Nodes.Statement ); }
    
    public ExtendedIterator findExposed( TripleMatch m )
        { return findEither( m, false ); }
    
    public ExtendedIterator findEither( TripleMatch m, boolean showHidden )
        { return showHidden == concealing ? find( m ) : NullIterator.instance; }
        
    public int size()
        { return concealing ? 0 : tripleMap.size() + fragmentsMap.size(); }
    
    /**
        remove from the parent all of the triples that correspond to a reification
        of t on tag.
    */
    private void parentRemoveQuad( Node n, Triple t )
        {
        parent.delete( Triple.create( n, RDF.Nodes.type, RDF.Nodes.Statement ) );
        parent.delete( Triple.create( n, RDF.Nodes.subject, t.getSubject() ) );
        parent.delete( Triple.create( n, RDF.Nodes.predicate, t.getPredicate() ) );
        parent.delete( Triple.create( n, RDF.Nodes.object, t.getObject() ) ); 
        }        
    
    public static void graphAddQuad( GraphAdd g, Node node, Triple t )
        {
        g.add( Triple.create( node, RDF.Nodes.subject, t.getSubject() ) );
        g.add( Triple.create( node, RDF.Nodes.predicate, t.getPredicate() ) );
        g.add( Triple.create( node, RDF.Nodes.object, t.getObject() ) );
        g.add( Triple.create( node, RDF.Nodes.type, RDF.Nodes.Statement ) );
        }      
    
    /**
        our string representation is <R ...> wrapped round the string representation
        of our node map.
    */
    public String toString()
        { return "<R " + fragmentsMap + "|" + tripleMap + ">"; }

    /**
         Close this reifier - discard (big) resources.
    */
    public void close()
        {
        fragmentsMap = null;
        tripleMap = null;    
        closed = true;
        }

    /**
    	Answer true iff this SImpleReifier has been closed.
    */
    public boolean isClosed()
        { return closed; }
    }
    
/*
    (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
