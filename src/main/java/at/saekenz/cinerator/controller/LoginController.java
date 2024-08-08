package at.saekenz.cinerator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
//        String encodedPassword = new BCryptPasswordEncoder().encode(loginRequest.password());
//        log.info(encodedPassword);
//        System.out.println(encodedPassword);
//        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), encodedPassword);
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);

        if (authenticationResponse == null) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
        else {
            return new ResponseEntity<>(HttpStatusCode.valueOf(200));
        }
    }

    public record LoginRequest(String username, String password) {}
}
