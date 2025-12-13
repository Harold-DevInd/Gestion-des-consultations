package hepl.faad.serveurs_java.clients;

import hepl.faad.serveurs_java.library.CryptoManagement;
import hepl.faad.serveurs_java.library.protocol.MRPS.*;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Report;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.*;
import java.time.LocalDate;
import java.util.ArrayList;

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
    private JButton pushButtonAdd;
    private JButton pushButtonModify;

    // Ajout d'une variable pour contenir les données complètes (y compris le contenu)
    private Object[][] tableData;
    Socket socket;
    private Doctor doctorConnecte;
    private Report reportEnCours;
    private ObjectOutputStream oss;
    private ObjectInputStream ois;
    SecretKey sessionKey;
    long salt;

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

        pushButtonAdd = new JButton("Ajouter un rapport");
        pushButtonModify = new JButton("Modifier le rapport");

        logoutOk();
        addListeners();
        Security.addProvider(new BouncyCastleProvider());
    }

    private void addListeners() {
        pushButtonLogin.addActionListener(this::on_pushButtonLogin_clicked);
        pushButtonLogout.addActionListener(this::on_pushButtonLogout_clicked);
        pushButtonSearch.addActionListener(this::on_pushRechercheRapport_clicked);
        pushButtonAdd.addActionListener(this::on_pushButtonAddRapport_clicked);
        pushButtonModify.addActionListener(this::on_pushButtonModifyRapport_clicked);
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
        buttonPanel.add(pushButtonAdd);
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
    public Integer getPatientIdForSearch() {
        if(idPatientField.getText().trim().isEmpty()) {
            return -1;
        }
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
        pushButtonAdd.setEnabled(true);
        pushButtonModify.setEnabled(true);

        nomField.setEditable(false);
        prenomField.setEditable(false);
        idField.setEditable(false);
    }

    public void logoutOk() {
        pushButtonLogin.setEnabled(true);
        pushButtonLogout.setEnabled(false);
        pushButtonSearch.setEnabled(false);
        pushButtonAdd.setEnabled(false);
        pushButtonModify.setEnabled(false);
        nomField.setEditable(true);
        prenomField.setEditable(true);
        idField.setEditable(true);
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
        long sel;
        ReponseLOGIN reponse, reponse2;

        System.out.println("\nRequete : " + RequeteLOGIN.class.getSimpleName());
        System.out.println("lastName = " + lastName);
        System.out.println("FirstName = " + firstName);
        System.out.println("patientId = " + MedecinId);

        try{
            socket = new Socket(ipServeur, portServeur);
            oss = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            RequeteLOGIN requete = new RequeteLOGIN(MedecinId, 0, null);
            oss.writeObject(requete);
            reponse = (ReponseLOGIN) ois.readObject();


            /*System.out.println("\nEtape 1 : Reponse : " + reponse.isSuccess()+ " , sel = " + reponse.getSel()+
                    ", sessionKey = " + Arrays.toString(reponse.getSessionKey()));*/
            if(reponse.isSuccess())
            {
                sel = reponse.getSel();
                byte[] digestClient;
                MessageDigest md = MessageDigest.getInstance("SHA-1","BC");
                md.update(lastName.getBytes());
                md.update(firstName.getBytes());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.write(MedecinId);
                dos.writeLong(sel);
                md.update(baos.toByteArray());
                digestClient = md.digest();

                RequeteLOGIN requete2 = new RequeteLOGIN(MedecinId, sel, digestClient);
                oss.writeObject(requete2);
                reponse2 = (ReponseLOGIN) ois.readObject();

                /*System.out.println("\nEtape 2 : Reponse : " + reponse2.isSuccess()+ " , sel = " + reponse2.getSel()+
                        ", sessionKey = " + Arrays.toString(reponse2.getSessionKey()));*/
                System.out.println(reponse.isSuccess());
                if(reponse.isSuccess())
                {
                    dialogMessage("Sucess", "Connection reussie");
                    loginOk();
                    doctorConnecte.setIdDoctor(MedecinId);
                    doctorConnecte.setLastName(lastName);
                    doctorConnecte.setFirstName(firstName);

                    byte[] sessionKeyByte = CryptoManagement.DecryptAsymRSA(RecupereClePrivee(), reponse2.getSessionKey());
                    sessionKey = new javax.crypto.spec.SecretKeySpec(sessionKeyByte, "DES");
                    salt = reponse2.getSel();
                }
                else
                    dialogError("Erreur", "Erreur de connexion");
            }
            else
                dialogError("Erreur", "Erreur de connexion");


        }catch (IOException | ClassNotFoundException ex)
        {
            dialogError("Problème de connexion!","Erreur..." + ex.getMessage());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | IllegalBlockSizeException | BadPaddingException |
                 KeyStoreException | InvalidKeyException | CertificateException | UnrecoverableKeyException ex) {
            throw new RuntimeException(ex);
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

    public void on_pushButtonAddRapport_clicked(java.awt.event.ActionEvent e) {

        AjoutRapport dialog = new AjoutRapport(this, null, 1);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            String result = "non";
            int patientId = Integer.parseInt(dialog.getPatientId());
            String date = dialog.getDate();
            String raison = dialog.getRaison();

            System.out.println("\n--- Creation d un rapport ---");
            System.out.println("Doctor ID = " + doctorConnecte.getIdDoctor());
            System.out.println("Patient ID = " + patientId);
            System.out.println("Date: " + date );
            System.out.println("Raison: " + raison);

            Report newReport = new Report();
            newReport.setDoctorId(doctorConnecte.getIdDoctor());
            newReport.setPatientId(patientId);
            newReport.setDateReport(LocalDate.parse(date));
            newReport.setContent(raison);

            byte[] reportBytes = Report.convertReportToByte(newReport);
            try {
                byte[] encryptedReportBytes = CryptoManagement.CryptSymDES(sessionKey, reportBytes);

                RequeteADDREPORT requete = new RequeteADDREPORT(encryptedReportBytes,
                        CryptoManagement.SignData(RecupereClePrivee(), encryptedReportBytes));
                oss.writeObject(requete);

                ReponseADDREPORT reponse = (ReponseADDREPORT) ois.readObject();
                byte[] decryptedResponseBytes = CryptoManagement.DecryptSymDES(sessionKey, reponse.getMessage());

                String decryptedResponse = new String(decryptedResponseBytes);
                if (decryptedResponse.equals("oui")) {
                    dialogMessage("Succès", "Le rapport a été ajouté avec succès.");
                } else {
                    dialogError("Erreur", "Échec de l'ajout du rapport.");
                }
            } catch (IOException | ClassNotFoundException | CertificateException | SignatureException |
                     KeyStoreException | UnrecoverableKeyException | IllegalBlockSizeException | BadPaddingException |
                     InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void on_pushButtonModifyRapport_clicked(java.awt.event.ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) tableRapportMedical.getModel();
        Object[] rapportSelectionneByte = new Object[model.getColumnCount()];
        int selectedRow = getSelectionIndexTableRapportMedical();

        if (selectedRow == -1) {
            dialogError("Modification d un rapport", "Veuillez sélectionner un rapport dans la liste.");
            return;
        }

        for (int i = 0; i < model.getColumnCount(); i++) {
            rapportSelectionneByte[i] = model.getValueAt(selectedRow, i);
        }

        AjoutRapport dialog = new AjoutRapport(this, rapportSelectionneByte, 2);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            boolean result = false;
            int patientId = Integer.parseInt(dialog.getPatientId());
            String date = dialog.getDate();
            String raison = dialog.getRaison();

            System.out.println("\n--- Modification d un rapport ---");
            System.out.println("Doctor ID = " + doctorConnecte.getIdDoctor());
            System.out.println("Patient ID = " + patientId);
            System.out.println("Date: " + date );
            System.out.println("Raison: " + raison);
        }
    }

    public void on_pushRechercheRapport_clicked(java.awt.event.ActionEvent e)  {
        Integer patientId = getPatientIdForSearch();

        if(patientId == null) {
            patientId = -1;
        }

        RequeteLISTREPORTS requete = null;
        try {
            requete = new RequeteLISTREPORTS(CryptoManagement.CryptSymDES(sessionKey, String.valueOf(patientId).getBytes()));

            oss.writeObject(requete);
            ReponseLISTREPORTS reponse = (ReponseLISTREPORTS) ois.readObject();
            java.util.List<byte[]> decryptedResponseBytes = new ArrayList<>();

            for(byte[] reportData : reponse.getListeReports()) {
                byte[] decryptedBytes = CryptoManagement.DecryptSymDES(sessionKey, reportData);
                decryptedResponseBytes.add(decryptedBytes);
            }

            java.util.List<Report> listeReport = new ArrayList<>();
            for(byte[] reportBytes : decryptedResponseBytes) {
                ByteArrayInputStream bais = new ByteArrayInputStream(reportBytes);
                ObjectInputStream oisReport = new ObjectInputStream(bais);
                Report report = (Report) oisReport.readObject();
                listeReport.add(report);
            }

            if(listeReport.isEmpty()) {
                dialogMessage("Information", "Aucun rapport trouvé pour le patient ID: " + patientId);
                return;
            }

            dialogMessage("Succès", "Rapports récupérés avec succès.");
            clearTableRapportMedical();

            for (Report report : listeReport) {
                addTupleTableRapport(report.getIdReport(), "Patient " + report.getPatientId(),
                        "Doctor " + report.getDoctorId(), report.getDateReport().toString(),
                        report.getContent());
            }

        } catch (IOException | ClassNotFoundException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static PrivateKey RecupereClePrivee() throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("C:\\Users\\harol\\Documents\\HEPL\\Bach 3\\Q1\\Developpement logiciel RTI\\Labo\\Serveurs JAVA\\src\\main\\java\\hepl\\faad\\serveurs_java\\keyStore\\client\\KeystoreClientDoctor.jks"),
                "oracle".toCharArray());
        PrivateKey cle = (PrivateKey) ks.getKey("clientDoctor","oracle".toCharArray());
        return cle;
    }
    public static PublicKey RecupereClePublique() throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("C:\\Users\\harol\\Documents\\HEPL\\Bach 3\\Q1\\Developpement logiciel RTI\\Labo\\Serveurs JAVA\\src\\main\\java\\hepl\\faad\\serveurs_java\\keyStore\\client\\KeystoreClientDoctor.jks"),
                "oracle".toCharArray());
        PublicKey cle = (PublicKey) ks.getKey("clientDoctor","oracle".toCharArray());
        return cle;
    }
    public static PublicKey RecupereClePubliqueServeur() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        // Récupération de la clé publique du client dans le keystore du serveur
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("C:\\Users\\harol\\Documents\\HEPL\\Bach 3\\Q1\\Developpement logiciel RTI\\Labo\\Serveurs JAVA\\src\\main\\java\\hepl\\faad\\serveurs_java\\keyStore\\client\\KeystoreClientDoctor.jks"),
                "oracle".toCharArray());
        X509Certificate certif = (X509Certificate)ks.getCertificate("serveur");
        PublicKey cle = certif.getPublicKey();
        return cle;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(clientRaportMedical::new);
    }
}