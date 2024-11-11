package edu.upc.dsa.services;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.joda.time.DateTime;


import edu.upc.dsa.CampusManager;
import edu.upc.dsa.CampusManagerImpl;
import edu.upc.dsa.models.*;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import java.text.SimpleDateFormat;
import javax.ws.rs.core.Response;

@Api(value = "/campus", description = "Endpoint to Campus Service")
@Path("/campus")
public class CampusService {
    final static Logger logger = Logger.getLogger(CampusService.class);
    private CampusManager cm;


    public CampusService() {
        this.cm = CampusManagerImpl.getInstance();
        if (cm.size() == 0) {
            this.cm.addUser("1", "User1", "Surname1", "user1@example.com", "01/01/1990");
            this.cm.addUser("2", "User2", "Surname2", "user2@example.com", "01/01/1991");
            this.cm.addUser("3", "User3", "Surname3", "user3@example.com", "01/01/1992");

            this.cm.addPointOfInterest(new PointOfInterest(1, 1, ElementType.BRIDGE));
            this.cm.addPointOfInterest(new PointOfInterest(2, 2, ElementType.GRASS));
            this.cm.addPointOfInterest(new PointOfInterest(3, 3, ElementType.TREE));

            cm.size();
        }
    }

    @POST
    @ApiOperation(value = "add a new user", notes = "Adds a new user to the system")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = User.class),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Path("/addUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {

        if (user.getId() == null || user.getFirstName() == null || user.getLastName() == null || user.getEmail() == null || user.getBirthDate() == null) {
            return Response.status(500).entity(user).build();
        }

        logger.info("addUser called with parameters: " + user);
        this.cm.addUser(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getBirthDate());

        return Response.status(201).entity(user).build();
    }

    @GET
    @ApiOperation(value = "get all users", notes = "Returns a list of all users ordered alphabetically by last name and first name")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = User.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsers() {

        List<User> users = this.cm.listUsersAlfateicamente();
        GenericEntity<List<User>> entity = new GenericEntity<List<User>>(users) {};
        return Response.status(201).entity(entity).build();

    }

    @GET
    @ApiOperation(value = "get user by id", notes = "Returns a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = User.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") String id) {

        User user = this.cm.getUserById(id);

        if (user == null) {
            return Response.status(404).build();
        }
        //envio el user en un arraylist para que el generic entity lo pueda leer
        List<User> userList = new ArrayList<>();

        userList.add(user);
        GenericEntity<List<User>> entity = new GenericEntity<List<User>>(userList) {};
        return Response.status(201).entity(entity).build();
    }

    @POST
    @ApiOperation(value = "add a new point of interest", notes = "Adds a new point of interest to the system")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = PointOfInterest.class),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Path("/addPointOfInterest")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPointOfInterest(PointOfInterest poi) {

        if (poi.getX() == 0 || poi.getY() == 0 || poi.getType() == null) {
            return Response.status(500).entity(poi).build();
        }

        logger.info("addPointOfInterest called with parameters: " + poi);
        this.cm.addPointOfInterest(poi);
        return Response.status(201).entity(poi).build();
    }

    @POST
    @ApiOperation(value = "register user passing by a point of interest", notes = "Registers that a user passes by a point of interest")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "User or Point of Interest not found"),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Path("/registerPassage/{userId}/{x}/{y}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUserPassage(@PathParam("userId") String userId, @PathParam("x") int x, @PathParam("y") int y) {

        String response = this.cm.registerUserAtPoint(userId, x, y);

        logger.info("registerUserPassage called with parameters: userId=" + userId + ", x=" + x + ", y=" + y);


        return Response.status(201).entity(response).build();

    }

    @GET
    @ApiOperation(value = "get points of interest by user", notes = "Returns the points of interest a user has passed by, in the order they were registered")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = UserPointOfInterest.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Path("/userPoints/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserPoints(@PathParam("userId") String userId) {
        List<UserPointOfInterest> points = this.cm.getUserPoints(userId);

        GenericEntity<List<UserPointOfInterest>> entity = new GenericEntity<List<UserPointOfInterest>>(points) {};
        return Response.status(201).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "list users by point of interest", notes = "Lists the users who have passed by a point of interest identified by its coordinates")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = User.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Point of Interest not found"),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Path("/pointUsers/{x}/{y}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsersByPoint(@PathParam("x") int x, @PathParam("y") int y) {
        List<User> users = this.cm.getUsersAtPoint(x, y);
        if (users == null) {
            return Response.status(404).entity("Point of Interest not found").build();
        }
        GenericEntity<List<User>> entity = new GenericEntity<List<User>>(users) {};
        return Response.status(201).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "get points of interest by type", notes = "Returns the points of interest of a specified type with their coordinates")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = PointOfInterest.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Validation Error")
    })
    @Path("/pointsByType/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPointsByType(@PathParam("type") ElementType type) {
        List<PointOfInterest> points = this.cm.getPointsByType(type);
        GenericEntity<List<PointOfInterest>> entity = new GenericEntity<List<PointOfInterest>>(points) {};
        return Response.status(201).entity(entity).build();
    }











}
