/*
 * (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package jena.cmdline;

import java.util.* ;

/** A command line argument that has been foundspecification.
 *
 * @author  Andy Seaborne
 * @version $Id: Arg.java,v 1.9 2008/01/02 12:09:54 andy_seaborne Exp $
 */
public class Arg
{
    String name ;
    String value ;
    List values = new ArrayList() ; 
    
    Arg() { name = null ; value = null ; }
    
    Arg(String _name) { this() ; setName(_name) ; }
    
    Arg(String _name, String _value) { this() ; setName(_name) ; setValue(_value) ; }
    
    void setName(String n) { name = n ; }
    
    void setValue(String v) { value = v ; }
    void addValue(String v) { values.add(v) ; }
    
    public String getName() { return name ; }
    public String getValue() { return value; }
    public List getValues() { return values; }
    
    public boolean hasValue() { return value != null ; }
    
    public boolean matches(ArgDecl decl)
    {
        return decl.getNames().contains(name) ;
    }
    
        
}
/*
 *  (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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