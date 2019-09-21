import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Description
 *
 * @author YUHAO SONG
 * Student_id  981738
 * Date        2019-09-05
 * @version 1.5
 */
public class Dictionary {
    private Map<String, String> dictionary = new HashMap<>();

    // load and create the Dictionary object using chosen file path
    public Dictionary(String filePath) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(filePath));
            for (String key : properties.stringPropertyNames()) {
                dictionary.put(key, properties.get(key).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // save the dictionary to file before turn off
    public void saveDict(String filePath) {
        Properties properties = new Properties();
        if (!(dictionary == null)) {
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }
        try {
            properties.store(new FileOutputStream(filePath), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // synchronized function
    public synchronized boolean containsWord(String word) {
        return dictionary.containsKey(word);
    }

    // synchronized function
    public synchronized String getDefinition(String word) {
        return dictionary.get(word);
    }

    // synchronized function
    public synchronized void updateWord(String word, String definition) {
        dictionary.put(word, definition);
    }

    // synchronized function
    public synchronized void removeWord(String word) {
        dictionary.remove(word);
    }

    public int getSize() {
        return dictionary.size();
    }

    public Dictionary getDict() {
        return (Dictionary) dictionary;
    }

    public void printDict() {
        dictionary.forEach((key, value) -> System.out.println(key + " " + value));
    }
}