package Common.src.com.util;

import Common.src.com.Config.AppConfig;
import Common.src.com.Config.Configurator;
import Common.src.com.Exception.ResilientException;
import Common.src.com.SFDC.EnterpriseSession;
import Common.src.com.SFDC.QuerySFDC;
import com.sforce.soap.partner.SaveResult;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.joda.time.MutableDateTime;

/* 
########################################################################### 
# File..................: CustomLogger.java
# Version...............: 1.0
# Created by............: Vikram Middha
# Created Date..........: 27-Jul-2012
# Last Modified by......: 
# Last Modified Date....: 
# Description...........: Utility class for SFDC connectivity.
# Change Request History: 				   							 
########################################################################### 
*/

public class SalesforceUtils{
	
    private static Logger LOGGER = Logger.getLogger(SalesforceUtils.class);
    private static AppConfig appConfig ;
    private EnterpriseSession eSession = null;
	
    /**
     * Private constructor.
     */
    public SalesforceUtils()  {
        try {
            appConfig = Configurator.getAppConfig();
            //throw new UnsupportedOperationException("Class is not instantiable.");
        } catch (ResilientException ex) {
            LOGGER.error("Exception Occured while reading the properties file and initializing it. Cause :" + ex.getMessage());
        }
    }
    
    /**
     * Connects to Master SFDC.
     * 
     * @param appConfig AppConfig instance
     * @return a populated PartnerSession for the Master session.
     * @throws ResilientException 
     */
    public void connectToSFDC(String sessionId, String serverURL)  {
    	try{
            LOGGER.info("SalesforceUtils: initMasterSession(): Connecting to SFDC...........");
            EnterpriseSession session = new EnterpriseSession();
            boolean connected = session.connect(sessionId, serverURL, appConfig);
            eSession = session;
        }catch(Exception e){
            LOGGER.error("Exception Occured while initializing the salesforce connection. Cause :" + e.getMessage());
        }   
       
    }
    
    public ArrayList<String> generateupdateString(String billRun, String aggregatedDate) throws ResilientException{
        
        ArrayList<String> retList = new ArrayList<String>();
        String query = appConfig.getAggregatedRecordsQuery();
        if(appConfig.getIsOldManagedPackage()){
            query += " AND " + appConfig.getLogMPNameSpace() + "Date__c = " + formatDate(aggregatedDate) + " ";
        }else{
            query += " AND Date__c = " + formatDate(aggregatedDate) + " ";
        }
        
        QuerySFDC sfdcObj = new QuerySFDC(eSession);
        HashMap<String,Object>[] resMap = sfdcObj.executeQuery(query);
        if(resMap.length == 0){
            return retList;
        }
        
        for(HashMap<String,Object> hm : resMap){
            String updateString = billRun + "|";
            if(appConfig.getIsOldManagedPackage()){
                updateString += hm.get(appConfig.getLogMPNameSpace().toUpperCase() + "AGGREGATE_BY_VALUE__C") + "|" + hm.get(appConfig.getLogMPNameSpace().toUpperCase() + "BILL__R.NAME") + "|" + hm.get("ID");
            }else{
                updateString += hm.get("AGGREGATE_BY_VALUE__C") + "|" + hm.get("BILL__R.NAME") + "|" + hm.get("ID");
            }
            
            retList.add(updateString);
        }
        return retList;
        
    } 
    
    private String formatDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate; 
        String retDate = "";
        try {
            convertedDate = dateFormat.parse(date.split(" ")[0]);
            MutableDateTime mdt = new MutableDateTime(convertedDate);
            mdt.setDayOfMonth(1);
            retDate = mdt.getYear() + "-" + String.format("%2s",String.valueOf(mdt.getMonthOfYear())).replaceAll(" ", "0") + "-" + String.format("%2s",String.valueOf(mdt.getDayOfMonth())).replaceAll(" ", "0");
        } catch (ParseException ex) {
            LOGGER.error("Error converting the aggregated date to correct format. Cause :  " + ex.getLocalizedMessage());
        }
        
        return retDate;
    }
    
    public void updateAggregatedRecords(ArrayList<String> errorList, HashMap<String,String> inputMap){
        String[] ids = new String[errorList.size()];
        HashMap<String,Object>[] mapArr = new HashMap[errorList.size()];
        Integer cnt = 0;
        for(String s : errorList){
            ids[cnt] = inputMap.get(s);
            HashMap<String,Object> aggMap = new HashMap<String,Object>();
            aggMap.put(appConfig.getAggregatedSFDCApi(), false);
            aggMap.put("ObjectType",appConfig.getAggregatedObjAPI());
            mapArr[cnt++] = aggMap;
        }
        QuerySFDC sfdcObj = new QuerySFDC(eSession);
        try {
            LOGGER.info("Updating Synchronized status in SFDC for failed Aggregated Recods ");
            SaveResult[] srArr = sfdcObj.update(ids, mapArr);
            for(SaveResult sr : srArr){
                if(!sr.isSuccess()){
                    LOGGER.error("Could not update Aggregated record in SFDC for Id : "+sr.getId() + ". Cause :" + sr.getErrors()[0].getMessage());
                }
            }
            LOGGER.info("Salesforce Updation Complete for the marking Aggregation Sync status to false. ");
        } catch (ResilientException ex) {
            LOGGER.error("Could not update Aggregated record in SFDC . Cause :" + ex.getMessage());
        }
    }
    
    public void writeSFDCLog(String logName, String status, String type, String message){
        
        HashMap<String,Object>[] mapArr = new HashMap[1];
        HashMap<String,Object> aggMap = new HashMap<String,Object>();
        Calendar cal = Calendar.getInstance();
	cal.setTimeInMillis(System.currentTimeMillis());
        if("".equals(appConfig.getLogMPNameSpace().trim())){
            aggMap.put("Name", logName);
            aggMap.put("Status__c", status);
            aggMap.put("Type__c", type);
            aggMap.put("Message__c", message);
            aggMap.put("Start_Time__c",cal.getTime());
            aggMap.put("End_Time__c",cal.getTime());
            aggMap.put("ObjectType","Log__c");
        }else{
            aggMap.put("Name", logName);
            aggMap.put("MP_Esp__Status__c", status);
            aggMap.put("MP_Esp__Type__c", type);
            aggMap.put("MP_Esp__Message__c", message);
            aggMap.put("MP_Esp__Start_Time__c",cal.getTime());
            aggMap.put("MP_Esp__End_Time__c",cal.getTime());
            aggMap.put("ObjectType","MP_Esp__Log__c");
        }
        
        mapArr[0] = aggMap;
        QuerySFDC sfdcObj = new QuerySFDC(eSession);
        try {
            sfdcObj.insertRecords(mapArr);
        } catch (ResilientException ex) {
            LOGGER.error("Could not insert the log statements into SFDC . Cause :" + ex.getMessage());
        }
    }
    
    public void updateNextStage(String globalRecordId, Boolean setToIdle){
        QuerySFDC sfdcObj = new QuerySFDC(eSession);
        HashMap<String,Object>[] mapArr = new HashMap[1];
        HashMap<String,Object> aggMap = new HashMap<String,Object>();
        String[] ids = new String[1];
        ids[0] = globalRecordId;
        
        if(appConfig.getIsOldManagedPackage()){
            aggMap.put("ObjectType","Espresso_Bill__Global_Variable__c");
            if(setToIdle == false)
                aggMap.put("Espresso_Bill__Value__c", appConfig.getNextBillStage());
            else 
                aggMap.put("Espresso_Bill__Value__c", "idle");
        }else{
            aggMap.put("ObjectType","Bill_Run__c");
            if(setToIdle == false)
                aggMap.put("Billing_Stage__c", appConfig.getNextBillStage());
            else
                aggMap.put("Billing_Stage__c", "idle");
        }
        mapArr[0] = aggMap;
       
        try {
            SaveResult[] srArr = sfdcObj.update(ids, mapArr);
            for(SaveResult sr : srArr){
                if(!sr.isSuccess()){
                    LOGGER.error("Could not update the Next stage : "+sr.getId() + ". Cause :" + sr.getErrors()[0].getMessage());
                }
            }
        } catch (ResilientException ex) {
            LOGGER.error("Could not enter into next stage . Cause :" + ex.getMessage());
        }
    }
}
