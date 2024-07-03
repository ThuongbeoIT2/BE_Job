package com.example.oauth2.notify;

import com.example.oauth2.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "notify")
@Table(name = "notify")
public class Notify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notiId;

    @Column(nullable = false, name = "description")
    @NotBlank
    private String description;

    @Column(name = "notiStatus")
    private boolean notiStatus;

    @Column(name = "deletedNoti")
    private boolean deletedNoti;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userID", foreignKey = @ForeignKey(name = "fk_user_notify"))
    private User user;


}
