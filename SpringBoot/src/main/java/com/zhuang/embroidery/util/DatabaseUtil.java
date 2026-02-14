package com.zhuang.embroidery.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接工具类
 * 用于获取数据库连接和管理连接资源
 */
@Component
public class DatabaseUtil {

    @Autowired
    private DataSource dataSource;

    /**
     * 获取数据库连接
     *
     * @return 数据库连接对象
     * @throws SQLException 如果获取连接失败
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 关闭数据库连接
     *
     * @param connection 要关闭的连接对象
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查数据库连接是否有效
     *
     * @return 连接是否有效
     */
    public boolean isConnectionValid() {
        try (Connection connection = getConnection()) {
            return connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

}
