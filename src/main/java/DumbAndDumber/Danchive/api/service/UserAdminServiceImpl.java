package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.auth.CreateProfessorRequest;
import DumbAndDumber.Danchive.api.dto.user.UserDto;
import DumbAndDumber.Danchive.api.entity.Role;
import DumbAndDumber.Danchive.api.entity.User;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.api.util.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createProfessor(CreateProfessorRequest req) {

        // 1) 비밀번호 정책 검사 (팀 회원가입과 동일 규칙 재사용)
        if (!PasswordPolicy.valid(req.getPassword())) {
            throw new IllegalArgumentException("비밀번호 규칙 불일치(8~16자, 영문/숫자/특수문자)");
        }

        // 2) 이메일 중복 검사
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 3) 교수 User 엔티티 생성
        User prof = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .role(Role.PROF)
                .department(req.getDepartment())
                .studentId(req.getStudentId())    // 필요 없다면 null 그대로 저장
                .build();

        // 4) 저장
        userRepository.save(prof);

        // 5) DTO로 변환해서 반환
        return UserDto.of(prof);
    }
}