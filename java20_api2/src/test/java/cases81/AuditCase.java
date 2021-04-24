package cases81;

import Utils.Constants;
import Utils.ExcelUtils;
import Utils.HttpUtils;
import groovy.cli.Option;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.CaseInfo;

import java.util.List;
import java.util.Map;

public class AuditCase extends BaseCase {
    private static Logger logger = Logger.getLogger(AuditCase.class);

    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) {
        //1、参数化替换
        paramsReplace(caseInfo);
        //获取鉴权头
        Map<String, String> authorization = getAuthorization();
        //3、调用注册接口
        String body = HttpUtils.call(caseInfo, authorization);
        //4、添加接口相应回写内容
        addWriteBackData(sheetIndex, caseInfo.getId(), Constants.RESPONSE_CELL_NUM, body);
        //6、响应断言结果
        boolean responseAssertFlag = responseAssert(caseInfo.getExpectedResult(), body);
        //8、添加断言回写内容
        String assertResult = responseAssertFlag ? "PASSED" : "FAILED";
        addWriteBackData(sheetIndex, caseInfo.getId(), Constants.ASSERT_CELL_NUM, assertResult);
        //10、报表断言，断言失败应该在报表中提现
        Assert.assertEquals(assertResult,"PASSED");
    }

    @DataProvider
    public Object[] datas() throws Exception {
        List<CaseInfo> list = ExcelUtils.read(sheetIndex, 1, CaseInfo.class);
        return list.toArray();
    }
}

