const serverUpdate = (response) => {

    document.getElementById("display").innerHTML = response;
}

const main = (command) => {

    const javaServerURL = "http://127.0.0.1:4242";
    const data = {cmd: command};

    $.ajax({
        url: javaServerURL,
        jsonp: "serverUpdate",
        dataType: "jsonp",
        data: data
    })
}
