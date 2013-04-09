/*send event generic: uri, method, json data, done callback, success callback*/
function sendEvent(uri, method, data, done, success){
    var request = $.ajax({
        url: uri,
        type: method,
        data: JSON.stringify(data),
        dataType: 'json',
        success: function(data){
          if(success){
            success(data);
              }
        }
    });

    request.fail(function() {
        console.log('request failed :/');
    });
	if(done){request.done(done(request));}
};