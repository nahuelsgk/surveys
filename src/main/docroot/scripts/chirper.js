function addChirp(chirp){
    var chirpElement = $("#chirpTemplate.template").clone().appendTo("#chirps").removeClass("template");
    $("span.name",chirpElement).text(chirp.author.name);
    $("span.username",chirpElement).text(chirp.author.username);
    $("p",chirpElement).text(chirp.message);
    $("date",chirpElement).text(chirp.date);
    $("img",chirpElement).attr("src",chirp.author.avatar);
}

$(function(){
    $.getJSON("/api/chirps", function(tweets,textStatus,jqXHR){
        $(tweets).each(function(){
                addChirp(this);
        });
    })
});
