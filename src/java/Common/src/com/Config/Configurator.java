package Common.src.com.Config;

import Common.src.com.Exception.ResilientException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


/* 
########################################################################### 
# File..................: Configurator.java
# Version...............: 1.0
# Created by............: Vikram Middha
# Created Date..........: 27-Jul-2012
# Last Modified by......: 
# Last Modified Date....: 
# Description...........: This class reads the Resilient.properties file and 
*                         populates AppConfig object.
# Change Request History: 				   							 
########################################################################### 
*/
public class Configurator {

    //private static Logger LOGGER = Logger.getLogger(Configurator.class);

    /**
     * Private constructor.
     */
    private Configurator() {
            throw new UnsupportedOperationException("Class is not instantiable.");
    }

    /**
     * initialize and get the Configuration
     * 
     * @return
     */
    public static AppConfig getAppConfig() throws ResilientException {

        //LOGGER.info("Configurator:getAppConfig(): Configuring the Application credentials .........................");

        Properties props = new Properties();
        AppConfig appConfig = new AppConfig();

        try {
                File directory = new File (".");
                //LOGGER.info("Canonical path ==== "+ directory.getCanonicalPath());
                
                FileInputStream fis ;
                
                try{
                    fis = new FileInputStream("C:/MakePos/UpdateHandler.properties");
                }catch(FileNotFoundException e){
                    try{
                        //LOGGER.info("Canonical path ====" + directory.getCanonicalPath().substring(0,directory.getCanonicalPath().lastIndexOf("\\")) + "\\webapps\\GenerateReports\\Resilient.properties");
                        fis = new FileInputStream(directory.getCanonicalPath().substring(0,directory.getCanonicalPath().lastIndexOf("\\")) + "\\webapps\\GenerateReports\\Resilient.properties"); 
                    }catch(Exception e1){
                        //LOGGER.info("Canonical path ====" + directory.getCanonicalPath()+ "\\webapps\\GenerateReports\\Resilient.properties");
                        fis = new FileInputStream(directory.getCanonicalPath()+ "\\webapps\\GenerateReports\\Resilient.properties"); 
                    }
                }catch(Exception e){
                    fis = new FileInputStream("C:/Resilient.properties");
                }                
                             
                props.load(fis);
               // props.load(new FileInputStream(directory.getCanonicalPath() + "/Resilient.properties"));
                                
                //props.load(new FileInputStream("C:/Resilient.properties"));
                //props.load(Configurator.class.getClassLoader().getResourceAsStream("Resilient.properties"));
                // SFDC
                //LOGGER.info(" Resilient Properties loaded successfully ");
                appConfig.setSfdcEndpoint(props.getProperty("sfdc.sfdcEndpoint"));
                appConfig.setSfdcUsername(props.getProperty("sfdc.sfdcUsername"));
                appConfig.setSfdcPassword(props.getProperty("sfdc.sfdcPassword"));
                appConfig.setAggregatedRecordsQuery(props.getProperty("sfdc.aggregateRecordsQuery"));  
                appConfig.setBatchSize(props.getProperty("batchSize"));
                appConfig.setAggregatedSFDCApi(props.getProperty("sfdc.aggregated.sfdc.api"));
                appConfig.setAggregatedObjAPI(props.getProperty("sfdc.aggregatedObjApi"));
                appConfig.setLogMPNameSpace(props.getProperty("sfdc.Log.ManagedPackage.Namespace"));
                appConfig.setNextBillStage(props.getProperty("sfdc.nextBillStage"));
                appConfig.setIsOldManagedPackage(props.getProperty("sfdc.isOldManagedPackage").equals("Yes") ? true : false);


        } catch (Exception e) {
            //LOGGER.error("Exception while configuring the Application credentials ..." + e);
            throw new ResilientException(e.getMessage());
        } 
        return appConfig;
    }

}
