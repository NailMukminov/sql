package dao;

import entity.Test;
import exception.DaoException;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestDao {
    private static final TestDao INSTANCE = new TestDao();
    private static final String DELETE_SQL = """
            DELETE FROM union_reporting.test
            WHERE id = ?;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO union_reporting.test (name, status_id, method_name, project_id, session_id, start_time, end_time, env, browser, author_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE union_reporting.test
            SET name = ?,
                status_id = ?,
                method_name = ?,
                project_id = ?,
                session_id = ?,
                start_time = ?,
                end_time = ?,
                env = ?,
                browser = ?,
                author_id = ?
            WHERE id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id,
                name,
                status_id,
                method_name,
                project_id,
                session_id,
                start_time,
                end_time,
                env,
                browser
            FROM union_reporting.test
            """;

    private static final String FIND_BY_ID = FIND_ALL_SQL + """
            WHERE id = ?;
            """;

    private TestDao() {
    }

    public List<Test> findAll() {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();
            List<Test> testList = new ArrayList<>();
            while (resultSet.next()) {
                testList.add(buildTest(resultSet));
            }
            return testList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Test findById(Long id) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID)) {
            prepareStatement.setLong(1, id);

            var resultSet = prepareStatement.executeQuery();
            Test test = null;
            if (resultSet.next()) {
                test = buildTest(resultSet);
            }
            return test;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Test buildTest(ResultSet resultSet) throws SQLException {
        return new Test(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getInt("status_id"),
                resultSet.getString("method_name"),
                resultSet.getLong("project_id"),
                resultSet.getLong("session_id"),
                resultSet.getDate("start_time"),
                resultSet.getDate("end_time"),
                resultSet.getString("env"),
                resultSet.getString("browser")
        );
    }

    public void update(Test test) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setString(1, test.getName());
            prepareStatement.setInt(2, test.getStatus_id());
            prepareStatement.setString(3, test.getMethod_name());
            prepareStatement.setLong(4, test.getProject_id());
            prepareStatement.setLong(5, test.getSession_id());
            prepareStatement.setDate(6, test.getStart_time());
            prepareStatement.setDate(7, test.getEnd_time());
            prepareStatement.setString(8, test.getEnv());
            prepareStatement.setString(9, test.getBrowser());
            prepareStatement.setLong(10, test.getAuthor_id());
            prepareStatement.setLong(11, test.getId());

            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Test save(Test test) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)
        ) {
            prepareStatement.setString(1, test.getName());
            prepareStatement.setInt(2, test.getStatus_id());
            prepareStatement.setString(3, test.getMethod_name());
            prepareStatement.setLong(4, test.getProject_id());
            prepareStatement.setLong(5, test.getSession_id());
            prepareStatement.setDate(6, test.getStart_time());
            prepareStatement.setDate(7, test.getEnd_time());
            prepareStatement.setString(8, test.getEnv());
            prepareStatement.setString(9, test.getBrowser());
            if (test.getAuthor_id() == null)
                prepareStatement.setNull(10, Types.BIGINT);
            else
                prepareStatement.setLong(10, test.getAuthor_id());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next())
                test.setId(generatedKeys.getLong(1));
            return test;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean delete(Long id) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {
            prepareStatement.setLong(1, id);
            return prepareStatement.executeUpdate() > 1;
        } catch (SQLException throwable) {
            throw new DaoException(throwable);
        }
    }

    public static TestDao getInstance() {
        return INSTANCE;
    }
}
