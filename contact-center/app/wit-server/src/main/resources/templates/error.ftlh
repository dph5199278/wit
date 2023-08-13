<!DOCTYPE html>
<html lang="zh">
    <head>
        <title>
            ${status} ${error}
        </title>
        <meta charset="utf-8" />
    </head>
    <body style="text-align: center;">
        <h3>${status} ${error}</h3>
        <p><span>Message: </span><span>${message}</span></p>
        <p><span>Path: </span><span>${path}</span></p>
        <p><span>Datetime: </span><span>${timestamp?string("yyyy-MM-dd HH:mm:ss")}</span></p>
        <a href="/">Back to Home Page</a>
    </body>
</html>