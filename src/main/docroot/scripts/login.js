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
    correctlyLogged(currentUser.userName);
}

function correctlyLogged(username) {
    $('.login').find('h1').text(username);
    $('.login').find('form').attr('class','hidden');
    $('#login-help').attr('class','hidden');
    $('#logout').attr('class','logout');
    $('#logoutBtn').click(function() {
        logOut();
    });

   sayWelcome();
}

function logOut() {
    $('.login').find('h1').text('Login');
    $('.login').find('form').attr('class','login');
    $('#username').val('');
    $('#pwd').val('');
    $('#login-help').attr('class','login-help');
    $('#logout').attr('class','hidden');
    sayGoodBye();
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
    else if (!isValidField(email)) {
        $('#lemail').attr('class','fieldError');
        error = true;
    }
    else {
       var user = new User(username, pwd, email);
       currentUser = user;
       //console.log(JSON.stringify(user));
       var loc = '/api/user';
       sendEvent(loc, 'POST', user, null, userCreated);
    }

    if (error) {
        $('#userNotification').text('Error creating your account. Please check the red fields.');
        $('#userNotification').attr('class','error');
    }
}

function userCreated(data, location) {
    correctlyLogged(currentUser.userName);
    $('#userNotification').text('Your account has been created');
    $('#userNotification').attr('class','success');
    var idUser = location.replace('/api/user/','');
    console.log('ID: '+idUser);
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