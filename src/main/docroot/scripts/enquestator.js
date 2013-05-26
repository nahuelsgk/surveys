var currentView;
var CREATE_SURVEY = 0;
var LIST_SURVEYS = 1;
var EDIT_SURVEY = 2;
var ANSWER_SURVEY = 3;
var SIGN_IN = 4;
var WELCOME_VIEW = 5;
var currentSurvey;
var surveys = new Object();
var secret = new Object();
var surveyId = new Object();
var userId = "";
// Variable que guarda quin botó s'ha pitjat...si, en una global, si, em sento brut, odio javascript, odio aquest món
var clickedButton;

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

    var url = "http://localhost:8080/?id=" + survey.id;
    $("#link").html(url);
    $("#link").attr("href",url);

    template_form.find('#title').val(survey.title);
    template_form.find('#since').val(survey.since);
    template_form.find('#until').val(survey.until);
    template_form.find('#buttonSurveyCreate').attr('value', 'Update');
    template_form.find('#buttonSurveyCreate').attr('id', 'buttonEditSurvey');
    $('#dynamicContent').append(template_form);
    $('#dynamicContent').show();
    if (createdNow) {
        $('#notification').text('Your survey has been created');
        $('#notification').attr('class','success');
    } else {
        $('#notification').attr('class','hidden');
    }

    hideTimeout($('#notification'));

    initDatePicker();
    enableAddQuestions();
    if (typeof survey.questions !== 'undefined') {
        //console.log('rendering ['+survey.questions.length +'] questions...');

        //That adds by default two questions when no questions(that is when just created, or saved without questions)
        if (survey.questions.length == 0) {
            addNewQuestion();
            addNewQuestion();
        }
        //Otherwise render questions
        else{
          for(i = 0; i < survey.questions.length; ++i) {
            //console.log('original ID: '+survey.questions[i].id);
            addQuestion(survey.questions[i]);
          }
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
    console.log('rendering question of type: '+q.questionType);
    if (q.questionType === 'choice' || q.questionType === 'multichoice') {
        $('#'+name).val(q.questionType);
        displayTypeOfQuestion(q.id,q.questionType);
        var divNameTo = '#'+TYPE_TAG+q.id;
        var counter = 0;
        for(j=0; j < q.options.length; ++j) {
            var tag_id = q.id + SEPARATOR + counter;
            addOptionChoice(tag_id,divNameTo);
            ++counter;
            $('#'+tag_id).text(q.options[j]);
        }
    }
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
            text = $.trim(text);
            var order = index;
            var type = $(this).find('#'+SELECTOR_TAG+index).val();
            //var type = $('select').val();
            var q = new Question(type,order,text);
            console.log(index+") text: "+q.text+" type: "+q.questionType);
            if (type === 'multichoice' || type === 'choice') {
                $(this).find('.options').each(function(ind) {
                    addOptionToQuestion(q,$(this).val());
                });
            }
            addQuestionToSurvey(currentSurvey, q);
        });
    }
    var jsonSurvey = currentSurvey;
    jsonSurvey.secret = secret;

    console.log("obj: "+JSON.stringify(jsonSurvey));
    var loc = '/api/survey/'+currentSurvey.id;
    var now= new Date();
    now.setDate(now.getDate()- 1);
    if(Date.parse($('#since').val())< now){
        alert("The survey cannot start before today.");
        console.log("FAIL! Data d'inici menor a la data actual. now: "+ now + " - since: "+ Date.parse($('#since').val()));
        return false;
    }

    sendEvent(loc, 'PUT', jsonSurvey, null, surveyUpdatedCorrectly, surveyUpdateError);
}

function surveyUpdatedCorrectly() {
    $('#notification').text('Survey updated correctly!');
    $('#notification').attr('class','info');

    hideTimeout($('#notification'));

    window.scrollTo( 0, 0) ;

}

function surveyUpdateError() {
    $('#notification').text('You dont have permission to edit this survey');
    $('#notification').attr('class','error');
    hideTimeout($('#notification'));
     window.scrollTo(0, 0);
     console.log('Error updating Survey');
}

function editSurvey() {
    cleanView(currentView);
    currentView = EDIT_SURVEY;

    var urlAdmin = "";
    $("#linkadmin").html(urlAdmin);
    $("#linkadmin").attr("href",urlAdmin);
    $("#labellinkadmin").text("");

    /*
    var links = $('#links').clone();
    links.attr('class','hidden');
    $("#linkadmin").html("");
    $("#linkadmin").attr("href","");
    */
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

function surveyCreated(data, location) {
    if (location !== 'none') {
        //console.log("URI: "+location);
        //$('#editSurvey').attr('class','');
        var obj = $.parseJSON(data.value);
        secret = obj.secret;

        var urlAdmin = "http://localhost:8080/?id=" + obj.id + "&secret=" + secret;
        $("#linkadmin").html(urlAdmin);
        $("#linkadmin").attr("href",urlAdmin);
        $("#labellinkadmin").text("Your admin link:");

	    showEditButton();
	    //@TODO Evitar dues peticionsseguides (POST + GET)
        sendEvent(location, 'GET', null, null, displaySurvey);
    }
}

function submitCreateSurvey(){
    secret = Math.random().toString(36).substr(2,16);
    var now= new Date();
    now.setDate(now.getDate()- 1);
    if(Date.parse($('#since').val())< now){
        alert("The survey cannot start before today.");
        console.log("FAIL! Data d'inici menor a la data actual. now: "+ now + " - since: "+ Date.parse($('#since').val()));
        return false;
    }
    var data = {title : $('#title').val(), since : $('#since').val(), until : $('#until').val(), secret : secret};
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
    console.log(currentSurvey);
    showEditButton();
    secret = currentSurvey.secret;

    var urlAdmin = "";
    $("#linkadmin").html(urlAdmin);
    $("#linkadmin").attr("href",urlAdmin);
    $("#labellinkadmin").text("");

    renderEditSurvey(currentSurvey);
 }

function renderListSurveys(listOfSurveys) {
    $('#dynamicContent').empty();
    var surveysHtmlIni = $('<div id="surveysList">');
    var list = $('<table border="1">');
    surveysHtmlIni.append(list);
    list.append($('<tr><th>Survey</th><th>Answers</th></tr>'));
    var count = 0;
    var obj = $.parseJSON(listOfSurveys);
    var size = obj.length;
    for (var i = 0; i < size; ++i) {      // iteration over the all survey JSONs
        var survey = new Survey(obj[i]);
        if (typeof survey === 'undefined') {
            console.log('undefined survey');
        } else {
            var key = survey.id;
            surveys[key] = survey;
            var item = listSurvey(survey);
            list.append(item);
            count = count + 1;
        }
    }
    if (count == 0) {
        var noSurvey = $('<span>No surveys today</span>');
	    surveysHtmlIni.append(noSurvey);
    }
    surveysHtmlIni.append($('</table>'));


    displayContent(surveysHtmlIni, LIST_SURVEYS);
}

function listSurveys() {
    sendEvent('/api/surveys', 'GET', null, null, renderListSurveys);
}

function answeredSurveysByUser(){
    sendEvent('/api/userSurveysAnswered', 'GET', null, null, renderSurveysAnsweredByUser);
}

function renderSurveysAnsweredByUser(surveyList){
    $('#dynamicContent').empty();
    var surveysHtmlIni = $('<div id="surveysAnsweredList">');
    var list = $('<table border="1">');
    list.append($('<tr><th>Survey</th><th>Since</th><th>Until</th><th>Answer Date</th></tr>'));
    var obj = $.parseJSON(surveyList);
    var size = obj.length;
    var count = 0;
    for (var i = 0; i < size; ++i) {      // iteration over the all survey JSONs
        var survey = new Survey(obj[i]);
        if (typeof survey === 'undefined') {
            console.log('undefined survey');
        } else {
            var key = survey.id;
            surveys[key] = survey;
            var item = listSurveyAnswered(survey);
            list.append(item);
            count = count + 1;
        }
    }
    if (count == 0) {
        var noSurvey = $('<span>No surveys answered</span>');
        surveysHtmlIni.append(noSurvey);
    }
    surveysHtmlIni.append($('</table>'));

    displayContent(surveysHtmlIni, LIST_SURVEYS);
}

function listSurveyAnswered(survey){
    var item = $('#listSurveyAnsweredByUserItem').clone(true);
    item.attr('id','');
    item.attr('class','surveyAnsweredItem'); // remove the hidden class
    item.attr('name', survey.id);
    item.attr('data-since',survey.since);
    item.attr('data-until',survey.until);
    spans = item.children();
    spans.filter('.title').text(survey.title);
    spans.filter('.since').text(survey.since);
    spans.filter('.until').text(survey.until);
    spans.filter('.dateAnswered').text(survey.answers[0].dateAnswer);
    spans.filter('.answerState').text(survey.answers[0].stateAnswer);
    return item;
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
         case SIGN_IN:
            $('.signInContainer').remove();
         break;
         case WELCOME_VIEW:
            $('.signInContainer').remove();
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

    spans = item.children();
    spans.filter('.title').text(survey.title);
    spans.filter('.numAnswers').text(survey.answers.length);


    var img = $('#deleteSurveyID').clone();
    img.attr('id',survey.id);
    img.attr('class','deleteSurvey');
    img.click(function() {
       $(this).parent().remove(); //TODO: borrar l'enquesta per id
    });
    item.append(img);
    item.click(function() {
        sendEvent('/api/survey/'+survey.id, 'GET', null, null, updateCurrentSurvey, surveyAlreadyStarted);
    });

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
    //console.log('displaying type: '+type+" for question: "+idQuestion);
    var divNameTo = '#'+TYPE_TAG+idQuestion;
    switch(type) {
        case TYPE_TEXT:
            $(divNameTo).attr('class','hidden');
        break;
        case TYPE_CHOICE:
            enableAddChoices(divNameTo,idQuestion);
        break;
        case TYPE_MULTICHOICE:
            enableAddChoices(divNameTo,idQuestion);
        break;
    }
}

function addOptionChoice(idQuestion,divNameTo) {
    var div = $('#optionTemplateBig').clone();
    div.attr('id','div'+idQuestion);
    div.attr('class','optionDiv');
    var txt = div.find('#optionTemplate');
    txt.attr('id',idQuestion);
    txt.attr('class','options');
    $(divNameTo).find('#optionsInflator').append(div);
    $(div).find('img').click(function() {
        $(this).parent().remove();
    });
}

function enableAddChoices(divNameTo,idQuestion) {
    $(divNameTo).attr('class','questionOptions');
    $(divNameTo).find('img.add_question_icon').unbind('click');
    $(divNameTo).find('img.add_question_icon').click(function() {
        addOptionChoice(idQuestion, divNameTo);
    });
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
    var loc = '/api/survey/'+id+ '/answers/';
    sendEvent(loc, 'GET', null, null, getSurveyQuestions, surveyAlreadyClosed);
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

function addAnswerBox(question, userAnswer) {

    question.id = answerCounter;

    var answer = $('#answerBox').clone();
    answer.attr('class','question');
    answer.attr('id','answerBox' + answerCounter);
    answer.find('#questionText').html(answerCounter + ". " + question.text);

    var text = $('<textarea>').attr({class: 'answerTextArea', id: AREA_TAG + answerCounter, row: '3', cols: '30'})
    if(userAnswer.length == 1 && userAnswer[0].idClient == userId){
        text.val(userAnswer[0].answered[answerCounter - 1].options[0]);
    }

    answer.append(text);
    $('#answerList').append(answer);
    ++answerCounter;
}

function addAnswerRadio(question, userAnswer){
    var radio;
    var radioLabel;
    var answer = $('#answerBox').clone();
    answer.attr('class','question');
    answer.attr('id','answerBox' + answerCounter);
    answer.find('#questionText').html(answerCounter + ". " + question.text);
    for(var i = 0; i < question.options.length; ++i){
        radio = $('<input>').attr({
              type: 'radio', name: 'question' + answerCounter, value: question.options[i], id: RADIO_TAG + answerCounter + '_' + i
        });
        radioLabel = $('<label>');
        radioLabel.attr('for', RADIO_TAG + answerCounter + '_' + i);
        radioLabel.text(question.options[i]);
        answer.append(radio);
        answer.append(radioLabel);
        //$('#radio' + answerCounter + '_' + i).text("hola");
        answer.append('<br>');

        // Retrieve old answeer
        if(userAnswer.length == 1 && userAnswer[0].idClient == userId){
            if(userAnswer[0].answered[answerCounter - 1].options[i] == 'true'){
                radio.prop('checked',true);
            }
        }
    }
    $('#answerList').append(answer);
    ++answerCounter;
}

function addAnswerCheckBox(question, userAnswer){
    var checkbox;
    var checkboxLabel;
    var answer = $('#answerBox').clone();
    answer.attr('class','question');
    answer.attr('id','answerBox' + answerCounter);
    answer.find('#questionText').html(answerCounter + ". " + question.text);
    for(var i = 0; i < question.options.length; ++i){
        checkbox = $('<input>').attr({
              type: 'checkbox', name: 'question' + answerCounter, value: question.options[i], id: CHECKBOX_TAG + answerCounter + '_' + i
        });
        checkboxLabel = $('<label>');
        checkboxLabel.attr('for',CHECKBOX_TAG + answerCounter + '_' + i);
        checkboxLabel.text(question.options[i]);
        answer.append(checkbox);
        answer.append(checkboxLabel);
        //$('#radio' + answerCounter + '_' + i).text("hola");
        answer.append('<br>');
        // Retrieve old answeer
        if(userAnswer.length == 1 && userAnswer[0].idClient == userId){
           if(userAnswer[0].answered[answerCounter - 1].options[i] == 'true'){
               checkbox.prop('checked',true);
           }
        }
    }
    $('#answerList').append(answer);
    ++answerCounter;
}


function renderSurveyAnswerForm(survey, createdNow){
      answerCounter = 1;
//    cleanView(currentView);
      currentView = ANSWER_SURVEY;
      $('#dynamicContent').empty();
      var template_form = $('#answerFormDiv').clone();
      template_form.find('#surveyTitle').html(survey.title);
      //template_form.find('#survey_description').html('Fullfill your info to update');
      template_form.attr('class', 'answerFormDiv');
      $('#dynamicContent').append(template_form);
      $('#dynamicContent').show();

      $("#linkanswer").html("");
      $("#linkanswer").attr("href","");
      $("#labellinkanswer").text("");


      renderAnswers();
      if (typeof survey.questions !== 'undefined') {
            console.log('rendering ['+survey.questions.length +'] questions...');
            for(var i = 0; i < survey.questions.length; ++i) {
                switch(survey.questions[i].questionType){
                    case TYPE_TEXT:
                                addAnswerBox(survey.questions[i],survey.answers);
                                break;
                    case TYPE_CHOICE:
                                addAnswerRadio(survey.questions[i],survey.answers);
                                break;
                    case TYPE_MULTICHOICE:
                                addAnswerCheckBox(survey.questions[i],survey.answers);
                                break;
                }
            }
      }
      $('#saveSurveyAnswers').click(function(){
            answerSurvey("pending");
      })
      $('#publishSurveyAnswers').click(function(){
            answerSurvey("done");
      });
}


function getTextAnswers(answerBox){
    var textAnswer = new Array();
    textAnswer.push(answerBox.find('textarea').val());

    return textAnswer;
}

function getCheckOrRadioAnswers(answerBox, questionType){
      var answers = new Array();
      answerBox.find($("input[type='" + questionType + "']")).each(function(){
        if($(this).is(":checked")){
            answers.push('true');
        }else{
            answers.push('false');
        }
      })
      return answers;
}


function answerSurvey(state) {
    //console.log("Updating survey: "+currentSurvey.title);
    var indexQuestion = 0;
    var answer = new AnswerList();
    var nAnswers = $('.question').length;
    var answerOptions;
    var answerType;
    var idQuestion;
    console.log("nAnswers: "+nAnswers);
    $('.question').each(function(index) {

        idQuestion = currentSurvey.questions[indexQuestion].id;
        answerType = currentSurvey.questions[indexQuestion].questionType;
        switch(answerType){
                            case TYPE_TEXT:
                                        answerOptions = getTextAnswers($(this))
                                        break;
                            case TYPE_CHOICE:
                                        answerOptions = getCheckOrRadioAnswers($(this),'radio');
                                        break;
                            case TYPE_MULTICHOICE:
                                        answerOptions = getCheckOrRadioAnswers($(this),'checkbox');
                                        break;
         }
        //answerText = $(this).find('textarea').val();
        answer.answered.push(new Answer(idQuestion,answerType,answerOptions));
        indexQuestion++;
    });
    // Finalitzem l'enquesta
    answer.stateAnswer = state;
    var jsonAnswer = answer;
    console.log("answer = " + JSON.stringify(jsonAnswer));
    // Mostrem link si es un save
    if(state == "pending"){
        showLink();
        clickedButton = "saveAnswer";
    }else{
        clickedButton = "finishAnswer";
    }
    if(userId == "" ){
        var loc = '/api/survey/'+currentSurvey.id+ '/answers/';
        if(state == 'pending'){
            sendEvent(loc, 'POST', jsonAnswer, null, surveyAnswered, surveyAlreadyStarted);
        }else if(state == 'done'){
            sendEvent(loc, 'POST', jsonAnswer, null, surveyAnswered, surveyAlreadyStarted);
        }

    }else{
        var loc = '/api/survey/'+currentSurvey.id+ '/answers/' + userId;
        if(state == "pending"){
            sendEvent(loc, 'PUT', jsonAnswer, null, showPostSaveSurveyNotification, surveyAlreadyStarted);
        }else if(state == 'done'){
            sendEvent(loc, 'PUT', jsonAnswer, null, showPostFinishNotification, surveyAlreadyStarted);
        }

    }

}

function showPostSaveSurveyNotification(){
    showSurveyAnsweredNotification("Your answers has been saved. Please click the link below to continue this survey in the future.");
}

function showPostFinishNotification(){
    showSurveyAnsweredNotification("Your survey has been send!");
}

function surveyAnswered(data){
    var obj = $.parseJSON(data.value);
    userId = obj.userId;
    if(clickedButton == "finishSurvey"){
        showSurveyAnsweredNotification("Your survey has been send!");
    }else if(clickedButton = "saveSurvey"){
        showSurveyAnsweredNotification("Your answers has been saved. Please click the link below to continue this survey in the future.");
    }
}

function showSurveyAnsweredNotification(notificationText){
    $('#notificationAnswer').text(notificationText);
//    $('#notificationAnswer').text('Survey answered!');
    $('#notificationAnswer').attr('class','info');
    hideTimeout($('#notificationAnswer'));
}

function showLink(){
    var urlAnswer = "http://localhost:8080/?id=" + surveyId + "&user=" + userId;
    $("#linkanswer").html(urlAnswer);
    $("#linkanswer").attr("href",urlAnswer);
    $("#labellinkanswer").text("Your answer link: ");
}

function surveySaved(data){
    var obj = $.parseJSON(data.value);
    userId = obj.userId;
    showSurveyAnsweredNotification();
    showLink();
}

function sendGetSurveyQuestionsByUser(surveyId, userId){
    var loc = '/api/survey/'+ surveyId + '/answers/' + userId;
    sendEvent(loc, 'GET', null, null, getSurveyQuestions, surveyAlreadyClosed);
}

function getSurveyQuestions(request) {
    var obj = $.parseJSON(request.value);
    var survey = new Survey(obj);
    currentSurvey = survey;
    //console.log(survey);
    //console.log("ID: "+survey.id);
    renderSurveyAnswerForm(survey,true);
}

function renderForm() {
      var prmstr = window.location.search.substr(1);
      var prmarr = prmstr.split ("&");
      var params = {};

      for ( var i = 0; i < prmarr.length; i++) {
          var tmparr = prmarr[i].split("=");
          params[tmparr[0]] = tmparr[1];
      }
      switch(prmarr.length) {
        case 0:
            renderCreateForm();
            break;
        case 1:
            if(params.id != null) {
                sendGetSurveyQuestions(params.id);
                surveyId = params.id;
            }
            else renderCreateForm();
            break;
        case 2:
            if(params.id && params.secret) {
                surveyId = params.id;
                secret = params.secret;
                sendEvent('/api/survey/'+params.id, 'GET', null, null, updateCurrentSurvey, surveyAlreadyStarted);
            } else if(params.id && params.user) {
                surveyId = params.id;
                userId = params.user;
                sendGetSurveyQuestionsByUser(params.id, params.user);
                console.log("EditRespuestas");
            }
            else renderCreateForm();
            break;
        default:
            renderCreateForm();
            break;

      }


}

function surveyAlreadyClosed(){
    $('#dynamicContent').empty();
    var notification =  $('#notificationAnswer').clone();
    notification.text('You can\'t answer this survey. It\'s already closed!');
    notification.attr('class','error');
    hideTimeout(notification);

    $('#dynamicContent').append(notification);
}

function surveyAlreadyStarted(){
    $('#dynamicContent').empty();
    var notification =  $('#notificationAnswer').clone();
    notification.text('You can\'t edit this survey. It\'s already anwered at least once!');
    notification.attr('class','error');
    hideTimeout(notification);
    $('#dynamicContent').append(notification);
    
}

function hideTimeout(element) {
    setTimeout(
        function() {
            //element.attr('class', element.attr('class') + ' hidden');
        },
        5000
    );
}



$(document).ready(function($) {
     renderForm();
     checkCookie();
    //renderNewSurveyForm();
});


