package server;

import exeptions.UserNotRegisteredException;
import exeptions.WrongPasswordException;
import exeptions.WrongUserDataException;
import protocol.ServerProtocol;
import protocol.messages.HelloMessage;
import protocol.messages.LoginMessage;
import protocol.messages.VoteSuccessfulMessage;
import protocol.messages.WrongVoteInfoMessage;
import sequrity.Crypto;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by bozhidar on 08.11.17.
 */
public class ServerRunnable implements Runnable {
    private final Socket client;
    private Server server;
    //Object streams
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    //The id of the person that communicates with this server connection
    private String id = "";
    //The last used nonce for communication, stored for reverse checking of the returned hashes
    private long lastNonce = -1;
    //Flags for voting states
    private boolean isVotingFinished = false;
    private boolean hasVoted = false;
    //Protocol for the server
    private ServerProtocol protocol;
    //A flag for whether to read from input; if false and the client is false the connection stops
    private boolean keepAlive = true;


    public ServerRunnable(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            //Initialise global variables
            objectOut = new ObjectOutputStream(client.getOutputStream());
            objectIn = new ObjectInputStream(client.getInputStream());
            protocol = new ServerProtocol(this);

            System.out.println("New connection started!");

            //Send greeting to the client
            reply(new HelloMessage("Please log in!"));

            Object msg;
            while (keepAlive) {
                if ((msg = objectIn.readObject()) != null) {
                    protocol.getNextStep(msg);
                }
            }

        } catch (IOException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            System.out.println("Server couldn't start! Shutting down...");
        }
    }

    /**
     * Takes a hashed name and the cnonce sent from the client and checks if this person is valid candidate.
     * It uses reverse engineering - gets every single eligible name and hashes it with the cnonce.
     *
     * @param hashedName is the byte[] representing the hashed name
     * @param cnonce is the client nonce
     * @return the name if it was found; otherwise - null
     */
    private String isCandidate(byte[] hashedName, long cnonce) {
        for (String name : server.getCandidates().getNames()) {
            if (Arrays.equals(Crypto.hashSHA256(name + cnonce), hashedName)) {
                return name;
            }
        }
        return null;
    }

    /**
     * Processes the vote sent by the client.
     *
     * @param hashName is the hashed name of the candidate
     * @param cnonce is the client nonce
     * @param hashCandidates is the hashed list of all candidates
     * @return an appropriate message object
     * @throws IOException
     */
    public Object processVote(byte[] hashName, long cnonce, byte[] hashCandidates) throws IOException {
        //Get a string list of all the names
        String names = "";
        for (String name : server.getCandidates().getNames()) {
            names += name;
        }
        //If the sent hash is the same as the sever hash (protects against fake messages)
        if(Arrays.equals(Crypto.hashSHA256(lastNonce + "" + cnonce + names), hashCandidates)) {
            //Check if the sent name is valid
            String votedName = isCandidate(hashName, cnonce);
            if (votedName != null) {
                //Reset nonce so that it is invulnerable to reply attacks
                lastNonce = Crypto.getSecureLong();
                //Change database files to reflect the new vote
                server.getCandidates().vote(votedName);
                ArrayList<byte[]> userData = server.getRegisteredStudents().read(id);
                server.getRegisteredStudents().write(id, userData.get(0), userData.get(1), userData.get(2), "t".getBytes());
                return new VoteSuccessfulMessage();
            } else {
                return new WrongVoteInfoMessage("The candidate name is wrong!");
            }
        } else {
            return new WrongVoteInfoMessage("You have received a tampered message!");
        }
    }

    /**
     * Checks if an users login info is correct.
     *
     * @param loginMessage is the message passed by the client
     * @return a flag for whether the log in was successful or an exception
     * @throws UserNotRegisteredException if the matric number is not in the database
     * @throws WrongPasswordException if the password is wrong
     * @throws WrongUserDataException if the name or dob of the user are wrong
     */
    public boolean userLogIn(LoginMessage loginMessage) throws UserNotRegisteredException, WrongPasswordException, WrongUserDataException {
        //Keep a record of who the person is, so that the files can be edited on vote
        id = loginMessage.getMatric();
        return server.getRegisteredStudents().checkLogInData(loginMessage);
    }

    /**
     * Updates the vote flags to reflect the current situation.
     */
    public void updateVoteFlags() {
        try {
            hasVoted = server.getRegisteredStudents().hasVoted(id);
        } catch (IOException e) {
            //Never happens
            e.printStackTrace();
        }
        isVotingFinished = server.getVotingComplete().get();
    }

    /**
     * Stops taking in input from the client.
     */
    public void logOut() {
        keepAlive = false;
    }

    /**
     * Sends a message to the client.
     *
     * @param msg is the message to be sent
     * @throws IOException
     */
    public void reply(Serializable msg) throws IOException {
        objectOut.writeObject(msg);
    }

    /**
     * Gets the names of the candidates, used for sending CandidatesListMessage;
     * It also sets a new nonce to be used in the message.
     *
     * @return the list of the names
     */
    public String[] getNames() {
        lastNonce = Crypto.getSecureLong();
        return server.getCandidates().getNames();
    }

    //Getters

    public void displayMessage(String msg) {
        System.out.println(msg);
    }

    public long getLastNonce() {
        return lastNonce;
    }

    public boolean isVotingFinished() {
        return isVotingFinished;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public Server getServer() {
        return server;
    }

    public ServerProtocol getProtocol() {
        return protocol;
    }

    public String getId() {
        return id;
    }
}
