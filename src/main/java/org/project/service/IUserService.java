package org.project.service;


import org.project.dto.RegisterDTO;

import org.project.dto.response.Response;


public interface IUserService {
    Response register(RegisterDTO dto);

}
