
import Nimbus_Rating_DB.Nimbus_Rating_DB;
import Nimbus_Rating_DB.RejectedCdrHelper;

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
        //System.out.println(db.isRatingRunning());
        //db.getRatingInfo("3", "2013");
        RejectedCdrHelper obj = new RejectedCdrHelper();
        obj.updateBillId(null, null, null, "00Di0000000Z6bQ!AR0AQCY_MpQDnVjsujIniHNLDblIa_4IgBpvokeDSLvw1UqJRAsihoTj6L9gotGH.DWQwRdeL72ohGXpJPuM424OPI5Asy8L","https://na15.salesforce.com/services/Soap/u/19.0/00Di0000000Z6bQ");
    }
    
}
