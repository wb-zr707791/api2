package cases81;

import Utils.*;
import com.alibaba.fastjson.JSONPath;
import groovy.cli.Option;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pojo.CaseInfo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RechargeCase extends BaseCase {
    private static Logger logger = Logger.getLogger(RechargeCase.class);

    @Test(dataProvider = "data")
    public void test(CaseInfo caseInfo) {
        //1、参数化替换
        paramsReplace(caseInfo);
        //获取鉴权头
        Map<String, String> authorization = getAuthorization();
        //2、数据库前置查询结果
        BigDecimal beforeSqlResult = (BigDecimal) SQLUtils.getSingleResult(caseInfo.getSql());
        //3、调用注册接口
        String body = HttpUtils.call(caseInfo, authorization);
        //4、添加接口相应回写内容
        addWriteBackData(sheetIndex, caseInfo.getId(), Constants.RESPONSE_CELL_NUM, body);
        //5、数据库后置查询结果
        BigDecimal afterSqlResult = (BigDecimal) SQLUtils.getSingleResult(caseInfo.getSql());
        //6、响应断言结果
        boolean responseAssertFlag = responseAssert(caseInfo.getExpectedResult(), body);
        //数据库断言
        //7、afterSqlResult-beforeSqlResult = 参数重amount
        //取出参数中的amount
        Boolean sqlAssertFlag = sqlAssert(caseInfo, beforeSqlResult, afterSqlResult);
        //8、添加断言回写内容
        String assertResult = responseAssertFlag && sqlAssertFlag ? "PASSED" : "FAILED";
        addWriteBackData(sheetIndex, caseInfo.getId(), Constants.ASSERT_CELL_NUM, assertResult);
        //10、报表断言，断言失败应该在报表中提现
        Assert.assertEquals(assertResult,"PASSED");
    }

    /**
     * 数据库断言
     *
     * @param caseInfo
     * @param beforeSqlResult
     * @param afterSqlResult
     */
    public Boolean sqlAssert(CaseInfo caseInfo, BigDecimal beforeSqlResult, BigDecimal afterSqlResult) {
        boolean flag = false;
        if (StringUtils.isNotBlank(caseInfo.getSql())) {
            String amountStr = JSONPath.read(caseInfo.getParams(), "$.amount").toString();
            //String -> bigDecimal
            BigDecimal amount = new BigDecimal(amountStr);
            //afterSqlResult.subtract(beforeSqlResult)等价于 this-subtract()
            //compareTo()返回值是01-1 ，-1的话就是比它小，0就是相等。1就是比它大
            if (afterSqlResult.subtract(beforeSqlResult).compareTo(amount) == 0) {
                logger.info("数据库断言：pass");
                flag = true;
            } else {
                logger.info("数据库断言：fail");
            }
        }
        return flag;
    }

    /**
     * 返回值类型是一个数组,一般情况下两个参数使用二维数组
     * @return
     * @throws Exception
     */
    @DataProvider
    public Object[] data() throws Exception {
        List<CaseInfo> list = ExcelUtils.read(sheetIndex, 1, CaseInfo.class);
        return list.toArray();
    }
}

