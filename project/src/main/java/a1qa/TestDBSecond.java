package a1qa;

import dao.AuthorDao;
import dao.ProjectDao;
import dao.SessionDao;
import dao.TestDao;
import entity.Author;
import entity.Project;
import entity.Session;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import util.Generator;
import util.JsonUtil;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TestDBSecond {
    private Project savedProject;
    private Session savedSes;
    private Author savedAuthor;
    List<entity.Test> savedTestsList;
    private static final String SYSTEM_ENV_USER = "USER";
    private static final String PATH_TO_CONFIG = "../project/src/main/resources/config.json";
    int amount = 0;

    @Parameters({"amount_copied_row"})
    @BeforeSuite
    public void preSuite(int amount_copied_row, ITestContext context) {
        var testDao = TestDao.getInstance();
        var allTestList = testDao.findAll();
        var idsList = Generator.doubleNumeralsMultiplesOf(amount_copied_row, allTestList.size());
        savedTestsList = new ArrayList<entity.Test>();
        for (var id : idsList) {
            var test = testDao.findById(id.longValue());
            if (test != null) {
                test.setName(context.getName());
                test.setEnv(System.getenv("USER"));
                var help = testDao.save(test);
                if (help != null)
                    savedTestsList.add(help);
            }
        }
    }

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
        Assert.assertEquals("test", "test");
    }

    @AfterMethod
    public void postTest(ITestResult result) {
        var testDao = TestDao.getInstance();
        var testForUpdate = savedTestsList.get(amount);
        var currentTest = testDao.findById(testForUpdate.getId());
        testForUpdate.setName(result.getTestContext().getName());
        testForUpdate.setStatus_id(result.getStatus());
        testForUpdate.setMethod_name(result.getInstanceName());
        testForUpdate.setProject_id(savedProject.getId());
        testForUpdate.setSession_id(savedSes.getId());
        testForUpdate.setStart_time(new Date(result.getStartMillis()));
        testForUpdate.setEnd_time(new Date(result.getEndMillis()));
        testForUpdate.setEnv(System.getenv().get(SYSTEM_ENV_USER));
        testForUpdate.setBrowser((String) JsonUtil.smartReadJson(PATH_TO_CONFIG, "browser"));
        testForUpdate.setAuthor_id(savedAuthor.getId());
        testDao.update(testForUpdate);
        Assert.assertNotEquals(currentTest, testForUpdate, "Test wasn't updated");
        amount++;
    }

    @AfterSuite
    public void postSuite() {
        var testDao = TestDao.getInstance();
        for (var test : savedTestsList) {
            boolean check = testDao.delete(test.getId());
            Assert.assertFalse(check);
        }
    }
}
