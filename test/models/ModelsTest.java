package models;

import models.db1.*;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ModelsTest extends WithApplication {
	
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }
    
    @Test
    public void createAndRetrieveUser() {
        new User("USER_1","Bob","bob@gmail.com", "secret").save();
        User bob = User.find.where().eq("email", "bob@gmail.com").findUnique();
        assertNotNull(bob);
        assertEquals("Bob", bob.name);
    }
    
    @Test
    public void tryAuthenticateUser() {
        new User("USER_1", "Bob","bob@gmail.com", "secret").save();
        
        assertNotNull(User.authenticate("USER_1", "secret"));
        assertNull(User.authenticate("USER_1", "badpassword"));
        assertNull(User.authenticate("USER_2", "secret"));
    }
}