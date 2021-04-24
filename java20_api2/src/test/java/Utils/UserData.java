package Utils;

import cn.binarywang.tools.generator.ChineseMobileNumberGenerator;
import java.util.HashMap;
import java.util.Map;

public class UserData {
    //存储接口响应变量
    public static Map<String,Object> VARS = new HashMap<>();
    //储存默认请求头
    public static Map<String,String> DEFAULT_HEADERS = new HashMap<>();

    static {
        //静态代码块：类在加载时自动加载一次本代码。
        //静态方法需要手动调用，静态代码块类加载自动执行一次。
        DEFAULT_HEADERS.put("X-Lemonban-Media-Type","lemonban.v2");
        DEFAULT_HEADERS.put("Content-Type","application/json");
        //把需要参数化的数据存储到VARS
        //随机手机号码
        VARS.put("${register_mb}", ChineseMobileNumberGenerator.getInstance().generate());
        VARS.put("${register_pwd}", "12345678");
        VARS.put("${amount}", "5000");
    }
}
