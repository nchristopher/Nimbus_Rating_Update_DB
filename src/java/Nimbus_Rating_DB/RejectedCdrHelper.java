/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Nimbus_Rating_DB;

import Common.src.com.Config.AppConfig;
import Common.src.com.Config.Configurator;
import Common.src.com.Exception.ResilientException;
import Common.src.com.SFDC.EnterpriseSession;
import Common.src.com.util.SalesforceUtils;
import com.sforce.soap.partner.SoapBindingStub;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

/**
 *
 * @author nimil.christopher
 */
public class RejectedCdrHelper {

    Session session = null;
    String[] processed = null;
    public static String INPUT_DELIMITER = ",";
    public static final int EXPECTED_FIELDS = 19;
    public static final int cdrGUID = 0;
    public static final int ENTRY_DATE = 1;
    public static final int IDX_CALL_REF = 2;
    public static final int IDX_NETWORK = 3;
    public static final int IDX_CALL_TYPE = 4;
    public static final int IDX_REMOTE_NETWORK = 5;
    public static final int IDX_REMOTE_SWITCH = 6;
    public static final int IDX_DIRECTION = 7;
    public static final int IDX_PORTING_PREFIX = 8;
    public static final int IDX_CLI = 9;
    public static final int IDX_DEST = 10;
    public static final int IDX_USAGE_TYPE = 11;
    public static final int IDX_NUMBER_TYPE = 12;
    public static final int IDX_START_DATE = 13;
    public static final int IDX_DURATION = 14;
    public static final int IDX_GUIDING_KEY = 15;
    public static final int IDX_DISPLAY_NUMBER = 16;
    public static final int IDX_PLATFORM = 17;
    public static final int IDX_MACHINE = 18;
    public static final int IDX_LINK_NUMBER = 19;
    public static final int IDX_CALLER = 20;
    public static final int IDX_SERVICE = 4;
    private static AppConfig appConfig = null;
    //Logger
    private static Logger myLogger = Logger.getLogger(RejectedCdrHelper.class.getName());
    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(RejectedCdrHelper.class);
    private static SalesforceUtils utils = new SalesforceUtils();
    
    static{
        try {
            appConfig = Configurator.getAppConfig();
        } catch (ResilientException ex) {
            Logger.getLogger(RejectedCdrHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RejectedCdrHelper()  {
        this.session = HibernateUtil.getSessionFactory().openSession();
        try {
            appConfig = Configurator.getAppConfig();
        } catch (ResilientException ex) {
            Logger.getLogger(RejectedCdrHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[] getCategoryCountByType(String Type) {
        Long rejectedCdr = null;
        List<String> Types = new ArrayList<String>();
        Types.add("General");
        Types.add("Category 1");
        Types.add("Category 2");
        Types.add("Category 3");
        Integer ROW = Types.size();
        Integer COLUMN = 2;
        String elementReturned[][] = new String[ROW][COLUMN];
        List<String> count = new ArrayList<String>();
        try {
            for (int i = 0; i < ROW; i++) {
                for (String element : Types) {
                    elementReturned[i][0] = element;
                    org.hibernate.Transaction tx = session.beginTransaction();
                    Query q = session.createQuery("select count(category.id) from RejectedCdr as category where category.suspenseCategory = '" + element + "'");
                    rejectedCdr = (Long) q.uniqueResult();
                    elementReturned[i][1] = rejectedCdr.toString();
                    i++;
                }
            }
            String returnString[] = new String[elementReturned.length];
            for (int i = 0; i < elementReturned.length; i++) {
                returnString[i] = elementReturned[i][0] + "|" + elementReturned[i][1];
            }
            return returnString;
        } catch (Exception e) {
            e.printStackTrace();
            count.add(e.getMessage());
            return count.toArray(new String[0]);
        }
    }

    public String[] getCategoryInfo(String suspenseType, int Index) {
        int Limit = 10;
        String returnString[] = null;
        List<RejectedCdr> categoryInfo = new ArrayList<RejectedCdr>();
        try {
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createSQLQuery(" SELECT t.id, t.ORIGINAL_DATA as originalData, t.SUSPENSE_CATEGORY as suspenseCategory FROM (  SELECT TOP 10 id FROM ( SELECT TOP " + Index * 10 + " id FROM rejected_cdr WHERE SUSPENSE_CATEGORY = '" + suspenseType + "' ORDER BY id ASC) AS inside ORDER BY id DESC) AS outside INNER JOIN rejected_cdr AS t ON outside.id = t.id ORDER BY outside.id ASC  ").setResultTransformer(Transformers.aliasToBean(RejectedCdr.class));
            q.setMaxResults(Limit);
            categoryInfo = (List<RejectedCdr>) q.list();
            returnString = new String[categoryInfo.size()];
            System.out.println("Size -- >" + categoryInfo.size());
            for (int i = 0; i < categoryInfo.size(); i++) {
                returnString[i] = categoryInfo.get(i).getOriginalData();
                System.out.println("-->" + returnString[i]);
            }
            returnString = process(returnString);
            return returnString;
        } catch (Exception e) {
            throw new UnsupportedOperationException("Error : " + e.getMessage());
        }

    }

    public static void updateBillId_Sync(String billRun, String aggregationDate, String globalRecordId, String sessionId, String serverURL){
        try {            
            String jobId = /*jobDateFormat.format(Calendar.getInstance()) + "_"  + */ String.valueOf(Double.valueOf(Math.random() * 10000).intValue());

            Aggregation_Sync aggregationThread = new Aggregation_Sync("aggregationThread-" + jobId, billRun, aggregationDate, globalRecordId, sessionId, serverURL);

            //return jobId;
        } catch (Exception e) {
            LOGGER.error("Exception occured while starting the new thread. Cause: " + e.getMessage());
        }
    }
    public void updateBillId(String billRun, String aggregationDate, String globalRecordId, String sessionId, String serverURL) {
        try {
            LOGGER.info("Entered into the function updateBillId");
            utils.connectToSFDC(sessionId, serverURL);
            utils.writeSFDCLog("Rating-UpdateAggregatedRecords", "Started", "RatingWS", "Started the Aggregation Process.");
            if(!session.isOpen()){
                session = HibernateUtil.getSessionFactory().openSession();
            }
            ArrayList<String> updateStringList = utils.generateupdateString(billRun, aggregationDate);
            LOGGER.info("Number of Aggregated records queried from SFDC :"+ updateStringList.size());
            
            Integer batchSize = Integer.valueOf(appConfig.getBatchSize());
            
            LOGGER.info("Batch Size :" + batchSize );
            utils.writeSFDCLog("Rating-UpdateAggregatedRecords", "Started", "RatingWS", "Total Number of Aggregated Records :" + updateStringList.size() + ". Batch Size :" + batchSize);
            Integer cnt = 0;
            Integer totalRecordsProcessed = 0;
            ArrayList<String> billIdList = new ArrayList<String>();
            ArrayList<String> aggIdList = new ArrayList<String>();
            String updateString = "";
            HashMap<String, String> aggSFDCIdMap = new HashMap<String,String>();
            for(String us : updateStringList){    
                LOGGER.info("Processing Record Number :" + totalRecordsProcessed );
                updateString = us;
                String stringToUse = updateString;
                String BillRunId = "";
                String AggregationId = "";
                String BillId = "";
                int updateCountAggregation;
                int updateCountRated;
                String splitString[] = stringToUse.split(",");

                

                 for (int i = 0; i < splitString.length; i++) {
                    String split[] = splitString[i].split("\\|");
                    aggSFDCIdMap.put(split[1], split[3]);
                    for (int j = 0; j < split.length; j++) {
                        if (j == 0) {
                            BillRunId = split[j];
                        } else if (j == 1) {
                            AggregationId = split[j];
                            aggIdList.add(AggregationId);
                        } else if ( j == 2) {
                            BillId = split[j];
                            billIdList.add(BillId);
                        } 
                    }
                 }
                 cnt++;
                 totalRecordsProcessed++;
                if(cnt != batchSize && totalRecordsProcessed < updateStringList.size()){
                    continue;
                }else{
                    cnt=0;
                    updateString = "";
                    
                }
                
                updateCountAggregation = performUpdate("AggregatedCdr", aggIdList, billIdList, "", aggSFDCIdMap);
                updateCountRated = performUpdate("RatedCdr", aggIdList, billIdList, BillRunId, aggSFDCIdMap);
                if (updateCountAggregation > 0) {
                    myLogger.log(Level.INFO, "Updated {0} record/s on AggregatedCDR", updateCountAggregation);
                } else {
                    myLogger.log(Level.INFO, "No AggregationId's on Aggregated CDR to update!");
                }
                if (updateCountRated > 0) {
                    myLogger.log(Level.INFO, "Updated {0} record/s on RatedCDR", updateCountRated);
                } else {
                    myLogger.log(Level.INFO, "No AggregationId's on RatedCDR to update!");
                }
                billIdList.clear();
                aggIdList.clear();
                
            }
            //return true;
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            utils.writeSFDCLog("Rating-UpdateAggregatedRecords", "Error", "RatingWS", "Exception :" + e.getMessage());
            //return false;
        }
         finally{
            session.close();
            utils.writeSFDCLog("Rating-UpdateAggregatedRecords", "Completed", "RatingWS", "Completed the processing of records.");
            utils.updateNextStage(globalRecordId,false);
        }
    }

    /** Private Utility Methods here!*/
    private String[] process(String[] returnString) {
        String[] swapString = null;
        for (int i = 0; i < returnString.length; i++) {
            swapString = returnString[i].split(INPUT_DELIMITER);
            if (swapString.length < EXPECTED_FIELDS) {
                return null;
            } else {
                returnString[i] = null;
                returnString[i] = "CallReference|" + swapString[IDX_CALL_REF] + "," + "Caller|" + swapString[IDX_CALLER] + "," + "CallType|" + swapString[IDX_CALL_TYPE] + "," + "BillingEntity|" + swapString[IDX_GUIDING_KEY];
            }
        }
        return returnString;
    }

    private int performUpdate(String dbTable, ArrayList<String> aggIdList, ArrayList<String> billIdList, String BillRunId, HashMap<String, String> aggSFDCMap) {
        
        String hqlUpdate = "";
        String AggIdString = "(";
        org.hibernate.Transaction tx = session.beginTransaction();
        hqlUpdate = "update " + dbTable + " set billId = case AGGREGATION_ID ";
            
        Integer count = 0;
        for(String aggId : aggIdList){
            hqlUpdate += " when '" + aggId + "'" + " then '" + billIdList.get(count) + "'";            
            if(count == aggIdList.size() - 1){
                AggIdString += "'"+aggId + "')";
            }else{
                 AggIdString += "'"+aggId + "',";
            }
            count++;
        }        
        
        if(dbTable.equalsIgnoreCase("aggregatedCdr")){                         
                    
            hqlUpdate += " end ,status = 'B'  where aggregationId IN "+AggIdString;
            
            //hqlUpdate = "update " + dbTable + " set billId = case when :BillId , status = 'B' where aggregationId = :AggregationId";
        }else if (dbTable.equalsIgnoreCase("ratedCdr")) {
                              
            hqlUpdate += " end,billStatus = 'B' , billRunId = :BillRunId where aggregationId IN "+AggIdString;            
            
            //hqlUpdate = "update " + dbTable + " set billId = 'B-00027301' , billStatus = 'B' , billRunId = :BillRunId where Id = '15600343'";
        }
        
        Query q = session.createQuery(hqlUpdate);
       // q.setString("AggIdString", AggIdString);
        //q.setString("BillId", BillId);
        if (dbTable.equalsIgnoreCase("ratedCdr")){ 
            q.setString("BillRunId", BillRunId);
        }
        LOGGER.info("Query for update = " + hqlUpdate);
        int executeUpdate = q.executeUpdate();
        tx.commit();
        LOGGER.info("Number of Records updated in " + dbTable + " table : " + executeUpdate);
        
        if(!dbTable.equalsIgnoreCase("ratedCdr")){
            ArrayList<String> errorList = queryORDB(aggIdList);
            if(errorList.size() > 0){
                LOGGER.error("Number of Records could not be updated in " + dbTable + " table : " + errorList.size());
                utils.updateAggregatedRecords(errorList,aggSFDCMap);
            }
        }
        return executeUpdate;
    }
    
    private ArrayList<String> queryORDB(ArrayList<String> aggIdList){
        
        ArrayList<String> errorList = new ArrayList<String>();
        
        org.hibernate.Transaction tx = session.beginTransaction();
        Criteria crit = session.createCriteria(AggregatedCdr.class);
        crit.add(Restrictions.in("aggregationId", aggIdList));
        crit.add(Restrictions.eq("status", "B"));
        HashMap<String, String> aggCDRMap = new HashMap<String,String>();
        List<AggregatedCdr> cdrList = (ArrayList<AggregatedCdr>) crit.list();
        //crit.add(Restrictions.between("startTimestamp", getMinimum(dt), getMaximum(dt)));
        for(AggregatedCdr ag : cdrList){
            aggCDRMap.put(ag.getAggregationId(),ag.getStatus());
            
        }
        
        for(String s : aggIdList){
            if(aggCDRMap.get(s) == null){
                errorList.add(s);
            }
        }
        
        return errorList;
    }
    
    //Below code is to rollback open rate changes when rollback is requested in Salesforce.
    public static void rollBackOpenRateChanges_sync(String aggregationDate, String globalRecordId, String sessionId, String serverURL){
        try {            
            String jobId = /*jobDateFormat.format(Calendar.getInstance()) + "_"  + */ String.valueOf(Double.valueOf(Math.random() * 10000).intValue());

            RollBackOpenRate_Sync rollBackThread = new RollBackOpenRate_Sync("aggregationThread-" + jobId, aggregationDate, globalRecordId, sessionId, serverURL);

            //return jobId;
        } catch (Exception e) {
            LOGGER.error("Exception occured while starting the new thread. Cause: " + e.getMessage());
        }
    }
    
    public void rollBackOpenRateChanges(String aggregatedDate, String globalRecordId, String sessionId, String serverURL){
        int updateCountAggregation;
        int updateCountRated;
                
        try{
            LOGGER.info("Entered into the rollBackOpenRateChanges funtion");
            utils.connectToSFDC(sessionId, serverURL);
            utils.writeSFDCLog("Rating-RollbackAggregatedRecords", "Started", "RatingWS", "Started the Rollback of Aggregated/Rated changes.");
            if(!session.isOpen()){
                session = HibernateUtil.getSessionFactory().openSession();
            }
            
            try{
                updateCountRated = performRollback("RatedCdr",aggregatedDate);
                if (updateCountRated > 0) {
                    myLogger.log(Level.INFO, "Updated {0} record/s on RatedCDR", updateCountRated);
                } else {
                    myLogger.log(Level.INFO, "No AggregationId's on RatedCDR to update!");
                }
                utils.writeSFDCLog("Rating-RollbackAggregatedRecords", "Completed", "RatingWS", "Rolled Back " + updateCountRated+ " rated records.");
            }catch(Exception e){
                LOGGER.error("Exception occured while Rolling back the Rated changes. Cause: " + e.getMessage());
            }try{
                updateCountAggregation = performRollback("AggregatedCdr", aggregatedDate);
                if (updateCountAggregation > 0) {
                    myLogger.log(Level.INFO, "Updated {0} record/s on AggregatedCDR", updateCountAggregation);
                } else {
                    myLogger.log(Level.INFO, "No AggregationId's on Aggregated CDR to update!");
                }
                utils.writeSFDCLog("Rating-RollbackAggregatedRecords", "Completed", "RatingWS", "Rolled Back " + updateCountAggregation+ " aggregated records.");
            }catch(Exception e){
                LOGGER.error("Exception occured while Rolling back the Aggregated changes. Cause: " + e.getMessage());
            }
           
           
        }catch(Exception e){
            LOGGER.error("Exception occured while Rolling back the Aggregated/Rated changes. Cause: " + e.getMessage());
        }finally{
            session.close();
            utils.writeSFDCLog("Rating-RollbackAggregatedRecords", "Completed", "RatingWS", "Completed the Rollback process  of Rated/Aggregated records.");
            utils.updateNextStage(globalRecordId, true);
        }
    }
    
     private int performRollback(String dbTable, String aggregatedDate) {
        
        String hqlUpdate = "";        
        org.hibernate.Transaction tx = session.beginTransaction();
        
        if (dbTable.equalsIgnoreCase("ratedCdr")) {
            hqlUpdate = "update " + dbTable + " set billId=0, billStatus='U', billRunId=null " +
                     " where aggregationId IN (select aggregationId from AggregatedCdr where date = '"+aggregatedDate+"')";
        }else{
             hqlUpdate = "update " + dbTable + " set status='U', billId=0 where Date = '"+aggregatedDate+"')";
        }        
        
        Query q = session.createQuery(hqlUpdate);
       
        LOGGER.info("Query for update = " + hqlUpdate);
        int executeUpdate = q.executeUpdate();
        tx.commit();
        LOGGER.info("Number of Records updated in " + dbTable + " table : " + executeUpdate);     
        
        return executeUpdate;
    }
     
     public static Boolean isRatingRunning(){
       
        Boolean retVal = false;
        try 
        {
            String command = appConfig.getPortcommand();
            Process p=Runtime.getRuntime().exec(command); 
            //p.waitFor(); 
            BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
            String line=reader.readLine(); 
            while(line!=null) 
            { 
                if(line.contains(appConfig.getOrPort())){
                    retVal=true;
                    break;
                } 
                line=reader.readLine();
            } 
            
            if(retVal){
                BufferedReader br = new BufferedReader(new FileReader(appConfig.getLogFile()));
                while ((line = br.readLine()) != null) {
                    if(line.contains("Exception")){
                        retVal=false;
                    }
                }
            }

        } 
        catch(Exception e){
            LOGGER.info("Exception occured in  isRatingRunning. Cause " + e.getStackTrace());
            retVal=false;
        }
         
        LOGGER.info("Rating service status : " + retVal); 
        
        return retVal;
     }
     
     public static void startRating(){
         
         try{
            Runtime rt = Runtime.getRuntime();
            rt.exec("cmd.exe /c start "+appConfig.getOrDirectory()  + "\\startup.bat");
            LOGGER.info("Request sent for Rating. ");
            //p.waitFor(); 
             
         }catch(Exception e){
             LOGGER.error("Exception occured while starting the Open Rate. Cause " + e.getStackTrace());
         }
     }
}