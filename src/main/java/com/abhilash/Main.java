package com.abhilash;

import jdk.swing.interop.SwingInterOpUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.*;
import java.util.Locale;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

public class Main {

    public static void main(String[] args) {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        String subDomain = dotenv.get("SUBDOMAIN");
        String user = dotenv.get("USERNAME");
        String token = dotenv.get("TOKEN");
        String menu = "menu";
        String quit = "quit";
        boolean isQuit = false;



        System.out.println("\n\n\n********** MOBILE TICKET VIEWER **********");
        System.out.println("\n\nWelcome to the ticket viewer\n\n");
        Scanner scanner = new Scanner(System.in);
        while(true) {

            if(isQuit) break;

            // Displays the menu for the Ticket Viewer

            System.out.println("Type 'menu' to view the menu or 'quit' to quit");
            String menuOption = scanner.nextLine();

            if (menuOption.toLowerCase().equals(menu)) {
                while (true) {
                    System.out.println("\n\n\t\t\tSelect View Options:");
                    System.out.println("\t\t\t\t* Press 1 to view all tickets");
                    System.out.println("\t\t\t\t* Press 2 to view a ticket");
                    System.out.println("\t\t\t\t* Type 'quit' to exit");
                    String viewOption = scanner.nextLine();
                    switch (viewOption) {
                        case "1":
                            printAllTickets(subDomain,user,token);
                            break;
                        case "2":
                            System.out.println("enter ticket id : ");
                            int ticketNumber = scanner.nextInt();
                            scanner.nextLine();
                            printSpecificTicket(ticketNumber,subDomain,user,token);
                            break;
                        case "quit":
                            isQuit = true;
                            break;
                        default:
                            System.out.println(" Invalid Option! Please try again \n");
                            break;
                    }
                    if (isQuit) {
                        System.out.println("Thank you for using the ticket viewer");
                        System.out.println("Goodbye :)");
                        break;
                    }
                }
            } else if (menuOption.toLowerCase().equals(quit)) {
                System.out.println("Thank you for using the ticket viewer");
                System.out.println("Goodbye :)");
                break;
            } else {
                System.out.println("Invalid Option!");
                System.out.println("Please try again \n");
            }
        }

    }

    //method to print all tickets
    public static void printAllTickets(String subdomain, String user, String token){


        JSONArray arr = retrieveAllData(subdomain, user, token);
        // An array of JSON objects are retrieved
        // Each ticket is listed with minimal information
        // Displays 25 tickets for each page
        if(arr == null) return;
        int ticketCount = 0;
        Boolean isNext = true;
        int i = 0;
        for (i = 0; i < arr.size(); i++) {
            JSONObject new_obj = (JSONObject) arr.get(i);
            System.out.println("Ticket id : " + new_obj.get("id"));
            System.out.println("Subject: " + new_obj.get("subject"));
            System.out.println("Submitter: " + new_obj.get("submitter_id"));
            System.out.println("Last updated on " + new_obj.get("updated_at") + "\n");
            ticketCount++;
            if(ticketCount == 25){
                Boolean option = true;
                ticketCount = 0;
                while(option) {
                    System.out.println("Next page? 'yes' / 'no' :");
                    Scanner tScanner = new Scanner(System.in);
                    String nextPage = tScanner.nextLine();
                    if (nextPage.toLowerCase().equals("no")){
                        option =false;
                        isNext = false;
                        break;
                    }
                    else if (nextPage.toLowerCase().equals("yes")) {
                        option = false;
                        continue;
                    }
                    else System.out.println("Invalid option, try again");
                }
                if(!isNext) break;
            }
        }
        if(i == arr.size()){
            System.out.println("End of list! No more tickets to show.");
        }
        
    }

    // Method to display detailed information for a specific ticket

    public static void printSpecificTicket(int ticketNumber, String subdomain, String user, String token){

        JSONObject obj = retrieveTicket(ticketNumber,subdomain, user, token);
        // A JSON object is retrieved and the required values are displayed

        if(obj == null) return;
        System.out.println("\nTICKET DETAILS");
        System.out.println("Ticket id : " + obj.get("id"));
        System.out.println("Created on : " + obj.get("created_at"));
        System.out.println("Subject : " + obj.get("raw_subject"));
        System.out.println("Requester : " + obj.get("requester_id"));
        System.out.println("Assigned to : " + obj.get("assignee_id"));
        System.out.println("Last updated on : "  + obj.get("updated_at"));
        System.out.println("Ticket Status : "  + obj.get("status"));
        System.out.println("Full Description : ");
        System.out.println(obj.get("description"));
    }

    // Method to fetch the information regarding a specific ticket from Zendesk using the API
    public static JSONObject retrieveTicket(int ticketNumber, String subdomain, String user, String token) {

        String fullUser = user + "/token";
        // Providing authentication for the API request


        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fullUser,token.toCharArray());
            }
        });
        JSONObject obj = null;
        try {

            // GET request to get a ticket from Zendesk

            URL url = new URL("https://"+subdomain+".zendesk.com/api/v2/tickets/"+ ticketNumber + ".json?");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            int responseCode = conn.getResponseCode();

            // Response code error handling
            if (responseCode == 404){
                System.err.println("No such ticket exists! Please enter a valid ticket number");
            }else if(responseCode == 400){
                System.err.println("The request was invalid! Please check the ticket number");
            }else if(responseCode == 500 ){
                System.err.println("Internal Server Error. The API is not available right now!");
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }else if(responseCode == 401){
                System.err.println("Uh oh! you are not authorized to view this content");
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }else if (responseCode != 200) {
                System.err.println("\n\nOops! looks like we ran into some error. Please find the error as follows: \n");
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                JSONParser parse = new JSONParser();
                JSONObject data_obj = (JSONObject) parse.parse(inline);
                obj = (JSONObject) data_obj.get("ticket");
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    // Method to retrieve all tickets from Zendesk using the API
    public static JSONArray retrieveAllData(String subdomain, String user, String token){

        JSONArray arr = null;
        String fulluser = user + "/token";

        // Providing authentication for the API request

        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fulluser,token.toCharArray());
            }
        });

        try {
            // GET request to get all tickets from Zendesk

            URL url = new URL("https://" + subdomain + ".zendesk.com/api/v2/tickets.json?");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            int responseCode = conn.getResponseCode();

            // Response code error handling

            if (responseCode == 404){
                System.err.println("No such tickets exist!");
            }else if(responseCode == 500 ){
                System.err.println("Internal Server Error. The API is not available right now!");
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }else if(responseCode == 401){
                System.err.println("Uh oh! you are not authorized to view this content");
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }else if (responseCode != 200) {
                System.err.println("\n\nOops! looks like we ran into some error. Please find the error as follows: \n");
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                JSONParser parse = new JSONParser();
                JSONObject data_obj = (JSONObject) parse.parse(inline);
                arr = (JSONArray) data_obj.get("tickets");
                
            }

        } catch (Exception e) {
        e.printStackTrace();
    }
        return arr;
}



}
