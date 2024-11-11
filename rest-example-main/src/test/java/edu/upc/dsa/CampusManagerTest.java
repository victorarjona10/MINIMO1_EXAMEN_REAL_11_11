package edu.upc.dsa;
import edu.upc.dsa.CampusManagerImpl;
import edu.upc.dsa.models.ElementType;
import edu.upc.dsa.models.PointOfInterest;
import edu.upc.dsa.models.User;
import edu.upc.dsa.models.UserPointOfInterest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.List;
public class CampusManagerTest {

    private CampusManagerImpl cm;

    @Before
    public void setUp() {
        cm = CampusManagerImpl.getInstance();

        // Add some initial data
        cm.addUser("1", "User1", "Surname1", "user1@example.com", "01/01/1990");
        cm.addPointOfInterest(new PointOfInterest(1, 1, ElementType.BRIDGE));
        cm.registerUserAtPoint("1", 1, 1);
    }

    @After
    public void tearDown() {
        cm.clear();
    }

    @Test
    public void testListUsersByPoint() {
        List<User> users = cm.getUsersAtPoint(1, 1);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("User1", users.get(0).getFirstName());
    }

    @Test
    public void testGetPointsByType() {
        List<PointOfInterest> points = cm.getPointsByType(ElementType.BRIDGE);
        Assert.assertEquals(1, points.size());
        Assert.assertEquals(1, points.get(0).getX());
        Assert.assertEquals(1, points.get(0).getY());
    }

    @Test
    public void testGetUserPoints() {
        List<UserPointOfInterest> points = cm.getUserPoints("1");
        Assert.assertEquals(1, points.size());
        Assert.assertEquals(1, points.get(0).getPointOfInterest().getX());
        Assert.assertEquals(1, points.get(0).getPointOfInterest().getY());
    }
}
