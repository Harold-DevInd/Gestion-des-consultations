package hepl.faad.serveurs_java.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ReservationDialog extends JDialog {

    // Champs de l'interface
    private JTextField idField;
    private JTextField patientField;
    private JTextField raisonField;
    private JTextField dateField;
    private JTextField heureField;

    private boolean confirmed = false;

    // Formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public ReservationDialog(JFrame owner, Object[] consultationData) {
        super(owner, "Confirmer/Modifier la Réservation", true); // 'true' rend le dialogue modal

        // Initialisation des champs
        idField = new JTextField(consultationData[0].toString(), 15);
        idField.setEditable(false); // L'ID de la consultation ne devrait pas être modifiable

        patientField = new JTextField(consultationData[1].toString(), 15); // Utiliser les infos du médecin connecté
        patientField.setEditable(false); // L'info du médecin est fixe lors de la réservation

        raisonField = new JTextField(consultationData[2].toString(), 15); // Raison par défaut

        dateField = new JTextField(consultationData[3].toString(), 15);
        heureField = new JTextField(consultationData[4].toString(), 15);

        // Assemblage des composants
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(createFieldsPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.pack();
        this.setLocationRelativeTo(owner);
    }

    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        panel.add(new JLabel("ID Consultation:"));
        panel.add(idField);

        panel.add(new JLabel("Nom du Patient:"));
        panel.add(patientField);

        panel.add(new JLabel("Raison:"));
        panel.add(raisonField);

        panel.add(new JLabel("Date (AAAA-MM-JJ):"));
        panel.add(dateField);

        panel.add(new JLabel("Heure (HH:MM):"));
        panel.add(heureField);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK (Confirmer)");
        JButton cancelButton = new JButton("Annuler");

        okButton.addActionListener(this::onOkClicked);
        cancelButton.addActionListener(this::onCancelClicked);

        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }

    private void onOkClicked(ActionEvent e) {
        // Valider la saisie avant de confirmer
        if (validateInput()) {
            confirmed = true;
            dispose(); // Fermer le dialogue
        }
    }

    private void onCancelClicked(ActionEvent e) {
        confirmed = false;
        dispose(); // Fermer le dialogue
    }

    private boolean validateInput() {
        // Validation simple du format de la date et de l'heure
        try {
            LocalDate.parse(dateField.getText(), DATE_FORMATTER);
            LocalTime.parse(heureField.getText(), HOUR_FORMATTER);
            if (raisonField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "La raison ne peut pas être vide.", "Erreur de Saisie", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format de Date ou d'Heure invalide (AAAA-MM-JJ / HH:MM).", "Erreur de Format", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // --- Méthodes publiques pour récupérer les données ---

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getConsultationId() {
        return idField.getText();
    }

    public String getPatient() {
        return patientField.getText();
    }

    public String getRaison() {
        return raisonField.getText();
    }

    public String getDate() {
        return dateField.getText();
    }

    public String getHeure() {
        return heureField.getText();
    }
}