package dao;

import entity.Author;
import exception.DaoException;
import util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AuthorDao {
    private static final AuthorDao INSTANCE = new AuthorDao();
    private static final String DELETE_SQL = """
            DELETE FROM union_reporting.author
            WHERE id = ?;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO union_reporting.author (name, login, email)
            VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name = ?;
            """;

    private static final String SELECT_BY_NAME = """
            SELECT *
            FROM union_reporting.author
            WHERE union_reporting.author.name = ?;
            """;
    private static final String UPDATE_SQL = """
            UPDATE union_reporting.author
            SET name = ?,
                login = ?,
                email = ?
            WHERE id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, name, login, email,
            FROM union_reporting.author
            """;

    private static final String FIND_BY_ID = FIND_ALL_SQL + """
            WHERE id = ?;
            """;

    private static final String FIND_BY_NAME = FIND_ALL_SQL + """
            WHERE name = ?;
            """;

    private AuthorDao() {

    }

    public List<Author> findAll() {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();
            List<Author> authorsList = new ArrayList<>();
            while (resultSet.next()) {
                authorsList.add(buildAuthor(resultSet));
            }
            return authorsList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Author findById(Long id) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID)) {
            prepareStatement.setLong(1, id);

            var resultSet = prepareStatement.executeQuery();
            Author author = null;
            if (resultSet.next()) {
                author = buildAuthor(resultSet);
            }
            return author;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Author findByName(String name) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_NAME)) {
            prepareStatement.setString(1, name);

            var resultSet = prepareStatement.executeQuery();
            Author author = null;
            if (resultSet.next()) {
                author = buildAuthor(resultSet);
            }
            return author;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Author buildAuthor(ResultSet resultSet) throws SQLException {
        return new Author(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("login"),
                resultSet.getString("email")
        );
    }

    public void update(Author author) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setLong(1, author.getId());
            prepareStatement.setString(2, author.getName());
            prepareStatement.setString(3, author.getLogin());
            prepareStatement.setString(4, author.getEmail());

            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Author save(Author author) {
        try (var connection = ConnectionManager.open();
             var firstPrepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)
        ) {
            firstPrepareStatement.setString(1, author.getName());
            firstPrepareStatement.setString(2, author.getLogin());
            firstPrepareStatement.setString(3, author.getEmail());
            firstPrepareStatement.setString(4, author.getName());

            firstPrepareStatement.executeUpdate();

            var generatedKeys = firstPrepareStatement.getGeneratedKeys();
            if (generatedKeys.next())
                author.setId(generatedKeys.getLong("id"));
            else
            {
                try (var secondPrepareStatement = connection.prepareStatement(SELECT_BY_NAME, Statement.RETURN_GENERATED_KEYS)) {
                    secondPrepareStatement.setString(1, author.getName());

                    var resultSet = secondPrepareStatement.executeQuery();
                    if (resultSet.next()) {
                        author = buildAuthor(resultSet);
                    }
            } catch (SQLException e) {
                    throw new DaoException(e);
                }
            }
            return author;
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

    public static AuthorDao getInstance() {
        return INSTANCE;
    }
}
