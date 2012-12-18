/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Nimbus_Rating_DB;

/**
 *
 * @author Vikram Middha
 */
public class Aggregation_Sync implements Runnable {
    Thread runner;
    String mappingString;
    String aggregatedDate;
    String globalRecordId;
    String sessionId;
    String serverURL;
    
    
    public Aggregation_Sync(String threadName, String mappingString, String aggregatedDate, String globalRecordId, String sessionId, String serverURL) {
        this.mappingString = mappingString;
        this.globalRecordId = globalRecordId;
        this.aggregatedDate = aggregatedDate;
        this.serverURL = serverURL;
        this.sessionId = sessionId;
        runner = new Thread(this, threadName);
        runner.start();
    }
    
    public void run() {
        RejectedCdrHelper rch = new RejectedCdrHelper();
        rch.updateBillId(mappingString, aggregatedDate, globalRecordId, sessionId, serverURL);
    }
}
