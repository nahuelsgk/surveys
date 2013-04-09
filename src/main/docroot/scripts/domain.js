/* Domain Objects */
function Survey() {
    switch (arguments.length) {     //multiple constructors
        case 1:
            var json = arguments[0];
            init(this,json.id,json.title,json.since,json.until,json.state);
        break;
    }

    this.addQuestion = function(question) {
        questions[question.id] = question;
    }

    this.addAnswer = function(answer) {
        answer[answer.id] = answer;
    }

    function init(survey, title, since, until, id, state) {
        survey.title = title;
        survey.since  = since;
        survey.until = until;
        survey.id = id;
        survey.state = state;
        survey.questions = new Object(); // Map for all the survey's questions
        survey.answers = new Object(); // Map for all the survey's answers
    }

}

function Question() {
    switch (arguments.length) {
        case 3:
            this.id  = id;
            this.type  = type;
            this.order = order;
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