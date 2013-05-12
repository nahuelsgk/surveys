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