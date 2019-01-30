package com.example.hashboard;

import org.json.JSONObject;
public class HttpResponse {

    private int _code;
    private JSONObject _JSONObject; 
    
    public int getCode(){
        return _code;
    }
    
    public void setCode(int code){
        _code = code;
    }
    
    public JSONObject getJSONObject(){
        return _JSONObject;
    }
    
    public void setJSONObject(JSONObject JSONObject){
        _JSONObject = JSONObject;
    }

    public boolean isSuccesful(){
        switch (_code){
            case 200:
                return true;
            case 201:
                return true;
            case 202:
                return true;
            default :
                return false;
        }

    }
}
