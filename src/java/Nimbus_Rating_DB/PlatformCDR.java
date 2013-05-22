/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Nimbus_Rating_DB;

import java.util.Date;

/**
 *
 * @author Vikram
 */
public class PlatformCDR implements java.io.Serializable{
    
     private int id;
     private String cdrGuid;
     private Date dateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCdrGuid() {
        return cdrGuid;
    }

    public void setCdrGuid(String cdrGuid) {
        this.cdrGuid = cdrGuid;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
        
}
