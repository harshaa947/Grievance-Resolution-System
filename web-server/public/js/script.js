/**
 * Created by Harsh on 27-02-2016.
 */
function showLogin(){
var loginbox = document.getElementById("loginbox");
    //loginbox.style.color="pink";
    if(loginbox.classList)
        loginbox.className = loginbox.className.replace('hidden', 'show');
}
function closeLogin(){
    var loginbox = document.getElementById("loginbox");
    if(loginbox.classList)
        loginbox.className = loginbox.className.replace('show', 'hidden');
}