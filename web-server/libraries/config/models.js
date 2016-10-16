/**
 * Created by Harsh on 24-03-2016.
 */

//var mongodb = require('mongodb');
var mongo = require('mongoskin');
var mongoclient = mongo.MongoClient;
var databases = require('./database');
var urldbuser = databases.dbuser.url ;

var db = mongoclient.connect(urldbuser);
var Users = db.collection('user', {safe: true});
var Complaint =  db.collection('complaint', {strict: true});
var notifications = db.collection('notifications', {safe: true});
var misc = db.collection('misc',{safe:true});
/*Users.insertOne({email:"cs1140221",password:"12345"},function(err,result){
    if(err)
    console.log(err);
    console.log(result);
});*/

exports.Users = Users;
exports.Complaints = Complaint;
exports.notifications = notifications;
exports.misc =misc;
