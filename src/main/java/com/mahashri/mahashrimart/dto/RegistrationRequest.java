package com.mahashri.mahashrimart.dto;

import com.mahashri.mahashrimart.model.Role;

public record RegistrationRequest(String name, String email, String password, Role role) {}