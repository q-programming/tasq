package com.qprogramming.tasq.projects;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jromaniszyn on 04.07.2016.
 */
@Entity
public class Holiday implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "holiday_seq_gen")
    @SequenceGenerator(name = "holiday_seq_gen", sequenceName = "holiday_id_seq", allocationSize = 1)
    private Long id;

    @Column
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Holiday holiday = (Holiday) o;

        if (getId() != null ? !getId().equals(holiday.getId()) : holiday.getId() != null) {
            return false;
        }
        return getDate() != null ? getDate().equals(holiday.getDate()) : holiday.getDate() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        return result;
    }
}
