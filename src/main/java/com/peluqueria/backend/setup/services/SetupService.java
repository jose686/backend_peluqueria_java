package com.peluqueria.backend.setup.services;

import com.peluqueria.backend.setup.dtos.InitialAdminRequest;
import com.peluqueria.backend.users.entities.UserAccount;

public interface SetupService {
    boolean isSetupRequired();
    UserAccount createInitialAdmin(InitialAdminRequest request);
}
