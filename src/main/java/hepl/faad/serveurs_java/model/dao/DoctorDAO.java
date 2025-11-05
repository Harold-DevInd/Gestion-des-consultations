package hepl.faad.serveurs_java.model.dao;

import hepl.faad.serveurs_java.model.ConnectDB;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Specialty;
import hepl.faad.serveurs_java.model.viewmodel.DoctorSearchVM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorDAO {
    private ConnectDB connectDB;
    private ArrayList<Doctor> doctors;

    public DoctorDAO() {
        connectDB = new ConnectDB();
        doctors = new ArrayList<>();
    }

    public ConnectDB getConnectDB() {
        return connectDB;
    }

    public void setConnectDB(ConnectDB connectDB) {
        this.connectDB = connectDB;
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public Doctor getDoctorById(Integer id)
    {
        for (Doctor d : doctors) {
            if (Objects.equals(d.getIdDoctor(), id)) return d;
        }
        return null;
    }

    public ArrayList<Doctor> load()
    {
        return this.load(null);
    }

    public ArrayList<Doctor> load(DoctorSearchVM dsvm) {
        try {
            String sql = "SELECT d.id AS doctor_id, d.specialty_id, d.last_name, d.first_name, s.name AS specialty_name " +
                    "FROM doctors d " +
                    "LEFT JOIN specialties s ON d.specialty_id = s.id ";

            if(dsvm != null)
            {
                String where = " WHERE 1=1 ";

                if(dsvm.getIdDoctor() != null)
                    where += " AND d.id = ? ";

                if(dsvm.getFirstName() != null)
                    where += " AND d.first_name = ? ";
                if(dsvm.getLastName() != null)
                    where += " AND d.last_name = ? ";

                sql += where;
                sql += " ORDER BY d.id;";
            }
            PreparedStatement stmt = connectDB.getConn().prepareStatement(sql);

            if(dsvm != null)
            {
                int param = 0;
                if(dsvm.getIdDoctor() != null)
                {
                    param++;
                    stmt.setInt(param, dsvm.getIdDoctor());
                }

                if(dsvm.getFirstName() != null)
                {
                    param++;
                    stmt.setString(param, dsvm.getFirstName());
                }
                if(dsvm.getLastName() != null)
                {
                    param++;
                    stmt.setString(param, dsvm.getLastName());
                }
            }

            ResultSet rs = stmt.executeQuery();
            doctors.clear();

            while (rs.next()) {
                Integer id = rs.getInt("doctor_id");
                Integer specId = rs.getInt("specialty_id");
                String last = rs.getString("last_name");
                String first = rs.getString("first_name");
                String specName = rs.getString("specialty_name");

                Specialty sp = (specId != 0 ? new Specialty(specId, specName) : null);
                Doctor d = new Doctor(id, sp, last, first);

                doctors.add(d);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DoctorDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return doctors;
        }
    }

    public void save(Doctor doc) {
        if (doc == null) return;
        try {
            if (doc.getIdDoctor() != null) {
                // Update
                String sql = "UPDATE doctors SET specialty_id = ?, last_name = ?, first_name = ? WHERE id = ?";

                PreparedStatement pStmt = connectDB.getConn().prepareStatement(sql);

                if (doc.getSpecialty() != null && doc.getSpecialty().getIdSpecialty() != null)
                    pStmt.setInt(1, doc.getSpecialty().getIdSpecialty());
                else
                    pStmt.setNull(1, java.sql.Types.INTEGER);

                pStmt.setString(2, doc.getLastName());
                pStmt.setString(3, doc.getFirstName());
                pStmt.setInt(4, doc.getIdDoctor());
                pStmt.executeUpdate();
                pStmt.close();
            } else {
                // Insert
                String sql = "INSERT INTO doctors (specialty_id, last_name, first_name) VALUES (?, ?, ?)";

                PreparedStatement pStmt = connectDB.getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                if (doc.getSpecialty() != null && doc.getSpecialty().getIdSpecialty() != null)
                    pStmt.setInt(1, doc.getSpecialty().getIdSpecialty());
                else
                    pStmt.setNull(1, java.sql.Types.INTEGER);

                pStmt.setString(2, doc.getLastName());
                pStmt.setString(3, doc.getFirstName());
                pStmt.executeUpdate();

                ResultSet rs = pStmt.getGeneratedKeys();
                if (rs.next()) {
                    doc.setIdDoctor(rs.getInt(1));
                }

                rs.close();
                pStmt.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DoctorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(Doctor doc) {
        if (doc != null && doc.getIdDoctor() != null) this.delete(doc.getIdDoctor());
    }

    public void delete(Integer id) {
        if (id == null) return;
        try {
            String sql = "DELETE FROM doctors WHERE id = ?";
            PreparedStatement stmt = connectDB.getConn().prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DoctorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
