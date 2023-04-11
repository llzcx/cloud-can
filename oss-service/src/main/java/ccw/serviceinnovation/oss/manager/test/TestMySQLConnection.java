package ccw.serviceinnovation.oss.manager.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestMySQLConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/oss?useSSL=false&amp&serverTimezone=UTC"; // 数据库连接地址
        String user = "root"; // 数据库用户名
        String password = "chenxiang"; // 数据库密码

        Connection conn = null;

        try {
            // 加载MySQL驱动程序
            Class.forName("com.mysql.jdbc.Driver");

            // 建立连接
            conn = DriverManager.getConnection(url, user, password);

            if (conn != null) {
                System.out.println("MySQL数据库连接成功！");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("找不到MySQL驱动程序！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("MySQL数据库连接失败！");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
