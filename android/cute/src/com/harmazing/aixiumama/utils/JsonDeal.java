package com.harmazing.aixiumama.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonDeal {
	private String result;
	private	JSONArray jsonArray;
	public JsonDeal(String result){
		this.result=result;
	}
	
	public JSONArray Deal(){
		
		if(result!=null)
		{
			
			Log.v("服务器获得的json",result);
            try {
            result = result.substring(result.indexOf("{"),
                    result.lastIndexOf("}") + 1);
            result = "{objects:[" + result + "]}";
        }catch (Exception e){
                Log.v("***********","服务器返回空");
                return null;
            }
			
			try {
				JSONObject obj = new JSONObject(result);
				  jsonArray = obj.getJSONArray("objects");

				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonArray;
	
}
	
	public JSONArray dealJsonArray()
	{

        try {
            JSONObject obj = new JSONObject(result);
            jsonArray = obj.getJSONArray("diseaseInfo");


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return jsonArray;
		
	}
}