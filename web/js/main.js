"use strict";

let results = [];

const serverUpdate = (response) => {

    var text = "";
    var i;

    results.push(response);

    console.log(results.length);

    for (i = results.length - 1; i >=0; i--) {
        text += results[i] + '<br />';
    }

    document.getElementById("display").innerHTML = text;
    
    //document.getElementById("display").innerHTML = response;
}

const main = (command) => {

    //const javaServerURL = "http://104.248.44.230:4242";
    const javaServerURL = "http://127.0.0.1:4242";
    const data = {cmd: command};

    $.ajax({
        url: javaServerURL,
        jsonp: "serverUpdate",
        dataType: "jsonp",
        data: data
    })
}
