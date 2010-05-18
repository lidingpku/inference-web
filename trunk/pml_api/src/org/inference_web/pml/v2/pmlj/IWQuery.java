/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj;

import java.util.*;

import org.inference_web.pml.v2.pmlp.*;


/**
 * Represents queries that are asked to inference web applications. These queries
 * are then answered using inference engines and the proof from these answers is
 * represented using nodesets.
 */
public interface IWQuery extends IWJustificationElement {

    /**
     * Returns the content of the query i.e. the text of the query
     * @return the content of the query i.e. the text of the query
     */
    public String getContentRawString();

    /**
     * Sets the content of the query i.e. the text of the query
     * @param newContent the new content of the query i.e. the text of the query
     */
    public void setContentRawString(String newContent);

    /**
     * Returns an IWQuestion instance corresponding to the question asked in the query
     * @return an IWQuestion instance corresponding to the question asked in the query
     */
    public List getIsQueryFor();

    /**
     * Sets the question of a query to the specified IWQuestion insance
     * @param newQuestions an IWQuestion instance corresponding to the new question of the query
     */
    public void setIsQueryFor(List newQuestions);
    
    public void addIsQueryFor(IWQuestion question);
    public void addIsQueryFor(String questionURI);

    /**
     * Returns the language in which the query is expressed
     * @return the language in which the query is expressed
     */
//    public IWLanguage getLanguage() ;

    /**
     * Sets the language in which the query is expressed
     * @param newLanguage the new language in which the query is expressed
     */
//    public void setLanguage(IWLanguage newLanguage) ;

    /**
     * Returns the inference engine which answered the query
     * @return an IWInferenceEngine instance correspoding to the inference engine which answered the query
     */
//    public IWInferenceEngine getInferenceEngine();

    /**
     * Sets the inference engine to the specified engine
     * @pram newEngine the new engine to which the inference engine attribute must be set
     */
//    public void setInferenceEngine(IWInferenceEngine newEngine);

    /**
     * Returns a list of Nodesets of the answers for the query
     * @return a list of Nodesets of the answers for the query
     */
    public List getHasAnswer();

    /**
     * Sets the list of Nodesets of the answers for the query
     * @param _newAnswers the new list of Nodesets of the answers for the query
     */
    public void setHasAnswer(List _newAnswers);

    /**
     * Adds the specified Nodeset to the list of answer nodesets
     ( @param newAnswer a nodeset corresponding to a new answer of the query
     */
    public void addHasAnswer(IWNodeSet newAnswer);
    public void addHasAnswer(String answerURI);
    
    public IWLanguage getLanguage();
    
    public void setLanguage(IWLanguage newLanguage);
    
    public IWInferenceEngine getIsFromEngine();
    public void setIsFromEngine(IWInferenceEngine newEngine);
    public void setIsFromEngine(String engineURI);
    
    public IWInformation getHasContent();
    public void setHasContent(IWInformation newContent);

}   /* END OF IWQuery */
