package Utils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;
import pojo.Member;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class SQLUtils {
    public static void main(String[] args) throws Exception {
        query();
        update();
        BeanListHanderShow();
    }

    public static void query() {
    }

    /**
     * sql查询方法
     * @param sql
     * @return
     */
    public static Object getSingleResult(String sql) {
        if (StringUtils.isBlank(sql)) {
            return null;
        }
        //1、定义返回值
        Object result = null;
        try {
            //2、创建DBUtils sql语句操作类
            QueryRunner runner = new QueryRunner();
            //3、获取数据库连接
            Connection conn = JDBCUtils.getConnection();
            //4、创建ScalarHandler，针对单行单列的数据
            ScalarHandler scalarHandler = new ScalarHandler<>();
            //5、执行sql语句
            result = runner.query(conn, sql, scalarHandler);
            //6、关闭数据库连接
            JDBCUtils.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void BeanListHanderShow() {
        QueryRunner runner = new QueryRunner();
        String sql = "SELECT leave_amount FROM member where id = 1;";
        Connection connection = JDBCUtils.getConnection();
        int count = 0;
        try {

            BeanHandler handler = new BeanHandler(Member.class);
            Member member = (Member) runner.query(connection, sql, handler);
            System.out.println(member);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(count);
        JDBCUtils.close(connection);
    }

    public static void ScalarHandlerShow() {
        QueryRunner runner = new QueryRunner();
        String sql = "SELECT leave_amount FROM member where id = 1;";
        Connection connection = JDBCUtils.getConnection();
        int count = 0;
        try {
            ScalarHandler<BigDecimal> scalarHandler = new ScalarHandler();
            BigDecimal query = runner.query(connection, sql, scalarHandler);
            System.out.println(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(count);
        JDBCUtils.close(connection);
    }

    public static void MapHandlerShow() {
        QueryRunner runner = new QueryRunner();
        String sql = "SELECT leave_amount FROM member where id = 1;";
        Connection connection = JDBCUtils.getConnection();
        int count = 0;
        try {
            MapHandler mapHandler = new MapHandler();
            Map<String, Object> query = runner.query(connection, sql, mapHandler);
            System.out.println(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(count);
        JDBCUtils.close(connection);
    }

    public static void update() throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "update member set leave_amount = 200 where id = 1;";
        Connection connection = JDBCUtils.getConnection();
        int count = runner.update(connection, sql);
        System.out.println(count);
        JDBCUtils.close(connection);
    }

    public static void insert() throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "insert into member values(null,'周大哥','123456','123456789',1,998877,NOW());";
        Connection connection = JDBCUtils.getConnection();
        int count = runner.update(connection, sql);
        System.out.println(count);
        JDBCUtils.close(connection);
    }
}
