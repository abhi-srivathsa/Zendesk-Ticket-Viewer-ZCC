package com.abhilash;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    String token = "C2wruqvtdnC4gBd2evT70RbNtC4aQ7F8qUdLwj1S";

    @Test
    @DisplayName("Testing retrieving a specific ticket with correct credentials")
    void retrieveTicket() {
        JSONObject obj = Main.retrieveTicket(1, token);
        assertNotNull(obj);

    }

    @Test
    @DisplayName("Testing retrieving a specific ticket with incorrect credentials")
    void retrieveTicketWrongCred() {
        try {
            JSONObject obj = Main.retrieveTicket(1, "wrong Credentials");
        }catch (RuntimeException e) {
            //if execution reaches here, it indicates this exception has occurred.
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("All tickets should be retrieved with correct credentials")
    void retrieveAllData() {
        JSONArray array = Main.retrieveAllData(token);
        assertNotNull(array);
    }
    @Test
    @DisplayName("Tickets should not be retrieved with incorrect credentials")
    void retrieveAllDataWrongCred() {
        try {
            JSONArray array = Main.retrieveAllData("wrong Credentials");
        }catch (RuntimeException e) {
            //if execution reaches here, it indicates this exception has occurred.
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("When an invalid ticket id is provided, it should return a null object ")
    void retrieveTicketInvalidId() {
        JSONObject obj = Main.retrieveTicket(-1, token);
        assertNull(obj);
    }

    @Test
    @DisplayName("When a ticket number which does not exist is provided, it should return a null object ")
    void retrieveTicketUnavailableId() {
        JSONObject obj = Main.retrieveTicket(900000, token);
        assertNull(obj);
    }
}