package com.cop290.ashwattha.assn2.iitdcomplaints;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;
import com.android.volley.VolleyError;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.NetworkOperations;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.ResponseListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.component.CardThumbnailView;

public class Login extends AppCompatActivity {
    Gson gson ;
    NetworkOperations network;


    public static class ImplementListener implements ResponseListener {
        WeakReference<Login> activity;
        ImplementListener(Login activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            Login activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("login.json"))
                    activity.userauthentication(json);
                else if(URL.contains("validatesession")){
                    activity.Sessionauthentication(json);
                }
            }
            catch(JSONException e){
                if (activity != null && !activity.isFinishing())
                activity.raiseAlert("Oops", "Server behaved unusually");
                e.getStackTrace();
                return;
            }
        }

        @Override
        public void onError(VolleyError error,String URL) {
            Login activity=this.activity.get();
            if (activity != null && !activity.isFinishing())
            activity.raiseAlert("Oops1", error.toString());
            if(URL.contains("validatesession")){
                if (activity != null && !activity.isFinishing())

                    activity.Sessionauthentication(null);
            }

        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        network = new NetworkOperations();
        gson = new Gson();
        checkSession();

    }

    // function for authentication of user (check response )
    public void userauthentication(JSONObject response){
        System.out.println(response);
        String userdata="";
        try{
        if(response.getString("success").equals("true"))
        {
        try{
        userdata = response.getString("userdata");}
        catch (JSONException e){
            System.out.println(e);
        }
        Intent intent = new Intent(Login.this, com.cop290.ashwattha.assn2.iitdcomplaints.activity.Home.class);
        intent.putExtra("user",userdata);
        startActivity(intent);}}
        catch(JSONException e){
            raiseAlert("oops",e.toString());
        }
    }

    // function request for login
    public void login(){
        EditText username =(EditText) findViewById(R.id.Username_EditText);
        EditText password = (EditText) findViewById(R.id.Password_EditText);
        String url = "http://192.168.56.1:8000/login.json";
        LinkedHashMap<String, String> formdata = new LinkedHashMap<String, String>();
        formdata.put("email",username.getText().toString());
        formdata.put("password",password.getText().toString());
        //url+= username.getText() + "&password=" + password.getText();
        if(network.NetworkCheck(this)){
            network.sendJson(url,formdata,getApplicationContext(),new ImplementListener(this));
        }
        else{
            showSnackbar("Connect to the internet");
        }
    }

    public void checkSession(){
       // JSONObject response = null;
        String url = "http://192.168.56.1:8000/validatesession";

        if(network.NetworkCheck(this)){
            network.sendStringrequest(url,new ImplementListener(this),getApplicationContext());
        }
        else{
            showSnackbar("Connect to the internet");
        }
       // Sessionauthentication(response);

    }

    public void Sessionauthentication(JSONObject response) {

        System.out.println(response);
        String userdata="";
        try{
            if(response.getString("success").equals("true"))
            userdata = response.getString("message");}
        catch (Exception e){
            System.out.println(e);
        }

        if(userdata=="" || userdata == null){
        setContentView(R.layout.activity_login);
        FloatingActionButton fab_login = (FloatingActionButton) findViewById(R.id.fab_login);
        fab_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }

        });}
        else{
            Intent intent = new Intent(Login.this, com.cop290.ashwattha.assn2.iitdcomplaints.activity.Home.class);
            intent.putExtra("user",userdata);
            startActivity(intent);
        }
    }
    public void raiseAlert(String Title, String Description){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setMessage(Description);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showSnackbar(String s){
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
