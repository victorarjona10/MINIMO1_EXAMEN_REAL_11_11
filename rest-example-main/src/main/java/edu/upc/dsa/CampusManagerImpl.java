package edu.upc.dsa;


import edu.upc.dsa.exceptions.TrackNotFoundException;



import edu.upc.dsa.models.ElementType;
import edu.upc.dsa.models.PointOfInterest;
import edu.upc.dsa.models.User;
import edu.upc.dsa.models.UserPointOfInterest;

import java.util.*;
import java.util.Date;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;


import org.apache.log4j.Logger;

import edu.upc.dsa.models.*;
import org.apache.log4j.Logger;
import org.eclipse.persistence.internal.jpa.parsing.LikeNode;

public class CampusManagerImpl implements CampusManager {
    private static CampusManagerImpl instance;
    private static final Logger logger = Logger.getLogger(CampusManagerImpl.class);

    protected List<User> users;
    protected List<PointOfInterest> pointsOfInterest;
    protected List<UserPointOfInterest> userPoints;


    private CampusManagerImpl() {
        //alomejor necesito que sean linkedlist y asi me funciona mejor
        this.users = new ArrayList<>();
        this.pointsOfInterest = new ArrayList<>();
        this.userPoints = new ArrayList<>();
    }


    public static CampusManagerImpl getInstance() {
        if (instance == null) {
            instance = new CampusManagerImpl();
        }
        return instance;
    }


    @Override
    public void addUser(String id, String firstName, String lastName, String email, String birthDate) {
        logger.info("addUser called with parameters: id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", birthDate=" + birthDate);
        User user = new User(id, firstName, lastName, email, birthDate);
        this.users.add(user);
        logger.info("User added: " + user);
    }


    @Override
    public List<User> listUsersAlfateicamente() {
        logger.info("listUsersAlfateicamente called");
        List<User> userList = new ArrayList<>(this.users);
        userList.sort(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName));
        logger.info("listUsersAlfateicamente finished, returning sorted list of users");
        return userList;
    }


    @Override
    public User getUserById(String id) {
        logger.info("getUserById called with parameter: id=" + id);
        for (User user : this.users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        logger.error("User not found with id: " + id);
        return null;
    }


    @Override
    public void addPointOfInterest(PointOfInterest poi) {
        logger.info("addPointOfInterest called with parameter: " + poi);
        this.pointsOfInterest.add(poi);
        logger.info("Point of Interest added: " + poi);
    }


    @Override
    public String registerUserAtPoint(String userId, int x, int y) { // me tendra que devolver los errores pk los pide
        logger.info("registerUserAtPoint called with parameters: userId=" + userId + ", x=" + x + ", y=" + y);
        User user = getUserById(userId);
        if (user == null) {
            logger.error("User not found with id: " + userId);
            return "there was no user whit that id";
        }
        PointOfInterest poi = this.pointsOfInterest.stream()
                .filter(p -> p.getX() == x && p.getY() == y)
                .findFirst()
                .orElse(null);
        if (poi == null) {
            logger.error("Point of Interest not found at coordinates: (" + x + ", " + y + ")");
            return "THE POINT OF INTEREST WAS NOT FOUND ON THOSE COORDINATES";
        }
        this.userPoints.add(new UserPointOfInterest(userId, poi));
        logger.info("User registered at Point of Interest: userId=" + userId + ", pointOfInterest=" + poi);
        return "User registered at Point of Interest";
    }


    @Override
    public List<UserPointOfInterest> getUserPoints(String userId) {  // me tendra que devolver los errores pk los pide y nose si ordena bien como pide
        logger.info("getUserPoints called with parameter: userId=" + userId);
        List<UserPointOfInterest> points = new LinkedList<>(); //para que se mantenga el orden he usado una linkedlist que creo que funcionara
        for (UserPointOfInterest upoi : this.userPoints) {
            if (upoi.getUserId().equals(userId)) {
                points.add(upoi);
            }
        }
        logger.info("getUserPoints finished, returning list of points for user with id: " + userId);
        return points;
    }

    @Override
    public List<User> getUsersAtPoint(int x, int y) { // me tendra que devolver los errores pk los pide
        logger.info("getUsersAtPoint called with parameters: x=" + x + ", y=" + y);
        List<User> usersAtPoint = new ArrayList<>();
        boolean pointExists = false;
        for (UserPointOfInterest upoi : this.userPoints) {
            if (upoi.getPointOfInterest().getX() == x && upoi.getPointOfInterest().getY() == y) {
                pointExists = true;
                User user = getUserById(upoi.getUserId());
                if (user != null) {
                    usersAtPoint.add(user);
                }
            }
        }
        if (!pointExists) {
            logger.error("Point of Interest not found at coordinates: (" + x + ", " + y + ")");
            return null;
        }
        logger.info("getUsersAtPoint finished, returning list of users at point: (" + x + ", " + y + ")");
        return usersAtPoint;
    }

    @Override
    public List<PointOfInterest> getPointsByType(ElementType type) { // quiero quemuestre los puntos
        logger.info("getPointsByType called with parameter: type=" + type);
        List<PointOfInterest> pointsByType = new ArrayList<>();
        for (PointOfInterest poi : this.pointsOfInterest) {
            if (poi.getType() == type) {
                pointsByType.add(poi);
            }
        }
        logger.info("getPointsByType finished, returning list of points of type: " + type);
        return pointsByType;
    }
    public int size() {
        int ret = this.users.size() + this.pointsOfInterest.size() + this.userPoints.size();
        logger.info("size " + ret);

        return ret;
    }
    public void clear() {
        this.users.clear();
        this.pointsOfInterest.clear();
        this.userPoints.clear();
    }








}
