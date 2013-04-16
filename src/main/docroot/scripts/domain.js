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
        answer[answer.id] = answer;
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

function Question() {
    switch (arguments.length) {
        case 4:
            this.id  = id;
            this.questionType  = type;
            this.order = order;
            this.text = text;
        break;
        case 2:
            this.questionType = arguments[0];
            this.text = arguments[1];
        break;
    }
}

function Answer() {
    switch (arguments.length) {
        case 3:
            this.id  = id;
            this.idQuestion  = type;
            this.idClient = order;
            this.idType = type;
        break;
    }
}
