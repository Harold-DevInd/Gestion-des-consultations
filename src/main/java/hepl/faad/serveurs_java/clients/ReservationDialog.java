package hepl.faad.serveurs_java.clients;

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
    private JTextField dureeField;
    private JTextField nombreField;

    private boolean confirmed = false;

    // Formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    int Option;

    public ReservationDialog(JFrame owner, Object[] consultationData, int option) {
        super(owner, "Confirmer/Modifier la Réservation", true);
        Option = option;

        if(option == 1)
        {
            //Ajouter
            idField = new JTextField(null, 15);
            idField.setEditable(false);
            patientField = new JTextField(null, 15);
            raisonField = new JTextField(null, 15);
            dateField = new JTextField(null, 15);
            heureField = new JTextField(null, 15);
            dureeField = new JTextField(null, 15);
            nombreField = new JTextField(null, 15);
        }
        else //Update
        {
            idField = new JTextField(consultationData[0].toString(), 15);
            idField.setEditable(false);
            patientField = new JTextField(consultationData[1].toString(), 15);
            raisonField = new JTextField(consultationData[2].toString(), 15);
            dateField = new JTextField(consultationData[3].toString(), 15);
            heureField = new JTextField(consultationData[4].toString(), 15);
        }

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

        panel.add(new JLabel("Date (AAAA-MM-JJ):"));
        panel.add(dateField);

        panel.add(new JLabel("Heure (HH:MM):"));
        panel.add(heureField);

        if(this.Option == 1)
        {
            panel.add(new JLabel("Duree:"));
            panel.add(dureeField);

            panel.add(new JLabel("Nombre de consultation:"));
            panel.add(nombreField);
        }
        else {
            panel.add(new JLabel("Nom du Patient:"));
            panel.add(patientField);

            panel.add(new JLabel("Raison:"));
            panel.add(raisonField);
        }

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

            if(Option == 2)
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

    public String getDuree() {
        return dureeField.getText();
    }

    public String getNombreConsultation() {
        return nombreField.getText();
    }
}