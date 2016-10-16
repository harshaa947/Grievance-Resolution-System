package com.cop290.ashwattha.assn2.iitdcomplaints.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cop290.ashwattha.assn2.iitdcomplaints.R;
import com.cop290.ashwattha.assn2.iitdcomplaints.classes.Complaint;
import com.cop290.ashwattha.assn2.iitdcomplaints.classes.Notification;

import org.json.JSONObject;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardViewNative;


public class NotificationAuthorityFragment extends Fragment {
    private ArrayList<Notification> Notifications;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    private Context mContext;
    protected NotificationAuthorityAdapter mAdapter;

    public NotificationAuthorityFragment() {
        // Required empty public constructor
    }




    static class NotificationAuthorityAdapter extends RecyclerView.Adapter<NotificationAuthorityAdapter.ViewHolder> {
        private static final String TAG = "NotificationAdapter";

        private ArrayList<Notification> mDataSet;

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */


        public static class ViewHolder extends RecyclerView.ViewHolder {

            /* String thread_comment ;
             TextView Thread_comment  ;
             thread_comment
             */
            public TextView notification;
            ///notification_list_recycler_view

            public ViewHolder(View v) {
                super(v);
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Element " + getPosition() + " clicked.");
                    }
                });
                notification = (TextView) v.findViewById(R.id.notification_authority_TextView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.notification_authority_layout, viewGroup, false);

            return new ViewHolder(v);
        }

        NotificationAuthorityAdapter(ArrayList<Notification> DataSet) {
            mDataSet = DataSet;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Notification notification = mDataSet.get(i);
            viewHolder.notification.setText(notification.detail);
            // check for commentsss

        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_notification_authority, container, false);
        requestNotifications();
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        mContext = this.getActivity();
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

    public  void requestNotifications(){
        JSONObject response = null;
        updateLayout(response);
    }

    public void updateLayout( JSONObject response){
        Notifications = new ArrayList<Notification>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.personal_card_recycler_view) ;
        mLayoutManager = new LinearLayoutManager(mContext);
        mAdapter = new NotificationAuthorityAdapter(Notifications);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }
}
