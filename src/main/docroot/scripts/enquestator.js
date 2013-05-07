var currentView;
var CREATE_SURVEY = 0;
var LIST_SURVEYS = 1;
var EDIT_SURVEY = 2;
var ANSWER_SURVEY = 3;
var currentSurvey;
var surveys = new Object();

function renderLastChangeNotification(){
    date = new Date();
    $('h2').text('Survey updated at ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds   ());
}

function renderCreateForm(){
     $('#dynamicContent').empty();
    var template_form = $('#surveyForm').clone();
    template_form.attr('class', '');
    template_form.attr('id', 'editForm');
    template_form.find('form').attr('id', 'create_survey_form');
    $('#dynamicContent').append(template_form);
    $('#dynamicContent').show();
    initDatePicker();
    currentView = CREATE_SURVEY;
    $('#buttonSurveyCreate').click(submitCreateSurvey);
    //Still needs a update action
}

function renderEditSurvey(survey, createdNow){
    questionCounter = 0;
    cleanView(currentView);
    currentView = EDIT_SURVEY;
    $('#dynamicContent').empty();
    var template_form = $('#surveyForm').clone();
    template_form.find('#contentTitle').html('Update your survey');
    template_form.find('#survey_description').html('Fullfill your info to update');
    template_form.attr('class', '');
    template_form.attr('id', 'editForm');
    template_form.find('form').attr('id', 'edit_survey_form');
    //console.log(survey);
    template_form.find('#title').val(survey.title);
    template_form.find('#since').val(survey.until);
    template_form.find('#until').val(survey.since);
    template_form.find('#buttonSurveyCreate').attr('value', 'Update');
    template_form.find('#buttonSurveyCreate').attr('id', 'buttonEditSurvey');
    $('#dynamicContent').append(template_form);
    $('#dynamicContent').show();
    if (createdNow) {
        $('#notification').text('Your survey has been created');
        $('#notification').attr('class','success');
    }
    else {
        $('#notification').attr('class','hidden');
    }
    initDatePicker();
    enableAddQuestions();
    if (typeof survey.questions !== 'undefined') {
        console.log('rendering ['+survey.questions.length +'] questions...');
        for(i = 0; i < survey.questions.length; ++i) {
            //console.log('original ID: '+survey.questions[i].id);
            addQuestion(survey.questions[i]);
        }

    }
    $('#buttonEditSurvey').hide();
    $('#updateSurvey').click(function(){
        updateSurvey();
    });
}

function addQuestion(q) {

    q.id = questionCounter;  //TODO: millorar

    var question = $('#newQuestion').clone();
    question.attr('id',QUESTION_TAG+q.id);
    question.attr('class','question');
    $('#questionList').append(question);
    var name = SELECTOR_TAG+q.id;
    var deleteTag = DELETE_TAG+q.id;
    $('#typeSelector').attr('id',name);
    $('#questionArea').attr('id',AREA_TAG+q.id);
    $('#'+AREA_TAG+q.id).text(q.text);
    $('#trash').attr('id',deleteTag);
    $('#'+name).change(function() {
        var idQuestion = $(this).parent().attr('id');
        idQuestion = idQuestion.replace(QUESTION_TAG,'');
        var selectorName = '#'+SELECTOR_TAG+idQuestion+' option:selected';
        displayTypeOfQuestion(idQuestion,$(selectorName).val());
    });
    var divName = TYPE_TAG+q.id;
    $('#typeInflator').attr('id',divName);
    $('#'+deleteTag).click(function() {
        deleteQuestion($(this));
    });
    ++questionCounter;
}

function updateSurvey() {
    //console.log("Updating survey: "+currentSurvey.title);
    currentSurvey.title = $('#title').val();
    currentSurvey.since = $('#since').val();
    currentSurvey.until = $('#until').val();
    var nQuestions = $('.question').length;
    console.log("nQuestions: "+nQuestions);
    if (nQuestions > 0) {
        cleanQuestions(currentSurvey);
        $('.question').each(function(index) {
            //var text = $(this).find('#'+AREA_TAG+index).val();     //TODO: index no esta be
            var text = $(this).find('textarea').val();
            var order = index;
            //var type = $(this).find('#'+SELECTOR_TAG+index).val();
            var type = $('select').val();
            var q = new Question(type,order,text);
            console.log(index+") text: "+q.text+" type: "+q.type);
            addQuestionToSurvey(currentSurvey, q);
        });
    }
    var jsonSurvey = currentSurvey;
    console.log("obj: "+JSON.stringify(jsonSurvey));
    var loc = '/api/survey/'+currentSurvey.id;
    sendEvent(loc, 'PUT', jsonSurvey, null, surveyUpdated);
}

function surveyUpdated() {
    $('#notification').text('Survey updated correctly!');
    $('#notification').attr('class','info');

}

function editSurvey() {
    cleanView(currentView);
    currentView = EDIT_SURVEY;
    renderEditSurvey(currentSurvey);
}

function displaySurvey(request) {
    var obj = $.parseJSON(request.value);
    var survey = new Survey(obj);
    currentSurvey = survey;
    //console.log(survey);
    //console.log("ID: "+survey.id);
    renderEditSurvey(currentSurvey,true);
}

function surveyCreated(uri, location) {
    if (location !== 'none') {
        //console.log("URI: "+location);
        //$('#editSurvey').attr('class','');
	    showEditButton();
        sendEvent(location, 'GET', null, null, displaySurvey);
    }
}

function submitCreateSurvey(){
    var data = {title : $('#title').val(), since : $('#since').val(), until : $('#until').val()};
    var method = 'POST';
    sendEvent('/api/survey', method, data, null, surveyCreated);
    return false;
}

function showEditButton(){
    $('#editSurvey').attr('class','');
}

function updateCurrentSurvey(survey){
    //console.log('updating Current Survey');
    currentSurvey = $.parseJSON(survey.value);
    //console.log(currentSurvey);
    showEditButton();
    renderEditSurvey(currentSurvey);
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
      sendEvent('/api/survey/'+id, 'GET', null, null, updateCurrentSurvey);
    });


    displayContent(surveysHtmlIni, LIST_SURVEYS);
}

function listSurveys() {
    sendEvent('/api/surveys', 'GET', null, null, renderListSurveys);
}


function createSurvey() {
    cleanView(currentView);
    //$('#dynamicContent').show();
    renderCreateForm();
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
            $('#dynamicContent').empty();
            $('#dynamicContent').hide();
            break;
        case LIST_SURVEYS:
            $('#surveysList').remove();
            break;
	case EDIT_SURVEY:
	    $('#dynamicContent').empty();
	    break;
	case ANSWER_SURVEY:
	     $('#dynamicContent').empty();
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


/*function renderNewSurveyForm() {
    initDatePicker();
    currentView = CREATE_SURVEY;
    $('#buttonSurveyCreate').click(submitCreateSurvey);
    //enableAddQuestions();
}*/

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

function deleteQuestion(question){
    var parent = question.parent();
    var id = question.attr('id').replace(DELETE_TAG,'');
    console.log("deleting question: "+id);
    $('#questionList').children('#'+QUESTION_TAG+id).remove();

}

function addNewQuestion() {
    var question = $('#newQuestion').clone();
    question.attr('id',QUESTION_TAG+questionCounter);
    question.attr('class','question');
    $('#questionList').append(question);
    var name = SELECTOR_TAG+questionCounter;
    $('#typeSelector').attr('id',name);
    $('#questionArea').attr('id',AREA_TAG+questionCounter);
    $('#trash').attr('id',DELETE_TAG+questionCounter);
    $('#'+name).change(function() {
        var idQuestion = $(this).parent().attr('id');
        idQuestion = idQuestion.replace(QUESTION_TAG,'');
        var selectorName = '#'+SELECTOR_TAG+idQuestion+' option:selected';
        displayTypeOfQuestion(idQuestion,$(selectorName).val());
    });
    var divName = TYPE_TAG+questionCounter;
    $('#typeInflator').attr('id',divName);
    $('#'+DELETE_TAG+questionCounter).click(function() {
        deleteQuestion($(this));
    });
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

function sendGetSurveyQuestions(id){
    sendEvent('/api/survey/'+id, 'GET', null, null, getSurveyQuestions);
}

function getSurveyQuestions(request) {
    var obj = $.parseJSON(request.value);
    var survey = new Survey(obj);
    currentSurvey = survey;
    //console.log(survey);
    //console.log("ID: "+survey.id);
    renderSurveyAnswerForm(survey,true);
}


function renderAnswers() {
    var answers = $('#answers').clone();
    answers.attr('id','');
    answers.attr('class','');
    $('#dynamicContent').append(answers);
}

function addAnswerBox(a) {

    a.id = answerCounter;

    var answer = $('#newAnswerBox').clone();
    answer.attr('class','question');
    answer.find('#questionText').html(answerCounter + ". " + a.text);
    answer.find('#answerTextArea').attr('id',AREA_TAG + answerCounter);
    $('#answerList').append(answer);
    ++answerCounter;
}

function renderSurveyAnswerForm(survey, createdNow){
      answerCounter = 1;
//    cleanView(currentView);
      currentView = ANSWER_SURVEY;
      $('#dynamicContent').empty();
      var template_form = $('#answerForm').clone();
      template_form.find('#surveyTitle').html(survey.title);
      //template_form.find('#survey_description').html('Fullfill your info to update');
      template_form.attr('class', '');
      $('#dynamicContent').append(template_form);
      $('#dynamicContent').show();
      renderAnswers();
      if (typeof survey.questions !== 'undefined') {
            console.log('rendering ['+survey.questions.length +'] questions...');
            for(i = 0; i < survey.questions.length; ++i) {
                //console.log('original ID: '+survey.questions[i].id);
                addAnswerBox(survey.questions[i]);
            }
      }
      $('#publishSurveyAnswers').click(function(){
            answerSurvey();
      });
}

function answerSurvey() {
    //console.log("Updating survey: "+currentSurvey.title);
    var indexQuestion = 0;
    var answer = new AnswerList();
    var nAnswers = $('.question').length;
    var answerText;
    var answerType;
    var idQuestion;
    console.log("nAnswers: "+nAnswers);
    $('.question').each(function(index) {

        idQuestion = currentSurvey.questions[indexQuestion].id;
        answerType = currentSurvey.questions[indexQuestion].questionType;
        answerText = $(this).find('textarea').val();
        answer.answered.push(new Answer(idQuestion,answerType,answerText));
        indexQuestion++;
    });
    // Finalitzem l'enquesta
    answer.stateAnswer = "done";
    var jsonAnswer = answer;
    console.log("answer = " + JSON.stringify(jsonAnswer));
    var loc = '/api/survey/'+currentSurvey.id;
    sendEvent(loc, 'PUT', jsonAnswer, null, surveyAnswered);
}

function surveyAnswered(){
    $('#notificationAnswer').text('Survey answered!');
    $('#notificationAnswer').attr('class','info');
}


$(document).ready(function($) {
    var prmstr = window.location.search.substr(1);
    var prmarr = prmstr.split ("&");
    var params = [];

    for ( var i = 0; i < prmarr.length; i++) {
        var tmparr = prmarr[i].split("=");
        params[tmparr[0]] = tmparr[1];
    }
    if(params.id == null){
        renderCreateForm();
    }else{
        sendGetSurveyQuestions(params.id)
    }

    //renderNewSurveyForm();
});


