package msa.poc.microservices.user.service.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import msa.poc.microservices.user.api.dto.UserDto;
import msa.poc.microservices.user.service.persistence.entities.User;

@Repository("userRepositoryJpa")
public interface UserRepository extends JpaRepository<User, Long> {

     @Query("SELECT count(u) FROM User u WHERE u.email = :email")
     Long repeatedEmail(@Param("email") String email);
     
     User findByEmail(String email);
     
}
