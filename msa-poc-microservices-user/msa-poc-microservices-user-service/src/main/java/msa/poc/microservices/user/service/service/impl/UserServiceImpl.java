package msa.poc.microservices.user.service.service.impl;

import msa.poc.microservices.user.api.dto.UserDto;
import msa.poc.microservices.user.service.exception.EmailNotAvailableException;
import msa.poc.microservices.user.service.persistence.entities.User;
import msa.poc.microservices.user.service.repositories.UserRepository;
import msa.poc.microservices.user.service.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    public List<UserDto> list() {
        List<UserDto> userDtoList = new ArrayList<>();
        for(User user : userRepository.findAll()) {
            userDtoList.add(this.modelMapper.map(user, UserDto.class));
        }
        return userDtoList;
    }
    
    public UserDto getUserByEmail(String email) {
    	return this.modelMapper.map(this.userRepository.findByEmail(email), UserDto.class);
    }
    public UserDto getUser(Long id) {
    	if(!this.userRepository.existsById(id)) 
    		throw new EntityNotFoundException(); 
        return this.modelMapper.map(this.userRepository.getOne(id), UserDto.class);
    }

    public UserDto add(UserDto userDto) {
        if(this.userRepository.repeatedEmail(userDto.getEmail()) > 0) throw  new EmailNotAvailableException();
        return this.modelMapper.map(this.userRepository.save(this.modelMapper.map(userDto, User.class)), UserDto.class);
    }

    public UserDto update(Long id, UserDto userDto) {
        if(!this.userRepository.existsById(id)) throw new EntityNotFoundException();
        if(this.userRepository.repeatedEmail(userDto.getEmail()) > 0) throw  new EmailNotAvailableException();
        User user = this.modelMapper.map(userDto, User.class);
        user.setId(id);
        return this.modelMapper.map(this.userRepository.save(user), UserDto.class);
    }
    
    public UserDto updateUserByEmail(String email, UserDto userDto) {
    	User userToUpdate=this.userRepository.findByEmail(email);
    	User updatedUser =this.modelMapper.map(userDto, User.class);
    	updatedUser.setId(userToUpdate.getId());
    	return this.modelMapper.map(this.userRepository.save(updatedUser), UserDto.class);
    }

    public UserDto delete(Long id) {
    	if(!this.userRepository.existsById(id)) throw new EntityNotFoundException(); 
        UserDto userDto = this.modelMapper.map(this.userRepository.getOne(id), UserDto.class);
        this.userRepository.deleteById(id);
        return userDto;
    }
    
    public UserDto deleteUserByEmail(String email) {
    	User userToDelete=this.userRepository.findByEmail(email);
    	this.userRepository.deleteById(userToDelete.getId());
    	
    	return this.modelMapper.map(userToDelete, UserDto.class);
    }


}
