/**
 * Created by Harsh on 28-03-2016.
 */
var user = require('./models').Users;
ObjectId = require('mongodb').ObjectId;

//var jsonwebtoken = require('jsonwebtoken');
var _ = require('underscore');
var notifications =require('./models').notifications;
var misc = require('./models').misc;

exports.getNotifications = function(userid,callback){
    user.findOne({id:userid},function(err,result){
        if(err)
            callback(false,false);
        else if(result){
            var notification = result.notifications;
            notifications.find({_id:{$in:notification}}).toArray(function(err,docs){
                if(err || !docs || !docs.length){
                    console.log(err);console.log(docs);
                    callback(false,docs);
                    return;
                }else
                {
                    for(var i=0;i<docs.length;i++){
                        docs[i].users=null;
                    }
                    callback(true,docs);

                }

            });
        }
    })
}
exports.markRead = function(userid,notarray,callback){
    notifications.update({_id:{$in:notarray}},{$pull:{users:userid}},function(err,result){
        if(err || !result){
            callback(false,"unable to proceed");
        }
        else {
            callback(true,true)
        }
    })
    user.update({id:userid},{$pull:{notifications:notarray}},function(err,result){

    })
}