package hepl.faad.serveurs_java.model.dao;

import hepl.faad.serveurs_java.model.ConnectDB;
import hepl.faad.serveurs_java.model.entity.Specialty;
import hepl.faad.serveurs_java.model.viewmodel.SpecialtySearchVM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpecialtyDAO {
    private ConnectDB connectDB;

    public SpecialtyDAO() {
        connectDB = new ConnectDB();
    }

    public ConnectDB getConnectDB() {
        return connectDB;
    }

    public void setConnectDB(ConnectDB connectDB) {
        this.connectDB = connectDB;
    }

    public ArrayList<Specialty> load()
    {
        return this.load(null);
    }

    public ArrayList<Specialty> load(SpecialtySearchVM ssvm) {
        ArrayList<Specialty> specialties = new ArrayList<>();

        try {
            String sql = "SELECT id, name " +
                    "FROM specialties ";

            if (ssvm != null) {
                String where = " WHERE 1 = 1 ";

                if(ssvm.getIdSpecialty() != null)
                    where += " AND idSpecialty = ? ";
                if(ssvm.getNom() != null)
                    where += " AND name = ? ";

                sql += where;
                sql += " ORDER BY id;";
            }
            PreparedStatement stmt = connectDB.getConn().prepareStatement(sql);

            if(ssvm != null)
            {
                int param = 0;
                if(ssvm.getIdSpecialty() != null)
                {
                    param++;
                    stmt.setInt(param++, ssvm.getIdSpecialty());
                }
                if(ssvm.getNom() != null)
                {
                    param++;
                    stmt.setString(param++, ssvm.getNom());
                }
            }

            ResultSet rs = stmt.executeQuery();
            specialties.clear();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                Specialty sp = new Specialty(id, name);
                specialties.add(sp);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SpecialtyDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return specialties;
        }
    }

    public void save(Specialty sp) {
        if (sp == null) return;
        try {
            if (sp.getIdSpecialty() != null) {
                // Update
                String sql = "UPDATE specialties" +
                        " SET name = ?" +
                        " WHERE id = ?";

                PreparedStatement pStmt = connectDB.getConn().prepareStatement(sql);
                pStmt.setString(1, sp.getNom());
                pStmt.setInt(2, sp.getIdSpecialty());
                pStmt.executeUpdate();
                pStmt.close();
            } else {
                // Insert
                String sql = "INSERT INTO specialties (name) VALUES (?)";

                PreparedStatement pStmt = connectDB.getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                pStmt.setString(1, sp.getNom());

                pStmt.executeUpdate();
                ResultSet rs = pStmt.getGeneratedKeys();
                if (rs.next()) {
                    sp.setIdSpecialty(rs.getInt(1));
                }

                rs.close();
                pStmt.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SpecialtyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(Specialty sp) {
        if (sp != null && sp.getIdSpecialty() != null) this.delete(sp.getIdSpecialty());
    }

    public void delete(Integer id) {
        if (id == null) return;
        try {
            String sql = "DELETE FROM specialties WHERE id = ?";
            PreparedStatement stmt = connectDB.getConn().prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SpecialtyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
