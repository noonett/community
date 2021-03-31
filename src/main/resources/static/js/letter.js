$(function () {
    $("#sendBtn").click(send_letter);
    $(".close").click(delete_msg);
});

function send_letter() {

    // 发送AJAX请求之前，将CSRF令牌设置到请求的消息头中
    // var token = $("meta[name = '_csrf']").attr("content");
    // var header = $("meta[name = '_csrf_header']").attr("content");
    // $(document).ajaxSend(function (e, xhr, options) {
    //     xhr.setRequestHeader(header, token);
    // });

    $("#sendModal").modal("hide");

    var toName = $("#recipient-name").val();
    var content = $("#message-text").val();
    $.post(
        CONTEXT_PATH + "/letter/send",
        {"toName": toName, "content": content},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#hintBody").text("发送成功")
            } else {
                $("#hintBody").text(data.msg)
            }
        })

    $("#hintModal").modal("show");
    setTimeout(function () {
        $("#hintModal").modal("hide");
        location.reload();
    }, 2000);
}

function delete_msg() {

    // 发送AJAX请求之前，将CSRF令牌设置到请求的消息头中
    // var token = $("meta[name = '_csrf']").attr("content");
    // var header = $("meta[name = '_csrf_header']").attr("content");
    // $(document).ajaxSend(function (e, xhr, options) {
    //     xhr.setRequestHeader(header, token);
    // });

    // 删除数据
    var letterId = $("#letterId").val();
    $.post(
        CONTEXT_PATH + "/letter/delete",
        {"letterId": letterId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#hintBody").text("删除成功");
            } else {
                $("#hintBody").text(data.msg)
            }
        }
    )
    $(this).parents(".media").remove();
}

// websocket对象
var ws = null;
// 打开页面建立连接
$().ready(function () {
    connect();
});

window.addEventListener("beforeunload", function () {
    disconnect();
});

//连接
function connect() {
    var target =  $("#userId").val();
    ws = new WebSocket("ws://localhost:8080/letternotice/" + target);
    // Socket建立连接状态触发函数
    ws.onopen = function () {
        log('Info: WebSocket connection opened.');
    };
    // Socket收到消息触发函数
    ws.onmessage = function (event) {
        log('Info: New Letter.');
        data = $.parseJSON(event.data);
        var code = data.code;
        var msg = data.msg;
        if (code == 1) {
            var name = data.name;
            var content = data.content;
            notifyMe(name, content);
            console.log(msg);
        }else{
            console.log(msg);
        }
    };
    // Socket关闭连接触发函数
    ws.onclose = function () {
        log('Info: WebSocket connection closed.');
    }
}

// 断开连接
function disconnect() {
    if (ws != null) {
        ws.close();
        ws = null;
    }
}

// notification弹窗
function notifyMe(name, content) {
    if (!("Notification" in window)) {
        alert("浏览器不支持弹窗！");
    } else if (Notification.permission === "granted") {
        var notification = new Notification(name, {
            body: content,
            icon: 'https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1493952301,1278532971&fm=26&gp=0.jpg',
        });
        notification.onclick = function () {
            var userId = $("#userId").val();
            var url = "http://localhost:8080/elec5619/notification/user/" + userId;
            window.location.href = url;
            window.focus();
            notification.close();
        };
        setTimeout(notification.close.bind(notification), 15000);
    } else if (Notification.permission !== 'denied') {
        Notification.requestPermission(function (permission) {

            if (permission === "granted") {
                var notification = new Notification('Covid-19 Check-In', {
                    body: 'Successfully subscribed to Covid-19 Alert!'
                });
            }
        });
    }
}

//日志输出
function log(message) {
    console.log(message);
}