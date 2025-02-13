package github.yagocranchi.linkshortener.repository;

import github.yagocranchi.linkshortener.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<User, UUID> {
    
}
