package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class LeaveRemainId implements Serializable {
    @Serial
    private static final long serialVersionUID = -8579252889358683727L;
    @Column(name = "UserID", nullable = false)
    private Integer userID;

    @Column(name = "Year", nullable = false)
    private Integer year;

    public LeaveRemainId() {
    }
    public LeaveRemainId(Integer userID, Integer year) {
        this.userID = userID;
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LeaveRemainId entity = (LeaveRemainId) o;
        return Objects.equals(this.year, entity.year) &&
                Objects.equals(this.userID, entity.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, userID);
    }

}