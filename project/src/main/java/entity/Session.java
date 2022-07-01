package entity;

import java.sql.Date;

public class Session {
    private Long id;
    private String session_key;
    private Date created_time;
    private Long build_number;

    public Session() {
    }

    public Session(String session_key, Date created_time, Long build_number) {
        this.session_key = session_key;
        this.created_time = created_time;
        this.build_number = build_number;
    }

    public Session(Long id, String session_key, Date created_time, Long build_number) {
        this.id = id;
        this.session_key = session_key;
        this.created_time = created_time;
        this.build_number = build_number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public Long getBuild_number() {
        return build_number;
    }

    public void setBuild_number(Long build_number) {
        this.build_number = build_number;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", session_key='" + session_key + '\'' +
                ", created_time=" + created_time +
                ", build_number=" + build_number +
                '}';
    }
}
