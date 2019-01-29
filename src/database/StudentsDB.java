package database;

import exeptions.UserNotRegisteredException;
import exeptions.WrongPasswordException;
import exeptions.WrongUserDataException;
import protocol.messages.LoginMessage;
import sequrity.Crypto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by bozhidar on 08.11.17.
 *
 * Helps interacting with the database with registered students.
 */
public class StudentsDB {
    private String location;

    public StudentsDB(String location) {
        this.location = location;
    }

    public ArrayList<byte[]> read(String fileName) throws IOException {
        File file = new File(location, fileName);
        //If the file exists
        if (file.exists()) {
            //If we can read the file
            if (file.canRead()) {
                //Create reader, content holder and line holder
                ArrayList<byte[]> content = new ArrayList<>();
                byte[] data = Files.readAllBytes(file.toPath());

                //Read the hashes, salt and flag for vote
                content.add(Arrays.copyOfRange(data, 0, 32));
                content.add(Arrays.copyOfRange(data, 32, 64));
                content.add(Arrays.copyOfRange(data, 64, data.length - 1));
                content.add(Arrays.copyOfRange(data, data.length - 1, data.length));

                //Return content of the file
                return content;
            } else throw new IOException();
        } else throw new FileNotFoundException();
    }

    /**
     * Write data to a file
     * @param fileName is the name of the file
     * @param data is the data that needs to be written
     * @throws IOException
     */
    public void write(String fileName, byte[]... data) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //Write data
        for (byte[] line : data) {
            stream.write(line);
        }
        Files.write(Paths.get(location + fileName), stream.toByteArray());
    }

    /**
     * Checks the login data send from the server.
     *
     * @param msg is the passed LoginMessage
     * @return a boolean showing if the user data is valid or not
     * @throws UserNotRegisteredException if the user is not registered
     * @throws WrongPasswordException if the password sent is wrong
     * @throws WrongUserDataException if the name and date of birth are wrong
     */
    public boolean checkLogInData(LoginMessage msg) throws UserNotRegisteredException, WrongPasswordException, WrongUserDataException {
        //Get the user file
        String fileInDatabase = msg.getMatric();
        File file = new File(location, fileInDatabase);
        //If the file exists then the user is registered
        if (file.exists()) {
            try {
                //Read the file
                ArrayList<byte[]> data = this.read(fileInDatabase);
                byte[] dbHashedStudent = data.get(0);
                byte[] dbPwd = data.get(1);
                String salt = new String(data.get(2));

                //If the student data is the same
                byte[] hashedStudent = Crypto.hashSHA256(msg.getName() + msg.getMatric() + msg.getDob());
                if (Arrays.equals(hashedStudent, dbHashedStudent)) {
                    //If the password is the same
                    byte[] hashedPassword = Crypto.hashSHA256(msg.getPwd() + salt);
                    if (Arrays.equals(hashedPassword, dbPwd)) {
                        return true;
                    } else throw new WrongPasswordException("The password is wrong!");
                } else throw new WrongUserDataException("The user data is wrong!");
            } catch (FileNotFoundException e) {
                //Not possible as a check has been made before that
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else throw new UserNotRegisteredException("User is not registered in the system!");
    }

    /**
     * Checks if a person has voted.
     *
     * @param id is the matric number of the person
     * @return a flag is the person has voted or not
     * @throws IOException
     */
    public boolean hasVoted(String id) throws IOException {
        byte[] flag = this.read(id).get(3);
        return new String(flag).equals("t");
    }

    public String getLocation() {
        return location;
    }
}
