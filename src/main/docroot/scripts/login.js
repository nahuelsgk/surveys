var currentUser;

function logIn() {
    var user = $('#username').val();
    var pwd = $('#pwd').val();
    //console.log('Login user: '+user+' with pwd: '+pwd);
    if (!isValidField(user) || !isValidField(pwd)) {
        badlyLogged();
    }
    else {
        //fer la crida a la API i tractar el resultat
        $('#loginfeedback').attr('class','hidden');
        console.log('Login correct');
        correctlyLogged(user);
    }

}

function correctlyLogged(username) {
    $('.login').find('h1').text(username);
    $('.login').find('form').attr('class','hidden');
    $('#login-help').attr('class','hidden');
    $('#logout').attr('class','logout');
    $('#logoutBtn').click(function() {
        logOut();
    });
}

function logOut() {
    $('.login').find('h1').text('Login');
    $('.login').find('form').attr('class','login');
    $('#username').val('');
    $('#pwd').val('');
    $('#login-help').attr('class','login-help');
    $('#logout').attr('class','hidden');
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

function signIn() {
    var username = $('#newusername').val();
    var pwd = $('#newpwd').val();
    var pwd2 = $('#newpwd2').val();
    var email = $('#newemail').val();
    if (!isValidField(username) || !isValidField(pwd)
        || !isValidField(pwd2) || !isValidField(email) || pwd !== pwd2) {
         $('#userNotification').text('Error creating your account');
         $('#userNotification').attr('class','error');
    }
    else {
       var user = new User(username, pwd, email);
       currentUser = user;
       console.log(JSON.stringify(user));
       var loc = '/api/user';
       sendEvent(loc, 'POST', user, null, userCreated);
    }
}

function userCreated(data, location) {
    $('#userNotification').text('Your account has been created');
    $('#userNotification').attr('class','success');
    $('#signInDiv').find('h1').text('Welcome '+currentUser.userName);
    $('#signInDiv').find('form').remove();
    var idUser = location.replace('/api/user/','');
    console.log('ID: '+idUser);
}