/*
 Copyright (c) 2007, The Board of Trustees of the Leland Stanford Junior University
 All rights reserved.
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are
 met:
 Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.
 Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 Neither the name of Stanford University nor the names of its
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, ANY IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.inference_web.pml.shared.util;

import java.util.*;

public class ParameterHelper {

/**
 * Returns a string of the time instance in W3C date time format of <br><br>
 *  Complete date plus hours, minutes and seconds:<br>
 *  YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
 * @param timeInstance calendar instance
 * @return time in string 
 */
  public static String getTimeStr(Calendar timeInstance) {
    Calendar time = timeInstance;
    int field;
    String year = "", month = "", day = "", hour = "", minute = "", second = "";
    String timeStr = "";


    field = time.get(Calendar.YEAR);
    if (field <10) {
    	year="000";
    }
    year = year+Integer.toString(field);
    
    field = time.get(Calendar.MONTH) + 1;
    if (field < 10) {
      month = "0";
    }
    month = month + Integer.toString(field);
    
    field = time.get(Calendar.DAY_OF_MONTH);
    if (field < 10) {
      day = "0";
    }
    day = day + Integer.toString(field);
    
    field = time.get(Calendar.HOUR_OF_DAY);
    if (field < 10) {
      hour = "0";
    }
    hour = hour + Integer.toString(field);
    field = time.get(Calendar.MINUTE);
    if (field < 10) {
      minute = "0";
    }
    minute = minute + Integer.toString(field);
    field = time.get(Calendar.SECOND);
    if (field < 10) {
      second = "0";
    }
    second = second + Integer.toString(field);

    int zoneOffset = time.get(Calendar.ZONE_OFFSET);
    int dstOffset = time.get(Calendar.DST_OFFSET);
    int offSec = (zoneOffset+dstOffset)/1000;
//    int offSec = time.get(Calendar.ZONE_OFFSET)/1000;
    String pm = null, offMinStr = null, offHrStr = null;

    if (offSec>=0) {
      pm = "+";
    } else {
      pm = "-";
    }
    offSec = Math.abs(offSec);
    int offMin = (offSec%3600)/60;
    int offHr = offSec/3600;
       
    if (offMin<10) {
      offMinStr="0" + Integer.toString(offMin);
    } else {
      offMinStr = Integer.toString(offMin);
    }
    if (offHr<10) {
      offHrStr="0" +Integer.toString(offHr);
    } else {
      offHrStr = Integer.toString(offHr);
    }
    timeStr = year + "-" + month + "-" +  day + "T" + hour + ":" + minute + ":"  + second + pm + offHrStr + ":" + offMinStr;
    return timeStr;
  }

  public static String getTimeStrWithMillisecond(Calendar timeInstance) {
    Calendar time = timeInstance;
    int field;
    String year = "", month = "", day = "", hour = "", minute = "", second = "", millisecond = "";
    String timeStr = "";

    year = Integer.toString(time.get(Calendar.YEAR));
    field = time.get(Calendar.MONTH) + 1;
    if (field < 10) {
      month = "0";
    }
    month = month + Integer.toString(field);
    field = time.get(Calendar.DAY_OF_MONTH);
    if (field < 10) {
      day = "0";
    }
    day = day + Integer.toString(field);
    field = time.get(Calendar.HOUR_OF_DAY);
    if (field < 10) {
      hour = "0";
    }
    hour = hour + Integer.toString(field);
    field = time.get(Calendar.MINUTE);
    if (field < 10) {
      minute = "0";
    }
    minute = minute + Integer.toString(field);
    field = time.get(Calendar.SECOND);
    if (field < 10) {
      second = "0";
    }
    second = second + Integer.toString(field);
    field = time.get(Calendar.MILLISECOND);
    if (field < 10) {
      millisecond = "00";
    } else if (field < 100) {
      millisecond = "0";
    }
    millisecond = millisecond + Integer.toString(field);

    int zoneOffset = time.get(Calendar.ZONE_OFFSET);
    int dstOffset = time.get(Calendar.DST_OFFSET);
    int offSec = (zoneOffset+dstOffset)/1000;
//    int offSec = time.get(Calendar.ZONE_OFFSET)/1000;
    String pm = null, offMinStr = null, offHrStr = null;

    if (offSec>=0) {
      pm = "+";
    } else {
      pm = "-";
    }
    offSec = Math.abs(offSec);
    int offMin = (offSec%3600)/60;
    int offHr = offSec/3600;
       
    if (offMin<10) {
      offMinStr="0" + Integer.toString(offMin);
    } else {
      offMinStr = Integer.toString(offMin);
    }
    if (offHr<10) {
      offHrStr="0" +Integer.toString(offHr);
    } else {
      offHrStr = Integer.toString(offHr);
    }
    timeStr = year + "-" + month + "-" +  day + "T" + hour + ":" + minute + ":"  + second + "."+millisecond+ pm + offHrStr + ":" + offMinStr;
    return timeStr;
  }
  
  /**
   * Returns a string of the time instance in string format for internal use. <br><br>
   *  Complete date plus hours, minutes and seconds:<br>
   *  YYYYMMDDhhmmss (eg 19970716192030)
   * @param timeInstance calendar instance
   * @return time in string 
   */
    public static String getTimeStr3(Calendar timeInstance) {
      Calendar time = timeInstance;
      int field;
      String year = "", month = "", day = "", hour = "", minute = "", second = "";
      String timeStr = "";


      field = time.get(Calendar.YEAR);
      if (field <10) {
      	year="000";
      }
      year = year+Integer.toString(field);
      
      field = time.get(Calendar.MONTH) + 1;
      if (field < 10) {
        month = "0";
      }
      month = month + Integer.toString(field);
      
      field = time.get(Calendar.DAY_OF_MONTH);
      if (field < 10) {
        day = "0";
      }
      day = day + Integer.toString(field);
      
      field = time.get(Calendar.HOUR_OF_DAY);
      if (field < 10) {
        hour = "0";
      }
      hour = hour + Integer.toString(field);
      field = time.get(Calendar.MINUTE);
      if (field < 10) {
        minute = "0";
      }
      minute = minute + Integer.toString(field);
      field = time.get(Calendar.SECOND);
      if (field < 10) {
        second = "0";
      }
      second = second + Integer.toString(field);

      timeStr = year + month + day + hour + minute + second;
      return timeStr;
    }


  public static String getCurrentTimeStr () {
    return getTimeStr(Calendar.getInstance());
  }
  
  public static String getCurrentTimeStr2 () {
    return getTimeStrWithMillisecond(Calendar.getInstance());
  }
  
  public static String getCurrentTimeStr3 () {
    return getTimeStr3(Calendar.getInstance());
  }
  
  public static String getCurrentYearStr() {

    Calendar now = Calendar.getInstance();
    String year = "";

    year = Integer.toString(now.get(Calendar.YEAR));
    return year;
  }
  public static String getCurrentMonthStr() {

    Calendar now = Calendar.getInstance();
    int field;
    String month = "";

    field = now.get(Calendar.MONTH) + 1;
    if (field < 10) {
      month = "0";
    }
    month = month + Integer.toString(field);
    return month;
  }
  public static String getCurrentDayStr() {
    Calendar now = Calendar.getInstance();
    int field;
    String day = "";

    field = now.get(Calendar.DAY_OF_MONTH);
    if (field < 10) {
      day = "0";
    }
    day = day + Integer.toString(field);
    return day;
  }
  
  public static String convertTimeStr(String oldTimeStr) {
  	if (oldTimeStr.indexOf("T")>0) {
  		return oldTimeStr;
  	} else {
    Calendar time = Calendar.getInstance();
    int field;
    String year = "", month = "", day = "", hour = "", minute = "", second = "";
    String timeStr = "";


    year = Integer.toString(time.get(Calendar.YEAR));
    field = time.get(Calendar.MONTH) + 1;
    if (field < 10) {
      month = "0";
    }
    month = month + Integer.toString(field);
    field = time.get(Calendar.DAY_OF_MONTH);
    if (field < 10) {
      day = "0";
    }
    day = day + Integer.toString(field);
    field = time.get(Calendar.HOUR_OF_DAY);
    if (field < 10) {
      hour = "0";
    }
    hour = hour + Integer.toString(field);
    field = time.get(Calendar.MINUTE);
    if (field < 10) {
      minute = "0";
    }
    minute = minute + Integer.toString(field);
    field = time.get(Calendar.SECOND);
    if (field < 10) {
      second = "0";
    }
    second = second + Integer.toString(field);

    year = oldTimeStr.substring(0,4);
    month = oldTimeStr.substring(4,6);
    day = oldTimeStr.substring(6,8);
    hour = oldTimeStr.substring(8,10);
    minute = oldTimeStr.substring(10,12);
    second = oldTimeStr.substring(12);

    int offSec = time.get(Calendar.ZONE_OFFSET)/1000;
    String pm = null, offMinStr = null, offHrStr = null;

    if (offSec>=0) {
      pm = "+";
    } else {
      pm = "-";
    }
    offSec = Math.abs(offSec);
    int offMin = (offSec%3600)/60;
    int offHr = offSec/3600;
       
    if (offMin<10) {
      offMinStr="0" + Integer.toString(offMin);
    } else {
      offMinStr = Integer.toString(offMin);
    }
    if (offHr<10) {
      offHrStr="0" +Integer.toString(offHr);
    } else {
      offHrStr = Integer.toString(offHr);
    }
    timeStr = year + "-" + month + "-" +  day + "T" + hour + ":" + minute + ":"  + second + pm + offHrStr + ":" + offMinStr;
    return timeStr;
  	}
  }


/*  
  public static String getSubmitter(HttpServletRequest request) throws Exception {

    String authUser = request.getRemoteUser();
    String submitterURI = null;
    
    PMLOntologyModel iwOntModel = IWContext.ontologyModel;
    OntProperty submitterProp = iwOntModel.getProperty("hasSubmitter");
    String submitterClassName  = submitterProp.getRange().getLocalName();

    if (! DBUtils.areTablesEmpty(submitterClassName)) { 
      if (authUser != null) {
        submitterURI = DBUtils.getProvenanceURI(submitterClassName,authUser);
        if (submitterURI != null) {
          return submitterURI;
        } else {
          throw new Exception(
          "Invalid Submitter : The logged in online registrar user " + authUser +" can not verified as submitter -- not in registry.");                  
        }
      } else {
        throw new Exception(
        "Invalid Submitter : Unable to obtain authorized online registrar user name.");        
      }
    } else { // usually empty database, init state of node, allow null   
      return null;
    }
  }
  */
  
  public static List toList (Set s) {
  	List result = null;
  	if (s != null ) {
  		result = new ArrayList();
  		Iterator sIt = s.iterator();
  		while (sIt.hasNext()) {
  			result.add(sIt.next());
  		}
  	}
  	return result;
  }
  public static void testRun() {
  	System.out.println(getCurrentTimeStr());
  }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testRun();
	}

}