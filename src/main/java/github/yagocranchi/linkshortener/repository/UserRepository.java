package github.yagocranchi.linkshortener.repository;

import github.yagocranchi.linkshortener.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Role, Long> {
    
}
