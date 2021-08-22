package com.transportinfo.webbackend.services;


import com.transportinfo.webbackend.dto.request.LoginForm;
import com.transportinfo.webbackend.dto.request.SignUpForm;
import com.transportinfo.webbackend.dto.response.IdentityResponse;
import com.transportinfo.webbackend.dto.response.JwtResponse;
import com.transportinfo.webbackend.model.Role;
import com.transportinfo.webbackend.model.RoleName;
import com.transportinfo.webbackend.model.User;
import com.transportinfo.webbackend.repository.RoleRepository;
import com.transportinfo.webbackend.repository.UserRepository;
import com.transportinfo.webbackend.security.jwt.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SignUpAndSignInService {

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public IdentityResponse signUp(SignUpForm signUpRequest) {


        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            //TODO
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            //TODO
        }

        User user = new User();
        UUID id = UUID.randomUUID();
        String uuid = id.toString();
        user.setId(uuid);
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setRoles(getRolesOrThrow(signUpRequest.getRole()));
        userRepository.saveAndFlush(user);

        return new IdentityResponse(uuid);
    }


    public JwtResponse signIn(LoginForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        return new JwtResponse(jwt);
    }

    public String getLoggedAuthUser() {

        Object authUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<String> loggedInAuthUserId = null;

        if (authUser instanceof UserDetails) {

            String username = ((UserDetails) authUser).getUsername();
            loggedInAuthUserId = userRepository.findAuthUsersById(username);


        } else if (authUser instanceof UserDetails == false) {
            throw new RuntimeException("LoggedIn user does not  account.");

        } else {
            String username = authUser.toString();

            System.out.println(username);
        }
        return loggedInAuthUserId.get();

    }


    private Set<Role> getRolesOrThrow(Set<String> roles2) {
        Set<Role> roles = new HashSet<>();
        for (String role : roles2) {
            Optional<Role> roleOptional = roleRepository.findByName(RoleName.valueOf(role));
            System.out.println(roleOptional.get());
            if (!roleOptional.isPresent()) {
                throw new ValidationException("Role '" + role + "' does not exist.");
            }
            roles.add(roleOptional.get());
        }
        return roles;
    }
}
