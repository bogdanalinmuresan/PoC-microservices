package msa.poc.microservices.user.service.persistence.entities;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

   @Transient
    private static final long serialVersionUID = 8336005734898759258L;

    @Id
    @SequenceGenerator(name = "t_user_user_id_seq", sequenceName = "t_user_user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_user_user_id_seq")
    @Column(name = "user_id", updatable = false)
    private Long id;

   
    @Column(name = "user_email", length = 128, nullable = false)
    private String email;

    @Column(name = "user_name", length = 100, nullable = false)
    private String name;

    @Column(name = "user_surname1", length = 100, nullable = false)
    private String surname1;

    @Column(name = "user_surname2", length = 100, nullable = true)
    private String surname2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname1() {
        return surname1;
    }

    public void setSurname1(String surname1) {
        this.surname1 = surname1;
    }

    public String getSurname2() {
        return surname2;
    }

    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

}


