(function(){
    $(".button").click(function() {

//        var dataString = 'name='+ name + '&email=' + email + '&phone=' + phone;
//        //alert (dataString);return false;
//        var title = $("input#title").val();
//        var since = $("input#since").val();
//        var until = $("input#until").val();
//        var dataString = 'title='+ title + '&since=' + since + '&until=' + until;
//
//        $.ajax({
//        type: "POST",
//        url: "/survey",
//        data: dataString,
//        success: function() {
//            $('#create_survey_form').html("<div id='message'></div>");
//            $('#message').html("<h2>Create Survey Form Submitted!</h2>")
//            .append("<p>We will be in touch soon.</p>")
//            .hide()
//            .fadeIn(1500, function() {
//                $('#message').append("<img id='checkmark' src='images/check.png' />");
//            });
//        }
//        });
        return false;
    })
    $('#create_survey_form').ajaxForm(function() {
        alert("Thank you for your comment!");
    });




})();

