package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);


//    @Query ("SELECT u FROM users u WHERE u.email = :email ")
////    Optional<User> findIdByEmail(String email);
}
