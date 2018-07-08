package com.example.user.driveremergency;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 12/6/2017.
 */

public class jbobject
{
    public JSONObject jb (String Customer_name,String pass, String Contact_No,String Address,String token_no)
    {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Customer_name", Customer_name);
            obj.put("pass", pass);
            obj.put("Contact_No", Contact_No);
            obj.put("Address", Address);
            obj.put("token_no", token_no);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  obj;
    }
    public JSONObject fbobject(String Customer_name,String pass, String Contact_No,String Address,String token_no)
    {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Customer_name", Customer_name);
            obj.put("pass", pass);
            obj.put("Contact_No", Contact_No);
            obj.put("Address", Address);
            obj.put("token_no", token_no);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  obj;
    }
    public  JSONObject resptoreq(String mobile_no,String lat,String longi,String token,String myToken,String usermobile){
        JSONObject obj = new JSONObject();
        try {
            obj.put("mobile_no", mobile_no);
            obj.put("lat", lat);
            obj.put("longi",longi);
            obj.put("token", token);
            obj.put("token_no", myToken);
            obj.put("usermobile", usermobile);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  obj;
    }
}
