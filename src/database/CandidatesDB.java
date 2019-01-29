package database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bozhidar on 09.11.17.
 *
 * Helps manage the database with the names of the candidates.
 */
public class CandidatesDB {
    private ArrayList<String> candidates = new ArrayList<>();
    private ArrayList<Integer> votes = new ArrayList<>();
    private String filePath;
    private int totalVotes = 0;

    public CandidatesDB(String filePath) throws IOException {
        this.filePath = filePath;

        //Read file data
        List<String> lines = Files.readAllLines(new File(filePath).toPath());

        for(String line : lines) {
            String[] split = line.split("-");
            //Put data in the arrayLists
            candidates.add(split[0]);
            votes.add(Integer.parseInt(split[1]));
        }
    }

    public String[] getNames() {
        return candidates.toArray(new String[candidates.size()]);
    }

    /**
     * Increases the votes for a person by one and synchronously updates the file.
     *
     * @param name is the person's name
     */
    public synchronized void vote(String name) {
        if (candidates.contains(name)) {
            votes.set(candidates.indexOf(name), votes.get(candidates.indexOf(name)) + 1);
            totalVotes ++;
            try {
                this.updateFile();
            } catch (IOException e) {
                //Cannot happen
                e.printStackTrace();
            }

        }
    }

    /**
     * Updates the file.
     *
     * @throws IOException
     */
    private synchronized void updateFile() throws IOException {
        ArrayList<String> data = new ArrayList<>();
        //Add the lines to the arrayList
        data.add("votes " + totalVotes);
        for (int i = 0; i < candidates.size(); i ++) {
            data.add(candidates.get(i) + "-" + votes.get(i));
        }
        //Write the lines to the file
        Files.write(new File(filePath).toPath(), data);
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public ArrayList<String> getCandidates() {
        return candidates;
    }

    public ArrayList<Integer> getVotes() {
        return votes;
    }
}
