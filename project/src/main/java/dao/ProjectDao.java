package dao;

import com.google.common.base.Optional;
import entity.Project;
import exception.DaoException;
import util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProjectDao {
    private static final ProjectDao INSTANCE = new ProjectDao();
    private static final String DELETE_SQL = """
            DELETE FROM union_reporting.project
            WHERE id = ?;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO union_reporting.project (name)
            VALUES (?) ON DUPLICATE KEY UPDATE name = ?;
            """;
    private static final String SELECT_BY_NAME = """
            SELECT *
            FROM project
            WHERE name = ?
            """;
    private static final String UPDATE_SQL = """
            UPDATE union_reporting.project
            SET name = ?
            WHERE id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, name
            FROM union_reporting.project
            """;

    private static final String FIND_BY_ID = FIND_ALL_SQL + """
            WHERE id = ?;
            """;

    private static final String FIND_BY_NAME = FIND_ALL_SQL + """
            WHERE name = ?;
            """;

    private ProjectDao() {
    }

    public List<Project> findAll() {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = prepareStatement.executeQuery();
            List<Project> projectList = new ArrayList<>();
            while (resultSet.next()) {
                projectList.add(buildProject(resultSet));
            }
            return projectList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Project findById(Long id) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID)) {
            prepareStatement.setLong(1, id);

            var resultSet = prepareStatement.executeQuery();
            Project project = null;
            if (resultSet.next()) {
                project = buildProject(resultSet);
            }
            return project;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Project findByName(String name) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(FIND_BY_NAME)) {
            prepareStatement.setString(1, name);

            var resultSet = prepareStatement.executeQuery();
            Project project = null;
            if (resultSet.next()) {
                project = buildProject(resultSet);
            }
            return project;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Project buildProject(ResultSet resultSet) throws SQLException {
        return new Project(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }

    public void update(Project project) {
        try (var connection = ConnectionManager.open();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setLong(1, project.getId());
            prepareStatement.setString(2, project.getName());

            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Project save(Project project) {
        try (var connection = ConnectionManager.open();
             var firstPrepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)
        ) {
            firstPrepareStatement.setString(1, project.getName());
            firstPrepareStatement.setString(2, project.getName());

            firstPrepareStatement.executeUpdate();

            var generatedKeys = firstPrepareStatement.getGeneratedKeys();
            if (generatedKeys.next())
                project.setId(generatedKeys.getLong(1));
            else
            {
                try (var secondPrepareStatement = connection.prepareStatement(SELECT_BY_NAME, Statement.RETURN_GENERATED_KEYS)) {
                    secondPrepareStatement.setString(1, project.getName());

                    var resultSet = secondPrepareStatement.executeQuery();
                    if (resultSet.next())
                        project = buildProject(resultSet);
                } catch (SQLException e) {
                    throw new DaoException(e);
                }
            }
            return project;
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

    public static ProjectDao getInstance() {
        return INSTANCE;
    }
}
