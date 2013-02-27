
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
        //System.out.println(db.isRatingRunning());
        db.startRating();
    }
    
}
