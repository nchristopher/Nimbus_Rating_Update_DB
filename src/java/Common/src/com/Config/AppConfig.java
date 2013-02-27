package Common.src.com.Config;

/* 
########################################################################### 
# File..................: AppConfig.java
# Version...............: 1.0
# Created by............: Vikram Middha
# Created Date..........: 27-Jul-2012
# Last Modified by......: 
# Last Modified Date....: 
# Description...........: This is a bean class for Resilient.properties file.
*                        For each entry in properties file, there should be a 
*                       field in this class and getter/setter for the same.
# Change Request History: 				   							 
########################################################################### 
*/

public final class AppConfig {
	
    // SFDC
    private String sfdcEndpoint;
    private String sfdcUsername;
    private String sfdcPassword;
    private String aggregatedRecordsQuery;
    private String batchSize;
    private String aggregatedSFDCApi;
    private String aggregatedObjAPI;
    private String logMPNameSpace;
    private String nextBillStage;
    private Boolean isOldManagedPackage;
    private String orPort;
    private String portcommand;
    private String logFile;
    private String orDirectory;

    public String getOrDirectory() {
        return orDirectory;
    }

    public void setOrDirectory(String orDirectory) {
        this.orDirectory = orDirectory;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getPortcommand() {
        return portcommand;
    }

    public void setPortcommand(String portcommand) {
        this.portcommand = portcommand;
    }

    public String getOrPort() {
        return orPort;
    }

    public void setOrPort(String orPort) {
        this.orPort = orPort;
    }

    public Boolean getIsOldManagedPackage() {
        return isOldManagedPackage;
    }

    public void setIsOldManagedPackage(Boolean isOldManagedPackage) {
        this.isOldManagedPackage = isOldManagedPackage;
    }

    public String getNextBillStage() {
        return nextBillStage;
    }

    public void setNextBillStage(String nextBillStage) {
        this.nextBillStage = nextBillStage;
    }

    public String getLogMPNameSpace() {
        return logMPNameSpace;
    }

    public void setLogMPNameSpace(String logMPNameSpace) {
        this.logMPNameSpace = logMPNameSpace;
    }

    public String getAggregatedObjAPI() {
        return aggregatedObjAPI;
    }

    public void setAggregatedObjAPI(String aggregatedObjAPI) {
        this.aggregatedObjAPI = aggregatedObjAPI;
    }

    public String getAggregatedSFDCApi() {
        return aggregatedSFDCApi;
    }

    public void setAggregatedSFDCApi(String aggregatedSFDCApi) {
        this.aggregatedSFDCApi = aggregatedSFDCApi;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public String getAggregatedRecordsQuery() {
        return aggregatedRecordsQuery;
    }

    public void setAggregatedRecordsQuery(String aggregatedRecordsQuery) {
        this.aggregatedRecordsQuery = aggregatedRecordsQuery;
    }

    
    public String getSfdcEndpoint() {
        return sfdcEndpoint;
    }
    public void setSfdcEndpoint(String sfdcEndpoint) {
        this.sfdcEndpoint = sfdcEndpoint;
    }
    
    public String getSfdcUsername() {
        return sfdcUsername;
    }
    public void setSfdcUsername(String sfdcUsername) {
        this.sfdcUsername = sfdcUsername;
    }
    
    public String getSfdcPassword() {
        return sfdcPassword;
    }
    public void setSfdcPassword(String sfdcPassword) {
        this.sfdcPassword = sfdcPassword;
    }   
   
}