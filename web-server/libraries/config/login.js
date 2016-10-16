var crypto = require('crypto');
var rand = require('csprng');
var user = require('./models').Users;
var jsonwebtoken = require('jsonwebtoken');
var _ = require('underscore');
var certpri = fs.readFileSync('././privatekeyhex.txt');  // get private key
var generate_key = function(user) {
    var sha = crypto.createHash('sha256');
    sha.update(Math.random().toString() + user);
    return sha.digest('hex');
};
exports.login = function(email,password,callback) {

    user.findOne({email: email},function(err,users){
        if(err)
        { callback({success:"false"});
        return;
        }
        if(users ){
            //console.log(users);
            /*if(!users[0])
            {
                callback({success:"false"});
                return;
            }*/
            var temp = users.salt;
            var hash_db = users.hpass;
            var id = users.id;
            var newpass = temp + password;
            var hashed_password = crypto.createHash('sha512').update(newpass).digest("hex");
            //console.log(hashed_password);
            var userdata = users.userdata;
            //console.log(hash_db);
            if(hash_db == hashed_password){
             //   var token = jsonwebtoken.sign({ 'userid':id}, certpri, { algorithm: 'RS256'});
                var sessionid = generate_key(id);
                user.updateOne({email: email},{$push : { "sessions" : sessionid  }},function(err,result){
                    if(err)
                    {
                        callback({"success" : false , "message" : "unable to generate id"});
                        return;
                    }
                    else if (result.result.n === 1)
                    {
                        var token = jsonwebtoken.sign({ 'userid':id,sessionkey:sessionid}, 'mysecretkey');
						var userdata = getUser(users);
                        callback({"success":true,'id':id,'userdata':userdata ,'token' : token });
                    }
                    else
                        callback({"success" : false , "message" : "unable to generate id"});
                });


            }else{

                callback({'response':"Invalid Password",'res':false});

            }
        }else {

            callback({'response':"User not exist",'res':false});
        }
    });
}
exports.authenticate = function(users,callback){
  /*  var decoded ;
    jsonwebtoken.verify(token,"mysecretkey",fumction(err,decoded){
        if(error)
        callback();

    })*/
    //console.log(users);
    user.findOne({id:users.userid},{maxTimeMS : 10000},function(err,result){
        if(err)
        {   console.log(err);
            callback(false);
        return;}
        else if(result){
            console.log(users.userid);
           // console.log(result + users.sessionkey);
            callback(_.indexOf(result.sessions,users.sessionkey)!=-1);
        }
    });
}
exports.deletesession = function(users,callback){
    user.updateOne({id:users.userid},{$pull : {sessions : users.sessionkey}},function(err,result){
        if(err)
        {callback(false);
            return;}
        else if(result){
            //console.log(result);
            if(result.result.n===1)
            callback(true);
            else callback(false);
        }
    });

}
exports.getUserdata = function(userid,callback){
    userone=  user.findOne({id:userid},{maxTimeMS : 10000},function(err,result){
        if(err)
        {callback(false,false);
            return;}
        else if(result ){
            //if (result.catg != 1)
            var user = getUser(result);
            callback(true,user);
            return;
        }
    });
}
exports.addUser = function(userid,userb,callback){
    user.findOne({id:userid},function(err,result){
        console.log(result);
        if(err || !result){
            callback(false,false);
        }
        else if(result.catg)
        {
            var userc = JSON.parse(userb);
            user.insertOne(userc,function(err,result){
                if(err || !result)
                {
                    callback(false,"unable to add data");
                }
                else {
                    callback(true,true);
                }
            })
        }
        else callback(false,"Unable to access");
    })

}
function getUser(user){
    var toreturn= {};
    toreturn.uname = user.uname;
    toreturn.entno = user.entno;
    toreturn.catg = user.catg;
    toreturn.comp = user.comp;
    toreturn.tomp = user.tcomp;
    toreturn.email = user.email;
    toreturn.hostel = user.hostel;
    toreturn.dept = user.dept;
    return toreturn;
}