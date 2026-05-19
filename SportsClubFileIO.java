package fileio;

import entity.Player;
import java.io.*;

public class SportsClubFileIO {
    private static final String FILE_NAME = "players.txt";

    public static void createFileIfNotExists() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static void addPlayer(Player player) throws IOException {
        createFileIfNotExists();
        FileWriter fw = new FileWriter(FILE_NAME, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(player.toString());
        bw.newLine();
        bw.close();
        fw.close();
    }

    public static boolean idExists(String id) {
        try {
            createFileIfNotExists();
            FileReader fr = new FileReader(FILE_NAME);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                Player p = Player.fromLine(line);
                if (p != null && p.getId().equals(id)) {
                    br.close();
                    fr.close();
                    return true;
                }
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Error checking ID");
        }
        return false;
    }

    public static int countRecords() {
        int count = 0;
        try {
            createFileIfNotExists();
            FileReader fr = new FileReader(FILE_NAME);
            BufferedReader br = new BufferedReader(fr);
            while (br.readLine() != null) {
                count++;
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Error counting records");
        }
        return count;
    }

    public static Object[][] getAllPlayers() {
        int total = countRecords();
        Object[][] data = new Object[total][4];
        try {
            createFileIfNotExists();
            FileReader fr = new FileReader(FILE_NAME);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                Player p = Player.fromLine(line);
                if (p != null) {
                    data[index][0] = p.getId();
                    data[index][1] = p.getName();
                    data[index][2] = p.getAge();
                    data[index][3] = p.getSportsType();
                    index++;
                }
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Error loading all data");
        }
        return data;
    }

    public static boolean updatePlayer(Player updatedPlayer) throws IOException {
        createFileIfNotExists();
        File oldFile = new File(FILE_NAME);
        File tempFile = new File("temp.txt");
        FileReader fr = new FileReader(oldFile);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter(tempFile, true);
        BufferedWriter bw = new BufferedWriter(fw);
        String line;
        boolean isUpdated = false;
        while ((line = br.readLine()) != null) {
            Player p = Player.fromLine(line);
            if (p != null && p.getId().equals(updatedPlayer.getId())) {
                bw.write(updatedPlayer.toString());
                isUpdated = true;
            } else {
                bw.write(line);
            }
            bw.newLine();
        }
        bw.close();
        fw.close();
        br.close();
        fr.close();
        if (oldFile.delete()) {
            tempFile.renameTo(oldFile);
        }
        return isUpdated;
    }

    public static boolean deletePlayer(String id) throws IOException {
        createFileIfNotExists();
        File oldFile = new File(FILE_NAME);
        File tempFile = new File("temp.txt");
        FileReader fr = new FileReader(oldFile);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter(tempFile, true);
        BufferedWriter bw = new BufferedWriter(fw);
        String line;
        boolean isDeleted = false;
        while ((line = br.readLine()) != null) {
            Player p = Player.fromLine(line);
            if (p != null && p.getId().equals(id)) {
                isDeleted = true;
                continue;
            }
            bw.write(line);
            bw.newLine();
        }
        bw.close();
        fw.close();
        br.close();
        fr.close();
        if (oldFile.delete()) {
            tempFile.renameTo(oldFile);
        }
        return isDeleted;
    }

    public static Object[][] searchPlayers(String keyword) {
        int matchCount = 0;
        String lowerKey = keyword.toLowerCase();
        try {
            createFileIfNotExists();
            FileReader fr = new FileReader(FILE_NAME);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                Player p = Player.fromLine(line);
                if (p != null && (p.getId().toLowerCase().contains(lowerKey)
                        || p.getName().toLowerCase().contains(lowerKey))) {
                    matchCount++;
                }
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Error scanning for search");
        }

        Object[][] result = new Object[matchCount][4];
        try {
            FileReader fr = new FileReader(FILE_NAME);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                Player p = Player.fromLine(line);
                if (p != null && (p.getId().toLowerCase().contains(lowerKey)
                        || p.getName().toLowerCase().contains(lowerKey))) {
                    result[index][0] = p.getId();
                    result[index][1] = p.getName();
                    result[index][2] = p.getAge();
                    result[index][3] = p.getSportsType();
                    index++;
                }
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Error filling search data");
        }
        return result;
    }
}