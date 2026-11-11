package com.ghostlead.api.controller;
import com.ghostlead.api.entity.User;
import com.ghostlead.api.repository.UserRepository;
import com.ghostlead.api.security.*;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final SecurityContextRepository contexts;
    private final CsrfTokenRepository csrf;
    private final CurrentUser current;
    private final AuthRateLimiter limiter;
    private final String dummyHash;
    public AuthController(UserRepository users, PasswordEncoder passwords, SecurityContextRepository contexts, CsrfTokenRepository csrf, CurrentUser current, AuthRateLimiter limiter) {
        this.users=users; this.passwords=passwords; this.contexts=contexts; this.csrf=csrf; this.current=current; this.limiter=limiter;
        this.dummyHash=passwords.encode(UUID.randomUUID().toString());
    }
    public record Register(@NotBlank @Size(max=120) String name, @NotBlank @Email @Size(max=254) String email, @NotBlank @Size(min=12,max=64) String password) {}
    public record Login(@NotBlank @Email @Size(max=254) String email, @NotBlank @Size(max=64) String password) {}
    public record Profile(UUID id, String name, String email) {}
    private Profile profile(User user) { return new Profile(user.getId(),user.getName(),user.getEmail()); }
    private String email(String value) { return value.strip().toLowerCase(Locale.ROOT); }
    @GetMapping("/csrf") public Map<String,String> token(CsrfToken token) { return Map.of("token",token.getToken(),"headerName",token.getHeaderName()); }
    @GetMapping("/me") public Profile me() { return profile(users.findById(current.id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED))); }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public Profile register(@Valid @RequestBody Register data, HttpServletRequest req, HttpServletResponse res) {
        limiter.check(req.getRemoteAddr());
        if(data.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Senha deve ter no máximo 72 bytes UTF-8");
        if(users.findByEmail(email(data.email())).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Não foi possível cadastrar com este e-mail");
        User user=users.saveAndFlush(new User(data.name().strip(),email(data.email()),passwords.encode(data.password()),null));
        signIn(user,req,res); return profile(user);
    }
    @PostMapping("/login") public Profile login(@Valid @RequestBody Login data, HttpServletRequest req, HttpServletResponse res) {
        limiter.check(req.getRemoteAddr());
        User user=users.findByEmail(email(data.email())).orElse(null);
        boolean matches=passwords.matches(data.password(),user==null ? dummyHash : user.getPasswordHash());
        if(user==null || !matches) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"E-mail ou senha incorretos");
        signIn(user,req,res); return profile(user);
    }
    private void signIn(User user,HttpServletRequest req,HttpServletResponse res) {
        if(req.getSession(false)!=null) req.changeSessionId();
        var context