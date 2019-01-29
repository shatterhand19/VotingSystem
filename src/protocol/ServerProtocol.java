package protocol;

import database.CandidatesDB;
import exeptions.UserNotRegisteredException;
import exeptions.WrongPasswordException;
import exeptions.WrongUserDataException;
import protocol.messages.*;
import server.ServerRunnable;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by bozhidar on 08.11.17.
 */
public class ServerProtocol {
    private Method current;
    private ServerRunnable serverInstance;

    public ServerProtocol (ServerRunnable serverInstance) {
        changeState("listening", Object.class);
        this.serverInstance = serverInstance;
    }

    public void getNextStep(Object msg) throws InvocationTargetException, IllegalAccessException {
        current.invoke(this, msg);
    }

    /**
     * Listens for initial messages. Should get a LoginMessage.
     *
     * @param msg
     */
    private void listening(Object msg) {
        if (msg instanceof LoginMessage) {
            changeState("login", LoginMessage.class);
            login((LoginMessage) msg);
        } else {
            returnMessage(new WrongMessage());
        }
    }

    /**
     * Sends a message to the client.
     *
     * @param msg
     */
    private void returnMessage(Serializable msg) {
        try {
            serverInstance.reply(msg);
        } catch (IOException e) {
            serverInstance.displayMessage(msg.toString());
        }
    }

    /**
     * Handles a LoginMessage. If there is an exception during log in, it goes back to the login menu
     * and listens for another log in. It also send a message to the client with the error type.
     *
     * @param msg
     */
    private void login(LoginMessage msg) {
        try {
            //Check with server
            boolean isLoggingIn = serverInstance.userLogIn(msg);
            if(isLoggingIn) {
                serverInstance.displayMessage(msg.getMatric() + " logged in.");
                //If the person actually logged in send a confirm message
                returnMessage(new SuccessLoginMessage());
                //Proceed to next state
                changeState("determinePostLoginState");
                determinePostLoginState();
            }
        } catch (UserNotRegisteredException e) {
            changeState("listening", Object.class);
            returnMessage(new UserNotRegisteredMessage());
        } catch (WrongPasswordException e) {
            changeState("listening", Object.class);
            returnMessage(new WrongPasswordMessage());
        } catch (WrongUserDataException e) {
            changeState("listening", Object.class);
            returnMessage(new WrongUserDataMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Based on server data decide the next state of the protocol.
     * If the person has voted but not everyone has finished, it will go to wait for results state.
     * If all people have finished then send the results directly.
     * If not any of the above, send the candidates list.
     *
     * @throws IOException
     */
    private void determinePostLoginState() throws IOException {
        //Update the vote flags
        serverInstance.updateVoteFlags();
        boolean hasVoted = serverInstance.hasVoted();
        boolean isVotingFinished = serverInstance.isVotingFinished();

        if (isVotingFinished) {
            changeState("sendResult");
            sendResult();
        } else if (hasVoted) {
            returnMessage(new VoteSuccessfulMessage());
            changeState("logOutUser");
            logOutUser();
        } else {
            changeState("sendCandidates");
            sendCandidates();
        }
    }

    /**
     * Sends a candidate list to the user.
     */
    private void sendCandidates() {
        String[] names = serverInstance.getNames();
        //Create and send message and change state to listening after login.
        CandidatesListMessage msg = new CandidatesListMessage(names, serverInstance.getLastNonce());
        returnMessage(msg);
        changeState("loggedListening", Object.class);
    }

    /**
     * Listens for messages after log in.
     * If the message is a VoteMessage then it will check with the server if this user
     * has already voted (in case someone has logged in from two computers, reached the vote stage
     * on both of them and then sends simultaneously vote. If they haven't voted, it will proceed
     * to process the vote message.
     * If the message is LogOutMessage it will log out the user.
     *
     * @param msg
     * @throws IOException
     */
    private void loggedListening(Object msg) throws IOException {
        if (msg instanceof VoteMessage) {
            serverInstance.updateVoteFlags();
            if (serverInstance.hasVoted()) {
                changeState("determinePostLoginState");
                determinePostLoginState();
            } else {
                changeState("processVoteMessage", Object.class);
                processVoteMessage(serverInstance.processVote(((VoteMessage) msg).getName(), ((VoteMessage) msg).getCnonce(), ((VoteMessage) msg).getHash()));
            }
        } else if (msg instanceof LogOutMessage) {
            changeState("logOutUser");
            logOutUser();
        } else {
            returnMessage(new WrongMessage());
        }
    }

    /**
     * Processes vote message.
     * If it is a successful vote, log the user out.
     * If it is not, send candidates again.
     *
     * @param msg
     */
    private void processVoteMessage(Object msg) {
        if (msg instanceof VoteSuccessfulMessage) {
            returnMessage((VoteSuccessfulMessage) msg);
            changeState("logOutUser");
            logOutUser();
        } else if (msg instanceof WrongVoteInfoMessage) {
            returnMessage((WrongVoteInfoMessage) msg);
            changeState("sendCandidates");
            sendCandidates();
        } else {
            returnMessage(new WrongMessage());
        }
    }

    /**
     * Sends the results to the client and then proceeds to logging him out.
     */
    public void sendResult() {
        CandidatesDB results = serverInstance.getServer().getCandidates();
        returnMessage(new ResultsMessage(results.getCandidates(), results.getVotes()));
        changeState("logOutUser");
        logOutUser();
    }

    /**
     * Logs out the user (stops taking input).
     */
    private void logOutUser() {
        returnMessage(new LogOutSuccessfulMessage());
        serverInstance.displayMessage(serverInstance.getId() + " logged out.");
        serverInstance.logOut();
    }

    /**
     * Changes the current state.
     *
     * @param next is the name of the next state
     * @param types are the types the method takes
     */
    private void changeState(String next, Class... types) {
        try {
            current = this.getClass().getDeclaredMethod(next, types);
        } catch (NoSuchMethodException e) {
            //Impossible
            e.printStackTrace();
        }
    }

}
