import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Description
 *
 * @author YUHAO SONG
 * Student_id  981738
 * Date        2019-09-05
 * @version 1.5
 */
public class ThreadHandler implements Runnable {
    public static Dictionary dictionary;
    private Socket socket;   // A connected socket
    private DataInputStream inputFromClient;
    private DataOutputStream outputToClient;

    public ThreadHandler(Socket socket, Dictionary dictionary) {
        this.socket = socket;
        ThreadHandler.dictionary = dictionary;
    }

    public static Dictionary getDictionary() {
        return dictionary;
    }

    /**
     * Parse the Object and do the corresponding task depends on the method
     *
     * @param jsonObject jsonObject from client
     * @throws IOException IO
     */
    private void parseAndReplyJSONObject(JSONObject jsonObject) throws IOException {
        String method = ((String) jsonObject.get("method_name")).trim().toLowerCase();
        String word = ((String) jsonObject.get("word_name")).trim().toLowerCase();
        String definition = ((String) jsonObject.get("word_definition")).trim().toLowerCase();
        String message = "";
        JSONObject result;

        switch (method) {
            // Search if the word is exist in the dictionary
            case "search":
                if (dictionary.containsWord(word)) {
                    definition = dictionary.getDefinition(word);
                    method = "searchsucc";
                    message = "Word <" + word + "> search successfully!" + "\n";
                } else {
                    method = "searchfail";
                    message = "Word <" + word + "> is not in the dictionary!" + "\n" + "Try to add by yourself!" + "\n";
                }
                break;

            // Add the word and definition to the dictionary, append the new meaning if the word already exist
            // And if the definition is same to the previous. Not change.
            case "add":
                if (!dictionary.containsWord(word)) {
                    dictionary.updateWord(word, definition);
                    message = "Word <" + word + "> added successfully!";
                } else {
                    String curDefinition = dictionary.getDefinition(word);
                    // Exist and same definition
                    if (curDefinition.contains(definition)) {
                        message = "Word <" + word + "> Exists." + "\n" + "Definition <" + definition + "> Exists."
                                + "\n" + "Try to add different definition!";
                    } else {   // Exist but new definition
                        definition = curDefinition + "#" + definition;
                        dictionary.updateWord(word, definition);
                        message = "Word <" + word + "> Exists." + "\n" + " New definition added successfully!";
                    }
                }
                break;

            // Remove the word if exist.
            case "remove":
                if (!dictionary.containsWord(word)) {
                    message = "Word <" + word + "> Not exist! Could not remove!";
                } else {
                    dictionary.removeWord(word);
                    message = "Word <" + word + "> removed successfully!";
                }
                break;

            // Decrease the number of client when close the window to check if the server could turn off later
            case "exit":
                DictServer.clientAmount--;
                message = "Bye";
                break;

        }
        sendBackToClient(method, word, definition, message);
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            // Create data input and output streams
            JSONParser jsonParser = new JSONParser();
            inputFromClient = new DataInputStream(socket.getInputStream());
            outputToClient = new DataOutputStream(socket.getOutputStream());

            // Continuously serve the client
            while (true) {
                if (inputFromClient.available() > 0) {
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(inputFromClient.readUTF());
                    parseAndReplyJSONObject(jsonObject);
                }
            }
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        } finally {
            try {
                inputFromClient.close();
                outputToClient.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Create the JSON Object and send back to the client
    private void sendBackToClient(String method, String word, String definition, String message) throws IOException {
        JSONObject jsonMsg = new JSONObject();
        jsonMsg.put("method_name", method);
        jsonMsg.put("word_name", word);
        jsonMsg.put("word_definition", definition);
        jsonMsg.put("system_message", message);

        outputToClient.writeUTF(jsonMsg.toJSONString());
        outputToClient.flush();
    }
}