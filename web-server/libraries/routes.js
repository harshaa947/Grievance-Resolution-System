/**
 * Created by Harsh on 24-03-2016.
 */
var login = require('./config/login');
var comp = require ('./config/complaintmanipulate');
var jwt = require('express-jwt');
var certpub =fs.readFileSync('././publickeyhex.txt');
var notify = require('./config/notification');
//console.log(certpub);
function authenticate(req,res,next) {
    var accept = function(state){
        if(state == true){
            next();
        } else {
            res.json({"success" : "false" , message : "invalid session"});
        }
    }
    if (req && req.userid)
    {
       if(req.userid){
            login.authenticate(req.userid,accept);
       }
    }
     else {

    }
}
var jwtcheck = jwt({
    secret: 'mysecretkey',
    credentialsRequired: true,
    requestProperty : 'userid',
    getToken: function fromHeaderOrQuerystring (req) {
        if (req.headers.authorization && req.headers.authorization.split(' ')[0] === 'Bearer') {
            return req.headers.authorization.split(' ')[1];
        } else if (req.query && req.query.token) {
            return req.query.token;
        }
        return null;
    }
})
module.exports = function(app){

    app.post('/login.json',function(req,res){
        var email = req.body.email;
        var password = req.body.password;
        if(email && password)
        login.login(email,password,function(sbc){
            res.json(sbc);
        });
        else res.json({"success":"false"})
    });

    app.get('/logout.json',jwtcheck,function(req,res){
        var accept = function(state){
            if(state == true){
                res.json ({"success" : true, message :"logout successfully"});
            } else {
                res.json({"success" : "false" , message : "invalid session"});
            }
        }
        if(req.userid)
        login.deletesession(req.userid,accept);
        else res.json ({"success" : false, message :"logout successfully"});
    });

    app.all('/complaint.json',jwtcheck,authenticate,function(req,res){

        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({ success : status , message : object});
            else res.json({ success : status , message : object});
        }
        console.log(req.userid);
        var userid=req.userid.userid,
            start=req.query.start,
            end= req.query.end,
            sort=req.query.sort,
            type=req.query.type;
        if(!userid || !start || !end || !sort || !type )
        {
            console.log("56556");
            res.json({ success : false , message : false});
            return;
        }

        start = parseInt(start);
        end = parseInt(end);
        sort = parseInt(sort);
        type = parseInt(type);
        comp.getComplaints(userid,start,end,sort,type,accept);
    });

    app.post('/post_complaint.json',jwtcheck,authenticate,function(req,res){
        console.log(req.userid);
        console.log(req.body);
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
            res.json({"complaint": object , success : status});
            else res.json({"complaint": object , success : status});
        }
        comp.addComplaint(req.userid.userid,req.body.complaint,accept);
      //  res.set({'Authorisation': req.headers.authorization}).json({"complaint":req.body.complaint});
    });

    app.all('/getinfo.json/',jwtcheck,authenticate,function(req,res){
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({ success : status , complaint:object});
            else res.json({ success : status , complaint:object});
        }   ;
        var compid = req.query.compid;
        comp.getinfo(req.userid.userid,compid,accept);
       // res.set({'Authorisation': req.headers.authorization}).json({"complaint":req.query.complaintid});
    });

    app.all('/vote.json',jwtcheck,authenticate,function(req,res){
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({ success : status});
            else res.json({ success : status});
        }
        var votetype,complaint_id;
        if (req.query.votetype == "up")  votetype =true ; else if (req.query.votetype == "down") votetype = false ;
        else{
            res.set({'Authorisation': req.headers.authorization}).json({"success" : "false"});
            return;
        }
        complaint_id = req.query.compid;
        comp.voteComplaint(votetype,req.userid.userid,complaint_id,accept);
       // res.set({'Authorisation': req.headers.authorization}).json({"complaint":req.query.complaintid});
    });

    app.all('/generate_tag',jwtcheck,authenticate,function(req,res){
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({ success : status});
            else res.json({ success : status});
        }
        var userid= req.userid.userid,compid=req.query.compid;
        var i= 0;
        var taggedusers =[];
        while(req.query['taggedfriend' +(i+1)]){
            taggedusers[i]=req.query['taggedfriend' +(i+1)];
            i++;
        }
        console.log(taggedusers);
        comp.tagwith(taggedusers,userid,compid,accept);
    });

    app.all('/remove_tag',jwtcheck,authenticate,function(req,res){
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({ success : status});
            else res.json({ success : status});
        }
        var userid= req.userid.userid,compid=req.query.compid;
        comp.removetag(userid,compid,accept);
    });

    app.all('/updatestatus',jwtcheck,authenticate,function(req,res){
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({ success : status , message : object});
            else res.json({ success : status , message : object});
        }
        var userid= req.userid.userid,compid=req.query.compid;
        var toStatus = req.query.toStatus;
        console.log(toStatus);
        if(toStatus !=1 && toStatus != 2 ){
            res.set({'Authorisation': req.headers.authorization});
            res.json({success:false,message :"invalid status resolve"});
            return;
        }
        comp.resolveComplaint(userid,compid,toStatus,accept);
    });

    app.post('/feedback',jwtcheck,authenticate,function(req,res){
       var feedbackid = req.query.fid;
       var userid = req.userid.userid;
       var feedback = req.body.feedback;
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({ success : status , message : object});
            else res.json({ success : status , message : object});
        }
       comp.resolveFeedback(feedbackid,feedback,userid,accept);
    });


    app.all('/post_comment',jwtcheck,authenticate,function(req,res){
        var comment = req.body.comment;
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({ success : status , message : object});
            else res.json({ success : status , message : object});
        }
        var compid= req.query.compid,userid=req.userid.userid;
        comp.setComment(comment,compid,userid,accept);
    });

    app.all('/validatesession',jwtcheck,authenticate,function(req,res){

        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({ success : status , message : object});
            else res.json({ success : status , message : object});
        }
        console.log(req.userid);
        if(req.userid){
        var userid=req.userid.userid;
        login.getUserdata(userid,accept);}
        else{
            res.set({'Authorisation': req.headers.authorization}).json({"success":"false"});

        }
        console.log(req.body);
        console.log(req.headers);
        console.log(req.query);
        console.log(req.headers.authorisation);
    });

    app.all('/cancelcomplaint',jwtcheck,authenticate,function(req,res){
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({ success : status , message : object});
            else res.json({ success : status , message : object});
        }
        var userid=req.userid.userid;
        var compid= req.query.compid;
        console.log(userid+compid);
        comp.cancelcomplainRequest(userid,compid,accept);
    });
    app.all('/addUser',jwtcheck,authenticate,function(req,res){
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({"complaint": object , success : status});
            else res.json({"complaint": object , success : status});
        }
        login.addUser(req.userid.userid,req.body.user,accept);
    })

    app.all('/getNotifications.json',jwtcheck,authenticate,function(req,res){
        var accept = function(status,object){
            res.set({'Authorisation': req.headers.authorization});
            if(status==true)
                res.json({"message": object , success : status});
            else res.json({"message": object , success : status});
        }
        notify.getNotifications(req.userid.userid,accept);
    })


    app.all('/markread',jwtcheck,authenticate,function(req,res){

    })

}



