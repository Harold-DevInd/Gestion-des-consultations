package hepl.faad.serveurs_java.client;

import hepl.faad.serveurs_java.library.protocol.CAP.ReponseLOGIN;
import hepl.faad.serveurs_java.library.protocol.CAP.RequeteLOGIN;
import hepl.faad.serveurs_java.library.protocol.CAP.RequeteLOGOUT;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Specialty;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Vector;

public class clientConsultation extends JFrame {

    // --- Composants de l'Interface Utilisateur (Équivalent de ui->...) ---
    private JTextField lineMedecinLastName;
    private JTextField lineMedecinFirstName;
    private JSpinner spinBoxIdMedecin;
    private JTextField lineMedecinSpeciality;
    private JButton pushButtonLogin;
    private JButton pushButtonLogout;

    private JSpinner spinBoxAjoutIdPatient;
    private JTextField lineEditAjoutLastName;
    private JTextField lineEditAjoutFirstName;
    private JButton pushButtonAjouterPatient;

    private JComboBox<String> comboBoxPatients;
    private JFormattedTextField dateEditStartDate;
    private JButton pushButtonRechercher;

    private JTable tableWidgetConsultations;
    private DefaultTableModel tableModelConsultations;
    private JButton pushButtonAjouterConsultation;
    private JButton pushButtonModifierConsultation;
    private JButton pushButtonSupprimerConsultation;

    // --- Format pour les dates ---
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    Socket socket;
    private Doctor doctorConnecte;
    private Specialty doctorSpecialte;
    private ObjectOutputStream oss;
    private ObjectInputStream ois;

    // ==================================================================================
    //                                CONSTRUCTEUR
    // ==================================================================================
    public clientConsultation() {
        super("Client consultation - Medecin");

        // Configuration de la fenêtre
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(10, 10)); // Utilisation d'un BorderLayout pour la structure principale

        // Initialisation et configuration des composants
        initComponents();

        // Assemblage des panneaux
        JPanel topPanel = createTopPanel();
        JPanel centerPanel = createCenterPanel();

        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);

        // Initialisation de la logique
        logoutOk();

        // Exemples d'utilisation (reproduits du code C++)
        addTupleTableConsultations(1, "Bryan", "Probleme de dos", "2025-10-01", "09:00");
        addTupleTableConsultations(2, "Junior", "Nez qui coule", "2025-10-06", "10:15");
        addTupleTableConsultations(3, "Ayden", "Maux de ventre", "2025-10-23", "14:30");

        addComboBoxPatients("Harold");
        addComboBoxPatients("Bryan");

        this.pack();
        this.setLocationRelativeTo(null); // Centrer la fenêtre

        doctorConnecte = new Doctor();
        doctorSpecialte = new Specialty();
        oss = null;
        ois = null;
    }

    private void initComponents() {
        // Zone Connexion/Patient
        lineMedecinLastName = new JTextField(15);
        lineMedecinFirstName = new JTextField(15);
        lineMedecinSpeciality = new JTextField(15);
        spinBoxIdMedecin = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        ((JSpinner.DefaultEditor)spinBoxIdMedecin.getEditor()).getTextField().setEditable(false); // Simuler setReadOnly pour JSpinBox
        pushButtonLogin = new JButton("Login");
        pushButtonLogout = new JButton("Logout");

        // --- Zone Ajout Patient ---
        spinBoxAjoutIdPatient = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        lineEditAjoutLastName = new JTextField(15);
        lineEditAjoutFirstName = new JTextField(15);
        pushButtonAjouterPatient = new JButton("Ajouter");

        // Zone Recherche/Filtres
        comboBoxPatients = new JComboBox<>();

        // Formatage des champs de date
        dateEditStartDate = new JFormattedTextField(DATE_FORMATTER.toFormat());

        pushButtonRechercher = new JButton("Rechercher");

        // Zone Table
        String[] columns = {"Id", "Patient", "Raison", "Date", "Heure"};
        tableModelConsultations = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // setEditTriggers(QAbstractItemView::NoEditTriggers);
            }
        };
        tableWidgetConsultations = new JTable(tableModelConsultations);

        // Configuration de la table (reproduction des styles Qt)
        tableWidgetConsultations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableWidgetConsultations.setSelectionBackground(Color.YELLOW.brighter());
        tableWidgetConsultations.setRowSelectionAllowed(true); // SelectRows
        tableWidgetConsultations.setColumnSelectionAllowed(false);
        tableWidgetConsultations.getTableHeader().setReorderingAllowed(false);
        tableWidgetConsultations.getTableHeader().setBackground(Color.decode("#FFFFE0")); // lightyellow

        // Définition des largeurs de colonne
        int[] columnWidths = {40, 150, 200, 150, 100};
        for (int col = 0; col < columns.length; col++) {
            tableWidgetConsultations.getColumnModel().getColumn(col).setPreferredWidth(columnWidths[col]);
        }

        pushButtonAjouterConsultation = new JButton("Ajouter");
        pushButtonModifierConsultation = new JButton("Modifier");
        pushButtonSupprimerConsultation = new JButton("Supprimer");

        // Ajout des Listeners
        addListeners();
    }

    private void addListeners() {
        pushButtonLogin.addActionListener(this::on_pushButtonLogin_clicked);
        pushButtonLogout.addActionListener(this::on_pushButtonLogout_clicked);
        pushButtonAjouterPatient.addActionListener(this::on_pushButtonAjouter_clicked);
        pushButtonRechercher.addActionListener(this::on_pushButtonRechercher_clicked);
        pushButtonAjouterConsultation.addActionListener(this::on_pushButtonAjouterConsultation_clicked);
        pushButtonModifierConsultation.addActionListener(this::on_pushButtonModifierConsultation_clicked);
        pushButtonSupprimerConsultation.addActionListener(this::on_pushButtonSupprimerConsultation_clicked);
    }

    // ==================================================================================
    //                             ASSEMBLAGE DES PANNEAUX
    // ==================================================================================

    private JPanel createTopPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel CONNEXION/PATIENT (existante)
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder("Medecin / Connexion"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Lignes et composants existants pour la connexion...
        gbc.gridx = 0; gbc.gridy = 0; loginPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; loginPanel.add(lineMedecinLastName, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; loginPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; loginPanel.add(lineMedecinFirstName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; loginPanel.add(new JLabel("ID Medecin:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; loginPanel.add(spinBoxIdMedecin, gbc);
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0; loginPanel.add(new JLabel("Specialite:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1.0; loginPanel.add(lineMedecinSpeciality, gbc);
        lineMedecinSpeciality.setEnabled(false);

        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 0;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(pushButtonLogin);
        btnPanel.add(pushButtonLogout);
        loginPanel.add(btnPanel, gbc);

        // ***************************************************************
        // NOUVEAU : Panel AJOUT PATIENT
        // ***************************************************************
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder("Ajout Patient"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; addPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; addPanel.add(spinBoxAjoutIdPatient, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; addPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; addPanel.add(lineEditAjoutLastName, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0; addPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 5; gbc.gridy = 0; gbc.weightx = 1.0; addPanel.add(lineEditAjoutFirstName, gbc);

        gbc.gridx = 6; gbc.gridy = 0; gbc.weightx = 0; addPanel.add(pushButtonAjouterPatient, gbc);

        // Organisation du TOP Panel
        JPanel northContainer = new JPanel(new GridLayout(1, 2, 10, 0)); // Grille 1 ligne, 2 colonnes
        northContainer.add(loginPanel);
        northContainer.add(addPanel);

        main.add(northContainer, BorderLayout.NORTH); // Le container est au Nord

        // Panel Recherche/Filtres
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche / Filtres"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; searchPanel.add(new JLabel("Patients:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; searchPanel.add(comboBoxPatients, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; searchPanel.add(new JLabel("Date Début:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; searchPanel.add(dateEditStartDate, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0; searchPanel.add(pushButtonRechercher, gbc);

        main.add(searchPanel, BorderLayout.SOUTH);

        return main;
    }

    private JPanel createCenterPanel() {
        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // JScrollPane pour la JTable
        JScrollPane scrollPane = new JScrollPane(tableWidgetConsultations);
        main.add(scrollPane, BorderLayout.CENTER);

        // Bouton Réserver
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(pushButtonAjouterConsultation);
        bottomPanel.add(pushButtonModifierConsultation);
        bottomPanel.add(pushButtonSupprimerConsultation);
        main.add(bottomPanel, BorderLayout.SOUTH);

        return main;
    }

    // ==================================================================================
    //                                  MÉTHODES UTILITAIRES
    // ==================================================================================

    /**
     * Ajoute une ligne à la table des consultations.
     */
    public void addTupleTableConsultations(int id, String specialty, String doctor, String date, String hour) {
        // Utilisation de Vector pour simuler l'ajout de ligne
        Vector<Object> rowData = new Vector<>();
        rowData.add(id);
        rowData.add(specialty);
        rowData.add(doctor);
        rowData.add(date);
        rowData.add(hour);

        // Ajout de la ligne au modèle
        tableModelConsultations.addRow(rowData);

        // Configuration de l'alignement pour les données (fait manuellement pour chaque cellule en Qt)
        // En Swing, on configure des Renderers pour les colonnes, mais pour la simplicité, on utilise l'alignement par défaut du modèle.
    }

    public void clearTableConsultations() {
        tableModelConsultations.setRowCount(0);
    }

    public int getSelectionIndexTableConsultations() {
        // Retourne l'index de la première ligne sélectionnée ou -1 si aucune
        return tableWidgetConsultations.getSelectedRow();
    }

    // --- Comboboxes ---

    public void addComboBoxPatients(String patient) {
        comboBoxPatients.addItem(patient);
    }

    public String getSelectionPatient() {
        return (String) comboBoxPatients.getSelectedItem();
    }

    public void clearComboBoxPatients() {
        comboBoxPatients.removeAllItems();
        addComboBoxPatients("--- TOUTES ---");
    }

    // --- Fenêtre ---
    public String getMedecinLastName() {
        return lineMedecinLastName.getText();
    }

    public String getMedecinFirstName() {
        return lineMedecinFirstName.getText();
    }

    public String getMedecinSpeciality(){
        return lineMedecinSpeciality.getText();
    }

    public int getMedecinId() {
        return (int) spinBoxIdMedecin.getValue();
    }

    public String getPatientSelected(){
        return (String) comboBoxPatients.getSelectedItem();
    }

    public String getStartDate() {
        return dateEditStartDate.getText();
    }

    public void setMedecinLastName(String value) {
        lineMedecinLastName.setText(value);
    }

    public void setMedecinFirstName(String value) {
        lineMedecinFirstName.setText(value);
    }

    public void setMedecinSpecialty(String value) {
        lineMedecinSpeciality.setText(value);
    }

    public void setPatientId(int value) {
        if (value > 0) spinBoxIdMedecin.setValue(value);
    }

    public void setStartDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
            dateEditStartDate.setText(date);
        } catch (Exception e) {
            // Ignorer si la date n'est pas valide
        }
    }


    // --- Gestion de l'état de la fenêtre ---

    public void loginOk() {
        lineMedecinLastName.setEditable(false);
        lineMedecinFirstName.setEditable(false);
        lineMedecinSpeciality.setEditable(false);
        spinBoxIdMedecin.setEnabled(false); // Simuler spinBoxId->setReadOnly(true)
        pushButtonLogout.setEnabled(true);
        pushButtonLogin.setEnabled(false);
        pushButtonAjouterPatient.setEnabled(true);
        pushButtonRechercher.setEnabled(true);
        pushButtonSupprimerConsultation.setEnabled(true);
        pushButtonModifierConsultation.setEnabled(true);
        pushButtonAjouterConsultation.setEnabled(true);
    }

    public void logoutOk() {
        lineMedecinLastName.setEditable(true);
        setMedecinLastName("");
        lineMedecinFirstName.setEditable(true);
        setMedecinFirstName("");
        spinBoxIdMedecin.setEnabled(true);
        setPatientId(1);
        lineMedecinSpeciality.setEditable(true);
        setMedecinSpecialty("");
        pushButtonLogout.setEnabled(false);
        pushButtonLogin.setEnabled(true);
        pushButtonAjouterPatient.setEnabled(false);
        pushButtonRechercher.setEnabled(false);
        pushButtonSupprimerConsultation.setEnabled(false);
        pushButtonModifierConsultation.setEnabled(false);
        pushButtonAjouterConsultation.setEnabled(false);
        setStartDate("2025-09-15");
        clearComboBoxPatients();
        clearTableConsultations();
    }

    // --- Boîtes de dialogue (JOptionPane) ---

    public void dialogMessage(final String title, final String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void dialogError(final String title, final String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public String dialogInputText(final String title, final String question) {
        // JOptionPane.showInputDialog retourne la saisie de l'utilisateur ou null si Annuler est pressé.
        return JOptionPane.showInputDialog(this, question, title, JOptionPane.QUESTION_MESSAGE);
    }

    // ==================================================================================
    //                                  GESTION DES BOUTONS
    // ==================================================================================

    public void on_pushButtonLogin_clicked(java.awt.event.ActionEvent e) {
        String lastName = getMedecinLastName();
        String firstName = getMedecinFirstName();
        int MedecinId = getMedecinId();
        String ipServeur = "127.0.0.1";
        int portServeur = 50050;

        System.out.println("lastName = " + lastName);
        System.out.println("FirstName = " + firstName);
        System.out.println("patientId = " + MedecinId);

        try{
            socket = new Socket(ipServeur, portServeur);
            oss = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            RequeteLOGIN requete = new RequeteLOGIN(MedecinId, lastName, firstName);
            oss.writeObject(requete);
            ReponseLOGIN reponse = (ReponseLOGIN) ois.readObject();

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
        RequeteLOGOUT requete = new RequeteLOGOUT(doctorConnecte);
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

    public void on_pushButtonAjouter_clicked(java.awt.event.ActionEvent e) {
        int selectedRow = getSelectionIndexTableConsultations();

        System.out.println("selectedRow = " + selectedRow);

        // Logique de réservation à ajouter ici
    }

    public void on_pushButtonRechercher_clicked(java.awt.event.ActionEvent e) {
        String patient = getPatientSelected();
        String startDate = getStartDate();

        System.out.println("Patient = " + patient);
        System.out.println("startDate = " + startDate);

        // Logique de recherche à ajouter ici
    }

    public void on_pushButtonAjouterConsultation_clicked(java.awt.event.ActionEvent e) {
        int selectedRow = getSelectionIndexTableConsultations();

        System.out.println("selectedRow = " + selectedRow);

        // Logique de réservation à ajouter ici
    }

    public void on_pushButtonModifierConsultation_clicked(java.awt.event.ActionEvent e) {
        int selectedRow = this.getSelectionIndexTableConsultations();

        if (selectedRow == -1) {
            dialogError("Réservation", "Veuillez sélectionner une consultation dans la liste.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tableWidgetConsultations.getModel();

        // Récupère les colonnes (Id, Patient, Raison, Date, Heure)
        Object[] rowData = new Object[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            rowData[i] = model.getValueAt(selectedRow, i);
        }

        // Informations du médecin connecté (pour le champ "Patient" du dialogue)
        String doctorInfo = "ID: " + getMedecinId() + " - " + getMedecinLastName() + " " + getMedecinFirstName();

        // 2. Ouvrir le dialogue de réservation
        ReservationDialog dialog = new ReservationDialog(this, rowData);
        dialog.setVisible(true); // Bloque jusqu'à ce que le dialogue soit fermé (modal)

        // 3. Traiter le résultat
        if (dialog.isConfirmed()) {
            String id = dialog.getConsultationId();
            String patient = dialog.getPatient();
            String raison = dialog.getRaison();
            String date = dialog.getDate();
            String heure = dialog.getHeure();

            // Affichage des données (à remplacer par l'envoi au serveur)
            System.out.println("--- Réservation Confirmée ---");
            System.out.println("ID Consultation: " + id);
            System.out.println("Patient" + patient);
            System.out.println("Médecin: " + doctorInfo);
            System.out.println("Raison: " + raison);
            System.out.println("Nouvelle Date/Heure: " + date + " à " + heure);

            dialogMessage("Confirmation de Réservation",
                    "Réservation de la consultation ID " + id + " confirmée pour le " + date + " à " + heure + ".");

            // TODO: Mettre ici le code pour envoyer la requête de réservation au serveur (ex: 'BOOK_CONSULTATION#id#date#heure#raison')

        } else {
            dialogMessage("Modification", "Modification annulée.");
        }
    }

    public void on_pushButtonSupprimerConsultation_clicked(java.awt.event.ActionEvent e) {
        int selectedRow = getSelectionIndexTableConsultations();

        System.out.println("selectedRow = " + selectedRow);

        // Logique de réservation à ajouter ici
    }

    // ==================================================================================
    //                                  POINT D'ENTRÉE (main)
    // ==================================================================================

    public static void main(String[] args) {
        // Lance l'interface graphique sur l'Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new clientConsultation().setVisible(true);
        });
    }
}