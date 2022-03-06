package com.library.entities;

import com.library.enums.Role;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
public class Customer {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @EqualsAndHashCode.Include
    private String idNumber;
    private String name;
    private String lastName;
    private String phoneNumber;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean active;

    public String getFullName() {
        return this.name + " " + this.lastName;
    }

}
