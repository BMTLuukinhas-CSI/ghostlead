package com.ghostlead.api.security;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.*;
@Configuration
public class SecurityConfig {
    @Bean org.springframework.security.core.userdetails.UserDetailsService noDefaultUser() {
        return username -> { throw new org.springframework.security.core.userdetails.UsernameNotFoundException("Use a autenticação da aplicação"); };
    }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
    @Bean SecurityContextRepository contextRepository() { return new HttpSessionSecurityContextRepository(); }
    @Bean CsrfTokenRepository csrfRepository() { return new HttpSessionCsrfTokenRepository(); }
    @Bean SecurityFilterChain security(HttpSecurity http, SecurityContextRepository contexts, CsrfTokenRepository csrf) throws Exception {
        return http
            .csrf(c -> c.csrfTokenRepository(csrf))
            .securityContext(c -> c.securityContextRepository(contexts))
            .authorizeHttpRequests(a -> a.requestMatchers("/api/health", "/api/auth/csrf", "/api/auth/login", "/api/auth/register", "/error").permitAll().anyRequest().authenticated())
            .requestCache(c -> c.disable())
            .exceptionHandling(e -> e.authenticationEntryPoint((req,res,ex) -> {
                res.setStatus(401); res.setContentType("application/json;charset=UTF-8"); res.getWriter().write("{\"error\":\"Entre na sua conta para continuar\"}");
            }).accessDeniedHandler((req,res,ex) -> {
                res.setStatus(403); res.setContentType("application/json;charset=UTF-8"); res.getWriter().write("{\"error\":\"Acesso negado ou sessão de segurança expirada. Atualize a página.\"}");
            }))
            .logout(l -> l.logoutUrl("/api/auth/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessHandler((req,res,a) -> res.setStatus(204)))
            .build();
    }
}
