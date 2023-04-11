package ccw.serviceinnovation.oss.manager.authority.api;

import ccw.serviceinnovation.common.request.ResultCode;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlToMarkdown {

    public static void createMarkDownFromMysql() {
        String url = "jdbc:mysql://101.35.43.156:3306/oss";
        String user = "root";
        String password = "123abc456";

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name,type,target,description FROM api")) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            PrintWriter writer = new PrintWriter(new FileWriter("D:\\API文档\\output.md"));

            // 输出表头
            for (int i = 1; i <= columnCount; i++) {
                writer.write("| " + meta.getColumnName(i) + " ");
            }
            writer.write("|\n");

            // 输出分隔线
            for (int i = 1; i <= columnCount; i++) {
                writer.write("| --- ");
            }
            writer.write("|\n");

            // 输出表数据
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    writer.write("| " + rs.getString(i) + " ");
                }
                writer.write("|\n");
            }

            writer.close();

            System.out.println("MySQL 数据库已成功转换为 Markdown 表格并保存到文件 output.md 中。");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void get() throws Exception {
        Integer columnCount = 2;


        PrintWriter writer = new PrintWriter(new FileWriter("D:\\API文档\\output1.md"));

        // 输出表头
        writer.write("| " + "Code" + " ");
        writer.write("| " + "Msg" + " ");
        writer.write("|\n");

        // 输出分隔线
        for (int i = 1; i <= columnCount; i++) {
            writer.write("| --- ");
        }
        writer.write("|\n");
        for (ResultCode value : ResultCode.values()) {
            writer.write("| " + value.getCode() + " ");
            writer.write("| " + value.getMessage() + " ");
            writer.write("|\n");
        }

        writer.close();

        System.out.println("MySQL 数据库已成功转换为 Markdown 表格并保存到文件 output.md 中。");

    }

    public static void main(String[] args) {
        try {
            createMarkDownFromMysql();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
