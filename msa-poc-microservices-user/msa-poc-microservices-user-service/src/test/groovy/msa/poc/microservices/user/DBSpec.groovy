package msa.poc.microservices.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

import groovy.json.internal.Type
import msa.poc.microservices.user.Application
import msa.poc.microservices.user.api.dto.UserDto
import msa.poc.microservices.user.service.service.impl.UserServiceImpl
import spock.lang.Shared
import spock.lang.Specification


@ActiveProfiles("test")
@DataJpaTest 
@ContextConfiguration
@ComponentScan(basePackages="msa.poc.microservices.user")
class DBSpec extends Specification {

	@Autowired
	UserServiceImpl userServiceImpl
	
	
	def "list all user from the database"(){
		given: "for user inserted in db"
			UserDto userDto1=new UserDto("nameTestdb","surname1Testdb","surname2Testdb","emailTestdb1")
			UserDto userDto2=new UserDto("nameTestdb","surname1Testdb","surname2Testdb","emailTestdb2")
			UserDto userDto3=new UserDto("nameTestdb","surname1Testdb","surname2Testdb","emailTestdb3")
			UserDto userDto4=new UserDto("nameTestdb","surname1Testdb","surname2Testdb","emailTestdb4")
			userServiceImpl.add(userDto1)
			userServiceImpl.add(userDto2)
			userServiceImpl.add(userDto3)
			userServiceImpl.add(userDto4)
			
		when:"list the users form db"
			List<UserDto> returnedUser=userServiceImpl.list()
		
		then: " get the same users we inserted"
			returnedUser.size()==4
	}
	
	def "get user by email"(){
		given: "one user stored in db"
			UserDto userDto=new UserDto("nameTestdb","surname1Testdb","surname2Testdb","emailTestdb")
			userServiceImpl.add(userDto)
			
		when: "get user by email"
			UserDto returnedUserDto=userServiceImpl.getUserByEmail(userDto.getEmail())
			
		then: "get the user with email stored"
			userDto.getEmail()==returnedUserDto.getEmail()
	}
	
	
	
	
	def "add a user to database"(){
		given: "total users in the db"
			UserDto userDto=new UserDto("nameTestdb","surname1Testdb","surname2Testdb","emailTestdb")
			Integer totalElementsBefore=userServiceImpl.list().size()
			
		when:"a new user is added "
			UserDto retDtoAdd=userServiceImpl.add(userDto)
			
		then:"db has increased in one"
			Integer totalElementsAfter=userServiceImpl.list().size()
			totalElementsBefore<totalElementsAfter
		
	}
	
	def "update user when user exist"(){
		given:"a stored user"
			UserDto userStored=new UserDto("nameTestdb","surname1Testdb","surname2Testdb","emailTestdb")
			userServiceImpl.add(userStored)
			
		when:"update the user "
			UserDto userUpdated=new UserDto("nameTestdbUpdated","surname1TestdbUpdated","surname2Testdb","emailTestdb")
			userServiceImpl.updateUserByEmail(userStored.getEmail(), userUpdated)
		
		then:"the user was updated with all new fields"
			userServiceImpl.getUserByEmail(userUpdated.getEmail()).getName()=="nameTestdbUpdated"
			userServiceImpl.getUserByEmail(userUpdated.getEmail()).getSurname1()=="surname1TestdbUpdated"
			userServiceImpl.getUserByEmail(userUpdated.getEmail()).getSurname2()=="surname2Testdb"
			userServiceImpl.getUserByEmail(userUpdated.getEmail()).getEmail()=="emailTestdb"
	}

	def "delete user"(){
		given: "a user" 
			UserDto userStored1=new UserDto("nameTestdb1","surname1Testdb1","surname2Testdb1","emailTestdb1")
			UserDto userStored2=new UserDto("nameTestdb2","surname1Testdb2","surname2Testdb2","emailTestdb2")
			userServiceImpl.add(userStored1)
			userServiceImpl.add(userStored2)
		
		when:"remove the user"
			userServiceImpl.deleteUserByEmail(userStored1.getEmail())
		
		then:"use has been deleted"
			userServiceImpl.list().size()==1
			userServiceImpl.getUserByEmail(userStored2.getEmail()).getEmail()=="emailTestdb2"
			
		
	}
	
	
}
