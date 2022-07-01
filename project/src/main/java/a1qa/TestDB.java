package a1qa;

import dao.AuthorDao;
import dao.ProjectDao;
import dao.SessionDao;
import dao.TestDao;
import entity.Author;
import entity.Project;
import entity.Session;
import org.testng.*;
import org.testng.annotations.*;
import util.JsonUtil;

import java.sql.Date;

public class TestDB {
    private Project savedProject;
    private Session savedSes;
    private Author savedAuthor;
    private static final String SYSTEM_ENV_USER = "USER";
    private static final String PATH_TO_CONFIG = "../project/src/main/resources/config.json";

    @BeforeTest
    public void beTest(ITestContext context) {
        var projectDao = ProjectDao.getInstance();
        var newPr = new Project();
        newPr.setName(context.getName());
        savedProject = projectDao.save(newPr);

        var sessionDao = SessionDao.getInstance();
        var newSes = new Session();
        newSes.setSession_key(context.getCurrentXmlTest().getName());
        var buildNumber = 1L;
        var prevSession = sessionDao.findBySessionKey(context.getCurrentXmlTest().getName());
        if (prevSession != null)
            buildNumber += prevSession.getBuild_number();
        newSes.setBuild_number(buildNumber);
        newSes.setCreated_time(new Date(context.getStartDate().getTime()));
        savedSes = sessionDao.save(newSes);

        var authorDao = AuthorDao.getInstance();
        var newAuthor = new Author();
        newAuthor.setName((String) JsonUtil.smartReadJson(PATH_TO_CONFIG, "author_name"));
        newAuthor.setLogin((String) JsonUtil.smartReadJson(PATH_TO_CONFIG, "author_login"));
        newAuthor.setEmail((String) JsonUtil.smartReadJson(PATH_TO_CONFIG, "author_email"));
        savedAuthor = authorDao.save(newAuthor);
    }

    @Test
    public void test() {
        Assert.assertEquals("test", "test", "Test isn't test");
    }

    @AfterMethod
    public void afTest(ITestResult result, ITestContext context) {
        var testDao = TestDao.getInstance();
        var newTest = new entity.Test();
        newTest.setName(result.getTestContext().getName());
        newTest.setStatus_id(result.getStatus());
        newTest.setMethod_name(result.getInstanceName());
        newTest.setProject_id(savedProject.getId());
        newTest.setSession_id(savedSes.getId());
        newTest.setStart_time(new Date(result.getStartMillis()));
        newTest.setEnd_time(new Date(result.getEndMillis()));
        newTest.setEnv(System.getenv().get(SYSTEM_ENV_USER));
        newTest.setBrowser((String)JsonUtil.smartReadJson(PATH_TO_CONFIG, "browser"));
        newTest.setAuthor_id(savedAuthor.getId());
        var savedTest = testDao.save(newTest);
        Assert.assertEquals(savedTest, newTest, "Saved test wasn't added or data of test isn't equals expected");
    }
}
