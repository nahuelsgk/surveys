//APP constants here
var QUESTION_TAG = 'question_';
var SELECTOR_TAG = 'selector_';
var TYPE_TAG ='divType_';
var AREA_TAG ='textArea_';
var RADIO_TAG = 'radio_';
var CHECKBOX_TAG = 'checkBox_';
var OPTION_TAG ='option_';
var DELETE_TAG ='delete_';
var TYPE_TEXT = 'text';
var TYPE_CHOICE = 'choice';
var TYPE_MULTICHOICE = 'multichoice';
var SEPARATOR = '_';


/*send event generic: uri, method, json data, done callback, success callback*/
function sendEvent(uri, method, data, done, success, error){
    var request = $.ajax({
        url: uri,
        type: method,
        data: JSON.stringify(data),
        dataType: 'json',
        success: function(data, status, xhr){
            var location = xhr.getResponseHeader('Location') || 'none';
            if(success){
                success(data,location);
            }
        },
        error: function(data, status, xhr){
            if (error) {
                error();
            }
        }
    });
    var userCookie = getCookie();
    if (userCookie != null) {
        request.setRequestHeader("cookie", JSON.stringify(userCookie));
    }
    request.fail(error);
	if(done){
	    request.done(done(request));
	}
};

function UserCookie() {
    switch (arguments.length) {
        case 3:
            this.id = arguments[0];
            this.username = arguments[1];
            this.expires = arguments[2];
        break;
    }
}

