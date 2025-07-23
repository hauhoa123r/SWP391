package org.project.service;

import jakarta.servlet.http.HttpServletResponse;
import org.project.dto.RegisterDTO;

import org.project.dto.response.Response;

public interface UserSecurityService {
    Response register(RegisterDTO dto);

}
