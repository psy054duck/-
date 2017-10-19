package storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class is used to write to or read from files.
 */
public class Storage {
    private static Storage instance = null;

    /**
     * Get the Storage singleton
     * 
     * @return this sole instance of this class.
     */
    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    /**
     * Save an object to a specific file. 
     * 
     * @param obj the object to be saved
     * @param filename the name of file where obj should be save to
     * @throws IllegalArgumentException if some argument is null
     */
    public void save(Object obj, String filename) {
        if (obj == null || filename == null || filename.equals("")) {
            throw new IllegalArgumentException(
                         "obj and filename should NOT be null");
        }
        try {
            FileOutputStream fp = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fp);
            out.writeObject(obj);
            out.close();
        } catch (Exception e) {
            System.out.println("failed to write to file");
        }

    }

    /**
     * Load an object from a specific file.
     * 
     * @param filename the name of file where the object should be load from
     * 
     * @return the object read from the specified file.
     * 
     * @throws IllegalArgumentException if filename is null
     */
    public Object load(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("filename should NOT be null");
        }
        try {
            FileInputStream fp = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fp);
            Object ret = in.readObject();
            in.close();
            return ret;
        } catch (Exception e) {
            System.out.println("failed to read from file");
        }
        return null;
    }

    /**
     * Private constructor to avoid user from creating more than one Storage object
     */
    private Storage() {}
}