package com.cop290.ashwattha.assn2.iitdcomplaints.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.ResponseListener;
import com.google.gson.Gson;

import com.cop290.ashwattha.assn2.iitdcomplaints.R;
import com.cop290.ashwattha.assn2.iitdcomplaints.classes.User;
import com.cop290.ashwattha.assn2.iitdcomplaints.fragment.AuthorityFragment;
import com.cop290.ashwattha.assn2.iitdcomplaints.fragment.NotificationAuthorityFragment;
import com.cop290.ashwattha.assn2.iitdcomplaints.fragment.NotificationStudentFragment;
import com.cop290.ashwattha.assn2.iitdcomplaints.fragment.StudentFragment;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.NetworkOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NetworkOperations network ;
    Gson gson ;
    User user;
    public static class ImplementListener implements ResponseListener {
        WeakReference<Home> activity;
        ImplementListener(Home activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            Home activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("logout.json"))
                    activity.updateLayout(json);

            }
            catch(JSONException e){
                if (activity != null && !activity.isFinishing())
                activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error,String URL) {
            Home activity=this.activity.get();
            if (activity != null && !activity.isFinishing())
            activity.raiseAlert("Oops1", "Server behaved unusually");

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        network = new NetworkOperations();
        gson = new Gson();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        user = gson.fromJson(getIntent().getStringExtra("user"),User.class);

        View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_home,null);
        TextView Drawer_UserName = (TextView) nav_header.findViewById(R.id.drawer_name);
        TextView Drawer_UserEmail = (TextView) nav_header.findViewById(R.id.drawer_mail);
        Drawer_UserName.setText(user.uname);
        Drawer_UserEmail.setText(user.email);
        navigationView.addHeaderView(nav_header);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume(){
        Fragment ComplaintsFragment = null;
        if (user.catg == "0")
            ComplaintsFragment = new StudentFragment();
        else
            ComplaintsFragment = new AuthorityFragment();
        getSupportActionBar().setTitle("Complaints");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_relativelayout, ComplaintsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch(id){
            case R.id.nav_complaints :    Fragment ComplaintsFragment = null;
                                        if (user.catg == "0")
                                            ComplaintsFragment = new StudentFragment();
                                        else
                                            ComplaintsFragment = new AuthorityFragment();
                                        getSupportActionBar().setTitle("Complaints");
                                        transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.main_relativelayout, ComplaintsFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                        break;
            case R.id.nav_notifications:    Fragment NotificationsFragment = null;
                                        if (user.catg == "0")
                                            NotificationsFragment = new NotificationStudentFragment();
                                        else
                                            NotificationsFragment = new NotificationAuthorityFragment();
                                        getSupportActionBar().setTitle("Notifications");
                                        transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.main_relativelayout, NotificationsFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                        break;
            case R.id.nav_logout :  logout();
                                    finish();
                                    break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void logout(){
        String url = "http://192.168.56.1:8000/logout.json";

        if(network.NetworkCheck(this)){
            network.sendStringrequest(url,new ImplementListener(this),getApplicationContext());
        }
        else{
            showSnackbar("Connect to the internet");
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
    public void updateLayout(JSONObject json) {
        try {
            if (json.getString("success").equals("true")) {

            }
        } catch (JSONException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
