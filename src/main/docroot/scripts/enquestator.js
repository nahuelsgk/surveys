var createSurveyHTML;
var surveysListHTML;
var questionCounter = 1;

$(document).ready(function($) {
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



    $('#create_survey_form').submit(function() {
        var form = $(this);
        var request = $.ajax({
            url: $(this).attr('action'),
            type: $(this).attr('method'),
            data: JSON.stringify({
                title : $('#title').val(),
                since : $('#since').val(),
                until : $('#until').val()
            })
        });

        request.fail(function() {
            console.log('request failed :/');
        });
        request.done(function (json) {
            console.log('sent request, method : ' + form.attr('method'));

            if (form.attr('method') === 'PUT') {
                date = new Date();
                $('h2').text('Survey updated at ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds   ());
            } else {
                form.attr('method', 'PUT');
                form.attr('action', json.getResponseHeader('location'));
                $('#submit_btn').attr('value','Edit');

                $('h2').text('Survey sent');
                $('#survey_description').text('Click the edit button to update it');
            }
        });

        return false;
    });

    createSurveyHTML = $('#dynamicContent').clone();

    addQuestion = function() {
        $('#question_list').append('<li><label for="question[' + questionCounter + ']">Question ' + questionCounter + '</label>' +
            '<input type="text" name="question[' + questionCounter + '] class="question" placeholder="Write your question here" required /></li>'
        );
        questionCounter++;
    };

    $('#add_question').click(addQuestion);

});

function listSurveys() {

    $.ajax({
        type:"GET",
        url:"/api/surveys",
        dataType:"json",
        success: function(json) {
            var surveysHtmlIni = '<div id="surveysList"><ul>';
            var surveysHtmlEnd = '</ul></div>';
            var count = 0;
            console.log("JSON: "+json);
            var obj = $.parseJSON(json);
            var size = obj.length;
            console.log("#surveys: "+size);
            for (var i = 0; i < size; ++i) {
                $.each(obj[i], function(id, v) {
                    if (id == "title") {
                        console.log("TITLE: "+v);
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
            surveysListHTML = surveysHtmlIni;

            displayContent('Surveys list', surveysListHTML);

        }
    });
}

function createSurvey() {

    displayContent('Create survey', createSurveyHTML);
}



/*
  This function fills the div "content" with a H2 header containing the arg "title" and a html bloc attached below.
*/
function displayContent(title, html)  {
   $('#contentTitle').remove();
   $('#dynamicContent').empty();
   var header = '<h2 id="contentTitle">'+title+'</h2>';
   $('#dynamicContent').append(header);
   $('#dynamicContent').append(html);
}
