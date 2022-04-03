package com.library.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @EqualsAndHashCode.Include
    private String isbn;
    private String title;
    private Integer year;
    private Integer copies;
    private Integer loaned;
    private Boolean active;

    @ManyToOne
    private Author author;

    @ManyToOne
    private Publisher publisher;

    @Column(nullable = true, length = 64)
    private String photos;

    public boolean isAvailable() {
        return (this.copies - this.loaned) > 0;
    }

    public String getPhotosImagePath() {
        if (photos == null || id == null) {
            return null;
        }
        return "/user-photos/" + id + "/" + photos;
    }
}
