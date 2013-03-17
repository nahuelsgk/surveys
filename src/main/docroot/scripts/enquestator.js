(function(){
    $('#create_survey_form').ajaxForm(function() {
        //TODO check until >= since
        if ($('#create_survey_form').attr('method') == 'PUT') {
            date = new Date();
            $('h2').text('Survey updated at ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds   ());
        } else {
            $('#create_survey_form').attr('method', 'PUT');
            $('#create_survey_form #submit_btn').attr('value','Edit');

            $('h2').text('Survey sent');
            $('#survey_description').text('Click the edit button to update it');
        }
        console.log('Sent Form with '+$('#create_survey_form').attr('method')+' Method');

    });
})();

