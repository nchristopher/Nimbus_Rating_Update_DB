/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Nimbus_Rating_DB;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
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
    //Logger
    private static Logger myLogger = Logger.getLogger(RejectedCdrHelper.class.getName());

    public RejectedCdrHelper() {
        this.session = HibernateUtil.getSessionFactory().getCurrentSession();
    }

    /*public String[] getCategoryCountByType(String Type) {
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
    */
    public boolean updateBillId(String updateString) {
        try {
            String stringToUse = updateString;
            String BillRunId = "";
            String AggregationId = "";
            String BillId = "";
            int updateCountAggregation;
            int updateCountRated;
            String splitString[] = stringToUse.split(",");

            org.hibernate.Transaction tx = session.beginTransaction();
            for (int i = 0; i < splitString.length; i++) {
                String split[] = splitString[i].split("\\|");
                for (int j = 0; j < split.length; j++) {
                    if (j == 0) {
                        BillRunId = split[j];
                    } else if (j == 1) {
                        AggregationId = split[j];
                    } else if ( j == 2) {
                        BillId = split[j];
                    }
                }
                updateCountAggregation = performUpdate("AggregatedCdr", AggregationId, BillId, "");
                updateCountRated = performUpdate("RatedCdr", AggregationId, BillId, BillRunId);
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

            }
            tx.commit();

            return true;
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            return false;
        }
    }

    /** Private Utility Methods here! */
    private int performUpdate(String dbTable, String AggId, String BillId, String BillRunId) {

        String hqlUpdate = "";
        if(dbTable.equalsIgnoreCase("aggregatedCdr")){
            hqlUpdate = "update " + dbTable + " set billId = :BillId , status = 'B' where aggregationId = :AggregationId";
        }else if (dbTable.equalsIgnoreCase("ratedCdr")) {
            hqlUpdate = "update " + dbTable + " set billId = :BillId , billStatus = 'B' , billRunId = :BillRunId where aggregationId = :AggregationId";
        }
        Query q = session.createQuery(hqlUpdate);
        q.setString("AggregationId", AggId);
        q.setString("BillId", BillId);
        if (dbTable.equalsIgnoreCase("ratedCdr")){ 
            q.setString("BillRunId", BillRunId);
        }
        int executeUpdate = q.executeUpdate();
        return executeUpdate;
    }
    /** Private Utility Methods here!
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
    */
}