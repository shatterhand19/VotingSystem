package mains;

import client.Client;

import java.io.IOException;

/**
 * Created by bozhidar on 08.11.17.
 */
public class client {
    private static String serverIP = "127.0.0.1";
    private static int serverPort = 10000;

    public static void main(String[] args) {
        //Read command line arguments if such exist
        if (args.length > 0 && (args.length != 4 || !args[0].equals("-ip") || !args[2].equals("-p"))) {
            System.out.println("Wrong command line arguments! Closing!");
        } else {
            serverIP = args[1];
            serverPort = Integer.parseInt(args[3]);
        }
        try {
            Client client = new Client(serverIP, serverPort);
        } catch (IOException e) {
            System.out.println("There was a problem with reading/writing to the database.");
        }
    }
}
