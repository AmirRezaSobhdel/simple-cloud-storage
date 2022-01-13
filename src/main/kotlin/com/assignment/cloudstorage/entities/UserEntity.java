package com.assignment.cloudstorage.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Users")
@Getter
@Setter
@ToString
public class UserEntity implements Serializable {

    public UserEntity(String salt, String firstName, String lastName, String username, String password) {
        this.salt = salt;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }

    public UserEntity() {
    }

    public String salt;

    public String firstName;

    public String lastName;

    @Id
    public String username;

    public String password;

    @ColumnDefault("0")
    public Long usedStorage = 0L;

}
