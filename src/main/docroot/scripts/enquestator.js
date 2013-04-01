var createSurveyHTML;
var surveysListHTML;

$(document).ready(function($) {
    formatNumber = function(number) {
        return (number < 10) ?  '0' + number : number;
    }
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



    $('#submit_btn').click(function() {
        $.ajax({
            url: "/api/survey",
            type: "POST",
            data: JSON.stringify({
                title : $('#title').val(),
                since : $('#since').val(),
                until : $('#until').val()
            }),
            dataType: "json"
        }).done(function () {
            console.log('sent request (POST)');
        });

        return false;
    });

    createSurveyHTML = $('#dynamicContent').clone();
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