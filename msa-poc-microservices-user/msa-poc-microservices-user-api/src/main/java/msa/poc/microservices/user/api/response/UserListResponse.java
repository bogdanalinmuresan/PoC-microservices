package msa.poc.microservices.user.api.response;

import java.util.ArrayList;
import java.util.List;
import msa.poc.microservices.common.response.BaseResponse;
import msa.poc.microservices.user.api.dto.UserDto;

public class UserListResponse extends BaseResponse {
	
    private List<UserDto> users = new ArrayList<>(0);
    
    public UserListResponse() {
    	super();
    }
    
	public List<UserDto> getUsers() {
		return users;
	}
	
	public void setUsers(List<UserDto> users) {
		this.users = users;
	}

}