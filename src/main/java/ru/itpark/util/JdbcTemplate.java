package ru.itpark.util;

import ru.itpark.exception.DataAccesException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class JdbcTemplate {

    private static <T> T execute(String dataSource, String sql, PreparedStatementExecutor<T> executor) {
        try (
                Connection connection = DriverManager.getConnection(dataSource);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            return executor.execute(preparedStatement);
        } catch (SQLException e) {
            throw new DataAccesException();
        }
    }

    //    public static void executeInit(DataSource dataSource, String sql) {
//        execute(dataSource, sql, pstmt -> pstmt.execute());
//    }
    public static void executeInit(String dataSource, String sql) {

        try (
                Connection connection = DriverManager.getConnection(dataSource);
                Statement statement = connection.createStatement()

        ) {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void executeCreateQuery(String dataSource, String sql, PreparedStatementSetter setter) {
        execute(dataSource, sql, pstmt -> {
            setter.set(pstmt);
            pstmt.execute();
            return null;
        });
    }

    public static <T> List<T> executeQuery(String dataSource, String sql, RowMapper<T> mapper) {
        return execute(dataSource, sql, pstmt -> {
            try (ResultSet resultSet = pstmt.executeQuery()) {
                List<T> result = new LinkedList<>();
                while (resultSet.next()) {
                    result.add(mapper.map(resultSet));
                }
                return result;
            }
        });
    }
}
