package hepl.faad.serveurs_java.clients;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AjoutRapport extends JDialog{

    // Champs de l'interface
    private JTextField patientIdField;
    private JTextField dateField;
    private JTextArea raisonArea;

    private boolean confirmed = false;

    // Formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AjoutRapport(JFrame owner, Object[] rapportData, int option) {
        super(owner, "Ajouter un rapport", true);

        if(option == 1) {
            patientIdField = new JTextField(null, 15);
            dateField = new JTextField(null, 15);
            raisonArea = new JTextArea(5, 15);
        } else {
            patientIdField = new JTextField(rapportData[2].toString(), 15);
            dateField = new JTextField(rapportData[1].toString(), 15);
            raisonArea = new JTextArea(rapportData[4].toString(), 5, 15);
        }

        // Assemblage des composants
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(createFieldsPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.pack();
        this.setLocationRelativeTo(owner);
    }

    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Id du patient:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        patientIdField.setColumns(10);
        panel.add(patientIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Date (AAAA-MM-JJ):"), gbc);
        gbc.gridx = 1;
        dateField.setColumns(10);
        panel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Raison : "), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(new JScrollPane(raisonArea), gbc);

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
        if (validateInput()) {
            confirmed = true;
            dispose();
        }
    }

    private void onCancelClicked(ActionEvent e) {
        confirmed = false;
        dispose();
    }

    private boolean validateInput() {
        // Validation simple du format de la date et de l'heure
        try {
            LocalDate.parse(dateField.getText(), DATE_FORMATTER);

            if (raisonArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "La raison ne peut pas être vide.", "Erreur de Saisie", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format de Date invalide (AAAA-MM-JJ).", "Erreur de Format", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // --- Méthodes publiques pour récupérer les données ---

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getPatientId() {
        return patientIdField.getText();
    }

    public String getDate() {
        return dateField.getText();
    }

    public String getRaison() {
        return raisonArea.getText();
    }
}
