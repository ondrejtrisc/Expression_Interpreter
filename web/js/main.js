'use strict';

let results = [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], []];

const render = (response, id) => {

    results[id].push(response);

    let text = "";
    for (let i = results[id].length - 1; i >=0; i--) {

        text += results[id][i] + '<br />';
    }

    document.getElementById('display' + id).innerHTML = text;
};

let displayId;

const serverUpdate = response => {

    render(response, displayId);
};

const main = (command, id) => {

    //const javaServerURL = "http://127.0.0.1:4242";
    const javaServerURL = "http://104.248.44.230:4242";
    const data = {cmd: command};
    displayId = id;

    $.ajax({
        url: javaServerURL,
        jsonp: 'serverUpdate',
        dataType: 'jsonp',
        data: data
    })
}
