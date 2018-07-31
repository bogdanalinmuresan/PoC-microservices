package msa.poc.microservices.user.service.service;

import java.util.List;
import msa.poc.microservices.user.api.dto.UserDto;

public interface UserService {

    List<UserDto> list();

    UserDto getUser(Long id);

    UserDto add(UserDto userDto);

    UserDto update(Long id,UserDto userDto);

    UserDto delete(Long id);

}
