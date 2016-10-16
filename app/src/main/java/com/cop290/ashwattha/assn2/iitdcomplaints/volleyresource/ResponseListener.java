package com.cop290.ashwattha.assn2.iitdcomplaints.volleyresource;

import com.android.volley.VolleyError;

/**
 * Created by Harsh on 19-02-2016.
 */

// Interface for volley request
public interface ResponseListener {
    void onSuccess(String response,String URL);
    void onError(VolleyError e,String URL);

}
