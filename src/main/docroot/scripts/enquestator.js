var currentView;
var CREATE_SURVEY = 0;
var LIST_SURVEYS = 1;
var EDIT_SURVEY = 2;

var surveys = new Object();

function renderLastChangeNotification(){
    date = new Date();
    $('h2').text('Survey updated at ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds   ());
}

function editSurvey() {

    cleanView(currentView);
    renderEditSurvey();
}

function displaySurvey(request) {
    //var obj = $.parseJSON(request);
    //console.log("size: "+obj.length);
    var survey = new Survey(request);
    console.log("ID: "+survey.id);
}

function surveyCreated(uri,location) {
    if (location !== 'none') {
        console.log("URI: "+location);
        $('#editSurvey').attr('class','');
        sendEvent(location, 'GET', null, null, displaySurvey);
    }
}

function submitCreateSurvey(){
	var data = {title : $('#title').val(), since : $('#since').val(), until : $('#until').val()};
	var method = 'POST';
	sendEvent('/api/survey', method, data, null, surveyCreated);
    return false;
};


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
            list.append(listSurvey(survey));
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


function listSurvey(survey) {
    var item = $('#listSurveyItem').clone(true);
    item.attr('id','');
    item.attr('class','surveyItem'); // remove the hidden class
    item.attr('name', survey.id);
    item.attr('data-since',survey.since);
    item.attr('data-until',survey.until);
    item.text(survey.title);
    return item;
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

function renderNewSurveyForm() {
    initDatePicker();
    currentView = CREATE_SURVEY;
    $('#buttonSurveyCreate').click(submitCreateSurvey);
    //enableAddQuestions();
}

function displayTypeOfQuestion(idQuestion,type) {
    console.log('displaying type: '+type+" for question: "+idQuestion);
    var divNameTo = '#'+TYPE_TAG+idQuestion;
    switch(type) {
        case TYPE_TEXT:
            $(divNameTo).attr('class','hidden');
        break;
        case TYPE_CHOICE:
            $(divNameTo).attr('class','questionOptions');
        break;
        case TYPE_MULTICHOICE:
            $(divNameTo).attr('class','questionOptions');
        break;
    }
}

function addNewQuestion() {
    var question = $('#newQuestion').clone();
    question.attr('id',QUESTION_TAG+questionCounter);
    question.attr('class','question');
    $('#questionList').append(question);
    var name = SELECTOR_TAG+questionCounter;
    $('#typeSelector').attr('id',name);
    $('#'+name).change(function() {
        var idQuestion = $(this).parent().attr('id');
        idQuestion = idQuestion.replace(QUESTION_TAG,'');
        var selectorName = '#'+SELECTOR_TAG+idQuestion+' option:selected';
        displayTypeOfQuestion(idQuestion,$(selectorName).val());
    });
    var divName = TYPE_TAG+questionCounter;
    $('#typeInflator').attr('id',divName);
    ++questionCounter;
}

function enableAddQuestions() {
    var questions = $('#questions').clone();
    questions.attr('id','');
    questions.attr('class','');
    $('#dynamicContent').append(questions);
    $('#add_question').click(function() {
        addNewQuestion();
    });
}

$(document).ready(function($) {
    renderNewSurveyForm();
});


