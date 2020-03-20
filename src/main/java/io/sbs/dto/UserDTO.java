package io.sbs.dto;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @date 2020-03-16
 */
public class UserDTO implements Serializable {

    @Id
    private String uid;
    private String name;
    private Integer sex; // 1 man 0 woman
    private String username;
    private String password;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
