package github.yagocranchi.linkshortener.repository;

import github.yagocranchi.linkshortener.entities.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    
}
