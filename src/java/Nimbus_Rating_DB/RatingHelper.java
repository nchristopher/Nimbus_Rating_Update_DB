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
 * @author Vikram
 */
public class RatingHelper {
    
    Session session = null;
    Session sessionInput = null;
    private static Logger myLogger = Logger.getLogger(RatingHelper.class.getName());
    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(RatingHelper.class);
    
    public RatingHelper(){
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.sessionInput = HibernateUtil.getInputCDRSessionFactory().openSession();
    }
    
    public String getRatingInfo(String month, String year){
        
        String returnString = "";
        Long ratedCDRCount = null;
        Long rejectedCDRCount = null;
        Long inputCDRCount = null;
        List<TimeStamp> tsList = new ArrayList<TimeStamp>();
        
        if(!session.isOpen()){
            session = HibernateUtil.getSessionFactory().openSession();
        }
        
        if(!sessionInput.isOpen()){
            sessionInput = HibernateUtil.getInputCDRSessionFactory().openSession();
        }
        
        try{
            org.hibernate.Transaction tx = session.beginTransaction();
            Query qRated = session.createQuery("SELECT Count(*) from RatedCdr where MONTH(startTimestamp) = " + month 
                                            + " AND YEAR(startTimestamp) = " + year);
            ratedCDRCount = (Long) qRated.uniqueResult();
            
            Query qRejected = session.createQuery("SELECT Count(*) from RejectedCdr where MONTH(dateTime) = " + month 
                                            + " AND YEAR(dateTime) = " + year);
            rejectedCDRCount = (Long) qRejected.uniqueResult();
            
            Query qInput = sessionInput.createQuery("SELECT Count(*) from PlatformCDR where MONTH(dateTime) = " + month 
                                            + " AND YEAR(dateTime) = " + year);
            inputCDRCount = (Long) qInput.uniqueResult();
            
            Query qTimeStamp = sessionInput.createQuery("SELECT lastEntryTimeStamp as lastEntryTimeStamp, lastTimeStampRead as lastTimeStampRead,  "
                                    + " lastTimeTillCdrProessed as lastTimeTillCdrProessed from TimeStamp ").setResultTransformer(Transformers.aliasToBean(TimeStamp.class));
            //qTimeStamp.setMaxResults(1);
            tsList = (List<TimeStamp>)qTimeStamp.list();
            
        }catch(Exception e){
            returnString = "error";
            myLogger.log(Level.SEVERE, "Could not fetch the rating data. Cause : ", e.fillInStackTrace());
        }finally{
            session.close();
            sessionInput.close();
        }
        
        returnString = (ratedCDRCount != null ? String.valueOf(ratedCDRCount) : "0") + ","
                        + (rejectedCDRCount != null ? String.valueOf(rejectedCDRCount) : "0") + ","
                        + (inputCDRCount != null ? String.valueOf(inputCDRCount) : "0") + ","        
                        + (tsList.size() > 0 ? String.valueOf(tsList.get(0).getLastTimeTillCdrProessed()) : "") + ","
                        + (tsList.size() > 0 ? String.valueOf(tsList.get(0).getLastTimeStampRead()) : "") + ","
                        + (tsList.size() > 0 ? String.valueOf(tsList.get(0).getLastEntryTimeStamp()) : "");
        
        LOGGER.info("Rating Info : " + returnString);
        
        return returnString;
    }
}
