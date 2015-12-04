package com.forrest.gymr;

import android.test.AndroidTestCase;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Forrest on 3/12/15.
 */
public class MatchingTest extends AndroidTestCase {
    private ParseObject user;
    private GymrApplication application;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", "qQMIbNpwn7");
        Thread.sleep(50);
        List<ParseObject> users = query.find();
        user = ParseUser.getCurrentUser(); //users.get(0);
    }

    // Test for the correct Height
    public void testHeight() {
        assertEquals(185, user.getInt("height"));
    }

    // Test for the correct bench weight
    public void testBenchWeight() {
        assertEquals(220, user.getInt("benchWeight"));
    }

    // Test for correct Long value returned for Facebook Id
    public void testSelfFacbeookId() {
        assertEquals(Long.valueOf(10207077775718226L), Long.valueOf(user.getLong("facebookId")));
    }

    // Test that yourself is not contained in the query results
    public void testNoSelf() {
        List<ParseUser> parseUsers = queryResults();
        for (ParseUser u : parseUsers) {
            assertFalse(u.getObjectId() == user.getObjectId());
        }
    }

    // Test for Correct Configuration for whether matching with the opposite sex
    public void testMaleMatching() {
        user.put("oppositeSex", false);
        try {
            user.save();
        } catch (ParseException e) {
            return;
        }
        List<ParseUser> parseUser = queryResults();
        assertTrue(parseUser.isEmpty());
        user.put("oppositeSex", true);
        try {
            user.save();
        } catch (ParseException e) {
            return;
        }
    }

    // Test for the correct matching for workout body parts
    public void testBodyPartMatching() {
        user.put("bodyPart", "Chest");
        try {
            user.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ParseUser> parseUsers = queryResults();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(parseUsers);
        assertEquals(1, parseUsers.size());
        assertEquals("Jackie White", parseUsers.get(0).getString("name"));
        user.put("bodyPart", "Back");
        try {
            user.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        parseUsers = queryResults();
        assertNotNull(parseUsers);
        assertEquals(1, parseUsers.size());
        assertEquals("Dan Brown", parseUsers.get(0).getString("name"));
    }

    // Test when no user is matched
    public void testNoUsers() {
        user.put("bodyPart", "Tongue");
        try {
            user.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ParseUser> parseUsers = queryResults();
        assertTrue(parseUsers.isEmpty());
        user.put("bodyPart", "Chest");
        try {
            user.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private List<ParseUser> queryResults() {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("bodyPart", user.get("bodyPart"));
        if (user.get("oppositeSex") == false) {
            query.whereEqualTo("male", user.get("male"));
        }
        query.whereNotEqualTo("objectId", user.getObjectId());
        List<ParseUser> parseUsers = null;
        try {
            parseUsers = query.find();
        } catch (ParseException e) {
            return null;
        }
        return parseUsers;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
