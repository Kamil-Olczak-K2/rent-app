package com.rentapp.util;

import com.rentapp.gui.scene.SceneCtrl;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.function.Function;


public class Credentials {

    private static final String FILE_NAME = "data.bin";
    private static final String TMP_FILE_NAME = "data_tmp.bin";

    public static boolean addUser(byte[] userName, byte[] password, String role) {

        if(checkIfExists(userName)){
            SceneCtrl.showMessageWindow("Błąd", "Użytkownik o podanej nazwie już istnieje.");
            return false;
        }
        try {
            if(DBQuery.addUser(userName, password, role)){
                Files.write(Paths.get(FILE_NAME),
                        (BCrypt.hashpw(userName, BCrypt.gensalt()) +
                                "\n" + BCrypt.hashpw(role, BCrypt.gensalt()) +
                                "\n" + BCrypt.hashpw(password, BCrypt.gensalt()) + "\n").getBytes(),
                        StandardOpenOption.APPEND);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace(); Logger.logExToFile(e);
        } finally{
            Arrays.fill(password, (byte) 0);
            Arrays.fill(userName, (byte) 0);
        }

        return false;
    }

    public static boolean checkCredentials(byte[] userName, byte[] password) {
        try (BufferedReader bReader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = bReader.readLine();
            while (line != null) {
                if (BCrypt.checkpw(userName, line)) {
                    bReader.readLine();
                    line = bReader.readLine();
                    return BCrypt.checkpw(password, line);
                } else {
                    line = bReader.readLine();
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(); Logger.logExToFile(ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean checkCredentials(byte[] userName, byte[] password, Function<String, Integer> setRole) {
        try (BufferedReader bReader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = bReader.readLine();
            while (line != null) {
                if (BCrypt.checkpw(userName, line)) {
                    line = bReader.readLine();
                    setRole.apply(checkRole(line));
                    line = bReader.readLine();
                    return BCrypt.checkpw(password, line);
                } else {
                    line = bReader.readLine();
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(); Logger.logExToFile(ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Arrays.fill(password, (byte) 0);
            Arrays.fill(userName, (byte) 0);
        }

        return false;
    }

    public static String checkRole(String line){
        if(BCrypt.checkpw("admin", line)){
            return "admin";
        } else {
            return "user";
        }
    }

    public static boolean checkIfExists(byte[] userName){
        try (BufferedReader bReader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = bReader.readLine();
            while (line != null) {
                if (BCrypt.checkpw(userName, line)) {
                    return true;
                } else {
                    line = bReader.readLine();
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(); Logger.logExToFile(ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean changePassword(byte[] userName, byte[] newPassword) {
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(FILE_NAME));
            String line = bReader.readLine();
            Path tmp_file_path = Paths.get(TMP_FILE_NAME);
            if(!Files.exists(tmp_file_path)){
                Files.createFile(tmp_file_path);
            } else {
                Files.delete(tmp_file_path);
                Files.createFile(tmp_file_path);
            }
            while (line != null) {
                Files.write(tmp_file_path, (line + "\n").getBytes() , StandardOpenOption.APPEND);
                if (BCrypt.checkpw(userName, line)) {
                    line = bReader.readLine();
                    Files.write(tmp_file_path, (line + "\n").getBytes() , StandardOpenOption.APPEND);
                    line = bReader.readLine();
                    if(BCrypt.checkpw(newPassword, line)){
                        SceneCtrl.showMessageWindow("Błąd", "Nowe hasło nie może być takie samo jak obecne.");
                        Files.delete(tmp_file_path);
                        return false;
                    }
                    String newLine = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                    Files.write(tmp_file_path, (newLine + "\n").getBytes(), StandardOpenOption.APPEND);
                    line = bReader.readLine();
                } else {
                    line = bReader.readLine();
                }
            }
            bReader.close();
            Thread.sleep(100);
            Files.delete(Paths.get(FILE_NAME));
            Files.move(tmp_file_path, Paths.get(FILE_NAME));
            return DBQuery.updateDatabaseUser(userName, newPassword);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(); Logger.logExToFile(ex);
            SceneCtrl.showMessageWindow("Błąd", "Nie odnaleziono pliku konfiguracyjnego *.bin.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Brak dostępu do pliku konfiguracyjnego *.bin.");
        } finally {
            Arrays.fill(newPassword, (byte) 0);
            Arrays.fill(userName, (byte) 0);
        }
        return false;
    }

    public static boolean removeUser(byte[] userName){
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(FILE_NAME));
            if(!Files.exists(Paths.get(TMP_FILE_NAME))){
                Files.createFile(Paths.get(TMP_FILE_NAME));
            } else {
                Files.delete(Paths.get(TMP_FILE_NAME));
                Files.createFile(Paths.get(TMP_FILE_NAME));
            }
            String line = bReader.readLine();
            while (line != null) {
                if (BCrypt.checkpw(userName, line)) {
                    bReader.readLine();
                    bReader.readLine();
                    line = bReader.readLine();
                } else {
                    Files.write(Paths.get(TMP_FILE_NAME), (line + "\n").getBytes() , StandardOpenOption.APPEND);
                    line = bReader.readLine();
                }
            }
            bReader.close();
            Thread.sleep(100);
            Files.delete(Paths.get(FILE_NAME));
            Files.move(Paths.get(TMP_FILE_NAME), Paths.get(FILE_NAME));
            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(); Logger.logExToFile(ex);
            SceneCtrl.showMessageWindow("Błąd", "Nie odnaleziono pliku konfiguracyjnego *.bin.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); Logger.logExToFile(e);
            SceneCtrl.showMessageWindow("Błąd", "Brak dostępu do pliku konfiguracyjnego *.bin.");
        }
        return false;
    }

    public static String getUserRole(byte[] userName){
        try (BufferedReader bReader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = bReader.readLine();
            while (line != null) {
                if (BCrypt.checkpw(userName, line)) {
                    line = bReader.readLine();
                    return BCrypt.checkpw("admin", line) ? "admin" : "user";
                } else {
                    line = bReader.readLine();
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(); Logger.logExToFile(ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }



}