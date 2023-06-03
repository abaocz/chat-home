$(function(){
    $("#login_submit").click(function(){
        username=$("#username").val()
        password=$("#password").val()
        if(checkUsername(username)&&checkPassword(password)){
            $.message("验证成功")
            // 用户名和密码都验证成功，发送ajax
            $.post("/user/login",{'username':username,'password':password},function(res){
                //判断是否成功，跳转页面
                // console.log(res.code)
                if (String(res.code)==="1"){
                    $.message("登陆成功！")
                    location.replace("/main/main.html")
                }else {
                    $.message({
                        type:"error",
                        message:res.msg
                    })
                }

            })
            // location.replace("")
        }
    })
})

//用户名的校验逻辑，这里我只判空,以后可以增加功能
function checkUsername(username){
    if(username!=""){
        return true
    }
    $.message({
        message:"用户名不能为空",
        type:"error"
    })
    return false
}

//密码的校验逻辑，这里我只判空,以后可以增加功能
function checkPassword(password){
    if(password!=""){
        return true
    }
    $.message({
        message:"密码不能为空",
        type:"error"
    })
    return false
}