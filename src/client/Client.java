package client;

import protocol.ClientProtocol;
import protocol.messages.LoginMessage;
import protocol.messages.VoteMessage;
import sequrity.Crypto;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by bozhidar on 08.11.17.
 */
public class Client {

    private Socket client;

    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;
    private ClientProtocol protocol;

    private boolean keepAlive = true;
    private String namesStr = "";

    private int TIMEOUT = 120 * 1000; //2 minutes


    public Client(String serverIP, int serverPort) throws IOException {
        //Set the truststore
        System.setProperty("javax.net.ssl.trustStore", "keys/samplecacerts");
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        //Create the socket
        client = factory.createSocket(serverIP, serverPort);

        //Create object streams
        objectOut = new ObjectOutputStream(client.getOutputStream());
        objectIn = new ObjectInputStream(client.getInputStream());

        //Create the protocol
        protocol = new ClientProtocol(this);

        //Create a thread for reading input
        new Thread(() -> {
            Object msg;
            try {
                while (keepAlive && (msg = objectIn.readObject()) != null) {
                    //System.out.println(msg);
                    //Make next step
                    protocol.getNextStep(msg);
                }
            } catch (IOException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Builds a login message from the user's input
     * @return
     */
    public LoginMessage buildLoginMessage() {
        Scanner in = new Scanner(System.in);

        System.out.print("Name: ");
        String name = in.nextLine();

        System.out.print("Matric number: ");
        String matric = in.nextLine();

        System.out.print("DOB: ");
        String dob = in.nextLine();

        System.out.print("Password: ");
        String pwd = in.nextLine();

        return new LoginMessage(name, matric, dob, pwd);
    }

    /**
     * Displays the names of the candidates and takes the vote of the user.
     *
     * @param names are the names of the candidates
     * @param nonce is the nonce sent by the server
     * @return the VoteMessage to be sent to the server
     */
    public VoteMessage vote(String[] names, long nonce) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Who do you vote for?");
        System.out.print("Names: ");
        for (String name : names) {
            namesStr += name;
            System.out.print(name + ";  ");
        }

        long start = System.currentTimeMillis();
        //Run a timer
        while ((System.currentTimeMillis() - start) < TIMEOUT
                && !in.ready()) {
        }

        if (in.ready()) {
            String name = in.readLine();
            //Get cnonce
            long cnonce = Crypto.getSecureLong();
            //Hash the candidate name for security and anonymity
            byte[] hashName = Crypto.hashSHA256(name + cnonce);
            //Hash the candidates names to authenticate with the server that the message hasn't been compromised
            byte[] hashCandidates = Crypto.hashSHA256(nonce + "" + cnonce + namesStr);
            return new VoteMessage(hashName, cnonce, hashCandidates);
        } else {
            return null;
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param msg is the message object to be sent.
     */
    public void reply(Serializable msg) {
        try {
            objectOut.writeObject(msg);
        } catch (IOException e) {
            displayMsg("Error on sending message to the server.");
        }
    }

    /**
     * Displays a message locally to the user.
     *
     * @param msg is the message object
     */
    public void displayMsg(Object msg) {
        System.out.println(msg);
    }

    /**
     * Logs out the client.
     */
    public void logOut() {
        keepAlive = false;
    }

    public ClientProtocol getProtocol() {
        return protocol;
    }

    public int getTIMEOUT() {
        return TIMEOUT;
    }

    /**
     * Basically stops reading the input. Used when the server interrupts the user for clean log out.
     */
    public void stopReading() {
        TIMEOUT = 0;
    }
}
