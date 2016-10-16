package com.cop290.ashwattha.assn2.iitdcomplaints.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.volley.VolleyError;
import com.cop290.ashwattha.assn2.iitdcomplaints.R;
import com.cop290.ashwattha.assn2.iitdcomplaints.activity.Home;
import com.cop290.ashwattha.assn2.iitdcomplaints.activity.PostComplaint;
import com.cop290.ashwattha.assn2.iitdcomplaints.classes.User;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.NetworkOperations;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.ResponseListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;


public class StudentFragment extends Fragment {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private View view;
    private FragmentActivity myContext;
    private Bundle bundle;
    NetworkOperations network ;
    Gson gson ;
    User user;
    private int POST_COMPLAINT_REQUEST = 1;

    public static class ImplementListener implements ResponseListener {
        WeakReference<StudentFragment> activity;
        ImplementListener(StudentFragment activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            StudentFragment activity=this.activity.get();
            JSONObject json;
            try{
                json =new JSONObject(response);
                if(URL.contains("logout.json"))
                    activity.updateLayout(json);

            }
            catch(JSONException e){
                if (activity != null && !activity.getActivity().isFinishing())
                    activity.raiseAlert("Oops", "Server behaved unusually");
                return;
            }
        }

        @Override
        public void onError(VolleyError error, String URL) {
            StudentFragment activity=this.activity.get();
            if (activity != null && !activity.getActivity().isFinishing())
                activity.raiseAlert("Oops1", "Server behaved unusually");

        }
    }
    // TODO: Rename and change types of parameters

    //this Fragment uses it adapter to show the
    //elements(like grades,overveiw) of a particular course
    public StudentFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters

    public void onCreate(Bundle savedInstanceState) {
        bundle = getArguments();
        super.onCreate(savedInstanceState);
        network = new NetworkOperations();
        gson = new Gson();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_student, container, false);
        myContext = getActivity();
        FloatingActionButton fab_post_complaint = (FloatingActionButton) view.findViewById(R.id.fab_post_complaint);
        fab_post_complaint.setOnClickListener(new View.OnClickListener() { // click listener for start button
            @Override
            public void onClick(View view) {
                animate_go();
                Intent intent = new Intent(myContext, PostComplaint.class);
                startActivityForResult(intent, POST_COMPLAINT_REQUEST);
                myContext.overridePendingTransition(R.anim.fade_in, 0);
            }
        });
        return view;
    }

    public void animate_go(){ // animation when calling add member screen
        FloatingActionButton fab_post_complaint = (FloatingActionButton) view.findViewById(R.id.fab_post_complaint);
        Animation rotate_accelerate = AnimationUtils.loadAnimation(myContext, R.anim.rotate_accelerate);
        fab_post_complaint.startAnimation(rotate_accelerate);
    }

    public void animate_back(){ // animation when returning from add member screen
        FloatingActionButton fab_post_complaint = (FloatingActionButton) view.findViewById(R.id.fab_post_complaint);
        Animation rotate_decelerate = AnimationUtils.loadAnimation(myContext, R.anim.rotate_decelerate);
        fab_post_complaint.startAnimation(rotate_decelerate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) { // fills the information received from add member screen on the registrstion screen
        animate_back();
    }

    @Override
    public void onResume(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(myContext.getSupportFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.student_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        super.onResume();

    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information
     */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = null;
            switch (position) {
                case 0: fragment = new StudentPersonalComplaintsFragment();
                    break;
                case 1: fragment = new StudentHostelComplaintsFragment();
                    break;
                case 2: fragment = new StudentInstituteComplaintsFragment();
                    break;
            }

            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 3;
        }



        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Personal";
                case 1:
                    return "Hostel";
                case 2:
                    return "Institute";
            }
            return null;
        }
    }
    public void raiseAlert(String Title, String Description){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setMessage(Description);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showSnackbar(String s){
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    public void updateLayout(JSONObject json){

    }
}
