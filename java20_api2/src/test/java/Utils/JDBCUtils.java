package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
public class JDBCUtils {
    public static Connection getConnection(){
        /**
         *         定义数据库链接
         *
          */
        Connection connection =null;
        try {
            connection = DriverManager.getConnection(Constants.URL, Constants.USER, Constants.PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
    public static void close(Connection connection){
        if (connection!=null){
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
