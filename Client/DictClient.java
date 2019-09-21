import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Description
 *
 * @author YUHAO SONG
 * Student_id  981738
 * Date        2019-09-05
 * @version 1.5
 */
public class DictClient {

    public JFrame frmDistributedDictionaryClient;
    public Socket socket;
    private int portNumber = 2019;
    private JTextField textFieldSearch;
    private JTextField textFieldDefinition;
    private DataInputStream inputFromServer;
    private DataOutputStream outputToServer;
    private JTextArea textAreaDefinitionSearch;
    private JTextArea textAreaMessageSearch;
    private JTextArea textAreaMessageAdd;
    private JTextArea textAreaMessageRemove;
    private String wordIndicator = "Enter Word";

    /**
     * Create the application.
     */
    public DictClient() {
        initialize();
    }

    /**
     * Launch the application.
     */
//    public static void main(String[] args) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    DictClient window = new DictClient();
//                    window.frmDistributedDictionaryClient.setVisible(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmDistributedDictionaryClient = new JFrame();
        frmDistributedDictionaryClient.setTitle("Distributed Dictionary Client @ Yuhao Song");
        frmDistributedDictionaryClient.setBounds(100, 100, 700, 500);
        frmDistributedDictionaryClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmDistributedDictionaryClient.getContentPane().setLayout(new BorderLayout(0, 0));
        frmDistributedDictionaryClient.setResizable(false);
        // Send message to server when close the window to decrease the amount of connected clients
        frmDistributedDictionaryClient.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                try {
                    sendAndReadObjectToServer("exit", "null", "null");
                    System.exit(0);
//                    frmDistributedDictionaryClient.setVisible(false);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Geogira", Font.PLAIN, 16)));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Geogira", Font.PLAIN, 16)));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Bell MT", Font.PLAIN, 26));
        frmDistributedDictionaryClient.getContentPane().add(tabbedPane);

        JPanel searchPane = new JPanel();
        searchPane.setToolTipText("");
        tabbedPane.addTab("Search", null, searchPane, null);
        searchPane.setLayout(null);

        Font textFont = new Font("Georgia", Font.PLAIN, 20);
        JLabel lblWordSearch = new JLabel("WORD");
        lblWordSearch.setFont(textFont);
        lblWordSearch.setBounds(110, 50, 70, 50);
        searchPane.add(lblWordSearch);

        JTextField textFieldSearch = new JTextField();
        textFieldSearch.setText(wordIndicator);
        textFieldSearch.setForeground(Color.GRAY);
        textFieldSearch.setFont(textFont);
        textFieldSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textFieldSearch.getText().trim().equals(wordIndicator)) {
                    textFieldSearch.setText("");
                    textFieldSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textFieldSearch.getText().trim().equals("")) {
                    textFieldSearch.setText(wordIndicator);
                    textFieldSearch.setForeground(Color.GRAY);
                }
            }
        });
        textFieldSearch.setBounds(210, 50, 250, 50);
        searchPane.add(textFieldSearch);
        textFieldSearch.setColumns(10);

        textAreaMessageSearch = new JTextArea();
        textAreaMessageSearch.setForeground(Color.GRAY);
        textAreaMessageSearch.setText("System message will show here");
        textAreaMessageSearch.setLineWrap(true);
        textAreaMessageSearch.setEditable(false);
        textAreaMessageSearch.setFont(new Font("Georgia", Font.PLAIN, 20));
        textAreaMessageSearch.setBounds(210, 282, 400, 100);
        searchPane.add(textAreaMessageSearch);

        JButton btnSearch = new JButton("SEARCH");
        btnSearch.setToolTipText("Search exist word in the dictionary");
        // SEARCH BUTTON: if no word is entered or enter null to the filed, it will not send to the server
        btnSearch.addActionListener(e -> {
            String word = textFieldSearch.getText().trim();
            if (word.equals("") || word.equals(wordIndicator)) {
                textAreaMessageSearch.setText("Please enter word!");
                textAreaMessageSearch.setForeground(Color.RED);
            } else {
                try {
                    textAreaMessageSearch.setText("System message will show here");
                    textAreaMessageSearch.setForeground(Color.GRAY);
                    sendAndReadObjectToServer("search", word, "null");
                } catch (IOException | ParseException ex) {
                    ex.printStackTrace();
                }
            }
        });
        btnSearch.setFont(new Font("Georgia", Font.PLAIN, 20));
        btnSearch.setBackground(new Color(102, 204, 255));
        btnSearch.setBounds(485, 50, 125, 50);
        searchPane.add(btnSearch);

        JLabel lblDefinitionSearch = new JLabel("DEFINITION");
        lblDefinitionSearch.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblDefinitionSearch.setBounds(50, 130, 130, 50);
        searchPane.add(lblDefinitionSearch);

        textAreaDefinitionSearch = new JTextArea();
        textAreaDefinitionSearch.setForeground(Color.GRAY);
        textAreaDefinitionSearch.setText("Definition will show here");
        textAreaDefinitionSearch.setLineWrap(true);
        textAreaDefinitionSearch.setEditable(false);
        textAreaDefinitionSearch.setFont(new Font("Georgia", Font.PLAIN, 20));
        textAreaDefinitionSearch.setBounds(210, 142, 400, 100);
        searchPane.add(textAreaDefinitionSearch);

        JLabel lblMessageSearch = new JLabel("MESSAGE");
        lblMessageSearch.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblMessageSearch.setBounds(75, 270, 105, 50);
        searchPane.add(lblMessageSearch);

        JPanel addPane = new JPanel();
        tabbedPane.addTab("Add", null, addPane, null);
        addPane.setLayout(null);

        JLabel lblWordAdd = new JLabel("WORD");
        lblWordAdd.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblWordAdd.setBounds(110, 50, 70, 50);
        addPane.setLayout(null);
        addPane.add(lblWordAdd);

        JTextField textFieldAdd = new JTextField();
        textFieldAdd.setText(wordIndicator);
        textFieldAdd.setForeground(Color.GRAY);
        textFieldAdd.setFont(textFont);
        textFieldAdd.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textFieldAdd.getText().trim().equals(wordIndicator)) {
                    textFieldAdd.setText("");
                    textFieldAdd.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textFieldAdd.getText().trim().equals("")) {
                    textFieldAdd.setText(wordIndicator);
                    textFieldAdd.setForeground(Color.GRAY);
                }
            }
        });
        textFieldAdd.setBounds(210, 50, 250, 50);
        addPane.add(textFieldAdd);
        textFieldAdd.setColumns(10);

        textAreaMessageAdd = new JTextArea();
        textAreaMessageAdd.setForeground(Color.GRAY);
        textAreaMessageAdd.setText("System message will show here");
        textAreaMessageAdd.setLineWrap(true);
        textAreaMessageAdd.setEditable(false);
        textAreaMessageAdd.setFont(new Font("Georgia", Font.PLAIN, 20));
        textAreaMessageAdd.setBounds(210, 282, 400, 100);
        addPane.add(textAreaMessageAdd);

        JTextArea textAreaDefinitionAdd = new JTextArea();
        textAreaDefinitionAdd.setText("Enter Definition");
        textAreaDefinitionAdd.setForeground(Color.GRAY);
        textAreaDefinitionAdd.setFont(textFont);
        textAreaDefinitionAdd.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textAreaDefinitionAdd.getText().trim().equals("Enter Definition")) {
                    textAreaDefinitionAdd.setText("");
                    textAreaDefinitionAdd.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textAreaDefinitionAdd.getText().trim().equals("")) {
                    textAreaDefinitionAdd.setText("Enter Definition");
                    textAreaDefinitionAdd.setForeground(Color.GRAY);
                }
            }
        });
        textAreaDefinitionAdd.setLineWrap(true);
        textAreaDefinitionAdd.setFont(new Font("Georgia", Font.PLAIN, 20));
        textAreaDefinitionAdd.setBounds(210, 142, 400, 100);
        addPane.add(textAreaDefinitionAdd);

        // ADD BUTTON: Must enter both word and definition to send. Ask the user if sure to add the word
        JButton btnAdd = new JButton("ADD");
        btnAdd.setToolTipText("Add new word or definition to the dictionary");
        btnAdd.addActionListener(e -> {
            int addConfirmNum = JOptionPane.showConfirmDialog(null,
                    " Do you confirm to ADD?", "Select an Option...",
                    JOptionPane.YES_NO_OPTION);
            if (addConfirmNum == 0) {
                String word = textFieldAdd.getText().trim();
                String definition = textAreaDefinitionAdd.getText().trim();
                if (word.equals("") || word.equals(wordIndicator)
                        || definition.equals("") || definition.equals("Enter Definition")) {
                    textAreaMessageAdd.setText("Please enter both word and definition!");
                    textAreaMessageAdd.setForeground(Color.RED);
                } else {
                    try {
                        textAreaMessageAdd.setText("System message will show here");
                        textAreaMessageAdd.setForeground(Color.GRAY);
                        sendAndReadObjectToServer("add", word, definition);
                    } catch (IOException | ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        btnAdd.setFont(new Font("Georgia", Font.PLAIN, 20));
        btnAdd.setBackground(new Color(102, 255, 0));
        btnAdd.setBounds(485, 50, 125, 50);
        addPane.add(btnAdd);

        JLabel lblDefinitionAdd = new JLabel("DEFINITION");
        lblDefinitionAdd.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblDefinitionAdd.setBounds(50, 130, 130, 50);
        addPane.add(lblDefinitionAdd);

        JLabel lblMessageAdd = new JLabel("MESSAGE");
        lblMessageAdd.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblMessageAdd.setBounds(75, 270, 105, 50);
        addPane.add(lblMessageAdd);

        JPanel removePane = new JPanel();
        tabbedPane.addTab("Remove", null, removePane, null);

        JLabel lblWordRemove = new JLabel("WORD");
        lblWordRemove.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblWordRemove.setBounds(110, 50, 70, 50);
        removePane.setLayout(null);
        removePane.add(lblWordRemove);

        JTextField textFieldRemove = new JTextField();
        textFieldRemove.setText(wordIndicator);
        textFieldRemove.setForeground(Color.GRAY);
        textFieldRemove.setFont(textFont);
        textFieldRemove.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textFieldRemove.getText().trim().equals(wordIndicator)) {
                    textFieldRemove.setText("");
                    textFieldRemove.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textFieldRemove.getText().trim().equals("")) {
                    textFieldRemove.setText(wordIndicator);
                    textFieldRemove.setForeground(Color.GRAY);
                }
            }
        });
        textFieldRemove.setBounds(210, 50, 250, 50);
        removePane.add(textFieldRemove);
        textFieldRemove.setColumns(10);

        textAreaMessageRemove = new JTextArea();
        textAreaMessageRemove.setForeground(Color.GRAY);
        textAreaMessageRemove.setText("System message will show here");
        textAreaMessageRemove.setLineWrap(true);
        textAreaMessageRemove.setEditable(false);
        textAreaMessageRemove.setFont(new Font("Georgia", Font.PLAIN, 20));
        textAreaMessageRemove.setBounds(210, 282, 400, 100);
        removePane.add(textAreaMessageRemove);

        // REMOVE BUTTON: Ask the user first to confirm if he/she are sure to remove the word.
        JButton btnRemove = new JButton("REMOVE");
        btnRemove.setToolTipText("Remove a word from the dictionary");
        btnRemove.addActionListener(e -> {
            int removeConfirmNum = JOptionPane.showConfirmDialog(null,
                    "Do you confirm to REMOVE?", "Select an Option...",
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (removeConfirmNum == 0) {
                String word = textFieldRemove.getText().trim();
                if (word.equals("") || word.equals(wordIndicator)) {
                    textAreaMessageRemove.setText("Please enter word!");
                    textAreaMessageRemove.setForeground(Color.RED);
                } else {
                    try {
                        textAreaMessageRemove.setText("System message will show here");
                        textAreaMessageRemove.setForeground(Color.GRAY);
                        sendAndReadObjectToServer("remove", word, "null");
                    } catch (IOException | ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        btnRemove.setFont(new Font("Georgia", Font.PLAIN, 20));
        btnRemove.setBackground(new Color(255, 102, 51));
        btnRemove.setBounds(485, 50, 125, 50);
        removePane.add(btnRemove);

        JLabel lblWarningRemove = new JLabel("WARNING! Once remove, could not be recovered!");
        lblWarningRemove.setFont(new Font("Georgia", Font.BOLD, 20));
        lblWarningRemove.setIcon(new ImageIcon(DictClient.class.getResource("/javax/swing/plaf/metal/icons/Warn.gif")));
        lblWarningRemove.setBounds(60, 165, 550, 50);
        removePane.add(lblWarningRemove);

        JLabel lblMessageRemove = new JLabel("MESSAGE");
        lblMessageRemove.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblMessageRemove.setBounds(75, 270, 105, 50);
        removePane.add(lblMessageRemove);
    }

    // initialize the client socket using configurable ip and port
    private void clientInitialize(String ipAddress, int portNumber) {
        try {
            socket = new Socket(ipAddress, portNumber);
            inputFromServer = new DataInputStream(socket.getInputStream());
            outputToServer = new DataOutputStream(socket.getOutputStream());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }

    // Set the port and initialize the client, almost same as the one in DictServer Class
    public void setPort(JTextField textFieldIPAddress, JTextField textFieldPort)
            throws InvalidPortNumberException, UnknownHostException {
        String portStr = textFieldPort.getText();
        String ipAddress = textFieldIPAddress.getText();
        if (portStr.equals("") || (portStr.equals("Enter Port"))) {
            portNumber = 2019;
            clientInitialize(ipAddress, portNumber);
        } else {
            if (!DictServer.isNumeric(portStr)) {
                textFieldPort.setText("Enter Port");
                textFieldPort.setForeground(Color.GRAY);
                throw new InvalidPortNumberException();
            } else {
                portNumber = Integer.parseInt(portStr);
                if ((portNumber < 1025) || (portNumber > 65536)) {
                    textFieldPort.setText("Enter Port");
                    textFieldPort.setForeground(Color.GRAY);
                    throw new InvalidPortNumberException();
                } else {
                    clientInitialize(ipAddress, portNumber);
                }
            }
        }
    }

    // Send the JSON object to the server and receive the feedback
    private void sendAndReadObjectToServer(String method, String word, String definition)
            throws IOException, ParseException {
        // Output and Input Stream
        JSONObject jsonWord = new JSONObject();
        jsonWord.put("method_name", method);
        jsonWord.put("word_name", word);
        jsonWord.put("word_definition", definition);

        // Send message to Server
        outputToServer.writeUTF(jsonWord.toJSONString());
        outputToServer.flush();

        // Read the feedback
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(inputFromServer.readUTF());

        // Append to the text area
        appendJSONObject(jsonObject, textAreaDefinitionSearch, textAreaMessageSearch,
                textAreaMessageAdd, textAreaMessageRemove);
    }

    // Append the result from server to the Client UI to show for user at correct place
    private void appendJSONObject(JSONObject jsonObject, JTextArea textAreaDefinitionSearch,
                                  JTextArea textAreaMessageSearch, JTextArea textAreaMessageAdd,
                                  JTextArea textAreaMessageRemove) {
        String method = ((String) jsonObject.get("method_name")).trim().toLowerCase();
        String word = ((String) jsonObject.get("word_name")).trim().toLowerCase();
        String definition = ((String) jsonObject.get("word_definition")).trim().toLowerCase();
        String message = ((String) jsonObject.get("system_message")).trim();

        switch (method) {
            // Search successfully, show the result to user
            case "searchsucc":
                // If it contains multiple definitions, split it and add number in front of each
                if (definition.contains("#")) {
                    textAreaDefinitionSearch.setText("");
                    String[] definitionList = definition.split("#");
                    for (int i = 0; i < definitionList.length; i++) {
                        textAreaDefinitionSearch.append((i + 1) + "." + definitionList[i] + " ");
                    }
                } else {
                    textAreaDefinitionSearch.setText(definition);
                }
                textAreaDefinitionSearch.setForeground(new Color(0, 107, 255));
                textAreaMessageSearch.setText(message);
                textAreaMessageSearch.setForeground(new Color(255, 106, 0));
                break;

            case "searchfail":
                textAreaMessageSearch.setText(message);
                textAreaDefinitionSearch.setText(definition);
                textAreaMessageSearch.setForeground(new Color(255, 106, 0));
                break;

            case "add":
                textAreaMessageAdd.setText(message);
                textAreaMessageAdd.setForeground(new Color(255, 106, 0));
                break;

            case "remove":
                textAreaMessageRemove.setText(message);
                textAreaMessageRemove.setForeground(new Color(255, 106, 0));
                break;
        }
    }
}

