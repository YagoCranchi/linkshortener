package github.yagocranchi.linkshortener.controller;

import github.yagocranchi.linkshortener.controller.dto.CreateLinkDto;
import github.yagocranchi.linkshortener.repository.LinkRepository;
import github.yagocranchi.linkshortener.repository.UserRepository;
import github.yagocranchi.linkshortener.entities.Link;
import github.yagocranchi.linkshortener.entities.Role;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class LinkController {
    
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    public LinkController(LinkRepository linkRepository,
                          UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }
    
    @PostMapping("/links")
    public ResponseEntity<Void> createLink(@RequestBody CreateLinkDto dto,
                                           JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()));
        
        var link = new Link();
        link.setUser(user.get());
        link.setContent(dto.content());
        
        linkRepository.save(link);
        
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/links/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable("id") Long linkId,
                                           JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var link = linkRepository.findById(linkId)
                   .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        var isAdmin = user.get().getRoles()
                      .stream()
                      .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if(isAdmin || link.getUser().getUserId().equals(UUID.fromString(token.getName()))) {            
            linkRepository.deleteById(linkId);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok().build();
    }
}
