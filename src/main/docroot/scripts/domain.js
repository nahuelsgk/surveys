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