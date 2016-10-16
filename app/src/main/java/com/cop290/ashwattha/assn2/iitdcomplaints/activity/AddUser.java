package com.cop290.ashwattha.assn2.iitdcomplaints.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.cop290.ashwattha.assn2.iitdcomplaints.R;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.NetworkOperations;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.ResponseListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

/*
    This screen enters member details of one member
 */

public class AddUser extends AppCompatActivity implements View.OnClickListener {
    private String User_name = null;
    private String Entry_Number = null;
    private String Emailid= null;
    private String Department = null;
    private String Hostel= null;
    private String Password= null;
    
    private Intent intent = null;
    Gson gson ;
    NetworkOperations network;


    public static class ImplementListener implements ResponseListener {
        WeakReference<AddUser> activity;
        ImplementListener(AddUser activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            AddUser activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("addUser.json"))
                    activity.updatelayout(json);

            }
            catch(JSONException e){
                if (activity != null && !activity.isFinishing())
                    activity.raiseAlert("Oops", "Server behaved unusually");
                e.getStackTrace();
                return;
            }
        }

        @Override
        public void onError(VolleyError error, String URL) {
            AddUser activity=this.activity.get();
            if (activity != null && !activity.isFinishing())
                activity.raiseAlert("Oops1", error.toString());


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New User");
        intent = getIntent();
        network = new NetworkOperations();
        gson = new Gson();
        FloatingActionButton fab_cross_add_user = (FloatingActionButton) findViewById(R.id.fab_cross_add_user);
        FloatingActionButton fab_tick_add_user = (FloatingActionButton) findViewById(R.id.fab_tick_add_user);
        fab_cross_add_user.setOnClickListener(this);
        fab_tick_add_user.setOnClickListener(this);
        fab_tick_add_user.setBackgroundTintList(getResources().getColorStateList(R.color.colorYellow));

        fab_cross_add_user.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

        animate_in();
    }

    @Override
    public void onBackPressed() {
        animate_out();
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
        overridePendingTransition(0, R.anim.fade_out);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_cross_add_user:    onBackPressed();
                break;
            case R.id.fab_tick_add_user:     if(check_details()) //returns back to registration screeen after checking details
            {   animate_out();
                addUser();
                setResult(RESULT_OK, intent);

            }
            else
            {   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Oops...");
                alertDialogBuilder.setMessage("I think you entered an invalid Entry Number or Name .\nLet's try again!!");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
                break;
        }
    }

    private boolean check_details(){    // check the validity of user details
        EditText User_name_EditText = (EditText) findViewById(R.id.user_name_EditText);
        EditText User_entry_no_EditText = (EditText) findViewById(R.id.user_entry_no_EditText);
        EditText User_Email_Edittext =(EditText) findViewById(R.id.user_email_EditText);
        EditText User_Dept_Edittext =(EditText) findViewById(R.id.User_dept_EditText);
        EditText User_Hostel = (EditText) findViewById(R.id.hostel_EditText);
        EditText User_Password = (EditText) findViewById(R.id.user_password_EditText);


        User_name = User_name_EditText.getText().toString();
        Entry_Number = User_entry_no_EditText.getText().toString();
        Emailid = User_Email_Edittext.getText().toString();
        Department = User_Dept_Edittext.getText().toString();
        Password = User_Password.getText().toString();
        Hostel = User_Hostel.getText().toString();

        if(!User_name.matches("^[a-zA-Z\\s]*$"))
        {
            return false;
        }

        if(!Entry_Number.matches("^([2][0][0-9]{2}[A-Za-z]{2}[A-Za-z0-9][0-9]{4})$"))
        {
            return false;
        }

        return true;
    }


    public void addUser(){
        LinkedHashMap<String, String> formdata = new LinkedHashMap<String, String>();
        formdata.put("email",Emailid);
        formdata.put("pass",Password);
        formdata.put("uname", User_name);
        formdata.put("hostel", Hostel);
        formdata.put("dept", Department);
        formdata.put("entno", Entry_Number);
        String url = "http://192.168.56.1:8000/login.json";
        JSONObject myuser = new JSONObject(formdata);
        formdata = new LinkedHashMap<String,String>();
        formdata.put("user",myuser.toString());
        if(network.NetworkCheck(this)){
            network.sendJson(url,formdata,getApplicationContext(),new ImplementListener(this));
        }
        else{
            showSnackbar("Connect to the internet");
        }
    }

    public void animate_in(){   // animation when coming from registration screen
        LinearLayout AddUser_LinearLayout = (LinearLayout) findViewById(R.id.add_user_LinearLayout);
        FloatingActionButton fab_cross_add_user = (FloatingActionButton) findViewById(R.id.fab_cross_add_user);
        FloatingActionButton fab_tick_add_user = (FloatingActionButton) findViewById(R.id.fab_tick_add_user);
        Animation slide_down_in = AnimationUtils.loadAnimation(this, R.anim.slide_down_in);
        AddUser_LinearLayout.startAnimation(slide_down_in);
        Animation rotate_decelerate = AnimationUtils.loadAnimation(this, R.anim.rotate_decelerate);
        fab_cross_add_user.startAnimation(rotate_decelerate);
        fab_tick_add_user.startAnimation(rotate_decelerate);
    }

    public void animate_out(){ // animation when returning to registration screen
        FloatingActionButton fab_cross_add_user = (FloatingActionButton) findViewById(R.id.fab_cross_add_user);
        FloatingActionButton fab_tick_add_user = (FloatingActionButton) findViewById(R.id.fab_tick_add_user);
        Animation rotate_accelerate = AnimationUtils.loadAnimation(this, R.anim.rotate_accelerate);
        fab_cross_add_user.startAnimation(rotate_accelerate);
        fab_tick_add_user.startAnimation(rotate_accelerate);
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
    public void updatelayout(JSONObject json){
        String success="";
        try{
            success = json.getString("success");
        }catch(JSONException e){
            raiseAlert("Oops",e.toString());
        }
        if(success =="" || success.equals("false")){
            raiseAlert("Oops" , "Server behaved unusually");
        }else{
            finish();
            overridePendingTransition(0, R.anim.fade_out);
        }
    }
}
