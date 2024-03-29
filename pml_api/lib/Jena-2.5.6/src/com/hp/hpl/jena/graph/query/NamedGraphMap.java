/*
  (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: NamedGraphMap.java,v 1.8 2008/01/02 12:07:57 andy_seaborne Exp $
*/

package com.hp.hpl.jena.graph.query;

import java.util.*;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.util.CollectionFactory;

/**
    a mapping from from names to Graphs.
*/
public class NamedGraphMap
    {
    NamedGraphMap() {}      
    
    private Map map = CollectionFactory.createHashedMap();    
    
    /**
        Add a named graph to the map and return this map.
    	@param name the name to give this graph. Must not already be bound.
    	@param g the graph to name
    	@return this NamedGraphMap
    */
    public NamedGraphMap put( String name, Graph g ) 
        { map.put( name, g ); 
        return this; }       
    
    /**
        Answer the GRaph with the given name, or null if there isn't one.
    	@param name the name of the graph
    	@return the named graph, or null
    */
    public Graph get( String name ) 
        { return (Graph) map.get( name ); } 
    }

/*
    (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
