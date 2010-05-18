/* Copyright 2007 Inference Web Group.  All Rights Reserved. */


package org.inference_web.pml.v2.pmlp;


import java.util.List;

import org.inference_web.pml.v2.util.PMLObjectManager;
/**
 * Models inference rules.
 */
public interface IWInferenceRule extends IWIdentifiedThing { 
	public final String englishDescriptionPropName = "englishDescription";
	public final String englishExamplePropName = "englishExample";


	final String ruleSpecPropName = "ruleSpec"; // v1 only
    /** 
     * Returns the rule specification
     * @return the rule specification
     */
    public String getRuleSpec();

    /**
     * Sets the rule specification
     * @param newSpec the rule specification
     */
    public void setRuleSpec(String newSpec);
    
    public IWLanguage getLanguage();
    
    public String getEnglishDescription();
    
    public IWInformation getHasContent();
    public void setHasContent(IWInformation newContent);
    
    public String getHasEnglishDescriptionTemplate();
    
    public void setHasEnglishDescriptionTemplate(String template);
    
    public List getHasRuleExample();
    
    public void setHasRuleExample (List examples);
    
    public void addHasRuleExample(IWInformation example);

} /* END OF IWInferenceRule */
