package hepl.faad.serveurs_java.model.dao;

import hepl.faad.serveurs_java.model.ConnectDB;
import hepl.faad.serveurs_java.model.entity.Patient;
import hepl.faad.serveurs_java.model.viewmodel.PatientSearchVM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientDAO {
    private ConnectDB connectDB;
    private ArrayList<Patient> patients;

    public PatientDAO() {
        connectDB = new ConnectDB();
        patients = new ArrayList<>();
    }

    public ConnectDB getConnectDB() {
        return connectDB;
    }

    public void setConnectDB(ConnectDB connectDB) {
        this.connectDB = connectDB;
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    public Patient getPatientById(Integer id)
    {
        for (Patient p : patients) {
            if (Objects.equals(p.getIdPatient(), id)) return p;
        }
        return null;
    }

    public ArrayList<Patient> load()
    {
        return this.load(null);
    }

    public ArrayList<Patient> load(PatientSearchVM psvm) {
        try {
            String sql = "SELECT id, last_name, first_name, birth_date " +
                    "FROM patients " +
                    "ORDER BY last_name, first_name ";

            if(psvm != null)
            {
                String where = " WHERE 1=1 ";

                if(psvm.getIdPatient() != null)
                    where += " AND id_patient=? ";
                if(psvm.getFirstName() != null)
                    where += " AND first_name=? ";
                if(psvm.getLastName() != null)
                    where += " AND last_name=? ";

                sql += where;
                sql += " ORDER BY last_name, first_name;";
            }
            PreparedStatement stmt = connectDB.getConn().prepareStatement(sql);

            if(psvm != null)
            {
                int param = 0;
                if(psvm.getIdPatient() != null)
                {
                    param++;
                    stmt.setInt(param, psvm.getIdPatient());
                }
                if(psvm.getFirstName() != null)
                {
                    param++;
                    stmt.setString(param, psvm.getFirstName());
                }
                if(psvm.getLastName() != null)
                {
                    param++;
                    stmt.setString(param, psvm.getLastName());
                }
            }

            ResultSet rs = stmt.executeQuery();
            patients.clear();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String last = rs.getString("last_name");
                String first = rs.getString("first_name");
                java.sql.Date birth = rs.getDate("birth_date");
                LocalDate birthDate = (birth != null ? birth.toLocalDate() : null);

                Patient p = new Patient(id, last, first, birthDate);

                patients.add(p);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(PatientDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return patients;
        }
    }

    public void save(Patient p) {
        if (p == null) return;
        try {
            if (p.getIdPatient() != null) {
                // Update
                String sql = "UPDATE patients SET last_name = ?, first_name = ?, birth_date = ? WHERE id = ?";

                PreparedStatement pStmt = connectDB.getConn().prepareStatement(sql);

                pStmt.setString(1, p.getLastName());
                pStmt.setString(2, p.getFirstName());
                if (p.getDateNaissance() != null)
                    pStmt.setDate(3, java.sql.Date.valueOf(p.getDateNaissance()));
                else
                    pStmt.setNull(3, java.sql.Types.DATE);
                pStmt.setInt(4, p.getIdPatient());

                pStmt.executeUpdate();
                pStmt.close();
            } else {
                // Insert
                String sql = "INSERT INTO patients (last_name, first_name, birth_date) VALUES (?, ?, ?)";

                PreparedStatement pStmt = connectDB.getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                pStmt.setString(1, p.getLastName());
                pStmt.setString(2, p.getFirstName());
                if (p.getDateNaissance() != null)
                    pStmt.setDate(3, java.sql.Date.valueOf(p.getDateNaissance()));
                else
                    pStmt.setNull(3, java.sql.Types.DATE);

                pStmt.executeUpdate();
                ResultSet rs = pStmt.getGeneratedKeys();
                if (rs.next()) {
                    p.setIdPatient(rs.getInt(1));
                }

                rs.close();
                pStmt.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(PatientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(Patient p) {
        if (p != null && p.getIdPatient() != null) this.delete(p.getIdPatient());
    }

    public void delete(Integer id) {
        if (id == null) return;
        try {
            String sql = "DELETE FROM patients WHERE id = ?";
            PreparedStatement stmt = connectDB.getConn().prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(PatientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
