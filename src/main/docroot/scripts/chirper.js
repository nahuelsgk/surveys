function addChirp(chirp){
    var chirpElement = $("#chirpTemplate.template").clone().appendTo("#chirps").removeClass("template");
    $("span.name",chirpElement).text(chirp.author.name);
    $("span.username",chirpElement).text(chirp.author.username);
    $("p",chirpElement).text(chirp.message);
    $("date",chirpElement).text(chirp.date);
    $("img",chirpElement).attr("src",chirp.author.avatar);
}

$(function(){
    var url = "/api/chirps";
    $.ajax({
        url: url,
        success: function(tweets,textStatus,jqXHR){
            $(tweets).each(function(){
                    addChirp(this);
            });
        },
        dataType: "json",
        error: function(jqXHR, textStatus, error){
            if(jqXHR && jqXHR.status==404){
                $("<p>").addClass("error").text("Error getting the chirps... api server not found at " + url).appendTo("header")
            } else {
                $("<p>").addClass("error").text("Unknown error getting the chirps...").appendTo("header")
            }
            console.debug("Error!",jqXHR)
            console.debug("Text status", textStatus)
            console.debug("Error",error)
        }})
});
