package dao;

import com.google.common.base.Optional;
import entity.Session;
import exception.DaoException;
import util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SessionDao {
    private static final SessionDao INSTANCE = new SessionDao();
    private static final String DELETE_SQL = """
            DELETE FROM union_reporting.session
            WHERE id = ?;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO union_reporting.session (session_key, created_time, build_number)
            VALUES (?, ?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE union_reporting.session
            SET session_key = ?,
                created_time = ?,
                build_number = ?
            WHERE id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id,
                session_key,
                created_time,
                build_number
            FROM union_reporting.session
            """;

    private static final String FIND_BY_ID = FIND_ALL_SQL + """
            WHERE id = ?;
            """;

    private static final String FIND_BY_SESSION_KEY = FIND_ALL_SQL + """
            WHERE session_key = ?;
            """;

    private SessionDao() {
    }

    public List<Session> findAll() {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();
            List<Session> sessionList = new ArrayList<>();
            while (resultSet.next()) {
                sessionList.add(buildSession(resultSet));
            }
            return sessionList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Session findById(Long id) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID)) {
            prepareStatement.setLong(1, id);

            var resultSet = prepareStatement.executeQuery();
            Session session = null;
            if (resultSet.next()) {
                session = buildSession(resultSet);
            }
            return session;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Session findBySessionKey(String sessionKey) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_SESSION_KEY)) {
            prepareStatement.setString(1, sessionKey);

            var resultSet = prepareStatement.executeQuery();
            Session session = null;
            while (resultSet.next()) {
                session = buildSession(resultSet);
            }
            return session;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Session buildSession(ResultSet resultSet) throws SQLException {
        return new Session(
                resultSet.getLong("id"),
                resultSet.getString("session_key"),
                resultSet.getDate("created_time"),
                resultSet.getLong("build_number")
        );
    }

    public void update(Session session) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setString(1, session.getSession_key());
            prepareStatement.setDate(2, session.getCreated_time());
            prepareStatement.setLong(3, session.getBuild_number());

            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Session save(Session session) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)
        ) {
            prepareStatement.setString(1, session.getSession_key());
            prepareStatement.setDate(2, session.getCreated_time());
            prepareStatement.setLong(3, session.getBuild_number());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next())
                session.setId(generatedKeys.getLong(1));
            return session;
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

    public static SessionDao getInstance() {
        return INSTANCE;
    }
}
