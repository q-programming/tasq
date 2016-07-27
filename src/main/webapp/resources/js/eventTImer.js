onmessage = function (e) {
    var appUrl = e.data[0];
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState === XMLHttpRequest.DONE && xmlhttp.status === 200) {
            postMessage(xmlhttp.responseText);
        }
    };
    xmlhttp.open("GET", appUrl, true);
    xmlhttp.send();
    setInterval(function () {
        xmlhttp.open("GET", appUrl, true);
        xmlhttp.send();
    }, 60*1000);
}