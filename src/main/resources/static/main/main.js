

var my_friends=[]
//创建socket对象
var ws=new WebSocket("ws://localhost:8080/chat");
//验证登录
$(function () {
    // $.post("/user/getUser",function(res){
    //     console.log(res)
    //     if(String(res.code)==="1"){
    //         $("#user-info").text(res.data.nickname+"("+res.data.username+")")
    //         userName=res.data.username
    //         getFriends(res.data.username);
    //     }else{
    //         $.message({
    //             type:"error",
    //             message:res.msg
    //         })
    //     }
    // })
    $.ajax({
        type: 'POST',
        url: "/user/getUser",
        success: function(res) {
            // 处理成功响应的操作
            console.log(res)
            if(String(res.code)==="1"){
                $("#user-info").text(res.data.nickname+"("+res.data.username+")")
                userName=res.data.username
                getFriends(res.data.username);
            }else{
                $.message({
                    type:"error",
                    message:res.msg
                })
            }
        },
        async:false
    });
    //搜索用户
    search_user();
    //给ws绑定事件
    ws.onopen=function () {
        // $.post("/user/setStatus",{"status":1},function (res) {
        //     console.log(res)
        // })
    }

    ws.onmessage=function (evt) {
        //获取返回的数据
        var dataStr=evt.data;
        var res=JSON.parse(dataStr);
        //判断是否是系统消息
        if(res.isSystem){
            //系统消息
            // message,是现在登录的好友提示
            var message=res.message;
            var systemMessage=$("#notification").html();
            if (res.fromName=="1"){
                //这是我利用这个字段表示是否是离线提示
                systemMessage+="<li style=\"margin-left: 20px;margin-" +
                    "bottom: 5px\">你的好友 "+message+" 已离线</li>"
            }else{
                systemMessage+="<li style=\"margin-left: 20px;margin-" +
                    "bottom: 5px\">你的好友 "+message+" 已上线</li>"
            }
            getFriends(userName);
            $("#notification").html(systemMessage);
        }else{
            console.log(res.fromName)
            console.log(userName)
            console.log(res)
            console.log(toName)
            //非系统消息,将服务端推送的消息渲染
            let str="";
            if(res==null){
                console.log("判定空")
            }else if(res.fromName==userName){
                str="<div class=\"message outgoing\">\n" +
                    "<div class=\"message-content\">\n" +
                    "<p>"+res.message+"</p>\n" +
                    "<span class=\"message-time\">"+getTime()+"</span>\n" +
                    "</div>\n" +
                    "</div>"
            }else{
                str="<div class=\"message incoming\">\n" +
                    "<div class=\"message-content\">\n" +
                    "<p>"+res.message+"</p>\n" +
                    "<span class=\"message-time\">"+getTime()+"</span>\n" +
                    "</div>\n" +
                    "</div>"
            }
            // if (res.fromName==)
            // if (toName==res.fromName){
            $(".chat-messages").append(str)
            setScroll()
            allMessage+=str;
                // var chatData = sessionStorage.getItem(toName)
                //
                // if (chatData!=null){
                //     str = chatData+str
                // }
                // sessionStorage.setItem(toName,str)
            // }else {
                // var chatData = sessionStorage.getItem(res.fromName)

                // if (chatData!=null){
                //     str = chatData+str
                // }
                // sessionStorage.setItem(res.fromName,str)
            // }
        }
    }
    ws.onclose=function () {
        // $.post("/user/setStatus",{"status":0},function (res) {
        //     console.log(res)
        // })
    }

    $("#submit").click(function () {
        //获取输入的内容
        var data=$(".message-box textarea").val()
        //清空数据区
        $(".message-box textarea").val("");

        var json={"toName":toName,"message":data}

        //将数据展示到聊天区
        var str="<div class=\"message outgoing\">\n" +
            "<div class=\"message-content\">\n" +
            "<p>"+data+"</p>\n" +
            "<span class=\"message-time\">"+getTime()+"</span>\n" +
            "</div>\n" +
            "</div>"

        $(".chat-messages").append(str)

        // var chatData = sessionStorage.getItem(toName)
        //
        // if (chatData!=null){
        //     str = chatData+str
        // }
        // sessionStorage.setItem(toName,str)
        setScroll()
        //发送数据给服务器
        ws.send(JSON.stringify(json));
    })
})
//获取当前用户的朋友
function getFriends(username){
    console.log(username)
    $.post("/user/getFriends",{"username":username},function (res) {
        console.log(res)
        if(String(res.code)==="1"){
            let str=""
            for (i=0;i<res.data.length;i++){
                my_friends.push(res.data[i].username)
                let user_name= res.data[i].nickname+"("+res.data[i].username+")"
                let user_status=res.data[i].status ? "在线" : "离线"
                str=str+"<li class=\"friend-item\" onclick='showChat(\""+res.data[i].nickname+"\",\""+res.data[i].username+"\")'>\n" +
                    "<div class=\"friend-avatar\"></div>\n" +
                    "<div class=\"friend-info\">\n" +
                    "<h3>"+user_name+"</h3>\n" +
                    "<p>"+user_status+"</p>\n" +
                    "</div>\n" +
                    "</li>"
            }
            $("#my_friends").html(str)
        }else{
            $.message({
                type:"error",
                message:"出错了！"
            })
        }
    })
}

function search_user(){
    $(".search_input").keyup(function (event) {
        if(event.keyCode===13){
            let value=$(".search_input").val()
            const pattern = /^\d{10}$/
            //利用正则判断是否是username，我的username都是用数字字符串表示，类似qq
            let data={"nickname":value}
            if(pattern.test(value)){
                data={"username":value}
            }
            // console.log(my_friends)
            $.post("/user/searchUser",data,function (res) {
                if("1"===String(res.code)){
                    let str=""
                    for (j=0;j<res.data.length;j++){
                        let user_name= res.data[j].nickname+"("+res.data[j].username+")"
                        let user_status=res.data[j].status ? "在线" : "离线"
                        let action=my_friends.indexOf(res.data[j].username)===-1 ? "添加" : "发信息"
                        str=str+"<li class=\"friend-item\">\n" +
                            "<div class=\"friend-avatar\"></div>\n" +
                            "<div class=\"friend-info\">\n" +
                            "<h3>"+user_name+"</h3>\n" +
                            "<p>"+user_status+"</p>\n" +
                            "</div>\n" +
                            "<div class=\"add_chat\" style=\"margin-right: 10px\">"+action+"</div>\n" +
                            "</li>"
                    }
                    $("#add_friends").html(str)
                }else{
                    $.message({
                        type:"error",
                        message:"出错了！"
                    })
                }
            })
        }
    })
}

//切换聊天的用户
let toName;//定义给谁发送信息
let userName;
//获取所有的存储的信息
var allMessage="";
//打开对应用户的聊天页  str1是nickname，str2是username
function showChat(str1,str2) {
    $(".chat-messages").html("");
    toName=str2;
    $(".header div").text(str1+"("+str2+")")
    $(".chat-messages").css("display","inline")
    $(".message-box").css("display","")
    //把信息获取到sessionStorage
    // var chatData=sessionStorage.getItem(toName);
    sendMessageRequest("消息请求:"+toName);
    // if(allMessage!=null){
    //     //将聊天记录展示到聊天区
    //     $(".chat-messages").html(allMessage);
    // }
    // setScroll()
}
function sendMessageRequest(str) {
    ws.send(str);
}

//获取当前的时间，格式化为AM格式
function getTime() {
    var now = new Date();
    var currentHour = now.getHours();
    var currentMinute = now.getMinutes();

    var ampm = currentHour >= 12 ? 'PM' : 'AM';
    currentHour = currentHour % 12;
    currentHour = currentHour ? currentHour : 12;
    currentMinute = currentMinute < 10 ? '0' + currentMinute : currentMinute;

    var formattedTime = currentHour + ':' + currentMinute + ' ' + ampm;

    return formattedTime;
}

function setScroll() {
    var scrollContainer = $('.chat-messages');
    scrollContainer.scrollTop(scrollContainer[0].scrollHeight - scrollContainer.outerHeight());
}