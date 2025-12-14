package hepl.faad.serveurs_java.model.dao;

import hepl.faad.serveurs_java.model.ConnectDB;
import hepl.faad.serveurs_java.model.entity.Report;
import hepl.faad.serveurs_java.model.viewmodel.ReportSearchVM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportDAO {
    private ConnectDB connectDB;

    public ReportDAO() {
        connectDB = new ConnectDB();
    }

    public ConnectDB getConnectDB() {
        return connectDB;
    }

    public void setConnectDB(ConnectDB connectDB) {
        this.connectDB = connectDB;
    }

    public ArrayList<Report> load()
    {
        return this.load(null);
    }

    public ArrayList<Report> load(ReportSearchVM rsvm) {
        ArrayList<Report> reports = new ArrayList<>();

        try {
            String sql = "SELECT id, doctor_id, patient_id, date_report, content " +
                    " FROM reports ";

            if (rsvm != null) {
                String where = " WHERE 1 = 1 ";

                if(rsvm.getIdReport() != null)
                    where += " AND id = ? ";

                if(rsvm.getIdDoctor() != null)
                    where += " AND doctor_id = ? ";

                if(rsvm.getIdPatient() != null)
                    where += " AND patient_id = ? ";

                sql += where;
                sql += " ORDER BY id;";
            }
            PreparedStatement stmt = connectDB.getConn().prepareStatement(sql);

            if(rsvm != null)
            {
                int param = 0;
                if(rsvm.getIdReport() != null)
                {
                    param++;
                    stmt.setInt(param, rsvm.getIdReport());
                }
                if(rsvm.getIdDoctor() != null)
                {
                    param++;
                    stmt.setInt(param, rsvm.getIdDoctor());
                }
                if(rsvm.getIdPatient() != null)
                {
                    param++;
                    stmt.setInt(param, rsvm.getIdPatient());
                }
            }

            ResultSet rs = stmt.executeQuery();
            reports.clear();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer doctorId = rs.getInt("doctor_id");
                Integer patientId = rs.getInt("patient_id");
                LocalDate dateReport = rs.getDate("date_report").toLocalDate();
                String content = rs.getString("content");

                Report rep = new Report(id, doctorId, patientId, dateReport, content);
                reports.add(rep);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReportDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return reports;
        }
    }

    public boolean save(Report rep) {
        if (rep == null) return false;
        try {
            if (rep.getIdReport() != null) {
                // Update
                String sql = "UPDATE reports" +
                        " SET patient_id = ?, content = ?" +
                        " WHERE id = ?";

                PreparedStatement pStmt = connectDB.getConn().prepareStatement(sql);
                pStmt.setInt(1, rep.getPatientId());
                pStmt.setString(2, rep.getContent());
                pStmt.setInt(3, rep.getIdReport());
                pStmt.executeUpdate();
                pStmt.close();
                return true;
            } else {
                // Insert
                String sql = "INSERT INTO reports (doctor_id, patient_id, date_report, content) " +
                        " VALUES (?, ?, ?, ?)";

                PreparedStatement pStmt = connectDB.getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                pStmt.setInt(1, rep.getDoctorId());
                pStmt.setInt(2, rep.getPatientId());
                pStmt.setDate(3, java.sql.Date.valueOf(rep.getDateReport()));
                pStmt.setString(4, rep.getContent());

                pStmt.executeUpdate();
                ResultSet rs = pStmt.getGeneratedKeys();
                if (rs.next()) {
                    rep.setIdReport(rs.getInt(1));
                }

                rs.close();
                pStmt.close();

                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean delete(Report rep) {
        if (rep != null && rep.getIdReport() != null)
            return this.delete(rep.getIdReport());
        return false;
    }

    public boolean delete(Integer id) {
        if (id == null) return false;
        try {
            String sql = "DELETE FROM reports WHERE id = ?";
            PreparedStatement stmt = connectDB.getConn().prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ReportDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
