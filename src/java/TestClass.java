
import Nimbus_Rating_DB.Nimbus_Rating_DB;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Vikram Middha
 */
public class TestClass {
    
    public static void main(String[] args){
        Nimbus_Rating_DB db = new Nimbus_Rating_DB();
        db.rollBackOpenRateChanges("2012-11-01","a119000000088Cq","00D90000000hMU9!AQkAQC0B5K8ZSIFhwTRx3VLuS.oBJOmdtFKKJzG..zesbCmTnnUI8td4IPD3leMrjF7WwupiynvJ1Ot9Gcpdye3PMSTIDEev",
                "https://ap1.salesforce.com/services/Soap/u/19.0/00D90000000hMU9");
    }
    
}
