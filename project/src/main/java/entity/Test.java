package entity;

import com.google.common.base.Objects;

import java.sql.Date;

public class Test {
    private Long id;
    private String name;
    private int status_id;
    private String method_name;
    private Long project_id;
    private Long session_id;
    private Date start_time;
    private Date end_time;
    private String env;
    private String browser;
    private Long author_id;

    public Test() {
    }

    public Test(Long id, String name, int status_id, String method_name, Long project_id, Long session_id, Date start_time, Date end_time, String env, String browser) {
        this.id = id;
        this.name = name;
        this.status_id = status_id;
        this.method_name = method_name;
        this.project_id = project_id;
        this.session_id = session_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.env = env;
        this.browser = browser;
    }

    public Test(String name, int status_id, String method_name, Long project_id, Long session_id, Date start_time, Date end_time, String env, String browser, Long author_id) {
        this.name = name;
        this.status_id = status_id;
        this.method_name = method_name;
        this.project_id = project_id;
        this.session_id = session_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.env = env;
        this.browser = browser;
        this.author_id = author_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public Long getSession_id() {
        return session_id;
    }

    public void setSession_id(Long session_id) {
        this.session_id = session_id;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public Long getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(Long author_id) {
        this.author_id = author_id;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status_id=" + status_id +
                ", method_name='" + method_name + '\'' +
                ", project_id=" + project_id +
                ", session_id=" + session_id +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", env='" + env + '\'' +
                ", browser='" + browser + '\'' +
                ", author_id=" + author_id +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test that = (Test) o;

        return Objects.equal(this.id, that.id) &&
                Objects.equal(this.name, that.name) &&
                Objects.equal(this.status_id, that.status_id) &&
                Objects.equal(this.method_name, that.method_name) &&
                Objects.equal(this.project_id, that.project_id) &&
                Objects.equal(this.session_id, that.session_id) &&
                Objects.equal(this.start_time, that.start_time) &&
                Objects.equal(this.end_time, that.end_time) &&
                Objects.equal(this.env, that.env) &&
                Objects.equal(this.browser, that.browser) &&
                Objects.equal(this.author_id, that.author_id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, status_id, method_name, project_id, session_id,
                start_time, end_time, env, browser, author_id);
    }
}
