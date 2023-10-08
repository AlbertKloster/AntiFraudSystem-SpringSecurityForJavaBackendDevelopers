package antifraud.controller;

import antifraud.data.UserService;
import antifraud.dto.DeleteApiAuthUserResponseDto;
import antifraud.dto.GetApiAuthUserResponseDto;
import antifraud.dto.PostApiAuthUserRequestDto;
import antifraud.dto.PostApiAuthUserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final UserService userService;

    public ApiAuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<PostApiAuthUserResponseDto> postUser(@RequestBody PostApiAuthUserRequestDto userRequestDto) {
        return new ResponseEntity<>(userService.saveUser(userRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<GetApiAuthUserResponseDto>> getList() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<DeleteApiAuthUserResponseDto> deleteUserByUsername(@PathVariable String username) {
        return userService.deleteUserByUsername(username);
    }

}
