package com.cop290.ashwattha.assn2.iitdcomplaints.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.cop290.ashwattha.assn2.iitdcomplaints.R;
import com.cop290.ashwattha.assn2.iitdcomplaints.activity.AddUser;
import com.cop290.ashwattha.assn2.iitdcomplaints.activity.PostComplaint;
import com.cop290.ashwattha.assn2.iitdcomplaints.classes.ComplaintArraylist;
import com.cop290.ashwattha.assn2.iitdcomplaints.classes.Complaint;
import com.cop290.ashwattha.assn2.iitdcomplaints.classes.User;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.NetworkOperations;
import com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource.ResponseListener;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardViewNative;


public class AuthorityFragment extends Fragment {

    private ArrayList<Complaint> Complaints;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    private Context mContext;
    protected AuthorityComplaintsAdapter mAdapter;
    NetworkOperations network ;
    Gson gson ;
    User user;
    private int ADD_USER_REQUEST = 1;
    public static class ImplementListener implements ResponseListener {
        WeakReference<AuthorityFragment> activity;
        ImplementListener(AuthorityFragment activity) {
            this.activity = new WeakReference(activity);
        }
        @Override
        public void onSuccess(String response , String URL){
            AuthorityFragment activity=this.activity.get();
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
            AuthorityFragment activity=this.activity.get();
            if (activity != null && !activity.getActivity().isFinishing())
                activity.raiseAlert("Oops1", "Server behaved unusually");

        }
    }
    public AuthorityFragment() {
        // Required empty public constructor
    }




    static class AuthorityComplaintsAdapter extends RecyclerView.Adapter<AuthorityComplaintsAdapter.ViewHolder> {

        private ArrayList<Complaint> mDataSet;
        private Context mContext;

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */
        public class AuthorityCard extends Card {
            Complaint mComplaint;

            //Use your resource ID for your inner layout
            public AuthorityCard(Context context,Complaint complaint) {
                super(context, R.layout.authority_card);
                mComplaint = complaint;
            }

            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {

                if (view == null) return;

                //Retrieve TextView elements
                TextView description = (TextView) view.findViewById(R.id.authority_card_description);
                description.setText(mComplaint.detail.desc);
                ImageView imageView = (ImageView) view.findViewById(R.id.authority_card_image);

            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            CardViewNative mCardview;
            AuthorityCard mCard;
            Complaint mComplaint;

            public ViewHolder(View v) {
                super(v);
                mCardview = (CardViewNative) v.findViewById(R.id.complaint_cardview);

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.complaint_card, viewGroup, false);
            return new ViewHolder(v);
        }

        AuthorityComplaintsAdapter(ArrayList<Complaint> DataSet,Context context) {
            mDataSet = DataSet;
            mContext = context;
        }


        //this onBindViewHolder set the of each assignment
        // into the txtveiws created for it
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {

            viewHolder.mComplaint = mDataSet.get(i);
            viewHolder.mCard = new AuthorityCard(mContext,viewHolder.mComplaint);
            CardHeader header = new CardHeader(mContext);

            header.setTitle(viewHolder.mComplaint.detail.title);
            viewHolder.mCard.addCardHeader(header);

            CardThumbnail cardThumbnail = new CardThumbnail(mContext);
            cardThumbnail.setDrawableResource(R.drawable.minimal_background);
            viewHolder.mCard.addCardThumbnail(cardThumbnail);

            viewHolder.mCardview.setCard(viewHolder.mCard);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        network = new NetworkOperations();
        gson = new Gson();
        Complaints = new ArrayList<Complaint>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_authority, container, false);
        mContext = getActivity();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.authority_card_recycler_view) ;
        mLayoutManager = new LinearLayoutManager(mContext);
        FloatingActionButton fab_add_user = (FloatingActionButton) rootView.findViewById(R.id.fab_add_user);
        fab_add_user.setOnClickListener(new View.OnClickListener() { // click listener for start button
            @Override
            public void onClick(View view) {
                animate_go();
                Intent intent = new Intent(mContext, AddUser.class);
                startActivityForResult(intent, ADD_USER_REQUEST);
                getActivity().overridePendingTransition(R.anim.fade_in, 0);
            }
        });
        requestComplaints(0,100,0,0);
        return rootView;
    }

    public void animate_go(){ // animation when calling add member screen
        FloatingActionButton fab_add_user = (FloatingActionButton) rootView.findViewById(R.id.fab_add_user);
        Animation rotate_accelerate = AnimationUtils.loadAnimation(mContext, R.anim.rotate_accelerate);
        fab_add_user.startAnimation(rotate_accelerate);
    }

    public void animate_back(){ // animation when returning from add member screen
        FloatingActionButton fab_add_user = (FloatingActionButton) rootView.findViewById(R.id.fab_add_user);
        Animation rotate_decelerate = AnimationUtils.loadAnimation(mContext, R.anim.rotate_decelerate);
        fab_add_user.startAnimation(rotate_decelerate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) { // fills the information received from add member screen on the registrstion screen
        animate_back();
    }

    @Override
    public void onResume(){
        super.onResume();

        if(mAdapter == null)
            mAdapter = new AuthorityComplaintsAdapter(Complaints,mContext);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public  void requestComplaints(int start , int end , int sort , int type){
        /*JSONObject response = null;
        updateLayout(response);*/
        String url = "http://192.168.56.1:8000/complaint.json";
        url+="?start=" + start +"&end=" +end +"&sort=" +sort +"&type" + type;
        if(network.NetworkCheck(getActivity())){
            network.sendStringrequest(url,new ImplementListener(this),getActivity().getApplicationContext());
        }
        else{
            showSnackbar("Connect to the internet");
        }
    }

    public void updateLayout( JSONObject response){
        String complaintlist="";
        ComplaintArraylist Complaintobj = null;
        try{
          complaintlist = response.getString("message");
         Complaintobj =  gson.fromJson(response.toString(),ComplaintArraylist.class);
        }catch(JSONException e){
            raiseAlert("oops",e.toString());
        }
        if(Complaintobj == null){
            raiseAlert("oops","Server behaved unusually");
        }
        else {
            // Complaints contain arraylist of complaint
            Complaints = Complaintobj.message;


            mAdapter = new AuthorityComplaintsAdapter(Complaints,mContext);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(mLayoutManager);
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
}
