package com.peluqueria.backend.setup.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetupStatusResponse {
    private boolean setupRequired;
}
