package github.yagocranchi.linkshortener.repository;

import github.yagocranchi.linkshortener.entities.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    List<Link> findByUserUserId(UUID userId);
}
