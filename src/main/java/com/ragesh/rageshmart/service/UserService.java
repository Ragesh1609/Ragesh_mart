package com.ragesh.rageshmart.service;

import com.ragesh.rageshmart.dto.RegisterRequestDTO;
import com.ragesh.rageshmart.exception.AuthException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.model.User;

public interface UserService {
    User register(RegisterRequestDTO request) throws ValidationException;
    User login(String email, String password) throws AuthException, ValidationException;
}
