package com.example.oauth2.notify;


import com.example.oauth2.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


@Entity(name = "notify")
@Table(name = "notify")

public class Notify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notiId;
    @Column(nullable = false,name = "description")
    @NotBlank
    private String description;
    @Column(name = "notiStatus")
    private boolean notiStatus;
    @Column(name = "deletedNoti")
    private boolean deletedNoti;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userID",foreignKey = @ForeignKey(name = "fk_user_notify"))
    private User user;

    public int getNotiId() {
        return notiId;
    }

    public void setNotiId(int notiId) {
        this.notiId = notiId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isNotiStatus() {
        return notiStatus;
    }

    public void setNotiStatus(boolean notiStatus) {
        this.notiStatus = notiStatus;
    }

    public boolean isDeletedNoti() {
        return deletedNoti;
    }

    public void setDeletedNoti(boolean deletedNoti) {
        this.deletedNoti = deletedNoti;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
