package com.abhilash;

import jdk.swing.interop.SwingInterOpUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.*;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String token = "C2wruqvtdnC4gBd2evT70RbNtC4aQ7F8qUdLwj1S";
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
                            printAllTickets(token);
                            break;
                        case "2":
                            System.out.println("enter ticket id : ");
                            int ticketNumber = scanner.nextInt();
                            scanner.nextLine();
                            printSpecificTicket(ticketNumber,token);
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
    public static void printAllTickets(String token){
        // An array of JSON objects are returned
        // Each ticket is listed with minimal information
        // Displays 25 tickets for each page

        JSONArray arr = retrieveAllData(token);
        if(arr == null) return;
        int ticketCount = 0;
        Boolean isNext = true;
        for (int i = 0; i < arr.size(); i++) {
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
        
    }

    // Method to display detailed information for a specific ticket

    public static void printSpecificTicket(int ticketNumber, String token){
        // A JSON object is returned and the required values are displayed

        JSONObject obj = retrieveTicket(ticketNumber,token);
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
    public static JSONObject retrieveTicket(int ticketNumber, String token) {

        // Providing authentication for the API request

        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("asrivathsa@scu.edu/token",token.toCharArray());
            }
        });
        JSONObject obj = null;
        try {

            // GET request to get a ticket from Zendesk

            URL url = new URL("https://zccabhilash.zendesk.com/api/v2/tickets/"+ ticketNumber + ".json?");
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
    public static JSONArray retrieveAllData(String token){
        JSONArray arr = null;

        // Providing authentication for the API request

        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("asrivathsa@scu.edu/token",token.toCharArray());
            }
        });

        try {
            // GET request to get all tickets from Zendesk

            URL url = new URL("https://zccabhilash.zendesk.com/api/v2/tickets.json?");

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
