package com.cop290.ashwattha.assn2.iitdcomplaints.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.cop290.ashwattha.assn2.iitdcomplaints.R;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.NetworkOperations;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.ResponseListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;


public class PostComplaint extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    private String Title;
    private String Level;
    private String Type;
    private String Description;
    private String ImagePath;
    protected Spinner Level_Spinner;
    protected Spinner Type_Spinner;
    private int PICK_IMAGE_REQUEST = 1;
    private int RESULT_LOAD_IMAGE = 1;
    private Intent intent;
    Gson gson ;
    NetworkOperations network;


    public static class ImplementListener implements ResponseListener {
        WeakReference<PostComplaint> activity;
        ImplementListener(PostComplaint activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            PostComplaint activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("post_complaint.json"))
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
            PostComplaint activity=this.activity.get();
            if (activity != null && !activity.isFinishing())
                activity.raiseAlert("Oops1", error.toString());


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_complaint);
        Toolbar toolbar = (Toolbar) findViewById(R.id.post_complaint_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post Your Complaint");
        intent = getIntent();
        network = new NetworkOperations();
        gson = new Gson();
        FloatingActionButton fab_cross_post_complaint = (FloatingActionButton) findViewById(R.id.fab_cross_post_complaint);
        FloatingActionButton fab_send_post_complaint = (FloatingActionButton) findViewById(R.id.fab_send_post_complaint);
        Button select_image_button = (Button) findViewById(R.id.select_image_button);
        select_image_button.setOnClickListener(this);
        fab_cross_post_complaint.setOnClickListener(this);
        fab_send_post_complaint.setOnClickListener(this);
        fab_send_post_complaint.setBackgroundTintList(getResources().getColorStateList(R.color.colorYellow));
        Level_Spinner = (Spinner) findViewById(R.id.complaint_level_spinner);
        Type_Spinner = (Spinner) findViewById(R.id.complaint_type_spinner);
        Level_Spinner.setOnItemSelectedListener(this);
        Type_Spinner.setOnItemSelectedListener(this);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.Level,R.layout.complaint_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.notifyDataSetChanged();
        Level_Spinner.setAdapter(adapter);

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
            case R.id.fab_cross_post_complaint:    onBackPressed();
                break;
            case R.id.fab_send_post_complaint:     if(check_details()) //returns back to registration screeen after checking details
            {   animate_out();
                postComplaint();
                setResult(RESULT_OK, intent);

            }
            else
            {   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Oops...");
                alertDialogBuilder.setMessage("I think you did not enter Title or Description .\nLet's try again!!");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
                break;
            case R.id.select_image_button : Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        ArrayAdapter adapter;
        switch(String.valueOf(Level_Spinner.getSelectedItem())){
            case "Individual" : adapter = ArrayAdapter.createFromResource(this,R.array.Individual_array,R.layout.complaint_spinner);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                adapter.notifyDataSetChanged();
                                Type_Spinner.setAdapter(adapter);
                                break;
            case "Hostel" :     adapter = ArrayAdapter.createFromResource(this,R.array.Hostel_array,R.layout.complaint_spinner);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                adapter.notifyDataSetChanged();
                                Type_Spinner.setAdapter(adapter);
                                break;
            case "Institute" :  adapter = ArrayAdapter.createFromResource(this,R.array.Institute_array,R.layout.complaint_spinner);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                adapter.notifyDataSetChanged();
                                Type_Spinner.setAdapter(adapter);
                                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            ImagePath = cursor.getString(columnIndex);
            cursor.close();

            try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImage);
                ImageView imageView = (ImageView) findViewById(R.id.selected_image_imageview);
                imageView.setImageBitmap(bitmap);}catch(Exception e){
                e.getStackTrace();
            }


        }
    }
    private boolean check_details(){    // check the validity of user details
        EditText complaint_title_EditText = (EditText) findViewById(R.id.complaint_title_EditText);
        EditText complaint_description_EditText = (EditText) findViewById(R.id.complaint_description_EditText);


        Title = complaint_title_EditText.getText().toString();
        Description = complaint_description_EditText.getText().toString();
        Level = String.valueOf(Level_Spinner.getSelectedItem());
        Type = String.valueOf(Type_Spinner.getSelectedItem());

        if(Title == null || Title == "" || Description == null || Description == "")
        {
            return false;
        }

        return true;
    }


    public void postComplaint(){
        LinkedHashMap<String, String> formdata = new LinkedHashMap<String, String>();
        formdata.put("detail.title",Title);
        formdata.put("detail.desc",Description);
        formdata.put("label",Level);
        formdata.put("type",Type);
        if(ImagePath !="" && ImagePath != null){
        formdata.put("attacm",getStringImage(BitmapFactory.decodeFile(ImagePath)));
        }
        formdata.put("status","1");
        JSONObject complaint = new JSONObject(formdata);
        formdata= new LinkedHashMap<String, String>();
        formdata.put("complaint",complaint.toString());
        String url = "http://192.168.56.1:8000/post_complaint.json";
        if(network.NetworkCheck(this)){
            network.sendJson(url,formdata,getApplicationContext(),new ImplementListener(this));
        }
        else{
            showSnackbar("Connect to the internet");
        }
    }
    public void updatelayout(){

    }
    public void animate_in(){   // animation when coming from registration screen
        LinearLayout PostComplaint_LinearLayout = (LinearLayout) findViewById(R.id.post_complaint_LinearLayout);
        FloatingActionButton fab_cross_post_complaint = (FloatingActionButton) findViewById(R.id.fab_cross_post_complaint);
        FloatingActionButton fab_send_post_complaint = (FloatingActionButton) findViewById(R.id.fab_send_post_complaint);
        Animation slide_down_in = AnimationUtils.loadAnimation(this, R.anim.slide_down_in);
        PostComplaint_LinearLayout.startAnimation(slide_down_in);
        Animation rotate_decelerate = AnimationUtils.loadAnimation(this, R.anim.rotate_decelerate);
        fab_cross_post_complaint.startAnimation(rotate_decelerate);
        fab_send_post_complaint.startAnimation(rotate_decelerate);
    }

    public void animate_out(){ // animation when returning to registration screen
        FloatingActionButton fab_cross_post_complaint = (FloatingActionButton) findViewById(R.id.fab_cross_post_complaint);
        FloatingActionButton fab_send_post_complaint = (FloatingActionButton) findViewById(R.id.fab_send_post_complaint);
        Animation rotate_accelerate = AnimationUtils.loadAnimation(this, R.anim.rotate_accelerate);
        fab_cross_post_complaint.startAnimation(rotate_accelerate);
        fab_send_post_complaint.startAnimation(rotate_accelerate);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
        }else
        {
            finish();
            overridePendingTransition(0, R.anim.fade_out);
        }
    }

}
