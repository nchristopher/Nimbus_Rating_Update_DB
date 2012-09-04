/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Nimbus_Rating_DB;

import com.sun.xml.fastinfoset.util.StringArray;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author nimil.christopher
 */
@WebService()
public class Nimbus_Rating_DB {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getCount")
    public String[] getCount(@WebParam(name = "Type")
    String Type) {
        String count[] = null;
        RejectedCdrHelper rch = new RejectedCdrHelper();
        count = rch.getCategoryCountByType(Type);
        return count;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getCategoryInfoByType")
    public String[] getCategoryInfoByType(@WebParam(name = "suspenseType")
    String suspenseType, @WebParam(name = "Index")
    int Index) {
        //TODO write your implementation code here:
        String result[] = null;
        RejectedCdrHelper rch =  new RejectedCdrHelper();
        result = rch.getCategoryInfo(suspenseType,Index);
        return result;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "updateBillId")
    public Boolean updateBillId(@WebParam(name = "mappingString") String mappingString) {
        //TODO write your implementation code here:
        RejectedCdrHelper rch = new RejectedCdrHelper();
        return rch.updateBillId(mappingString);    
    }
}
