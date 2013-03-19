$(document).ready(function($) {
    formatNumber = function(number) {
        return (number < 10) ?  '0' + number : number;
    }
    //setting since and until to the current day
    d = new Date();
    today = d.toISOString().substr(0,10);
    
    $('#since').attr('value', today);
    $('#until').attr('value', today);


    $('#create_survey_form').ajaxForm(function() {
        if ($('#create_survey_form').attr('method') === 'PUT') {
            date = new Date();
            $('h2').text('Survey updated at ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds   ());
        } else {
            $('#create_survey_form').attr('method', 'PUT');
            $('#submit_btn').attr('value','Edit');

            $('h2').text('Survey sent');
            $('#survey_description').text('Click the edit button to update it');
        }
        console.log('Sent Form with '+$('#create_survey_form').attr('method')+' Method');

    });

    $('#since').change(function() {
        if ($('#since').attr('value') > $('#until').attr('value')) {
            $('#until').attr('value', $('#since').attr('value'));
        }
    });
    $('#until').change(function() {
        if ($('#since').attr('value') > $('#until').attr('value')) {
                $('#since').attr('value', $('#until').attr('value'));
        }
    });
});

function listSurveys() {
    $.ajax({
        url:"/api/surveys",
        dataType:"json",
        success: function(json) {
            $('h2').text('Surveys list');
            $('#survey_description').text("");

            $('#create_survey_form').fadeOut();
        }
    });
}

function createSurvey() {
    $('h2').text('Create survey');
    $('#survey_description').text("Start now creating your new survey filling the gaps below.");
    $('#create_survey_form').fadeIn();
}

