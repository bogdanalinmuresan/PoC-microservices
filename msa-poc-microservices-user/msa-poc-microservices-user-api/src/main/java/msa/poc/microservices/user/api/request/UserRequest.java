package msa.poc.microservices.user.api.request;

import java.util.Date;

import msa.poc.microservices.common.Action;
import msa.poc.microservices.common.request.BaseRequest;
import msa.poc.microservices.user.api.dto.UserDto;

public class UserRequest extends BaseRequest {
	
	
    private Action action;
    private UserDto userDto;

    public UserRequest() {

    }
    
    public UserRequest(int id, Date timestamp) {
        super(id,timestamp);
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

	public UserDto getUserDto() {
		return userDto;
	}
	public void setUserDto(UserDto userDto) {
		this.userDto = userDto;
	}

	@Override
	public String toString() {
		return "UserRequest [action=" + action + ", userDto=" + userDto.toString() + "]";
	}
}
