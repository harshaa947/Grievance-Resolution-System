/**
 * Created by Harsh on 25-03-2016.
 */
//var crypto = require('crypto');
//var rand = require('csprng');
var crypto = require('crypto');
//var rand = require('csprng');
var user = require('./models').Users;
ObjectId = require('mongodb').ObjectId;

//var jsonwebtoken = require('jsonwebtoken');
var _ = require('underscore');
var notifications =require('./models').notifications;
//var certpri = fs.readFileSync('././privatekeyhex.txt');  // get private key
var compdb = require('./models').Complaints;
var Validator = require('validator.js');
var misc = require('./models').misc;
var moment = require ('moment');
var Assert = Validator.Assert,
    validator =new  Validator.Validator();
var compdetailconst ={
    title : [new Assert().NotBlank(),new Assert().Length({min:0,max:20})],
    desc : [new Assert().NotBlank(),new Assert().Length({min:0,max:1000})],
};
var constraints ={
    //prio :[new Assert().Range(0,30)],
    //" label" : [new Assert().Range(0,20)],
    "date": [new Assert().NotBlank(),new Assert().Length({min:0,max:20})],
    filed:[new Assert().NotBlank()],

    //"status":[new Assert().Range(0,3)],
   // "complaint.pvotes": new Assert().Blank() ,
   // "complaint.nvotes":new Assert().Blank()
}
Array.prototype.diff = function(a) {
    return this.filter(function(i) {return a.indexOf(i) < 0;});
};

exports.addComplaint = function(userid,pcomplaint , callback){
     try{
     userone=  user.findOne({id:userid},{maxTimeMS : 10000},function(err,result){
         if(err)
         {callback(false,false);
             return;}
         else if(result ){
             if (result.catg != 0)
             callback(false,true);
             return;
         }

     });
        // console.log(pcomplaint);
    pcomplaint = JSON.parse(pcomplaint);
         pcomplaint.filed = userid;
        var complaint = getObject(pcomplaint);
     complaint.filed = userid;

    var toput =  valComplaint(complaint)  ;
    complaint.filed = userid;
    if(complaint.attacm){
        var attacm1=getPath();
         tempStoreImage(complaint.attacm,attacm1);
        complaint.attacm=attacm1;
    }
         console.log(complaint.attacm)
    }
    catch(err)
    {
        console.log(err);
        callback(true,false);
        return;
    }
    if(toput === true)
  compdb.insertOne(complaint,function(err,result){
      if(err || !result )
      callback(false,toput);
      else {
          callback(toput, complaint);
          user.update({id:userid},{$push : {"comp":complaint._id}},function(err,result){
              if(err){
                //console.log(error);
              }
              else if(result && result.result && result.result.n ==1){

               // console.log(result);
              }

              var usrs = complaint.tagged;
              if(usrs.length>0) {
                  var notificationp = {
                      type: 0, compid: compid, users: usrs,
                      detail: "You have been tagged in the complaint"
                  };
                  notifications.insertOne(notificationp, function (err, result) {/*console.log(err);console.log(result);*/
                  });
                  user.update({id: {$in: complaint.tagged}}, {
                      $addToSet: {
                          tcomp: complaint._id,
                          notifications: notificationp._id
                      }
                  }, function (err, result) {
                      // console.log(err);
                      // console.log(result);
                  });
                  var catg = getCatg(complaint.label);
                 user.update({catg:catg},{$addToSet:{compr : complaint._id}},function(err,result){} );

              }
          })



      }
  })
    else
     callback(false,toput);
}



exports.voteComplaint = function(votetype,userid,complaintid,callback){

if (votetype == true ){compdb.updateById(complaintid,{$addToSet : {pvote : userid},$pull :{nvote : userid}},function(err,result){
    //console.log(result);

    if(err)
        callback(false);
    else if(result && result.n==1 ){
        //console.log(result);
        callback(true);
    }
    else callback(false);
});}
    else{
    compdb.updateById(complaintid,{$addToSet : {nvote : userid},$pull :{pvote : userid}},function(err,result){
        //console.log(result);

        if(err)
            callback(false);
        else if(result && result.n==1 ){
            //console.log(result);
            callback(true);
        }
        else callback(false);
    });
}

}

exports.tagwith = function (taggedfriends,userid,complaintid,callback){

   compdb.findById(complaintid,function(err,result){
       if(err)
       callback(false);
       else if (result){
           //console.log(result);
         if(result.filed == userid)
         updatetags(complaintid,taggedfriends,callback);
         else if(result.tagged){
              if(_.indexOf(result.tagged,userid)!=-1)
              {
           updatetags(complaintid,taggedfriends,callback);
              }
               else callback(false);
           }
           else callback(false);
       }
   })
}

exports.removetag= function( userid,compid,callback){
    compdb.updateById(compid,{$pull : {tagged : userid}},function(err,result){
        if(err)
            callback(false);
        else callback(true);
    });
}

exports.getinfo = function (userid,compid,callback){
    var returncomment=false;
    user.findOne({id:userid},{maxTimeMS : 10000},function(err,result){
        if(err)
        {callback(false,false);
            return;}
        else if(result ){
            if (result.catg == 1)
                returncomment = true;
            return;
        }
    });
    compdb.findById(compid,function(err,result){
        if(err)
        callback(false,null);
        else if(result){
            if(returncomment)
            result.comments=null;
        callback(true,result);

        }
       else callback(false , "invalid complaint")
    })
}

function updatetags(ci,tf,callback){
    var tochange=[];
    compdb.findById(ci,function(err,result){
        if(result){
            tochange = tf.diff(result.tagged);
        }
    })
    compdb.updateById(ci,{$addToSet :{ tagged :  {$each : tf} }  },function(err,result){
        if(err)
        callback(false);
        else
        {
           // console.log(result) ;
            callback(true);
            if(tochange.length > 0){var notificationp = {type:0,compid:ci,users:tochange,
                detail:"You have been tagged in the complaint"};
                notificationp.date = Date.now();
                notifications.insertOne(notificationp,function(err,result){/*console.log(err);console.log(result);*/});
                user.update({id :{$in:tochange}},{$addToSet:{tcomp:ObjectId(ci) ,notifications: notificationp._id}},function(err,result){
                    // console.log(err);
                    //console.log(result);
                })  ;}

        }



    })
}

exports.resolveComplaint = function(userid,compid,toStatus,callback){
   compdb.findById(compid,function(err,result){
       if(err)
       callback(false,false);
       else if( result){
          user.findOne({id:userid},function(err,result1){
              if(err)
              callback(false,false)
              else if (result1){
                  if(isResolve(result1.catg,result.label))
                  {
                      compdb.updateById(compid,{$set : { status : toStatus , resolvedby : userid  }},function(err,result2){
                          if(err)
                          callback(false,false);
                          else if(result2.n==1)
                          {
                              callback(true,true);
                              var usrs = result.tagged;
                              usrs.push(result.filed);
                              var feed= {compid: ObjectId(compid),toStatus:toStatus , type: 0,usrs:usrs,feeds:[], fort:userid};
                               misc.insertOne(feed,function(err,result){console.log(err);console.log(result);});

                              var notificationp= {type:1,feedid:feed._id,compid:ObjectId(compid),users:usrs,
                                  detail:"Your complaint has been resolved. Please fill the feedback"}
                               notifications.insertOne(notificationp,function(err,result){console.log(err);console.log(result);});
                              user.update({id :{$in: usrs}},{$addToSet:{tcomp:notificationp._id}})  ;
                          }

                          else
                          callback(false,"failed");
                      })
                  }
              }
              else callback(false,"user not defined");
          })
       }
   })
}


exports.resolveFeedback = function(feedbackid,feedback,userid,callback){
    misc.findById(feedbackid,function(err,result){
        if(err)
         callback(false,false);
        else if (result){
            console.log(result);
            if(_.indexOf(result.usrs,userid.toString())== -1 )
            callback(false ,"access denied or feedback already filled");
            else misc.updateById(feedbackid,{$addToSet:{feeds:{userid:userid,feedback:ObjectId(feedback)}}},function(err,result2){
                if(err)
                callback(false,false);
                else if(result2.n==1){
                 callback(true,"feedback registered");
                    var usrs =[];
                    usrs.push(result.fort);
                    var notificationp= {type:2,feedid:ObjectId(feedbackid),compid:result.compid, usrs: usrs,feed:feedback,
                        detail:"Feedback generated for complaint"};
                    notificationp.date = Date.now();
                    notifications.insertOne(notificationp,function(err,result){});
                    misc.updateById(ObjectId(feedbackid),{$pull:{usrs:userid.toString()}},function(err,result){});
                    user.update({id :{$in: usrs}},{$addToSet:{notifications:notificationp._id}})  ;
                }
                else callback(false,false);
            })
        }
        else callback("invalid feedback id");
    })



}


exports.getComplaints=function(userid,start,end,sort,type,callback){
        if(type==0)
        {
            user.findOne({id:userid},function(err,result){
                if(err)
                {
                    callback(false,false);
                        console.log(err);
                        console.log("jdkjkjdwk")
                    return;
                }

                else if(result){
                    var comparray = result.comp;
                    compdb.find({_id:{$in:comparray}}).toArray(function(err,docs){
                        if(err || !docs || !docs.length){
                            console.log(err);console.log(docs);
                            callback(false,docs);
                            return;
                        }
                        else{if(result.catg==1)
                        {
                            for(var i=0;i<docs.length;i++){
                                docs[i].comments=null;
                            }
                        } callback(true,docs);
                        }
                    });
                }
            })
        }
    else if(type==1)
        {
            user.findOne({id:userid},function(err,result){
                if(err)
                    callback(false,false);
                else if(result){
                    var hostel = "10";
                    var length = start - end ;
                    compdb.find({label:hostel},{limit:length,skip:start}).toArray(function(err,docs){
                        if(err || !docs || !docs.length){
                            console.log(err);console.log(docs);
                            callback(false,docs);
                            return;
                        }
                        else{if(result.catg==1)
                        {
                            for(var i=0;i<docs.length;i++){
                                docs[i].comments=null;
                            }
                        } callback(true,docs);
                        }

                    });
                }
            })

        }
    else if(type==2){
            user.findOne({id:userid},function(err,result){
                if(err)
                    callback(false,false);
                else if(result){
                    //var hostel = getLabelh(result.comp);
                    var length = start - end ;
                    compdb.find({label:"15"},{limit:length,skip:start}).toArray(function(err,docs){
                        if(err || !docs || !docs.length){
                            callback(false,docs);
                            return;
                        }
                        else{if(result.catg==1)
                        {
                            for(var i=0;i<docs.length;i++){
                                docs[i].comments=null;
                            }
                        } callback(true,docs);
                        }
                    });
                }
            })
        }
    else if(type==3){
            user.findOne({id:userid},function(err,result){
                if(err)
                    callback(false,false);
                else if(result){
                    var comparray = result.tcomp;
                    var length = start - end ;
                    console.log(comparray);
                    compdb.find({_id:{$in:comparray}},{limit:length,skip:start}).toArray(function(err,docs){
                        if(err || !docs || !docs.length){
                            console.log(err);
                            console.log(docs);
                            callback(false,true);
                            return;
                        }
                        else{

                            if(result.catg==1)
                        {
                            for(var i=0;i<docs.length;i++){
                                docs[i].comments=null;
                            }
                        }
                            callback(true,docs);
                        }
                    });
                }
            })

        }
}

exports.setComment = function(comment,compid,userid,callback){
   /* try{
        var comment = JSON.parse(comment);

    }
    catch(err){
        callback(false,err);

        return;
    }*/
     comment.commentd=comment;
     comment.date = Date.now();
    comment.userid = comment;
    compdb.updateById(compid,{$addToSet:{comments : comment}},function(err,result){
        if(err)
        callback(false,err);
        else if(result && result.n==1){
            callback(true,true)
        }
        else callback(false,"Not FOUND. Invalid comp id");
    })
}

exports.cancelcomplainRequest = function(userid,compid,callback){
    compdb.findById(compid,function(err,result){
        if(err)
        callback(false,false);
        else if(result)
        {
            if(_.indexOf(getPersonalLabel(),result.label)!=-1)
            {
                if(userid == result.filed)
                {
                    var users=[];
                    compdb.findById(compid,function(err,result1){
                        if(result1){
                            users = result1.tagged;
                            users.push(result1.filed);
                            users.update({id :{$in : users}},{$pull:{ comp : ObjectId(compid),tcomp:ObjectId(compid)}});
                        }
                    })



                    compdb.removeById(compid,function(err,result){
                        if(err)
                        callback(true,false);
                        else callback(true,true);
                    })

                }
                else callback(false,"You can not cancel this complaint");

            }
            else callback(false,"Complaint is not personal")
        }
    })
}

function valComplaint(abc) {
    var abcd = validator.validate(abc,constraints) ;
    if(abcd) {
        console.log(abc.detail);
        var xyz = validator.validate(abc.detail, compdetailconst);
        if(xyz==true)
        {
            var dtvalid = moment(abc.date,"YYYY-MM-DD HH-MM-SS");
            if(dtvalid)
                return true;
            else return "Date is in invalid format";
        }
        else
            return xyz;
    }
    else return abcd;
    //   validator.
}

function getObject(pcomplaint){
    var complaint ={};
    //complaint.id = pcomplaint.id;
    //complaint.prio = pcomplaint.prio;
    complaint.detail = {};
    complaint.detail.title = pcomplaint.detail.title;
    complaint.detail.desc= pcomplaint.detail.desc;

    complaint.label = pcomplaint.label;
    complaint.date = moment().format("YYYY-MM-DD HH-MM-SS");
    complaint.filed = pcomplaint.filed;
    complaint.status = 1;
    if(pcomplaint.pvotes == complaint.filed){
        complaint.pvotes = complaint.filed;
        complaint.nvotes = [];
    }else if (pcomplaint.nvotes == complaint.filed){
        complaint.nvotes = complaint.filed;
        complaint.pvotes = [];
    }else {
        complaint.pvotes = [];
        complaint.nvotes = [];
    }
    if(pcomplaint.tagged){
        complaint.tagged = pcomplaint.tagged;
    } else complaint.tagged =[];
    complaint.attacm= pcomplaint.attacm;
    return complaint;
}

function isResolve(){
    return true;
}

function getCatg(label){
    return label;
}
function getConv(hostel){
    var hostel_num;
    switch (hostel){
        case "Aravali" :
            hostel_num = 1;
            break;
        case "Girnar":
            hostel_num = 2;
            break;
        case "Jwalamukhi" :
            hostel_num = 03;
            break;
        case "Karakoram" :
            hostel_num = 04;
            break;
        case "Kumaon" :
            hostel_num =05;
            break;
        case "Nilgiri" :
            hostel_num = 06;
            break;
        case "Shivalik" :
            hostel_num = 07;
            break;
        case "Satpura" :
            hostel_num = 08;
            break;
        case "Udaigiri" :
            hostel_num = 09;
            break;
        case "Vindhayanchal" :
            hostel_num = 10;
            break;
        case "Zanskar" :
            hostel_num = 11;
            break;
        case "Kailash" :
            hostel_num = 12;
            break;
        case "Himadri" :
            hostel_num = 13;
            break;
        case "NewKailash" :
            hostel_num = 14;
            break;
        default :
            hostel_num = 00;
            hostel_num ="10";
    }
    var array = ["10"];
    return array;
}
function getLabelh(hostel){
    var arr=[];
    for(i=1;i<6;i++){
        arr.push(2*1000+i*100+hostel);
    }
}
function getPersonalLabel(){
    return [1100,1200,1300,1400,1500];
}

function tempStoreImage(base64Data,userUploadedImagePath){
    // Save base64 image to disk
    try
    {
        // Decoding base-64 image
        // Source: http://stackoverflow.com/questions/20267939/nodejs-write-base64-image-file
        function decodeBase64Image(dataString)
        {   /*console.log(dataString)
            var matches = dataString.match(/^data:([A-Za-z-+\/]+);base64,(.+)$/);
            console.log(matches);
            var jhj = new Buffer(matches[2], 'base64');
            var response = {};

            if (matches.length !== 3)
            {
                return new Error('Invalid input string');
            }*/
             var response ={};
            response.type = 'jpeg';
            response.data = new Buffer(dataString, 'base64');

            return response;
        }
           // console.log(userUploadedImagePath);
           // console.log(base64Data);
        // Regular expression for image type:
        // This regular image extracts the "jpeg" from "image/jpeg"
        var imageTypeRegularExpression      = /\/(.*?)$/;

        // Generate random string
        //var crypto                          = require('crypto');

        /**/
        var imageBuffer                      = decodeBase64Image(base64Data);
        // Save decoded binary image to disk
        try
        {

            require('fs').writeFile(userUploadedImagePath, imageBuffer.data,{encoding: 'base64'},
                function(err)
                {
                    console.log('DEBUG - feed:message: Saved to disk image attached by user:', userUploadedImagePath);
                    return userUploadedImagePath;
                });
        }
        catch(error)
        {
            console.log('ERROR:1', error);
        }

    }
    catch(error)
    {
        console.log('ERROR:2', error);
    }
}

function getPath(){
    var seed                            = crypto.randomBytes(20);
    var uniqueSHA1String                = crypto
        .createHash('sha1')
        .update(seed)
        .digest('hex');

    // var base64Data = 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAZABkAAD/4Q3zaHR0cDovL25zLmFkb2JlLmN...';

   //
    var userUploadedFeedMessagesLocation = 'img/upload/feed/';

    var uniqueRandomImageName            = 'image-' + uniqueSHA1String;
    // This variable is actually an array which has 5 values,
    // The [1] value is the real image extension
    var imageTypeDetected                = 'jpeg'

    var userUploadedImagePath            = userUploadedFeedMessagesLocation +
        uniqueRandomImageName +
        '.' +
        imageTypeDetected;
    return userUploadedImagePath;
}
console.log(moment().format("YYYY-MM-DD HH-MM-SS"));