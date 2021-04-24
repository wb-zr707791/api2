package cases81;

import Utils.Constants;
import Utils.ExcelUtils;
import Utils.HttpUtils;
import Utils.UserData;
import groovy.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.*;
import pojo.CaseInfo;
import java.util.List;
import java.util.Set;

public class LoginCase extends BaseCase {
    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) {
        //1、参数化替换 现在只有占位符，没有真是的手机号码和密码
        //sql:select count(*) from member a where a.mobile_phone = '${register_mb}'
        //params:{"mobile_phone":"${register_mb}","pwd":"${register_pwd}"}
        paramsReplace(caseInfo);
        //3、调用接口
        String body = HttpUtils.call(caseInfo, UserData.DEFAULT_HEADERS);
        //使用jsonPath从响应体中取出token和memberId
        getParamsInUserData(body, "$.data.token_info.token", "${token}");
        getParamsInUserData(body, "$.data.id", "${member_id}");
        //  4、断言响应结果
        boolean responseAssertFlag = responseAssert(caseInfo.getExpectedResult(), body);
        //5、添加接口响应回写内容
        addWriteBackData(sheetIndex, caseInfo.getId(), Constants.RESPONSE_CELL_NUM, body);
        //8、添加断言回写内容
        String assertResult = responseAssertFlag ? "PASSED" : "FAILED";
        addWriteBackData(sheetIndex, caseInfo.getId(), Constants.ASSERT_CELL_NUM, assertResult);
        //9、添加日志，添加到注册类中了
        //10、报表断言，断言失败应该在报表中提现
        Assert.assertEquals(assertResult,"PASSED");
    }



    @DataProvider
    public Object[] datas() throws Exception {
        List<CaseInfo> list = ExcelUtils.read(sheetIndex, 1, CaseInfo.class);
        return list.toArray();
    }
}

