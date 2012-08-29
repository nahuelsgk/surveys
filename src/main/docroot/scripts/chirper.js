function addChirp(chirp){
    var chirpElement = $("#chirpTemplate.template").clone().appendTo("#chirps").removeClass("template");
    $("span.name",chirpElement).text(chirp.author.name);
    $("span.username",chirpElement).text(chirp.author.username);
    $("p",chirpElement).text(chirp.message);
    $("date",chirpElement).text(chirp.date);
    $("img",chirpElement).attr("src",chirp.author.avatar);
}

$(function(){
    var tweets = [
        {
            author: {
                name:"Jose Raya",
                username:"_joseraya",
                avatar:"img/avatar1.png"
            },
            date: "23 jul",
            message: "It works!"
        },
        {
            author: {
                name: "Jordi Pradel",
                username: "agile_jordi",
                avatar:"img/avatar4.png"
            },
            date: "24 jul",
            message: "Indeed! This is awesomeming awesome!"
        },
        {
            author: {
                name:"John Doe",
                username:"john_doe",
                avatar:"img/avatar2.png"
                },
            date: "20 jul",
            message: "A super awesome tweet by Jane, one of the users John follows... obviously. Text may have up to 140 characters so... use them all! I said all!"
        }
    ];
    $(tweets).each(function(){
        addChirp(this);
    });

});
