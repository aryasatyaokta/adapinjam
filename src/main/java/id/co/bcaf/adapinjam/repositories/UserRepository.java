package id.co.bcaf.adapinjam.repositories;

import id.co.bcaf.adapinjam.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Kamu bisa nambah custom query kalau perlu nanti
}
