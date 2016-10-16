/**
 * Created by Harsh on 25-03-2016.
 */
var rand = require('csprng');

exports.dbuser = {
name : 'user',
url :'mongodb://localhost:27017/assn2'
}
exports.dbcomplaint ={
    name : 'complaints',
url :'mongodb://localhost:27017/assn2'
}
exports.dbnotifications ={
    name : 'notifications',
url :'mongodb://localhost:27017/assn2'
}
notificationtypes =["tagging","feedback generated / complaint resolved"," feedback submitted"];
exports.sample={
   'user': [

       {id:'cs1140255', uname:'Vikas chouhan' , entno:'2014CS10255', catg:'1203',hpass:'',salt:'',comp:' ',not: ' ',tcomp:'',email:'vikaschouhan@yahoo.com',dep:'CHE',hostel:'1203'},
          {id:'cs1140256', uname:'Kushagra chouhan' , entno:'2014CS10256', catg:'2104',hpass:'',salt:'',comp:' ',not: ' ',tcomp:'',email:'kushagrachouhan@gmail.com',dep:'CSE',hostel:'2104'},
             {id:'cs1140222', uname:'Harshil meena' , entno:'2014CS10222', catg:'3109',hpass:'',salt:'',comp:' ',not: ' ',tcomp:'',email:'harshilmeena@gmail.com',dep:'CSE',hostel:'3109'},
             {id:'cs1140219', uname:'Srimant' , entno:'2014CS10219', catg:'3311',hpass:'',salt:'',comp:' ',not: ' ',tcomp:'',email:'srimant@gmail.com',dep:'CSE dual',hostel:'3111'},
             {id:'me2140762', uname:'Hemant' , entno:'2014ME20762', catg:'1512',hpass:'',salt:'',comp:' ',not: ' ',tcomp:'',email:'hemant@gmail.com',dep:'ME',hostel:'1512'},
            {id:'me2014774', uname:'Yash' , entno:'2014ME20774', catg:'1210',hpass:'',salt:'',comp:' ',not: ' ',tcomp:'',email:'yash@gmail.com',dep:'ME',hostel:'1210'}


   ],
   'complaints' : [
       {id:'',prio:'' , detail:'',tagged:'',date:'',filed:'',attacm:'',status:'',pvotes:'',nvotes:'',label:''},
       {id:'',prio:'' , detail:'',tagged:'',date:'',filed:'',attacm:'',status:'',votes:'',label:''},
       {id:'',prio:'' , detail:'',tagged:'',date:'',filed:'',attacm:'',status:'',votes:'',label:''},
       {id:'',prio:'' , detail:'',tagged:'',date:'',filed:'',attacm:'',status:'',votes:'',label:''},
       {id:'',prio:'' , detail:'',tagged:'',date:'',filed:'',attacm:'',status:'',votes:'',label:''},
       {id:'',prio:'' , detail:'',tagged:'',date:'',filed:'',attacm:'',status:'',votes:'',label:''},
       {id:'',prio:'' , detail:'',tagged:'',date:'',filed:'',attacm:'',status:'',votes:'',label:''},
       {id:'',prio:'' , detail:'',tagged:'',date:'',filed:'',attacm:'',status:'',votes:'',label:''},
       {id:'',prio:'' , detail:'',tagged:'',date:'',filed:'',attacm:'',status:'',votes:'',label:''}
   ],
    'notifications':[
        {id:'',detail:'',users:'',date:''},
        {id:'',detail:'',users:'',date:''},
        {id:'',detail:'',users:'',date:''},
        {id:'',detail:'',users:'',date:''},
        {id:'',detail:'',users:'',date:''},
    ]
}