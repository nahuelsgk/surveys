var DAYS_WITH_COOKIE = 10;
var currentUser;

$('#username').focusout(function() {
       $('#loginfeedback').attr('class','hidden');
});

function logIn() {
    var user = $('#username').val();
    var pwd = $('#pwd').val();
    //console.log('Login user: '+user+' with pwd: '+pwd);
    if (!isValidField(user) || !isValidField(pwd)) {
        badlyLogged();
    }
    else {
        //fer la crida a la API i tractar el resultat
        var loc = '/api/login';
        var user = new User(user,pwd);
        currentUser = user;
        sendEvent(loc, 'POST', user, null, loginSucceed, badlyLogged);
        $('#loginfeedback').attr('class','hidden');
        console.log('Login correct');
    }

}

function loginSucceed(data, location) {
    var idUser = location.replace('/api/user/','');
    if (idUser!=null && idUser!="") {
        correctlyLogged(currentUser.userName, true);
        if ($('#remember_me').is(':checked')) {
            setCookie(idUser,currentUser.userName, DAYS_WITH_COOKIE);
        }
        else setCookie(idUser,currentUser.userName);
    }
}

function correctlyLogged(username, sayHello) {
    $('.login').find('h1').text(username);
    $('.login').find('form').attr('class','hidden');
    $('#login-help').attr('class','hidden');
    $('#logout').attr('class','logout');
    $('#logoutBtn').click(function() {
        logOut();
    });
    $('#listSurveys').show();
    if (sayHello) sayWelcome();
}

function logOut() {
    $('.login').find('h1').text('Login');
    $('.login').find('form').attr('class','login');
    $('#username').val('');
    $('#pwd').val('');
    $('#login-help').attr('class','login-help');
    $('#logout').attr('class','hidden');
    sayGoodBye();
    $('#listSurveys').hide();
    document.cookie=null;
    var userCookie = getCookie();
    if (userCookie != null) {
        userCookie.expires = null;
        resetCookie(userCookie);
    }
}

function badlyLogged() {
    $('#username').focus();
    $('#loginfeedback').attr('class','');
}


function isValidField(text) {
    if ((text == "") || text.length < 3 || text.length > 15 || text == null) return false;
    else return true;
}

function showSignIn() {
    cleanView(currentView);
    currentView = SIGN_IN;
    var form = $('#signInDiv').clone();
    form.attr('class','signInContainer');
    $('#content').append(form);
}

function cleanLabelFields() {
    $('#lusername').attr('class','');
    $('#lpwd').attr('class','');
    $('#lpwd2').attr('class','');
    $('#lemail').attr('class','');
}

function signIn() {
    cleanLabelFields();
    var username = $('#newusername').val();
    var pwd = $('#newpwd').val();
    var pwd2 = $('#newpwd2').val();
    var email = $('#newemail').val();
    var error = false;
    if (!isValidField(username)) {
        $('#lusername').attr('class','fieldError');
        error = true;
    }
    else if (!isValidField(pwd) || pwd !== pwd2) {
        $('#lpwd').attr('class','fieldError');
        error = true;
    }
    else if (!isValidField(pwd2)) {
        $('#lpwd2').attr('class','fieldError');
        error = true;
    }
    else if (!isValidEmail(email)) {
        $('#lemail').attr('class','fieldError');
        error = true;
    }
    else {
       var user = new User(username, pwd, email);
       currentUser = user;
       //console.log(JSON.stringify(user));
       var loc = '/api/user';
       sendEvent(loc, 'POST', user, null, userCreated, userCreatedFail);
    }

    if (error) {
        $('#userNotification').text('Error creating your account. Please check the red fields.');
        $('#userNotification').attr('class','error');
    }
}

function userCreatedFail() {
    $('#userNotification').text('There is another user with that username.');
    $('#userNotification').attr('class','error');
}

function userCreated(data, location) {
    $('#userNotification').text('Your account has been created');
    $('#userNotification').attr('class','success');
    var idUser = location.replace('/api/user/','');
    if (idUser!=null && idUser!="") {
        correctlyLogged(currentUser.userName, true);
        setCookie(idUser,currentUser.userName, DAYS_WITH_COOKIE);
    }
}

function sayWelcome() {
    cleanView(currentView);
    currentView = WELCOME_VIEW;
    var container = $('#signInDiv').clone();
    container.attr('class','signInContainer');
    container.find('form').remove();
    container.find('h1').text('Welcome '+currentUser.userName);
    var info = $('#userState').clone();
    info.attr('class','');
    info.find('#totalSurveys').text('Total surveys: '+currentUser.surveys.length);
    container.append(info);
    $('#content').append(container);
}

function sayGoodBye() {
    cleanView(currentView);
    currentView = WELCOME_VIEW;
    var container = $('#signInDiv').clone();
    container.attr('class','signInContainer');
    container.find('form').remove();
    container.find('h1').text('Goodbye!');
    $('#content').append(container);
}

function getCookie() {
    var c = document.cookie;
    //console.log('retrieved: '+c);
    if (c != null) {
        try {
            var json = $.parseJSON(c);
            if (json != null) {
                var userCookie = new UserCookie(json.id,json.username,json.expires);
                return userCookie;
            }
        }
        catch(e) {
            return null;
        }
    }
    return null;
}

function setCookie(id,username,exdays) {
    var date = null;
    if (exdays !== 'undefined') {
        var exdate=new Date();
        exdate.setDate(exdate.getDate() + exdays);
        date = ((exdays==null) ? "" : exdate.toUTCString());
    }
    var myCookie = new UserCookie(id,username,date);
    document.cookie = JSON.stringify(myCookie);
    //console.log("cookie: "+document.cookie);
}


function checkCookie() {
    var userCookie=getCookie();
    if (userCookie!=null && userCookie.expires != "" && userCookie.id != null) {       //el client ja està loguejat
        correctlyLogged(userCookie.username, false);
    }
    else {  // el client no esta loguejat
        $('#listSurveys').hide();
        if (userCookie!=null) {
            resetCookie(userCookie);
        }
    }
}

function resetCookie(userCookie) {
    userCookie.username = null;
    userCookie.id = null;
    userCookie.expires = null;
    document.cookie = JSON.stringify(userCookie);
    //console.log("reset cookie: "+document.cookie);
}

function isValidEmail(mail) {
 if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(mail)) {
    return (true)
 }
 return (false)
}