var currentSurvey;
var questionCounter = 0;

/* Domain Objects */
function Survey() {
    switch (arguments.length) {     //multiple constructors
        case 1:
            var json = arguments[0];
            init(this,json);
        break;
    }

    this.addQuestion = function(question) {
        questions[question.id] = question;
    }

    this.addAnswer = function(answer) {
        answersQuestions[answer.id] = answer;
    }

    function init(survey, json) {
        survey.title = json.title || 'No title';
        survey.since  = json.since;
        survey.until = json.until;
        survey.id = json.id;
        survey.state = json.state;
        survey.questions = json.questions; // Array for all the survey's questions
        survey.answers = json.answers; // Map for all the survey's answers
        if (typeof survey.questions === 'undefined') {
            survey.questions = new Array();
        }
    }

}

function cleanQuestions(survey) {
    survey.questions = new Array();
}

function addQuestionToSurvey(survey, question) {
    survey.questions[survey.questions.length] = question;
}

function cleanAnswers(survey) {
    survey.answersQuestions = new Array();
}

function addAnswerToSurvey(survey, answer) {
    survey.answersQuestions[survey.answersQuestions.length] = answer;
}

function Question() {
    this.id  = "";  //default
    switch (arguments.length) {
        case 4:
            this.id  = id;
            this.questionType  = type;
            this.order = order;
            this.text = text;
            this.options = new Array();
        break;
        case 3:
            this.questionType = arguments[0];
            this.order = arguments[1];
            this.text = arguments[2];
            this.options = new Array();
        break;
        case 2:
            this.questionType = arguments[0];
            this.text = arguments[1];
            this.options = new Array();
        break;
    }
}

function addOptionToQuestion(question, option) {
    question.options[question.options.length] = option;
}

function AnswerList() {
    this.stateAnswer = "";
    this.answered = new Array();
}

function Answer() {
    switch (arguments.length){
        case 3:
            this.idQuestion = arguments[0];
            this.typeAnswer = arguments[1];
            this.options = arguments[2];
        break;
    }
}

function AnswerListRandomName() {

}


function User() {
    switch (arguments.length) {
        case 3:
            this.userName = arguments[0];
            this.password = arguments[1];
            this.email = arguments[2];
            this.surveys = new Array();
        break;
        case 2:
            this.userName = arguments[0];
            this.password = arguments[1];
            this.email = '';
            this.surveys = new Array();
        break;
    }
}