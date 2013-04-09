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




}

function Question () {
    switch (arguments.length) {
        case 3:
            this.id  = id;
            this.type  = type;
            this.order = order;
        break;
    }
}