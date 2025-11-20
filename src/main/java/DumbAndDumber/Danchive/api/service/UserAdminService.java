package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.auth.CreateProfessorRequest;
import DumbAndDumber.Danchive.api.dto.user.UserDto;

public interface UserAdminService {
    UserDto createProfessor(CreateProfessorRequest req);
}