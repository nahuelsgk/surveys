var currentView;
var CREATE_SURVEY = 0;
var LIST_SURVEYS = 1;
var questionCounter = 1;
var currentSurvey;
var surveys = new Object(); // Map for all the surveys retrieved

/* Domain Objects */
function Survey () {
    switch (arguments.length) {     //multiple constructors
        case 3:
            this.title = arguments[0];
            this.since  = arguments[1];
            this.until = arguments[2];
            this.questions = new Array();
        break;
        case 1:
            var json = arguments[0];
            this.title = json.title;
            this.since  = json.since;
            this.until = json.until;
            this.id = json.id;
            this.questions = new Array();
        break;
        default : /*NOP*/
    }

    this.listMe = function() {
        var item = $('#listSurveyItem').clone(true);
        item.attr('id','');
        item.attr('class','surveyItem'); // remove the hidden class
        item.attr('name', this.id);
	item.attr('data-since',this.since);
	item.attr('data-until',this.until);
        item.text(this.title);
        return item;
    }
}

var Question = function(name, type, value){
  this.name  = name;
  this.type  = type;
  this.value = value;
}

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

function renderLastChangeNotification(){
    date = new Date();
    $('h2').text('Survey updated at ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds   ());
}

function submitCreateSurvey(){
    var form = $(this);
	var data = {title : $('#title').val(), since : $('#since').val(), until : $('#until').val()};
	var method = 'POST';
	var done_callback = function (request) {
        console.log('sent request, method : ' + method);
        currentSurvey = new Survey( $('#title').val(), $('#since').val(), $('#until').val());
        if (form.attr('method') === 'PUT') {
            renderLastChangeNotification();
        }
        else {
            form.attr('method', 'PUT');
            form.attr('action', request.getResponseHeader('location'));
            $('#create_survey_form input[type=submit]').attr('value','Edit');
            $('h2').text('Survey sent');
            $('#survey_description').text('Click the edit button to update it');
            $('#questions').show();
            $('#add_question').show();
        }
    };
	sendEvent('/api/survey', method, data, done_callback, null);
    return false;
};

function renderAddNewQuestion(){
    $('#question_list').append('<li><label for="question[' + questionCounter + ']">Question ' + questionCounter + '</label>' +'<input type="text" name="question[' + questionCounter + '] class="question" placeholder="Write your question here" required /></li>');
    questionCounter++;
}


function renderListSurveys(listOfSurveys) {
    var surveysHtmlIni = $('<div id="surveysList">');
    var header = $('<h2 id="contentTitle">Surveys list</h2>');
    surveysHtmlIni.append(header);
    var list = $('<ul/>');
    surveysHtmlIni.append(list);
    var count = 0;
    var obj = $.parseJSON(listOfSurveys);
    var size = obj.length;
    for (var i = 0; i < size; ++i) {      // iteration over the all survey JSONs
        var survey = new Survey(obj[i]);
        if (typeof survey === 'undefined') {
            console.log('undefined survey');
        }
        else {
            var key = survey.id;
            surveys[key] = survey;
            list.append(survey.listMe());
            count = count + 1;
        }
    }
    if (count == 0) {
        var noSurvey = $('<span>No surveys today</span>');
	    surveysHtmlIni.append(noSurvey);
    }
    $('body').on('click', '.surveyItem', function(){
      var id = $(this).attr('name');
      sendEvent('/api/survey/'+id, 'GET', null, null, null);
    });


    displayContent(surveysHtmlIni, LIST_SURVEYS);
}

function listSurveys() {
    sendEvent('/api/surveys', 'GET', null, null, renderListSurveys);
}


function createSurvey() {
    cleanView(currentView);
    $('#dynamicContent').show();
    currentView = CREATE_SURVEY;
}


function displayContent(html, view)  {
    cleanView(currentView);
    $('#content').append(html);
    currentView = view;
}

function cleanView(view) {
    //console.log("Removing view: "+view);
    switch(view) {
        case CREATE_SURVEY:
            $('#dynamicContent').hide();
            break;
        case LIST_SURVEYS:
            $('#surveysList').remove();
            break;
    }
}

function showSurvey(id) {
    console.log('in');
    console.log("Showing survey: "+id);
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
    currentView = 0;
    $('#add_question').click(renderAddNewQuestion);
});


