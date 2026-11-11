package com.ghostlead.api.security;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
/** Single-instance guard. Production proxy must also limit authentication requests. */
@Component
public class AuthRateLimiter {
    private record Window(long start, int count) {}
    private final Map<String, Window> windows = new HashMap<>();
    public synchronized void check(String address) {
        long now = Instant.now().getEpochSecond();
        windows.entrySet().removeIf(e -> now - e.getValue().start() >= 900);
        Window w = windows.get(address);
        if ((w != null && w.count() >= 30) || (w == null && windows.size() >= 10000))
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Muitas tentativas. Tente novamente em 15 minutos.");
        windows.put(address, w == null ? new Window(now, 1) : new Window(w.start(), w.count()+1));
    }
}
