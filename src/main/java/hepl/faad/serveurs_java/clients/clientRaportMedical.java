package hepl.faad.serveurs_java.clients;

import hepl.faad.serveurs_java.library.protocol.MRPS.*;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Report;
import hepl.faad.serveurs_java.model.entity.Specialty;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Vector;
import java.security.*;
import java.security.cert.*;

public class clientRaportMedical extends JFrame {
    private JPanel searchControlPanel;

    private JTextField nomField;
    private JTextField prenomField;
    private JTextField idField;
    private JButton pushButtonLogin;
    private JButton pushButtonLogout;

    private JTextField idPatientField;
    private JButton pushButtonSearch;
    private JTable tableRapportMedical;
    private DefaultTableModel tableModelRapportMedical;

    private JTextArea contentArea;
    private JButton pushButtonModify;

    // Ajout d'une variable pour contenir les données complètes (y compris le contenu)
    private Object[][] tableData;
    Socket socket;
    private Doctor doctorConnecte;
    private Report reportEnCours;
    private ObjectOutputStream oss;
    private ObjectInputStream ois;

    public clientRaportMedical() {
        super("Application de Gestion des Rapports");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel panel1 = createAuthPanel();
        mainPanel.add(panel1);

        JPanel panel2 = createSearchListPanel();
        mainPanel.add(panel2);

        JPanel panel3 = createEditPanel();
        mainPanel.add(panel3);

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setMinimumSize(new Dimension(800, 600));
        setVisible(true);
    }

    private void initComponents(){
        doctorConnecte = new Doctor();
        nomField = new JTextField(10);
        prenomField = new JTextField(10);
        idField = new JTextField(5);

        pushButtonLogin = new JButton("Login");
        pushButtonLogout = new JButton("Logout");

        searchControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPatientField = new JTextField(15);
        pushButtonSearch = new JButton("Rechercher Rapports");

        searchControlPanel.add(new JLabel("ID Patient:"));
        searchControlPanel.add(idPatientField);
        searchControlPanel.add(pushButtonSearch);

        String[] columnNames = {"ID Rapport", "Date", "Patient", "Doctor", "Contenu"};
        tableModelRapportMedical = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableData = new Object[][]{
                {"001", "2023-10-01", "Martin", "Dr. Dupont",
                        "Contenu détaillé du rapport R001. C'est le texte complet du document qui sera affiché dans le champ ci-dessous. Il peut être très long."},
                {"002", "2023-10-15", "Pierre", "Dr. Dupont",
                        "Contenu détaillé du rapport R002. Le patient a montré des améliorations significatives ce mois-ci. Les tests sont positifs."},
                {"003", "2023-11-20", "Julien", "Dr. Smith",
                        "Contenu détaillé du rapport R003. Tout est conforme. Le patient est libéré. Fin du suivi."}
        };
        for (Object[] row : tableData) {
            tableModelRapportMedical.addRow(row);
        }
        tableRapportMedical = new JTable(tableModelRapportMedical);

        tableRapportMedical.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableRapportMedical.setSelectionBackground(Color.YELLOW.brighter());
        tableRapportMedical.setRowSelectionAllowed(true);
        tableRapportMedical.setColumnSelectionAllowed(false);
        tableRapportMedical.getTableHeader().setReorderingAllowed(false);
        tableRapportMedical.getTableHeader().setBackground(Color.decode("#FFFFE0"));

        int[] columnWidths = {40, 150, 200, 150, 100};
        for (int col = 0; col < columnNames.length; col++) {
            tableRapportMedical.getColumnModel().getColumn(col).setPreferredWidth(columnWidths[col]);
        }

        contentArea = new JTextArea(15, 60);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        pushButtonModify = new JButton("Modifier le rapport");

        logoutOk();
        addListeners();
    }

    private void addListeners() {
        pushButtonLogin.addActionListener(this::on_pushButtonLogin_clicked);
        pushButtonLogout.addActionListener(this::on_pushButtonLogout_clicked);
        pushButtonSearch.addActionListener(e -> {});
        pushButtonModify.addActionListener(e -> {});
    }

    private JPanel createAuthPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("1. Connexion"));

        panel.add(new JLabel("Nom:"));
        panel.add(nomField);
        panel.add(new JLabel("Prénom:"));
        panel.add(prenomField);
        panel.add(new JLabel("ID:"));
        panel.add(idField);

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonGroup.add(pushButtonLogin);
        buttonGroup.add(pushButtonLogout);
        panel.add(buttonGroup);

        Dimension preferredSize = panel.getPreferredSize();
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredSize.height));

        return panel;
    }

    private JPanel createSearchListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("2. Recherche de Rapports"));

        panel.add(searchControlPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tableRapportMedical);

        scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 300));

        panel.add(scrollPane, BorderLayout.CENTER);

        tableRapportMedical.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableRapportMedical.getSelectedRow();

                if (selectedRow != -1) {
                    int modelRow = tableRapportMedical.convertRowIndexToModel(selectedRow);

                    String content = (String) tableData[modelRow][4];

                    setRaportContent(content);
                    contentArea.setCaretPosition(0);
                } else {
                    setRaportContent("");
                }
            }
        });

        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("3. Contenu du Rapport et Modification"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(pushButtonModify);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================================================================================
    //                                  MÉTHODES UTILITAIRES
    // ==================================================================================

    /**
     * Ajoute une ligne à la table des consultations.
     */
    public void addTupleTableRapport(int id, String patient, String doctor, String date, String content) {
        Object[] rowData = {id, date, patient, doctor, content};

        tableModelRapportMedical.addRow(rowData);
    }

    public void clearTableRapportMedical() {
        tableModelRapportMedical.setRowCount(0);
    }

    public int getSelectionIndexTableRapportMedical() {
        return tableRapportMedical.getSelectedRow();
    }

    // --- Boîtes de dialogue (JOptionPane) ---
    public void dialogMessage(final String title, final String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void dialogError(final String title, final String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public String getMedecinLastName() {
        return nomField.getText().trim();
    }
    public String getMedecinFirstName() {
        return prenomField.getText().trim();
    }
    public int getMedecinId() {
        return Integer.parseInt(idField.getText().trim());
    }
    public int getPatientIdForSearch() {
        return Integer.parseInt(idPatientField.getText().trim());
    }
    public void setPatientIdForSearch(int id) {
        idPatientField.setText(String.valueOf(id));
    }
    public String getRaportContent() {
        return contentArea.getText().trim();
    }
    public void setRaportContent(String content) {
        contentArea.setText(content);
    }

    public void loginOk() {
        pushButtonLogin.setEnabled(false);
        pushButtonLogout.setEnabled(true);
        pushButtonSearch.setEnabled(true);
        pushButtonModify.setEnabled(true);

        nomField.setEditable(false);
        prenomField.setEditable(false);
        idField.setEditable(false);
    }

    public void logoutOk() {
        pushButtonLogin.setEnabled(true);
        pushButtonLogout.setEnabled(false);
        pushButtonSearch.setEnabled(false);
        pushButtonModify.setEnabled(false);
        //clearTableRapportMedical();
    }
    // ==================================================================================
    //                                  GESTION DES BOUTONS
    // ==================================================================================

    public void on_pushButtonLogin_clicked(java.awt.event.ActionEvent e) {
        String lastName = getMedecinLastName();
        String firstName = getMedecinFirstName();
        int MedecinId = getMedecinId();
        String ipServeur = "127.0.0.1";
        int portServeur = 50060;

        System.out.println("\nRequete : " + RequeteLOGIN.class.getSimpleName());
        System.out.println("lastName = " + lastName);
        System.out.println("FirstName = " + firstName);
        System.out.println("patientId = " + MedecinId);

        try{
            socket = new Socket(ipServeur, portServeur);
            oss = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            RequeteLOGIN requete = new RequeteLOGIN(MedecinId, lastName, firstName, null);
            oss.writeObject(requete);
            ReponseLOGIN reponse = (ReponseLOGIN) ois.readObject();

            System.out.println(Arrays.toString(reponse.getSessionKey()));
            System.out.println(reponse.isSuccess());
            if(reponse.isSuccess())
            {
                dialogMessage("Sucess", "Connection reussie");
                loginOk();
                doctorConnecte.setIdDoctor(MedecinId);
                doctorConnecte.setLastName(lastName);
                doctorConnecte.setFirstName(firstName);
            }
            else
                dialogError("Erreur", "Erreur de connexion");
        }catch (IOException | ClassNotFoundException ex)
        {
            dialogError("Problème de connexion!","Erreur..." + ex.getMessage());
        }
    }

    public void on_pushButtonLogout_clicked(java.awt.event.ActionEvent e) {

        System.out.println("\nRequete : " + RequeteLOGOUT.class.getSimpleName());

        RequeteLOGOUT requete = new RequeteLOGOUT(doctorConnecte);
        System.out.println("Medecin ID = " + doctorConnecte.getIdDoctor());
        System.out.println("lastName = " + doctorConnecte.getLastName());
        System.out.println("firstName = " + doctorConnecte.getFirstName());
        try {
            oss.writeObject(requete);
            oss.close();
            ois.close();
            socket.close();
            logoutOk();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(clientRaportMedical::new);
    }
}