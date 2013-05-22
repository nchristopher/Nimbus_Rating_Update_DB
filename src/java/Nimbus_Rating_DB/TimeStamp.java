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
public class TimeStamp {
    
    private int id;
    private Date lastEntryTimeStamp;
    private Date lastTimeStampRead;
    private Date lastTimeTillCdrProessed;
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public Date getLastEntryTimeStamp() {
        return lastEntryTimeStamp;
    }

    public void setLastEntryTimeStamp(Date lastEntryTimeStamp) {
        this.lastEntryTimeStamp = lastEntryTimeStamp;
    }

    public Date getLastTimeStampRead() {
        return lastTimeStampRead;
    }

    public void setLastTimeStampRead(Date lastTimeStampRead) {
        this.lastTimeStampRead = lastTimeStampRead;
    }

    public Date getLastTimeTillCdrProessed() {
        return lastTimeTillCdrProessed;
    }

    public void setLastTimeTillCdrProessed(Date lastTimeTillCdrProessed) {
        this.lastTimeTillCdrProessed = lastTimeTillCdrProessed;
    }
    
}
