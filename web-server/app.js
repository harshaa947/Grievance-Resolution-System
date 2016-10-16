var express = require('express'),
  path = require('path'),
  logger = require('morgan'),
  favicon = require('serve-favicon'),
    errorhandler = require('errorhandler'),

  bodyparser = require('body-parser'),
  serveIndex = require('serve-index'),
    util=require('util');
   fs = require ('fs');
var app = express();
 app.set('views', path.join(__dirname, 'views'));
 app.set('view engine', 'jade');
var routes = require('./libraries/routes.js');
/*var crypto = require('crypto');

var prime_length = 1500;
var diffHell = crypto.createDiffieHellman(prime_length);

diffHell.generateKeys('base64');
console.log("Public Key : " ,diffHell.getPublicKey('base64'));
console.log("Private Key : " ,diffHell.getPrivateKey('base64'));
console.log("Public Key : " ,diffHell.getPublicKey('hex'));
console.log("Private Key : " ,diffHell.getPrivateKey('hex'));
fs.writeFile('publickeybase64.txt',diffHell.getPublicKey('base64'),function(err){
    if(err)
    console.log('failed write');
});
fs.writeFile('privatekeybase64.txt',diffHell.getPrivateKey('base64'),function(err){
    if(err)
        console.log('failed write');
});
fs.writeFile('publickeyhex.txt',diffHell.getPublicKey('hex'),function(err){
    if(err)
        console.log('failed write');
});
fs.writeFile('privatekeyhex.txt',diffHell.getPrivateKey('hex'),function(err){
    if(err)
        console.log('failed write');
});*/

__dirname = path.resolve(path.dirname());


app.set('port', process.env.PORT || 8000);


app.use(favicon(path.join('public', 'favicon.ico')));
app.use('/img',express.static('img'))
app.use('/public', serveIndex(
'/public',
  {'icons': true}
));
/*app.use('/img', serveIndex(
    '/img',
    {'icons': true}
));*/
app.use(express.static('public'));
//app.use(express.static('img'));
//app.use('/img',express.static('img'))
//var uri = 'mongodb:////localhost:27017/example'
app.use(errorhandler());
app.use(bodyparser({
    keepExtensions: true,
    limit: 1024 * 1024 * 1024 * 500,
    defer: true
}));
routes(app);
var http = require('http').Server(app);

http.listen(3000, "0.0.0.0");



//var server = app.listen(app.get('port'),"0.0.0.0");
