package server;

import database.CandidatesDB;
import database.StudentsDB;
import pool.ThreadPool;
import sequrity.Crypto;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bozhidar on 08.11.17.
 */
public class Server {
    private SSLServerSocket serverSocket;
    public final static String algorithm = "SSL";

    private StudentsDB registeredStudents;
    private CandidatesDB candidates;
    private int totalVotes;
    private AtomicBoolean votingComplete = new AtomicBoolean(false);
    private ThreadPool pool;

    private File timestamp = new File("database/timestamp");
    private long start;

    private final long DURATION = 30000;
    private final int QUEUE_SIZE = 10000;
    private final int THREADS = 50;

    public Server(int port) {
        try {
            setSettings(port);

            //Initialise databases
            registeredStudents = new StudentsDB("database/students/");
            candidates = new CandidatesDB("database/candidates/names");

            //Create pool
            pool = new ThreadPool(QUEUE_SIZE, THREADS);

            //Create timestamp to be used in event of crash
            start = createTimestamp();
        } catch (IOException | CertificateException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            System.out.println("Server couldn't start, exiting...");
            e.printStackTrace();
            return;
        }

        //The election complete thread
        new Thread(() -> {
            //Checks if the voting is complete, if ti is, the thread stops
            while (!votingComplete.get()) {
                //Get current votes
                totalVotes = this.getCandidates().getTotalVotes();
                int totalNumberOfStudents = new File(this.getRegisteredStudents().getLocation()).list().length - 1;
                //Check if the election is complete
                if (totalVotes == totalNumberOfStudents || System.currentTimeMillis() - start > DURATION) {
                    //Change flag
                    votingComplete.set(true);
                    try {
                        //If the timer has run out then send a message to all logged in with the results
                        if (totalNumberOfStudents != totalVotes) {
                            pool.sendMessageToAll();
                        }
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else votingComplete.set(false);
                //The thread is doing the check with pauses so that it doesn't cause bugs and/or lags for the other threads
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //Thread for opening socket connections
        //I am doing it in a separate thread as otherwise the server thread will be blocked on the .accept() method
        new Thread(() -> {while (true) {
            try {
                //Get the new socket and add it to the pool
                Socket client = serverSocket.accept();
                pool.submitTask(new ServerRunnable(client, this));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }}).start();

    }

    /**
     * Method for registering a student to the system.
     *
     * @param studentName is the name of the student
     * @param matricNumber is the matriculation number
     * @param dateOfBirth is the date of birth
     * @param password is the password of the student
     */
    public void registerStudent(String studentName, String matricNumber, String dateOfBirth, String password) {
        try {
            //Get salt
            long salt = Crypto.getSecureLong();

            //Get student hash - this keeps their personal data safe
            byte[] studentHash = Crypto.hashSHA256(studentName + matricNumber + dateOfBirth);

            //Hash password with salt - helps against rainbow attacks
            byte[] hashPassword = Crypto.hashSHA256(password + salt);

            //Writes the data to the database file
            registeredStudents.write(matricNumber, studentHash, hashPassword, String.valueOf(salt).getBytes(), "f".getBytes());
        } catch (IOException e) {
            System.out.println("Problem with writing the student data to the database!");
        }
    }

    /**
     * Creates a timestamp file for the timer of the server.
     * It is deleted only on JVM termination, so in a case of crash this timestamp is saved and reused on start.
     *
     * @return
     * @throws IOException
     */
    private long createTimestamp() throws IOException {
        if (timestamp.exists()) {
            //if it exists then the last time the server crashed, read the data and return the timestamp
            timestamp.deleteOnExit();
            return ByteBuffer.wrap(Files.readAllBytes(timestamp.toPath())).getLong();
        } else {
            //if there is no file, create a new one and write the current time, which is returned by the method
            long now = System.currentTimeMillis();
            timestamp.createNewFile();
            Files.write(timestamp.toPath(), ByteBuffer.allocate(Long.BYTES).putLong(now).array());
            timestamp.deleteOnExit();
            return now;
        }

    }

    /**
     * Sets the settings for the SSL socket and creates the socket itself; the code is taken from the book.
     *
     * @param port
     * @throws IOException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws KeyManagementException
     */
    private void setSettings(int port) throws IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException {
        SSLContext context = SSLContext.getInstance(algorithm);
        // The reference implementation only supports X.509 keys
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");

        // Oracle's default kind of key store
        KeyStore ks = KeyStore.getInstance("JKS");

        // For security, every key store is encrypted with a
        // passphrase that must be provided before we can load
        // it from disk. The passphrase is stored as a char[] array
        // so it can be wiped from memory quickly rather than
        // waiting for a garbage collector.
        char[] password = "passphrase".toCharArray();
        ks.load(new FileInputStream("keys/testkeys"), password);
        kmf.init(ks, password);
        context.init(kmf.getKeyManagers(), null, null);

        Arrays.fill(password, '0');
        SSLServerSocketFactory factory = context.getServerSocketFactory();

        serverSocket = (SSLServerSocket) factory.createServerSocket(port);

        // add anonymous (non-authenticated) cipher suites
        String[] supported = serverSocket.getSupportedCipherSuites();
        String[] anonCipherSuitesSupported = new String[supported.length];
        int numAnonCipherSuitesSupported = 0;
        for (int i = 0; i < supported.length; i++) {
            if (supported[i].indexOf("_anon_") > 0) {
                anonCipherSuitesSupported[numAnonCipherSuitesSupported++] =
                        supported[i];
            }
        }
        String[] oldEnabled = serverSocket.getEnabledCipherSuites();
        String[] newEnabled = new String[oldEnabled.length + numAnonCipherSuitesSupported];
        System.arraycopy(oldEnabled, 0, newEnabled, 0, oldEnabled.length);
        System.arraycopy(anonCipherSuitesSupported, 0, newEnabled, oldEnabled.length, numAnonCipherSuitesSupported);
        serverSocket.setEnabledCipherSuites(newEnabled);
    }

    public StudentsDB getRegisteredStudents() {
        return registeredStudents;
    }

    public CandidatesDB getCandidates() {
        return candidates;
    }

    public AtomicBoolean getVotingComplete() {
        return votingComplete;
    }
}
