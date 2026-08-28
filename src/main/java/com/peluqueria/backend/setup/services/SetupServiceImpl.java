package com.peluqueria.backend.setup.services;

import com.peluqueria.backend.setup.dtos.InitialAdminRequest;
import com.peluqueria.backend.users.entities.Role;
import com.peluqueria.backend.users.entities.UserAccount;
import com.peluqueria.backend.users.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetupServiceImpl implements SetupService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SetupServiceImpl(UserAccountRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean isSetupRequired() {
        return userRepository.count() == 0;
    }

    @Override
    @Transactional
    public UserAccount createInitialAdmin(InitialAdminRequest request) {
        if (!isSetupRequired()) {
            throw new IllegalStateException("Setup ya completado");
        }

        UserAccount admin = UserAccount.builder()
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .activo(true)
                .build();

        return userRepository.save(admin);
    }
}
