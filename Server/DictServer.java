import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Description
 *
 * @author YUHAO SONG
 * Student_id  981738
 * Date        2019-09-05
 * @version 1.5
 */
public class DictServer {

    public static int clientAmount = 0;
    private String filepath;
    private JFrame frmDistributedDictionaryServer;
    private JTextField textFieldIP;
    private JTextField textFieldPort;
    private JTextArea serverMessagePane;
    private int portNumber = 2019;
    private int clientNo = 0;
    private JLabel lblIP;
    private JLabel lblPort;
    private InetAddress ipAddress;
    private ServerSocket serverSocket;
    private JButton btnPath;
    private String portIndicator = "Enter Port";

    /**
     * Create the application.
     */
    public DictServer() throws UnknownHostException {
        initialize();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DictServer window = new DictServer();
                    window.frmDistributedDictionaryServer.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Check the String input is number or not
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() throws UnknownHostException {
        frmDistributedDictionaryServer = new JFrame();
        frmDistributedDictionaryServer.setTitle("Distributed Dictionary Server @ Yuhao Song");
        frmDistributedDictionaryServer.setBounds(100, 100, 700, 500);
        frmDistributedDictionaryServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmDistributedDictionaryServer.getContentPane().setLayout(null);
        frmDistributedDictionaryServer.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frmDistributedDictionaryServer.setResizable(false);

        // Ask for confirmation when close the WINDOW. Could not turn off when there is client connect to.
        // Save dictionary before exit.
        frmDistributedDictionaryServer.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                int turnOffConfirmNum = JOptionPane.showConfirmDialog(null,
                        "Do you confirm to turn off the server?", "Select an Option...",
                        JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (turnOffConfirmNum == 0) {
                    if (clientAmount != 0) {
                        serverMessagePane.append("Could not turn off the server when clients connect to it!" + '\n');
                        serverMessagePane.append("Connected client amount: " + clientAmount + '\n');
                    } else {
                        closeDict();
                        try {
                            serverSocket.close();
                        } catch (NullPointerException ex) {
                            System.exit(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                    }
                }
            }
        });

        // Set the prompot window font
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Geogira", Font.PLAIN, 16)));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Geogira", Font.PLAIN, 16)));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(40, 100, 600, 270);
        frmDistributedDictionaryServer.getContentPane().add(scrollPane);

        serverMessagePane = new JTextArea();
        serverMessagePane.setFont(new Font("Georgia", Font.PLAIN, 20));
        serverMessagePane.setLineWrap(true);
        serverMessagePane.setEditable(false);
        scrollPane.setViewportView(serverMessagePane);

        JButton btnTurnOff = new JButton("TURN OFF");
        btnTurnOff.setToolTipText("Turn off the server");

        // TURN OFF BUTTON: Confirm to turn off the server (NOT CLOSE WINDOW)
        btnTurnOff.addActionListener(e -> {
            int turnOffConfirmNum = JOptionPane.showConfirmDialog(null,
                    "Do you confirm to turn off the server?", "Select an Option...",
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            try {
                turnOffServer(turnOffConfirmNum, clientAmount);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        btnTurnOff.setBackground(new Color(255, 102, 51));
        btnTurnOff.setFont(new Font("Georgia", Font.PLAIN, 20));
        btnTurnOff.setBounds(380, 390, 160, 50);
        frmDistributedDictionaryServer.getContentPane().add(btnTurnOff);

        // START BUTTON: start the server
        JButton btnDisconnect = new JButton("START");
        btnDisconnect.setToolTipText("Start the server");
        btnDisconnect.addActionListener(e -> {
            try {
                setPort();
                serverMessagePane.setForeground(Color.BLACK);
            } catch (UnknownHostException | InvalidPortNumberException ex) {
                serverMessagePane.append(ex.getMessage());
                serverMessagePane.setForeground(Color.RED);
            }
        });
        btnDisconnect.setBackground(new Color(0, 204, 51));
        btnDisconnect.setFont(new Font("Georgia", Font.PLAIN, 20));
        btnDisconnect.setBounds(540, 30, 100, 50);
        frmDistributedDictionaryServer.getContentPane().add(btnDisconnect);

        ipAddress = InetAddress.getLocalHost();
        textFieldIP = new JTextField(ipAddress.getHostAddress());
        textFieldIP.setFont(new Font("Georgia", Font.PLAIN, 20));
        textFieldIP.setBounds(100, 30, 200, 50);
        textFieldIP.setEditable(false);
        frmDistributedDictionaryServer.getContentPane().add(textFieldIP);
        textFieldIP.setColumns(10);

        textFieldPort = new JTextField();
        textFieldPort.setFont(new Font("Georgia", Font.PLAIN, 20));
        textFieldPort.setBounds(390, 30, 110, 50);
        textFieldPort.setText(portIndicator);
        textFieldPort.setForeground(Color.GRAY);

        // Indicate the user to enter the port number
        textFieldPort.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textFieldPort.getText().trim().equals(portIndicator)) {
                    textFieldPort.setText("");
                    textFieldPort.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textFieldPort.getText().trim().equals("")) {
                    textFieldPort.setText(portIndicator);
                    textFieldPort.setForeground(Color.GRAY);
                }
            }
        });
        frmDistributedDictionaryServer.getContentPane().add(textFieldPort);
        textFieldPort.setColumns(10);

        lblIP = new JLabel("  IP:");
        lblIP.setForeground(new Color(0, 153, 255));
        lblIP.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblIP.setBounds(40, 30, 60, 50);
        frmDistributedDictionaryServer.getContentPane().add(lblIP);

        lblPort = new JLabel("PORT:");
        lblPort.setForeground(new Color(0, 153, 255));
        lblPort.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblPort.setBounds(316, 30, 60, 50);
        frmDistributedDictionaryServer.getContentPane().add(lblPort);

        btnPath = new JButton("PATH");
        btnPath.setToolTipText("Choose the file path");
        btnPath.setBackground(new Color(0, 153, 255));
        btnPath.setFont(new Font("Georgia", Font.PLAIN, 20));
        btnPath.setBounds(130, 390, 100, 50);
        frmDistributedDictionaryServer.getContentPane().add(btnPath);

        // PATH BUTTON: ask the user to choose the file path before server start.
        // Could not start if not select the file path.
        btnPath.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            int returnVal = chooser.showOpenDialog(btnPath);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                filepath = chooser.getSelectedFile().getAbsolutePath();
            }
        });
    }

    // Print out the initial info of the server
    private void printInitialInfo() throws UnknownHostException {
        ipAddress = InetAddress.getLocalHost();
        serverMessagePane.append("Dictionary Server started at " + new Date() + '\n');
        serverMessagePane.append("Current IP Address : " + ipAddress.getHostAddress() + "\n");
        if (portNumber == 2019) {
            serverMessagePane.append("Using Default Port = " + portNumber + '\n');
        } else {
            serverMessagePane.append("Current Port = " + portNumber + "\n");
        }
    }

    /**
     * @param PORT
     * @throws UnknownHostException
     */
    private void serverInitialize(int PORT) throws UnknownHostException {
        if (filepath != null) {
            {
                printInitialInfo();
                new Thread(() -> {
                    try {
                        // Create a server socket
                        serverSocket = new ServerSocket(PORT);
                        Dictionary dictionary = new Dictionary(filepath);
                        while (true) {
                            // Listen for a new connection request
                            Socket clientSocket = serverSocket.accept();

                            clientNo++;
                            clientAmount++;
                            serverMessagePane.append("Client " + clientNo + ": connection accepted!\n");
                            serverMessagePane.append("Remote Hostname: " + clientSocket.getInetAddress().getHostName()
                                    + ". Local Port: " + clientSocket.getLocalPort() + "\n");

                            // Create and start a new thread for the connection
                            new Thread(new ThreadHandler(clientSocket, dictionary)).start();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        } else {
            serverMessagePane.append("Please choose file path first!" + "\n");
        }
    }

    /**
     * Set the port for the server, if not set, use default port - 2019
     * And initialize the server if everything setting up
     *
     * @throws InvalidPortNumberException port number is not in the reasonable range
     * @throws UnknownHostException       host unknown
     */
    private void setPort() throws InvalidPortNumberException, UnknownHostException {
        String portStr = textFieldPort.getText();
        if (portStr.equals("") || (portStr.equals(portIndicator))) {
            portNumber = 2019;
            serverInitialize(portNumber);
        } else {
            if (!isNumeric(portStr)) {
                textFieldPort.setText(portIndicator);
                textFieldPort.setForeground(Color.GRAY);
                throw new InvalidPortNumberException();
            } else {
                portNumber = Integer.parseInt(portStr);
                if ((portNumber < 1025) || (portNumber > 65536)) {
                    textFieldPort.setText(portIndicator);
                    textFieldPort.setForeground(Color.GRAY);
                    throw new InvalidPortNumberException();
                } else {
                    serverInitialize(portNumber);
                }
            }
        }
    }

    /**
     * Turn off the server when there is no user connect to it
     *
     * @param command      ask if the user confirm to turn off, 0 for yes, 1 for no.
     * @param clientAmount the current number of connected clients
     * @throws IOException
     */
    private void turnOffServer(int command, int clientAmount) throws IOException {
        if (command == 0) {
            if (clientAmount != 0) {
                serverMessagePane.append("Could not turn off the server when clients connect to it!" + '\n');
                serverMessagePane.append("Connected client amount: " + clientAmount + '\n');
            } else if (serverSocket == null) {
                serverMessagePane.append("Server did not start!" + "\n");
                serverMessagePane.setForeground(Color.RED);
            } else {
                try {
                    closeDict();
                    serverSocket.close();
                    serverMessagePane.append("Server turned off successfully!" + "\n");
                    serverMessagePane.setForeground(new Color(0, 96, 255));
                } catch (NullPointerException ignored) {
                }
            }
        }
    }

    // Safely save the dictionary to the file path when there is dictionary input stream
    private void closeDict() {
        Dictionary dictionaryNow = ThreadHandler.dictionary;
        if (!(dictionaryNow == null)) {
            dictionaryNow.saveDict(filepath);
        }
    }
}
