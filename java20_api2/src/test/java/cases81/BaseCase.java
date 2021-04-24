package cases81;

import Utils.ExcelUtils;
import Utils.UserData;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import pojo.CaseInfo;
import pojo.WriteBackData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseCase {
    /**
     * 日志
     */
    private static Logger logger = Logger.getLogger(BaseCase.class);
    public int sheetIndex;

    @BeforeClass
    @Parameters({"sheetIndex"})
    public void beforeClass(int sheetIndex) {
        //System.out.println("sheetIndex:"+sheetIndex);
        this.sheetIndex = sheetIndex;
    }

    @AfterSuite
    public void finish() throws Exception {
        ExcelUtils.batchWrite();
    }

    /**
     * 添加回写对象到集合中
     *
     * @param sheetIndex
     * @param rowNum
     * @param cellNum
     * @param content
     */
    public void addWriteBackData(int sheetIndex, int rowNum, int cellNum, String content) {
        WriteBackData writeBackData = new WriteBackData(sheetIndex, rowNum, cellNum, content);
        //批量回写，存储到一个list集合中
        ExcelUtils.writeBackDataList.add(writeBackData);
    }

    /**
     * 从responseBody 通过jsonPath取出对应参数，存入到UserData.vars中
     *
     * @param body               接口响应json字符串
     * @param jsonPathExpression jsonPath表达式
     * @param userDataKey        Vars中的Key
     */
    public void getParamsInUserData(String body, String jsonPathExpression, String userDataKey) {
        Object userDataValue = JSONPath.read(body, jsonPathExpression);
        //存储到vars中
        if (userDataValue != null) {
            UserData.VARS.put(userDataKey, userDataValue);
        }
    }

    /**
     * 获取鉴权头，加入默认请求头，并且返回
     *
     * @return
     */
    public HashMap<String, String> getAuthorization() {
        //执行注册接口测试逻辑
        //调用注册接口
        //从用户变量中取出登录接口存入的token值。
        Object token = UserData.VARS.get("${token}");
        HashMap<String, String> headers = new HashMap<String, String>();
        //添加鉴权头
        headers.put("Authorization", "Bearer " + token);
        //添加所有默认头
        headers.putAll(UserData.DEFAULT_HEADERS);
        return headers;
    }

    /**
     * 接口响应断言
     *
     * @param expectedResult
     * @param body
     * @return
     */
    public boolean responseAssert(String expectedResult, String body) {
        //断言响应结果 断言：期望值与实际值匹配就是断言成功
        //{"获取实际值的表达式":期望值,"$.msg":"OK"}
        //期望值转成map
        Map<String, Object> map = JSONObject.parseObject(expectedResult, Map.class);
        //遍历map
        Set<String> keySet = map.keySet();
        boolean responseAssertFlag = false;
        for (String actualValueExpression : keySet) {
            //获取期望值
            Object expectedValue = map.get(actualValueExpression);
            //通过表达式从响应体获取响应值
            Object actualValue = JSONPath.read(body, actualValueExpression);
            //断言
            if (expectedValue.equals(actualValue)) {
                //断言失败
                responseAssertFlag = true;
                break;
            }
        }
        logger.info("断言结果: " + responseAssertFlag);
        return responseAssertFlag;
    }

    @Step("参数化")
    public void paramsReplace(CaseInfo caseInfo) {
        //1、取出所有的占位符和实际值
        Set<String> keySet = UserData.VARS.keySet();
        //2、遍历所有的占位符
        //4、取出需要替换的字段
        String params = caseInfo.getParams();
        String sql = caseInfo.getSql();
        String expectedResult = caseInfo.getExpectedResult();
        String url = caseInfo.getUrl();
        for (String placeHolder : keySet) {
            //3、取出实际值
            String value = UserData.VARS.get(placeHolder).toString();

            //该怎么替换就怎么替换
            if (StringUtils.isNotBlank(params)) {
                //实际值value替换占位符placeHolder
                //将placeHolder的值替换为value
                params = (String) params.replace(placeHolder, value);
            }
            if (StringUtils.isNotBlank(sql)) {
                sql = (String) sql.replace(placeHolder, value);
            }
            if (StringUtils.isNotBlank(expectedResult)) {
                expectedResult = (String) expectedResult.replace(placeHolder, value);
            }
            if (StringUtils.isNotBlank(url)) {
                url = (String) url.replace(placeHolder, value);
            }
        }
        caseInfo.setParams(params);
        caseInfo.setSql(sql);
        caseInfo.setExpectedResult(expectedResult);
        caseInfo.setUrl(url);
        logger.info(caseInfo);
    }
}
