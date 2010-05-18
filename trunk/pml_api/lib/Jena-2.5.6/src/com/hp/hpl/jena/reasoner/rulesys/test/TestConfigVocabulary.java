/*
 	(c) Copyright 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: TestConfigVocabulary.java,v 1.1 2008/01/21 14:54:18 chris-dollin Exp $
*/

package com.hp.hpl.jena.reasoner.rulesys.test;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.vocabulary.*;

/**
    Tests for configuration vocabulary added as part of ModelSpec removal

 	@author kers
*/
public class TestConfigVocabulary extends ModelTestBase
    {
    public TestConfigVocabulary( String name )
        { super( name ); }

    public void testExistingVocabulary()
        {
        assertIsProperty( "name", ReasonerVocabulary.nameP );
        assertIsProperty( "description", ReasonerVocabulary.descriptionP );
        assertIsProperty( "version", ReasonerVocabulary.versionP );
        assertIsProperty( "supports", ReasonerVocabulary.supportsP );
        assertIsProperty( "configurationProperty", ReasonerVocabulary.configurationP );
        assertIsProperty( "individualAsThing", ReasonerVocabulary.individualAsThingP );
        }
    
    public void testPropVocavulary()
        {
        assertIsPropProperty( "derivationLogging", ReasonerVocabulary.PROPderivationLogging );
        assertIsPropProperty( "traceOn", ReasonerVocabulary.PROPtraceOn );
        assertIsPropProperty( "ruleMode", ReasonerVocabulary.PROPruleMode );
        assertIsPropProperty( "enableOWLTranslation", ReasonerVocabulary.PROPenableOWLTranslation );
        assertIsPropProperty( "enableTGCCaching", ReasonerVocabulary.PROPenableTGCCaching );
        assertIsPropProperty( "enableCMPScan", ReasonerVocabulary.PROPenableCMPScan );
        assertIsPropProperty( "setRDFSLevel", ReasonerVocabulary.PROPsetRDFSLevel );
        assertIsPropProperty( "enableFunctorFiltering", ReasonerVocabulary.PROPenableFunctorFiltering );
        }

    public void testDirectVocabulary()
        {
        assertIsDirectProperty( RDFS.subClassOf, ReasonerVocabulary.directSubClassOf );
        assertIsDirectProperty( RDFS.subPropertyOf, ReasonerVocabulary.directSubPropertyOf );
        assertIsDirectProperty( RDF.type, ReasonerVocabulary.directRDFType );
        }

    public void testRuleSetVocabulary()
        {
        assertIsProperty( "ruleSet", ReasonerVocabulary.ruleSet );
        assertIsProperty( "ruleSetURL", ReasonerVocabulary.ruleSetURL );
        assertIsProperty( "hasRule", ReasonerVocabulary.hasRule );
        assertIsProperty( "schemaURL", ReasonerVocabulary.schemaURL );
        }

    private void assertIsDirectProperty( Resource r, Property p )
        {
        assertEquals( ReasonerRegistry.makeDirect( r.getURI() ), p.getURI() );
        }

    private void assertIsProperty( String name, Property p )
        {
        assertEquals( ReasonerVocabulary.getJenaReasonerNS() + name, p.getURI() );
        }

    private void assertIsPropProperty( String name, Property p )
        {
        assertEquals( ReasonerVocabulary.PropURI + "#" + name, p.getURI() );
        }
    }
