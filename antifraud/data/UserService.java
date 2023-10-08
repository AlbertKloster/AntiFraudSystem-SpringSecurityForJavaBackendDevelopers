package antifraud.data;

import antifraud.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PostApiAuthUserResponseDto saveUser(PostApiAuthUserRequestDto userRequestDto) {
        if (userRequestDto.name() == null || userRequestDto.username() == null || userRequestDto.password() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Optional<AppUser> userByUsername = appUserRepository.findUserByUsernameIgnoreCase(userRequestDto.username());
        if (userByUsername.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        else {
            AppUser appUser = new AppUser();
            appUser.setName(userRequestDto.name());
            appUser.setUsername(userRequestDto.username());
            appUser.setPassword(passwordEncoder.encode(userRequestDto.password()));
            appUser.setId(appUserRepository.save(appUser).getId());
            return new PostApiAuthUserResponseDto(appUser.getId(), appUser.getName(), appUser.getUsername());
        }
    }

    public List<GetApiAuthUserResponseDto> findAll() {
        return appUserRepository.findAll().stream().map(appUser -> new GetApiAuthUserResponseDto(appUser.id, appUser.name, appUser.username)).toList();
    }

    public ResponseEntity<DeleteApiAuthUserResponseDto> deleteUserByUsername(String username) {
        AppUser appUser = appUserRepository.findUserByUsernameIgnoreCase(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        appUserRepository.delete(appUser);
        return ResponseEntity.ok().body(new DeleteApiAuthUserResponseDto(appUser.username, UserStatus.DELETED.message));

    }
}
