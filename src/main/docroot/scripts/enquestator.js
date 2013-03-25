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

    var pickerOpts = {
        dateFormat: $.datepicker.ISO_8601
    };

    $( "#since" ).datepicker(pickerOpts);
    $( "#until" ).datepicker(pickerOpts);



    $('#create_survey_form').ajaxForm(function() {
        if ($('#create_survey_form').attr('method') === 'PUT') {
            date = new Date();
            $('h2').text('Survey updated at ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds   ());
        } else {
            $('#create_survey_form').attr('method', 'PUT');
            $('#submit_btn').attr('value','Edit');

            $('h2').text('Survey sent');
            $('#survey_description').text('Click the edit button to update it');
        }
        console.log('Sent Form with '+$('#create_survey_form').attr('method')+' Method');

    });

    $('#since').change(function() {
        if ($('#since').attr('value') > $('#until').attr('value')) {
            $('#until').attr('value', $('#since').attr('value'));
        }
    });
    $('#until').change(function() {
        if ($('#since').attr('value') > $('#until').attr('value')) {
                $('#since').attr('value', $('#until').attr('value'));
        }
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
            for(var i = 0; i < json.length; ++i){
                var obj = json[i];
                console.log("JSON["+i+"]: "+obj);
                for(var key in obj){
                    var attrName = key;
                    var attrValue = obj[key];
                    if (attrName == "title") {
                       var html = '<li>'+title+'</li>';
                       surveysHtmlIni = surveysHtmlIni + html;
                       count = count +1;
                    }
                }
            }
            if (count == 0) {
                var html = 'No surveys today';
                surveysHtmlIni = surveysHtmlIni + html;
            }
            surveysHtmlIni = surveysHtmlIni + surveysHtmlEnd;
            console.log("HTML: "+surveysHtmlIni);
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