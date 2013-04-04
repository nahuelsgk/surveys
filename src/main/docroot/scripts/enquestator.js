var createSurveyHTML;
var surveysListHTML;
var currentView;
var CREATE_SURVEY = 0;
var LIST_SURVEYS = 1;
var questionCounter = 1;

/*send event generic: uri, method, json data, done callback*/
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
        request.done(done(request));
};

function submitCreateSurvey(){
        var form = $(this);
	var data = {title : $('#title').val(), since : $('#since').val(), until : $('#until').val()};
	var method = 'POST';
	var success_callback = function (request) {
            console.log('sent request, method : ' + method);

            if (form.attr('method') === 'PUT') {
                date = new Date();
                $('h2').text('Survey updated at ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds   ());
            } else {
                form.attr('method', 'PUT');
                form.attr('action', request.getResponseHeader('location'));
                $('#create_survey_form input[type=submit]').attr('value','Edit');

                $('h2').text('Survey sent');
                $('#survey_description').text('Click the edit button to update it');
                $('#questions').show();
		$('#add_question').show();
            }
        };
	sendEvent('/api/survey', method, data, success_callback, null);
        return false;
};

function renderAddNewQuestion(){
    $('#question_list').append('<li><label for="question[' + questionCounter + ']">Question ' + questionCounter + '</label>' +'<input type="text" name="question[' + questionCounter + '] class="question" placeholder="Write your question here" required /></li>');
    questionCounter++;
}

function renderListSurveys(listOfSurveys) {
    var surveysHtmlIni = '<div id="surveysList">';
    var header = '<h2 id="contentTitle">Surveys list</h2><ul>';
    surveysHtmlIni += header;
    var surveysHtmlEnd = '</ul></div>';
    var count = 0;
    //console.log("JSON: "+json);
    var obj = $.parseJSON(listOfSurveys);
    var size = obj.length;
    console.log("#surveys: "+size);
    for (var i = 0; i < size; ++i) {
        $.each(obj[i], function(id, v) {
            if (id == "title") {
                //console.log("TITLE: "+v);
	        var html = '<li>'+v+'</li>';
	        surveysHtmlIni = surveysHtmlIni + html;
	        count = count +1; 
	    }
        });
    }
    if (count == 0) {
        var html = 'No surveys today';
	surveysHtmlIni = surveysHtmlIni + html;
    }
    surveysHtmlIni = surveysHtmlIni + surveysHtmlEnd;
    //console.log("HTML: "+surveysHtmlIni);

    displayContent(surveysHtmlIni, LIST_SURVEYS);
}

function listSurveys() {
    sendEvent('/api/surveys', 'GET', null, null, renderListSurveys)
}

function createSurvey() {
    cleanView(currentView);
    $('#dynamicContent').show();
    currentView = CREATE_SURVEY;
}

/*
  This function fills the div "content" with a H2 header containing the arg "title" and a html bloc attached below.
*/
function displayContent(html, view)  {
   cleanView(currentView);
   $('#content').append(html);
   currentView = view;
}

function cleanView(view) {
    console.log("Removing view: "+view);
    switch(view) {
        case CREATE_SURVEY:
            $('#dynamicContent').hide();
            break;
        case LIST_SURVEYS:
            $('#surveysList').remove();
            break;
    }
}

function initDatePicker() {
    //setting since and until to the current day
    d = new Date();
    today = d.toISOString().substr(0,10);

    $('#since').attr('value', today);
    $('#until').attr('value', today);

    $( "#since" ).datepicker({
        dateFormat: $.datepicker.ISO_8601,
        onClose: function( selectedDate ) {
            $( "#until" ).datepicker( "option", "minDate", selectedDate );
        }
    });
    $( "#until" ).datepicker({
        dateFormat: $.datepicker.ISO_8601,
        onClose: function( selectedDate ) {
            $( "#since" ).datepicker( "option", "maxDate", selectedDate );
        }
   });

}
$(document).ready(function($) {
    initDatePicker();
    $('#create_survey_form').submit(submitCreateSurvey);
    createSurveyHTML = $('#dynamicContent').clone(true);
    currentView = 0;
    $('#add_question').click(renderAddNewQuestion);
});


