package cases81;

import Utils.*;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.*;
import pojo.CaseInfo;

import java.util.List;

/*
testNG框架驱动test方法，dataProvider
excelutils使用eastpoi读取数据并返回list
datas接受list集合转成数组返回
test执行，使用caseinfo中url和params
把url和params传入httputils.post();
*/
public class RegisterCase2 extends BaseCase {

    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) {
//        接口自动化：
//    1、参数化替换
        paramsReplace(caseInfo);
//    2、数据库前置查询结果（数据断言必须在执行前后都查询）
        Long beforeSqlResult = (Long) SQLUtils.getSingleResult(caseInfo.getSql());
//    3、调用接口
        String body = HttpUtils.call(caseInfo, UserData.DEFAULT_HEADERS);
//    4、断言响应结果：断言 期望值和实际值匹配
        boolean responseAssertFlag = responseAssert(caseInfo.getExpectedResult(), body);
//    5、添加接口响应回写内容
        addWriteBackData(sheetIndex, caseInfo.getId(), Constants.RESPONSE_CELL_NUM, body);
//    6、数据库后置查询结果
        Long afterSqlResult = (Long) SQLUtils.getSingleResult(caseInfo.getSql());
//    7、数据库断言  (根据打印语句，得到结果是long类型)

        //如果sql为空，则不需要数据库断言
        Boolean sqlAssertFlag = sqlAssert(caseInfo, beforeSqlResult, afterSqlResult);
        //8、添加断言回写内容
        String assertResult = responseAssertFlag && sqlAssertFlag ? "PASSED" : "FAILED";
        addWriteBackData(sheetIndex, caseInfo.getId(), Constants.ASSERT_CELL_NUM, assertResult);
//    9、添加日志
        //10、报表断言，断言失败应该在报表中提现
        Assert.assertEquals(assertResult, "PASSED");

    }

    /**
     * 数据库断言是和业务逻辑绑定的，每个接口的业务逻辑不一样，所以数据库断言方法不能抽取到父类方法中，供其他子类使用
     *
     * @param caseInfo
     * @param beforeSqlResult
     * @param afterSqlResult
     */
    public Boolean sqlAssert(CaseInfo caseInfo, Long beforeSqlResult, Long afterSqlResult) {
        boolean flag = false;
        if (StringUtils.isNotBlank(caseInfo.getSql())) {
            System.out.println("before:" + beforeSqlResult);
            System.out.println("after:" + afterSqlResult);
            if (beforeSqlResult == 0 && afterSqlResult == 1) {
                System.out.println("数据库断言：pass");
                flag = true;
            } else {
                System.out.println("数据库断言：fail");
            }
        }
        return false;
    }

    @DataProvider
    public Object[] datas() throws Exception {
        List list = ExcelUtils.read(sheetIndex, 1, CaseInfo.class);
        return list.toArray();
    }
}
