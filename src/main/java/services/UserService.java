package services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import tfg.model.TwitterUser;

@Path("/user")
public class UserService {

	@GET
	@Path("/get/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public TwitterUser getUsername() {
		// get username to check login is right
		return null;
	}
	
}
