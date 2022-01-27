package com.nicoardizzoli.javabackendloginregistrationemailverification.appuser;

import com.nicoardizzoli.javabackendloginregistrationemailverification.registration.token.ConfirmationToken;
import com.nicoardizzoli.javabackendloginregistrationemailverification.registration.token.ConfirmationTokenService;
import com.nicoardizzoli.javabackendloginregistrationemailverification.security.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

//esa interface es de SpringSecurity
@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found " + email));
    }

    public String signUpUser(AppUser appUser) {
        appUserRepository.findByEmail(appUser.getEmail()).ifPresentOrElse(
                email -> {
                    throw new IllegalStateException(email + "already taken");
                },
                () -> {
                    String passwordEncoded = passwordEncoder.bCryptPasswordEncoder().encode(appUser.getPassword());
                    appUser.setPassword(passwordEncoded);
                }
        );

        appUserRepository.save(appUser);

        //TODO send confirmation token.

        String token = String.valueOf(UUID.randomUUID());
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), appUser);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        //TODO send email

        return token;
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
}
