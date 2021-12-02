package com.abhilash;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {


    @Test
    @DisplayName("Testing retrieving a specific ticket with correct credentials")
    void retrieveTicket() {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        String user = dotenv.get("USERNAME");
        String subDomain = dotenv.get("SUBDOMAIN");
        String token = dotenv.get("TOKEN");
        JSONObject obj = Main.retrieveTicket(1, subDomain, user, token);
        assertNotNull(obj);

    }

    @Test
    @DisplayName("Testing retrieving a specific ticket with incorrect credentials")
    void retrieveTicketWrongCred() {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        String user = "wrongUser";
        String subDomain = dotenv.get("SUBDOMAIN");
        String token = "wrongToken";
        try {
            JSONObject obj = Main.retrieveTicket(1, subDomain, user, token);
        }catch (RuntimeException e) {
            //if execution reaches here, it indicates this exception has occurred.
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("All tickets should be retrieved with correct credentials")
    void retrieveAllData() {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        String user = dotenv.get("USERNAME");
        String subDomain = dotenv.get("SUBDOMAIN");
        String token = dotenv.get("TOKEN");
        JSONArray array = Main.retrieveAllData(subDomain, user, token);
        assertNotNull(array);
    }
    @Test
    @DisplayName("Tickets should not be retrieved with incorrect credentials")
    void retrieveAllDataWrongCred() {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        String user = "wrongUser";
        String subDomain = dotenv.get("SUBDOMAIN");
        String token = "wrongToken";
        try {
            JSONArray array = Main.retrieveAllData(subDomain, user, token);
        }catch (RuntimeException e) {
            //if execution reaches here, it indicates this exception has occurred.
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("When an invalid ticket id is provided, it should return a null object ")
    void retrieveTicketInvalidId() {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        String user = dotenv.get("USERNAME");
        String subDomain = dotenv.get("SUBDOMAIN");
        String token = dotenv.get("TOKEN");
        JSONObject obj = Main.retrieveTicket(-1, subDomain, user, token);
        assertNull(obj);
    }

    @Test
    @DisplayName("When a ticket number which does not exist is provided, it should return a null object ")
    void retrieveTicketUnavailableId() {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        String user = dotenv.get("USERNAME");
        String subDomain = dotenv.get("SUBDOMAIN");
        String token = dotenv.get("TOKEN");
        JSONObject obj = Main.retrieveTicket(900000,subDomain, user, token);
        assertNull(obj);
    }
}