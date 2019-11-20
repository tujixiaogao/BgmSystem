//------------- login.js -------------//
$(document).ready(function() {
    //for custom checkboxes
    $('input').not('.noStyle').iCheck({
        checkboxClass: 'icheckbox_flat-green'
    });

    //validate login form
    $("#loginForm").validate({
       // ignore: null,
        ignore: 'input[type="hidden"]',
        errorPlacement: function(error, element) {
            wrap = element.parent();
            wrap1 = wrap.parent();
            if (wrap1.hasClass('checkbox')) {
                error.insertAfter(wrap1);
            } else {
                if (element.attr('type')=='file') {
                    error.insertAfter(element.next());
                } else {
                    error.insertAfter(element);
                }
            }
        },
       // debug:true,
        errorClass: 'help-block',
        rules: {
            account: {
                required: true,
                account: true
            },
            password: {
                required: true,
                minlength: 6
            },
            checkCode: {
                required: true,
            }

        },
        messages: {
            account: "请输入账号！", //必填字段
            password: {
                required: "请输入密码！", //必填字段
            },
            checkCode: "请输入验证码！",
        },
        //执行登陆校验
        submitHandler: function(form) { //通过之后回调
            alert("我来了...!");
            //进行ajax传值

        },
        invalidHandler: function(form, validator) {
            alert("校验规则");
            return false;
        },
        highlight: function(element) {
            if ($(element).offsetParent().parent().hasClass('form-group')) {
                $(element).offsetParent().parent().removeClass('has-success').addClass('has-error');
            } else {
                if ($(element).attr('type')=='file') {
                    $(element).parent().parent().removeClass('has-success').addClass('has-error');
                }
                $(element).offsetParent().parent().parent().parent().removeClass('has-success').addClass('has-error');

            }
        },
        unhighlight: function(element,errorClass) {
            if ($(element).offsetParent().parent().hasClass('form-group')) {
                $(element).offsetParent().parent().removeClass('has-error').addClass('has-success');
                $(element.form).find("label[for=" + element.id + "]").removeClass(errorClass);
            } else if ($(element).offsetParent().parent().hasClass('checkbox')) {
                $(element).offsetParent().parent().parent().parent().removeClass('has-error').addClass('has-success');
                $(element.form).find("label[for=" + element.id + "]").removeClass(errorClass);
            } else if ($(element).next().hasClass('bootstrap-filestyle')) {
                $(element).parent().parent().removeClass('has-error').addClass('has-success');
            }
            else {
                $(element).offsetParent().parent().parent().removeClass('has-error').addClass('has-success');
            }
        }
    });

});