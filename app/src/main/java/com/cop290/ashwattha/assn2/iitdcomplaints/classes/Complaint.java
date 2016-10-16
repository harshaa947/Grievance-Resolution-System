package com.cop290.ashwattha.assn2.iitdcomplaints.classes;

import java.util.ArrayList;

/**
 * Created by Vikas on 27-03-2016.
 */
public class Complaint {
    public class DetailComp {
        public String title;
        public String desc;
    }

    public String _id;
    public String label;
    public String prio;
    public String date;
    public String filed;
    public String fort;
    public String resolvedby;
    public String status;
    public ArrayList<String> comments = new ArrayList<String>();
    public ArrayList<String> pvote = new ArrayList<String>();
    public ArrayList<String> nvote = new ArrayList<String>();
    public ArrayList<String> tagged = new ArrayList<String>();
    public DetailComp detail = new DetailComp();
}

 /*class DetailComp {
    public String title;
    public String desc;
}*/
