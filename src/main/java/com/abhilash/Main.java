package com.abhilash;

import jdk.swing.interop.SwingInterOpUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.*;
import java.util.Locale;
import java.util.Scanner;

public class Min {

    public static void main(String[] args) {
        String menu = "menu";
        String quit = "quit";
        boolean isQuit = false;

        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("asrivathsa@scu.edu/token","C2wruqvtdnC4gBd2evT70RbNtC4aQ7F8qUdLwj1S".toCharArray());
            }
        });

        System.out.println("\n\n\n********** MOBILE TICKET VIEWER **********");
        System.out.println("\n\nWelcome to the ticket viewer\n\n");
        Scanner scanner = new Scanner(System.in);
        while(true) {

            if(isQuit) break;

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
                            retrieveAllData();
                            break;
                        case "2":
                            System.out.println("enter ticket id : ");
                            int ticketNumber = scanner.nextInt();
                            scanner.nextLine();
                            retrieveTicket(ticketNumber);
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

    public static void retrieveTicket(int ticketNumber) {

        try {
            URL url = new URL("https://zccabhilash.zendesk.com/api/v2/tickets/"+ ticketNumber + ".json?");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responseCode = conn.getResponseCode();

            if (responseCode == 404){
                System.out.println("No such ticket exists! Please enter a valid ticket number");
            }else if(responseCode == 500 ){
                System.out.println("Internal Server Error. The API is not available right now!");
            }else if(responseCode == 401){
                System.out.println("Uh oh! you are not authorized to view this content");
            }else if (responseCode != 200) {
                System.err.println("\n\nOops! looks like we ran into some error. Please find the error as follows: \n");
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONParser parse = new JSONParser();
                JSONObject data_obj = (JSONObject) parse.parse(inline);
                JSONObject obj = (JSONObject) data_obj.get("ticket");
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void retrieveAllData(){
        try {

            URL url = new URL("https://zccabhilash.zendesk.com/api/v2/tickets.json?");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                System.err.println("\n\nOops! looks like we ran into some error. Please find the error as follows: \n");
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONParser parse = new JSONParser();
                JSONObject data_obj = (JSONObject) parse.parse(inline);
                JSONArray arr = (JSONArray) data_obj.get("tickets");
                int ticketCount = 0;
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject new_obj = (JSONObject) arr.get(i);
                    System.out.println("Ticket id : " + new_obj.get("id"));
                    System.out.println("Subject: " + new_obj.get("subject"));
                    System.out.println("Submitter: " + new_obj.get("submitter_id"));
                    System.out.println("Last updated on " + new_obj.get("updated_at") + "\n");
                    ticketCount++;
                    if(ticketCount == 25){
                        ticketCount = 0;
                        System.out.println("Next page? 'yes' / 'no' :");
                        Scanner tScanner = new Scanner(System.in);
                        String nextPage = tScanner.nextLine();
                        if(nextPage.equals("no")) break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
