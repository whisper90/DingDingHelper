package com.ucmap.dingdinghelper.utils;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public interface Constants {
    String IS_SIGN = "is_sign";
    String ACCOUNT = "account";
    String PASSWORD = "password";


    String DATE = "date";


    String MORNING_CHECK_IN_TIME = "MORNING_CHECK_IN_TIME";
    String AFTERNOON_CHECK_IN_TIME = "AFTERNOON_CHECK_IN_TIME";

    String ACCOUNT_LIST = "acccount_list";


    String POINT_SERVICES_ORDER = "settings put secure enabled_accessibility_services  com.ucmap.dingdinghelper/com.ucmap.dingdinghelper.services.DingDingHelperAccessibilityService";

    String ENABLE_SERVICE_PUT = "settings put secure accessibility_enabled 1";

    String DISENABLE_SERVICE_PUT = "settings put secure accessibility_enabled 0";


}
