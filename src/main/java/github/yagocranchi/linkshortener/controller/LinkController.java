package github.yagocranchi.linkshortener.controller;

import github.yagocranchi.linkshortener.controller.dto.CreateLinkDto;
import github.yagocranchi.linkshortener.controller.dto.LinkResponseDto;
import github.yagocranchi.linkshortener.controller.dto.AdminLinkResponseDto;
import github.yagocranchi.linkshortener.repository.LinkRepository;
import github.yagocranchi.linkshortener.repository.UserRepository;
import github.yagocranchi.linkshortener.entities.Link;
import github.yagocranchi.linkshortener.entities.Role;
import github.yagocranchi.linkshortener.utils.URLValidator;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

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
        if (!URLValidator.isValidURL(dto.content())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL format");
        }

        var user = userRepository.findById(UUID.fromString(token.getName()));

        long linkId = Stream.generate(() -> ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L))
                            .filter(id -> !linkRepository.existsById(id))
                            .findFirst()
                            .orElseThrow();

        var link = new Link();
        link.setLinkId(linkId);
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

    @GetMapping("/links/{id}")
    public ResponseEntity<LinkResponseDto> getLinkById(@PathVariable("id") Long linkId) {
        var link = linkRepository.findById(linkId)
                   .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found"));

        return ResponseEntity.ok(new LinkResponseDto(link.getLinkId(), link.getContent()));
    }

   @GetMapping("/links/list")
    public ResponseEntity<List<LinkResponseDto>> getMyLinks(JwtAuthenticationToken token) {
        var userId = UUID.fromString(token.getName());

        var links = linkRepository.findByUserUserId(userId)
                          .stream()
                          .map(link -> new LinkResponseDto(link.getLinkId(), link.getContent()))
                          .collect(Collectors.toList());

        return ResponseEntity.ok(links);
    }


    @GetMapping("/links/all")
    public ResponseEntity<List<AdminLinkResponseDto>> getAllLinks(JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        var isAdmin = user.getRoles()
                          .stream()
                          .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var links = linkRepository.findAll()
                        .stream()
                        .map(link -> new AdminLinkResponseDto(link.getLinkId(), link.getContent(), link.getUser().getUserId()))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(links);
    }
}