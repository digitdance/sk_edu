package org.springcrazy.common.enums;


public enum SettingNameEnum {
    CLASS_CLOSE_WITH_GRADUATE_STUDENT("class_close_with_graduate_student", "班级结业时班内学员自动结业", "true", "bool"),
    CLASS_AUTO_CREATE_ON_ONE2ONE("class_auto_create_on_one2one", "报名1V1课程时自动生成班级", "true", "bool"),
    LESSON_COUNT_LESS_WARNING_COUNT("lesson_count_less_warning_count", "课次数不足预警数量", "5", "int"),
    LESSON_COUNT_LESS_WARNING_TIMES("lesson_count_less_warning_times", "课次数不足提醒次数", "2", "int"),
    STUDENT_DEFAULT_PWD("student_default_pwd", "家长端初始密码", "1234567", "str"),
    SMS_ACCESS_KEY("sms_access_key", "短信接口KEY", "", "str"),
    SMS_ACCESS_SECRET("sms_access_secret", "短信接口秘钥", "", "str"),
    SMS_SIGN_NAME("sms_sign_name", "短信签名", "", "str"),
    SMS_CODE_TEMPLATE("sms_code_template", "验证码短信模板ID", "", "str"),
    SMS_TXY_SECRET_ID("sms_txy_secret_id", "短信接口id", "", "id"),
    SMS_TXY_SECRET_KET("sms_txy_secret_key", "短信接口key", "", "key"),
    SMS_TXY_SDK_APP_ID("sms_txy_app_id", "短信应用ID", "", "appid"),
    SMS_TXY_SIGN_NAME("sms_txy_secret_key", "短信签名内容", "", "短信签名"),
    WX_MP_APP_ID("wx_mp_app_id", "公众号AppId", "", "str"),
    WX_MP_SECRET("wx_mp_secret", "公众号Secret", "", "str"),
    WX_MP_TOKEN("wx_mp_token", "公众号Token", "", "str"),
    WX_MP_AES_KEY("wx_mp_aes_key", "公众号AesKey", "", "str"),
    WX_PAY_MCHID("wx_pay_mchid", "微信支付商户号", "", "str"),
    WX_PAY_MCHKEY("wx_pay_mchkey", "微信支付商户密钥", "", "str"),
    WX_PAY_SUBAPPID("wx_pay_subappid", "服务商模式子商户公众账号ID", "", "str"),
    WX_PAY_SUBMCHID("wx_pay_submchid", "服务商模式子商户号", "", "str"),
    WX_PAY_KEYPATH("wx_pay_keypath", "apiclient_cert.p12文件的绝对路径", "", "str"),
    LESSON_REMIND_TIME("lesson_remind_time", "每天上课提醒时间", "19:31", "time"),
    LESSON_COUNT_WARNING_TIME("lesson_count_warning_time", "每天上课次数预警时间", "07:01", "time"),
    STUDENT_CENTER_CAN_SIGN("student_center_can_sign", "关闭后只能在管理端通过老师点名消课", "false", "bool"),
    STUDENT_CAN_SIGN_WHOLE_DAY("student_can_sign_whole_day", "学生端开启签到功能才生效", "false", "bool"),
    DECREASE_LESSON_WHEN_ABSENCE("decrease_lesson_when_absence", "默认点名旷课时是否扣课时", "false", "bool"),
    AUTO_JOIN_LESSON_BY_APPOINTMENT("auto_join_lesson_by_appointment", "学员预约后自动加入到课时", "true", "bool"),
    COURSE_TRIAL_HELP("course_trial_help", "试听卡使用规则说明", "领取试听卡后可以到课时表里预约课程。", "str"),
    ORDER_REFUND_LIMIT_HOUR("order_refund_limit_hour", "在线支付后可申请退款的小时数(0表示不限制)", "24", "int"),
    ORDER_NOTICE_MOBILE("order_notice_mobile", "学员在线购课后，若无顾问则通知该账号", "", "str"),
    STUDENT_LEAVE_NOTIFY_MOBILE("student_leave_notify_mobile", "学生请假后，会额外提醒该账号", "", "str"),
    STUDENT_LEAVE_LIMIT_HOUR("student_leave_limit_hour", "学生在上课前多少小时可以请假", "2", "int"),
    STUDENT_MOBILE_MARKER_SWITCH("student_mobile_marker_switch", "是否脱敏学生的手机号码", "true", "bool"),
    TEACHER_SIGN_LIMIT_BEFORE_LESSON("teacher_sign_limit_before_lesson", "老师在上课前多少小时签到", "24", "int"),
    TEACHER_SIGN_LIMIT_AFTER_LESSON("teacher_sign_limit_after_lesson", "老师在下课后多少小时可签到", "24", "int");

    private String code;
    private String desc;
    private String defaultValue;
    private String valueType;

    private SettingNameEnum(String code, String desc, String defaultValue, String valueType) {
        this.code = code;
        this.desc = desc;
        this.defaultValue = defaultValue;
        this.valueType = valueType;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String getValueType() {
        return this.valueType;
    }
}
