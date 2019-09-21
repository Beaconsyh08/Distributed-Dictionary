import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.UnknownHostException;

/**
 * Description
 *
 * @author YUHAO SONG
 * Student_id  981738
 * Date        2019-09-05
 * @version 1.5
 */
public class WelcomeClient {
    private JFrame frmWelcomePage;
    private JTextField textFieldPort;
    private JLabel lblNewLabel;
    private JScrollPane scrollPane;
    private JTextArea txtSystemMessage;
    private JTextField textFieldIPAddress;

    /**
     * Create the application.
     */
    public WelcomeClient() {
        initialize();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                WelcomeClient window = new WelcomeClient();
                window.frmWelcomePage.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmWelcomePage = new JFrame();
        frmWelcomePage.setTitle("Distributed Dictionary Client @ Yuhao Song");
        frmWelcomePage.getContentPane().setFont(new Font("Georgia", Font.PLAIN, 20));
        frmWelcomePage.setBounds(100, 100, 700, 500);
        frmWelcomePage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmWelcomePage.getContentPane().setLayout(null);
        frmWelcomePage.setResizable(false);

        JLabel lblPort = new JLabel("PORT:");
        lblPort.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblPort.setBounds(152, 330, 60, 50);
        frmWelcomePage.getContentPane().add(lblPort);

        textFieldPort = new JTextField();
        textFieldPort.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldPort.setFont(new Font("Georgia", Font.PLAIN, 20));
        textFieldPort.setBounds(240, 330, 150, 50);
        textFieldPort.setText("Enter Port");
        textFieldPort.setForeground(Color.GRAY);
        textFieldPort.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textFieldPort.getText().trim().equals("Enter Port")) {
                    textFieldPort.setText("");
                    textFieldPort.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textFieldPort.getText().trim().equals("")) {
                    textFieldPort.setText("Enter Port");
                    textFieldPort.setForeground(Color.GRAY);
                }
            }
        });
        frmWelcomePage.getContentPane().add(textFieldPort);
        textFieldPort.setColumns(10);

        textFieldIPAddress = new JTextField();
        textFieldIPAddress.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldIPAddress.setFont(new Font("Georgia", Font.PLAIN, 20));
        textFieldIPAddress.setText("127.0.0.1");
        textFieldIPAddress.setBounds(238, 260, 330, 50);
        frmWelcomePage.getContentPane().add(textFieldIPAddress);
        textFieldIPAddress.setColumns(10);

        // CONNECT BUTTON: connect to the server and go to next window if connected
        JButton btnConnect = new JButton("CONNECT");
        btnConnect.setToolTipText("Connect to the server");
        btnConnect.addActionListener(e -> {
            DictClient dictClient = new DictClient();
            try {
                dictClient.setPort(textFieldIPAddress, textFieldPort);
            } catch (InvalidPortNumberException | UnknownHostException ex) {
                txtSystemMessage.setText(ex.getMessage());
                txtSystemMessage.setForeground(Color.RED);
            }
            try {
                if (dictClient.socket.isConnected()) {
                    dictClient.frmDistributedDictionaryClient.setVisible(true);
                    frmWelcomePage.setVisible(false);
                }
            } catch (NullPointerException ex) {
                txtSystemMessage.setText("Make sure server is on!" + "\n" + "Make sure correct port and IP Address are entered! ");
                txtSystemMessage.setForeground(Color.RED);
            }
        });
        btnConnect.setBackground(new Color(51, 255, 0));
        btnConnect.setFont(new Font("Georgia", Font.PLAIN, 20));
        btnConnect.setBounds(420, 330, 150, 50);
        frmWelcomePage.getContentPane().add(btnConnect);

        lblNewLabel = new JLabel("Welcome to the Distributed Ditionary");
        lblNewLabel.setForeground(new Color(0, 0, 102));
        lblNewLabel.setFont(new Font("Bahnschrift", Font.PLAIN, 28));
        lblNewLabel.setBounds(100, 30, 469, 85);
        frmWelcomePage.getContentPane().add(lblNewLabel);

        scrollPane = new JScrollPane();
        scrollPane.setBounds(70, 130, 535, 96);
        frmWelcomePage.getContentPane().add(scrollPane);

        txtSystemMessage = new JTextArea();
        txtSystemMessage.setEditable(false);
        txtSystemMessage.setForeground(new Color(255, 94, 33));
        txtSystemMessage.setFont(new Font("Georgia", Font.PLAIN, 20));
        txtSystemMessage.setText("If Port Number is not entered." + "\n" + "The default Port Number: 2019 will be used."
                + "\n" + "If IP Address is not entered." + "\n" + "The default IP Address: 127.0.0.1 will be used.");
        scrollPane.setViewportView(txtSystemMessage);

        JLabel lblIPAdress = new JLabel("IP ADDRESS:");
        lblIPAdress.setFont(new Font("Georgia", Font.PLAIN, 20));
        lblIPAdress.setBounds(90, 260, 130, 50);
        frmWelcomePage.getContentPane().add(lblIPAdress);

        JLabel lblCopyRight = new JLabel("\u00A9 2019 Yuhao Song All Rights Reserved");
        lblCopyRight.setForeground(Color.GRAY);
        lblCopyRight.setFont(new Font("Georgia", Font.ITALIC, 12));
        lblCopyRight.setBounds(221, 421, 229, 30);
        frmWelcomePage.getContentPane().add(lblCopyRight);
    }
}

