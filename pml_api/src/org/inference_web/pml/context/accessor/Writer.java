/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.context.accessor;

import java.util.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import org.inference_web.pml.v2.PMLObject;
import org.inference_web.pml.v2.util.PMLObjectManager;
//import org.inference_web.shared.rdf.jena.ToolJena;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.Model;

public class Writer  {
  
  public static boolean toFile(OntModel model, String fileName) {
    boolean result = false;
    try {
      File _dump = new File(fileName);
      Stack fileNameStack = new Stack();
      if (!_dump.exists()) {
        File parentFile = _dump.getParentFile();
        while (!parentFile.exists()) {
          String parentFilePath = parentFile.getAbsolutePath();
          fileNameStack.push(parentFilePath);
          parentFile = parentFile.getParentFile();
        }
        while (!fileNameStack.empty()) {
          String fn = (String)fileNameStack.pop();
          File df = new File (fn);
          df.mkdir();
        }
      } else {
      }
      OutputStream _fos;
      try {
      	_fos = new FileOutputStream(_dump);
      	model.write(_fos,"RDF/XML-ABBREV");
      } catch (java.lang.StackOverflowError soe) {
      	if(_dump.exists()) _dump.delete();
      	_fos = new FileOutputStream(_dump);
      	model.write(_fos,"RDF/XML");
      } catch (java.lang.OutOfMemoryError soe) {
      	if(_dump.exists()) _dump.delete();
      	_fos = new FileOutputStream(_dump);
      	model.write(_fos,"RDF/XML");
      }
      _fos.close();
      result = true;
      
    } catch (IOException ioxc) {
      throw new RuntimeException(ioxc.toString());
    }
    
    return result;
  }
  
  public static String printToString (OntModel model) {
		StringWriter sw ;
    try {
    	sw = new StringWriter();
  		model.write(sw,"RDF/XML-ABBREV");
    } catch (java.lang.StackOverflowError soe) {
    	sw = new StringWriter();
  		model.write(sw,"RDF/XML-ABBREV");
    } catch (java.lang.OutOfMemoryError soe) {
    	sw = new StringWriter();
  		model.write(sw,"RDF/XML-ABBREV");
    }
		return sw.toString();
	}


}
/* END OF Writer */