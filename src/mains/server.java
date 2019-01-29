package mains;

import server.Server;

import java.util.Scanner;

/**
 * Created by bozhidar on 08.11.17.
 */
public class server {
    private static int port = 10000;
    public static void main(String[] args) {
        if (args.length > 0 && !args[0].equals("-p")) {
            System.out.println("Wrong command line arguments!");
        } else {
            port = Integer.parseInt(args[1]);
        }

        Server server = new Server(port);
        Scanner in = new Scanner(System.in);

        //Loop for registering students to the database
        while (in.hasNext()) {
            String line = in.nextLine();
            if (line.equals("-r")) {
                System.out.print("Name: ");
                String name = in.nextLine();
                System.out.print("Matric number: ");
                String matric = in.nextLine();
                System.out.print("DOB: ");
                String date = in.nextLine();
                System.out.print("Password: ");
                String pass = in.nextLine();
                server.registerStudent(name, matric, date, pass);
            }
        }
    }
}
