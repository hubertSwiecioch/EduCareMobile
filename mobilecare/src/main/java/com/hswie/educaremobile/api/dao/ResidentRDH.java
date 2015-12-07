package com.hswie.educaremobile.api.dao;

import android.util.Log;

import com.hswie.educaremobile.api.DatabaseColumns;
import com.hswie.educaremobile.api.pojo.Family;
import com.hswie.educaremobile.api.pojo.Resident;
import com.hswie.educaremobile.helper.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by hswie on 11/5/2015.
 */
public class ResidentRDH {

    public static final String TAG = "ResidentRDH";

    public ArrayList<Resident> getAllResidents(){
        ArrayList<Resident> residents = new ArrayList<Resident>();
        List<NameValuePair> paramss = new ArrayList<NameValuePair>();
        paramss.add(new BasicNameValuePair(JsonHelper.TAG_MOD, JsonHelper.MOD_GET_RESIDENTS));

        String JSONString = JsonHelper.makeHttpRequest(JsonHelper.HOSTNAME, "POST", paramss);

        JSONArray jsonArray = JsonHelper.getJSONArrayFromString(JSONString);

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    residents.add(parseResident(obj));
                } catch (JSONException e) {
                    Log.d(TAG, "getResidentMessages " +  "JSONException e = " + e.getMessage());
                }
            }
        }

        return residents;
    }



    public void addResident(ArrayList<String> params){

        List<NameValuePair> paramss = new ArrayList<NameValuePair>();

        for (String param:params) {

            Log.d(TAG, "RDH param: "  + param);
        }


        paramss.add(new BasicNameValuePair(JsonHelper.TAG_MOD, JsonHelper.MOD_ADD_RESIDENT));
        paramss.add(new BasicNameValuePair(JsonHelper.TAG_FIRSTNAME, params.get(0)));
        paramss.add(new BasicNameValuePair(JsonHelper.TAG_LASTNAME, params.get(1)));
        paramss.add(new BasicNameValuePair(JsonHelper.TAG_DATEOFADOPTION, params.get(2)));
        paramss.add(new BasicNameValuePair(JsonHelper.TAG_BIRTHDATE, params.get(3)));
        paramss.add(new BasicNameValuePair(JsonHelper.TAG_ADDRESS, params.get(4)));
        paramss.add(new BasicNameValuePair(JsonHelper.TAG_CITY, params.get(5)));
        paramss.add(new BasicNameValuePair(JsonHelper.TAG_IMAGE, params.get(6)));

        String JSONString = JsonHelper.makeHttpRequest(JsonHelper.HOSTNAME, "POST", paramss);
        Log.d(TAG, "JSONString: "  + JSONString);

    }


    public Resident parseResident(JSONObject obj) {
        Resident resident = new Resident();
        try {
            resident.setID((obj.getString(DatabaseColumns.COL_ID)));
            resident.setBirthDate((obj.getString(DatabaseColumns.COL_BIRTH_DATE)));
            resident.setDateOfAdoption((obj.getString(DatabaseColumns.COL_DATE_OF_ADOPTION)));
            resident.setFirstName((obj.getString(DatabaseColumns.COL_FIRST_NAME)));
            resident.setLastName((obj.getString(DatabaseColumns.COL_LAST_NAME)));
            resident.setAddress((obj.getString(DatabaseColumns.COL_ADDRESS)));
            resident.setCity((obj.getString(DatabaseColumns.COL_CITY)));
            resident.setPhoto((obj.getString(DatabaseColumns.COL_PHOTO)));

        } catch (JSONException e) {
            Log.d(TAG,"parseCarerMessage " +  "JSONException e = " + e.getMessage());
        }

        return resident;
    }
}
