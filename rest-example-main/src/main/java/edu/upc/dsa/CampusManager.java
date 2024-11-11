package edu.upc.dsa;

import edu.upc.dsa.models.*;

import java.util.Date;
import java.util.List;

public interface CampusManager {


    public void addUser(String id, String firstName, String lastName, String email, String birthDate); //alomejos necesito que devuelva el user
    public List<User> listUsersAlfateicamente();
    public User getUserById(String id);
    public void addPointOfInterest(PointOfInterest poi);
    public String registerUserAtPoint(String userId, int x, int y);
    public List<UserPointOfInterest> getUserPoints(String userId);
    public List<User> getUsersAtPoint(int x, int y);
    public List<PointOfInterest> getPointsByType(ElementType type);
    public int size();
    public void clear();
}
