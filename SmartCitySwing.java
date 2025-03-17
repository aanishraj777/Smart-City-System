import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.net.URL;

public class SmartCitySwing extends JFrame {

    private Map<String, String> users = new HashMap<>();
    private String currentUser = null;
    private JTextArea displayArea;
    private JComboBox<String> categoryComboBox;
    private JTextField complaintTextField;
    private JTextField paymentTextField;
    private JLabel imageLabel;
    private JPanel imagePanel;

    private Map<String, java.util.List<String>> facilities = new HashMap<>();
    private java.util.List<String> touristPlaces = new ArrayList<>();
    private Map<String, String> complaints = new HashMap<>();
    private Map<String, Double> bills = new HashMap<>();

    private static final String USERS_FILE = "users.txt";
    private static final String FACILITIES_FILE = "facilities.txt";
    private static final String TOURIST_FILE = "tourist.txt";
    private static final String COMPLAINTS_FILE = "complaints.txt";
    private static final String BILLS_FILE = "bills.txt";

    private JPanel cardPanel;
    private CardLayout cardLayout;

    public SmartCitySwing() {
        setTitle("Smart City Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create CardLayout Panel
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Control Panel with Buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        controlPanel.setBackground(new Color(245, 245, 245));

        JButton homeButton = createStyledButton("Home", new Color(52, 152, 219));
        JButton registerButton = createStyledButton("Register", new Color(46, 204, 113));
        JButton loginButton = createStyledButton("Login", new Color(155, 89, 182));
        JButton facilitiesButton = createStyledButton("Facilities", new Color(241, 196, 15));
        JButton touristButton = createStyledButton("Tourist", new Color(231, 76, 60));
        JButton complaintButton = createStyledButton("Complaint", new Color(52, 73, 94));
        JButton paymentButton = createStyledButton("Payment", new Color(149, 165, 166));
        JButton logoutButton = createStyledButton("Logout", new Color(52, 152, 219));

        controlPanel.add(homeButton);
        controlPanel.add(registerButton);
        controlPanel.add(loginButton);
        controlPanel.add(facilitiesButton);
        controlPanel.add(touristButton);
        controlPanel.add(complaintButton);
        controlPanel.add(paymentButton);
        controlPanel.add(logoutButton);

        // Display Area for Text
        displayArea = new JTextArea();
        displayArea.setFont(new Font("SansSerif", Font.PLAIN, 20));
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Image Panel for Displaying Images
        imagePanel = new JPanel();
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagePanel.setBackground(new Color(240, 240, 240));
        imageLabel = new JLabel();
        imagePanel.add(imageLabel);

        // Add Panels to CardLayout
        cardPanel.add(createHomePanel(), "Home");
        cardPanel.add(scrollPane, "TextPanel");
        cardPanel.add(imagePanel, "ImagePanel");

        // Add Components to Frame
        add(controlPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        // Add Action Listeners
        homeButton.addActionListener(e -> cardLayout.show(cardPanel, "Home"));
        registerButton.addActionListener(e -> registerUser());
        loginButton.addActionListener(e -> loginUser());
        facilitiesButton.addActionListener(e -> {
            showFacilities();
            cardLayout.show(cardPanel, "TextPanel"); // Switch to the text panel
        });
        touristButton.addActionListener(e -> {
            showTouristPlaces();
            cardLayout.show(cardPanel, "TextPanel"); // Switch to the text panel
        });
        complaintButton.addActionListener(e -> fileComplaint());
        paymentButton.addActionListener(e -> makePayment());
        logoutButton.addActionListener(e -> logout());

        // Load Data
        loadData();

        setVisible(true);
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new GridLayout(2, 3, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load the background image
                ImageIcon icon = new ImageIcon("images/bkgd.jpg"); 
                Image image = icon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        homePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Image Paths
        String[] imagePaths = {
            "images/smartcity1.jpg",
            "images/futuristic.jpg",
            "images/OIP.jpeg",
            "images/th.jpeg",
            "images/Images-Artha15.jpg",
            "images/Indiansmartcity.jpg"
        };

        // Get the Screen Size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int imageWidth = (int) (screenSize.getWidth() / 3) - 20; // Divide screen width by 3 (for 3 columns)
        int imageHeight = (int) (screenSize.getHeight() / 2) - 20; // Divide screen height by 2 (for 2 rows)

        // Load and Add Images to Home Panel
        for (String path : imagePaths) {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image image = icon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                JLabel label = new JLabel(icon);
                homePanel.add(label);
            } else {
                JLabel label = new JLabel("Image not found");
                label.setHorizontalAlignment(JLabel.CENTER);
                homePanel.add(label);
            }
        }

        return homePanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        return button;
    }

    private void registerUser() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            users.put(usernameField.getText(), new String(passwordField.getPassword()));
            saveUsers();
            displayArea.append("User registered successfully.\n");
        }
    }

    private void loginUser() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (users.containsKey(usernameField.getText()) && users.get(usernameField.getText()).equals(new String(passwordField.getPassword()))) {
                currentUser = usernameField.getText();
                displayArea.append("Login successful. Welcome, " + currentUser + "!\n");
            } else {
                displayArea.append("Invalid username or password.\n");
            }
        }
    }

    private void showFacilities() {
        if (currentUser == null) {
            displayArea.append("Please log in first.\n");
            return;
        }
        if (categoryComboBox == null) {
            String[] categories = {"Hospitals", "ATMs", "Schools", "Police Stations", "Fire Stations"};
            categoryComboBox = new JComboBox<>(categories);
            JPanel panel = new JPanel(new FlowLayout());
            panel.add(new JLabel("Select Category:"));
            panel.add(categoryComboBox);
            int result = JOptionPane.showConfirmDialog(this, panel, "Select Category", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }
        }
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        java.util.List<String> facilityList = facilities.get(selectedCategory);
        if (facilityList != null && !facilityList.isEmpty()) {
            displayArea.append("Facilities in " + selectedCategory + ":\n");
            for (String facility : facilityList) {
                String[] parts = facility.split(":");
                if (parts.length == 2) {
                    displayArea.append("- " + parts[0] + "\n");
                    displayImage(parts[1]);
                } else {
                    displayArea.append("- " + facility + "\n");
                }
            }
        } else {
            displayArea.append("No facilities found in " + selectedCategory + ".\n");
        }
    }

    private void showTouristPlaces() {
        if (currentUser == null) {
            displayArea.append("Please log in first.\n");
            return;
        }
        displayArea.append("Tourist Places:\n");
        for (String place : touristPlaces) {
            String[] parts = place.split(":");
            if (parts.length >= 2) {
                displayArea.append("- " + parts[0] + ": " + parts[1] + "\n");
            } else {
                displayArea.append("- " + place + "\n");
            }
        }
    }

    private void fileComplaint() {
        if (currentUser == null) {
            displayArea.append("Please log in first.\n");
            return;
        }
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        complaintTextField = new JTextField(20);
        panel.add(new JLabel("Enter Complaint:"));
        panel.add(complaintTextField);

        int result = JOptionPane.showConfirmDialog(this, panel, "File Complaint", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            complaints.put(currentUser, complaintTextField.getText());
            saveComplaints();
            displayArea.append("Complaint filed successfully.\n");
        }
    }

    private void makePayment() {
        if (currentUser == null) {
            displayArea.append("Please log in first.\n");
            return;
        }
        if (!bills.containsKey(currentUser)) {
            displayArea.append("No bill found for " + currentUser + "\n");
            return;
        }
        double billAmount = bills.get(currentUser);

        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        paymentTextField = new JTextField(10);
        panel.add(new JLabel("Enter Payment Amount (Bill: $" + billAmount + "):"));
        panel.add(paymentTextField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Make Payment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double payment = Double.parseDouble(paymentTextField.getText());
                if (payment >= billAmount) {
                    bills.remove(currentUser);
                    saveBills();
                    displayArea.append("Payment successful.\n");
                } else {
                    displayArea.append("Payment amount is less than the bill.\n");
                }
            } catch (NumberFormatException e) {
                displayArea.append("Invalid payment amount.\n");
            }
        }
    }

    private void logout() {
        currentUser = null;
        displayArea.append("Logged out.\n");
    }

    private void loadData() {
        loadUsers();
        loadFacilities();
        loadTouristPlaces();
        loadComplaints();
        loadBills();
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {}
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
        } catch (IOException e) {}
    }

    private void loadFacilities() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FACILITIES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String category = parts[0];
                    java.util.List<String> facilityList = new ArrayList<>();
                    String[] facilitiesData = parts[1].split(";");
                    for (String facilityData : facilitiesData) {
                        facilityList.add(facilityData);
                    }
                    facilities.put(category, facilityList);
                }
            }
        } catch (IOException e) {}
    }

    private void saveFacilities() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FACILITIES_FILE))) {
            for (Map.Entry<String, java.util.List<String>> entry : facilities.entrySet()) {
                writer.write(entry.getKey() + ":");
                java.util.List<String> facilityList = entry.getValue();
                for (int i = 0; i < facilityList.size(); i++) {
                    writer.write(facilityList.get(i));
                    if (i < facilityList.size() - 1) {
                        writer.write(";");
                    }
                }
                writer.write("\n");
            }
        } catch (IOException e) {}
    }

    private void loadTouristPlaces() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TOURIST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                touristPlaces.add(line);
            }
        } catch (IOException e) {}
    }

    private void saveTouristPlaces() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TOURIST_FILE))) {
            for (String place : touristPlaces) {
                writer.write(place + "\n");
            }
        } catch (IOException e) {}
    }

    private void loadComplaints() {
        try (BufferedReader reader = new BufferedReader(new FileReader(COMPLAINTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    complaints.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {}
    }

    private void saveComplaints() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COMPLAINTS_FILE))) {
            for (Map.Entry<String, String> entry : complaints.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
        } catch (IOException e) {}
    }

    private void loadBills() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BILLS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        bills.put(parts[0], Double.parseDouble(parts[1]));
                    } catch (NumberFormatException e) {}
                }
            }
        } catch (IOException e) {}
    }

    private void saveBills() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BILLS_FILE))) {
            for (Map.Entry<String, Double> entry : bills.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
        } catch (IOException e) {}
    }

    private void displayImage(String imageURL) {
        try {
            System.out.println("Trying to load image from URL: " + imageURL);

            java.net.URL url = new URL(imageURL);
            ImageIcon icon = new ImageIcon(url);
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image image = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                imageLabel.setIcon(icon);
            } else {
                imageLabel.setIcon(null);
                displayArea.append("Error loading image from URL: " + imageURL + "\n");
            }
            imagePanel.repaint();
        } catch (Exception e) {
            System.out.println("Exception loading image from URL: " + imageURL);
            e.printStackTrace();
            imageLabel.setIcon(null);
            displayArea.append("Error loading image from URL: " + imageURL + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartCitySwing::new);
    }
}