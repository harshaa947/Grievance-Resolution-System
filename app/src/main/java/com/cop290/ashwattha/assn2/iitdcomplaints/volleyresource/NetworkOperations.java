package com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource;

/**
 * Created by Harsh on 29-03-2016.
 */


/**
 * Created by Harsh on 19-02-2016.
 */

        import android.app.Activity;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.support.v7.app.AlertDialog;

        import com.android.volley.AuthFailureError;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.VolleyLog;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.StringRequest;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.net.CookieHandler;
        import java.net.CookieManager;
        import java.util.HashMap;
        import java.util.LinkedHashMap;
        import java.util.Map;


public class NetworkOperations {
    // check connectivity
    public boolean NetworkCheck(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    // sends request process them using responselistener interface
    public void sendStringrequest(final String URL,  final ResponseListener mListener, final Context context){

        StringRequest req = new StringRequest(Request.Method.GET,URL,

                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        System.out.println(URL + s);
                        mListener.onSuccess(s,URL);
                    }
                } ,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mListener.onError(volleyError,URL);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                SharedPreferences prefs = context.getSharedPreferences("header token", context.MODE_PRIVATE);
                String auth= "Bearer "+ prefs.getString("token",null);
                params.put("Authorization", auth);
                //params.put("Accept-Language", "fr");
                //System.out.println("jio" +auth);
                return params;
            }
        };

        VolleyInstance.getInstance(context).addToRequestQueue(req);
    }


    public void sendJson(final String URL, LinkedHashMap params,final Context context, final ResponseListener mListener){
        JSONObject json= new JSONObject(params);
        System.out.println(json.toString());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,URL,json,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                        if(URL.contains("login.json")){
                                if(response.getString("success").equals("true"))
                                {
                            SharedPreferences.Editor editor = context.getSharedPreferences("header token", context.MODE_PRIVATE).edit();
                            editor.putString("token", response.getString("token"));
                           // editor.putInt("idName", 12);
                            editor.commit();}
                        }
                        else if(URL.contains("logout.json")){
                            if(response.getString("success").equals("true"))
                            {
                                SharedPreferences.Editor editor = context.getSharedPreferences("header token", context.MODE_PRIVATE).edit();
                               // editor.putString("token", response.getString("token"));
                                // editor.putInt("idName", 12);
                                editor.remove("token");
                                editor.commit();}
                        }

                        }
                        catch(JSONException e){
                            System.out.println(e);
                        }
                        mListener.onSuccess(response.toString(),URL);
                        }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.onError(error,URL);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                SharedPreferences prefs = context.getSharedPreferences("header token", context.MODE_PRIVATE);
                String auth= "Bearer "+ prefs.getString("token",null);
                params.put("Authorization", auth);
                //params.put("Accept-Language", "fr");

                return params;
            }
        };


        VolleyInstance.getInstance(context).addToRequestQueue(req);

    }
}



