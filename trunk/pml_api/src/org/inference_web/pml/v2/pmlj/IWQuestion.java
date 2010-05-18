/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj;

import java.util.List;

import org.inference_web.pml.v2.pmlp.*;
/**
 * <b>PROBLEM</b>
 */
public interface IWQuestion extends IWJustificationElement {

    /**
     * Returns the content of the question expressed in the language of the question
     * @return the content of the question expressed in the language of the question
     */
    public String getContentRawString();

    /**
     * Sets the content of the question (expressed in the language of the question)
     * @param newContent the new content of the question (expressed in the language of the question)
     */
    public void setContentRawString(String newContent);
    
    /**
     * Returns the content of the question. If the content has "PrettyString" representation,the <br>
     * pretty string is returned, otherwise, the raw string is returned.
     * @return the content of the question
     */
    public String getContentString();

    /**
     * <b>PROBLEM</b>
     */
    public List getHasAnswerPattern();

    /**
     * <b>PROBLEM</b>
     */
    public void setHasAnswerPattern(List newPatterns);
    public void addHasAnswerPattern(String newPattern);

    public IWInformation getHasContent();
    public void setHasContent(IWInformation newContent);
} 
// IWQuestion
