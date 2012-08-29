(function(){

var loggedUser = {
    name: "John Doe",
    username: "john_doe",
    avatar: "/img/avatar1.png",
    status: "The average John Doe, whose status is really, really boring."
}

var chirps = []

function renderChirps(){

    function addChirp(chirp){
        var chirpElement = $("#chirpTemplate.template").clone().appendTo("#chirps").removeClass("template");
        $("span.name",chirpElement).text(chirp.author.name);
        $("span.username",chirpElement).text("@" + chirp.author.username);
        $("p",chirpElement).text(chirp.message);
        $("date",chirpElement).text(chirp.date);
        $("img",chirpElement).attr("src",chirp.author.avatar);
    }

    $(chirps).each(function(){
            addChirp(this);
    });
}

function renderLoggedUser(){
    $("header h1").text(loggedUser.name);
    $("header h2").text("@" + loggedUser.username);
    $("header p.status").text(loggedUser.status);
    $("header img").attr("src",loggedUser.avatar)
}

$(function(){

    renderLoggedUser();

    var url = "/api/chirps";
    $.ajax({
        url: url,
        success: function(newChirps,textStatus,jqXHR){
            chirps = newChirps;
            renderChirps();
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


})();
