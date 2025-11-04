package hepl.faad.serveurs_java.model.dao;

import hepl.faad.serveurs_java.model.ConnectDB;
import hepl.faad.serveurs_java.model.entity.Consultation;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Patient;
import hepl.faad.serveurs_java.model.entity.Specialty;
import hepl.faad.serveurs_java.model.viewmodel.ConsultationSearchVM;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultationDAO {
    public ConnectDB getConnectDB() {
        return connectDB;
    }

    public void setConnectDB(ConnectDB connectDB) {
        this.connectDB = connectDB;
    }

    private ConnectDB connectDB;
    private ArrayList<Consultation> consultations;

    public ConsultationDAO() {
        connectDB = new ConnectDB();
        consultations = new ArrayList<>();
    }

    public ArrayList<Consultation> getConsultations() {
        return consultations;
    }

    public Consultation getConsultationById(Integer idConsul)
    {
        for(Consultation consul : consultations)
        {
            if(Objects.equals(consul.getIdConsultation(), idConsul))
                return consul;
        }
        return null;
    }

    public ArrayList<Consultation> load()
    {
        return this.load(null);
    }

    public ArrayList<Consultation> load(ConsultationSearchVM csvm)
    {
        try
        {
            String sql = "SELECT c.id AS c_id, c.doctor_id, c.patient_id, c.date AS c_date, c.hour AS c_hour, c.reason AS c_reason, " +
                            "d.id AS d_id, d.last_name AS d_last, d.first_name AS d_first, " +
                            "s.id AS s_id, s.name AS s_name, " +
                            "p.id AS p_id, p.last_name AS p_last, p.first_name AS p_first, p.birth_date AS p_birth " +
                            "FROM consultations c " +
                            "JOIN doctors d ON c.doctor_id = d.id " +
                            "JOIN specialties s ON d.specialty_id = s.id " +
                            "JOIN patients p ON c.patient_id = p.id ";

                    if(csvm != null)
                    {
                        String where = " WHERE 1=1 ";

                        if(csvm.getIdConsultation() != null)
                            where += " AND c_id = ? ";

                        if(csvm.getDoctor() != null)
                            where += " AND d_id = ? ";

                        if(csvm.getPatient() != null)
                            where += " AND p_id = ? ";

                        sql += where;
                        sql += " ORDER BY c_id;";
                    }
            PreparedStatement stmt = connectDB.getConn().prepareStatement(sql);

            if(csvm != null)
            {
                int param = 0;
                if(csvm.getIdConsultation() != null)
                {
                    param++;
                    stmt.setInt(param, csvm.getIdConsultation());
                }
                if(csvm.getDoctor() != null)
                {
                    param++;
                    stmt.setInt(param, csvm.getDoctor().getIdDoctor());
                }
                if(csvm.getPatient() != null)
                {
                    param++;
                    stmt.setInt(param, csvm.getPatient().getIdPatient());
                }
            }

            ResultSet rs = stmt.executeQuery();
            consultations.clear();

            while(rs.next())
            {
                Integer idConsul = rs.getInt("c_id");
                Integer idDoctor = rs.getInt("d_id");
                Integer idPatient = rs.getInt("p_id");
                Date date = rs.getDate("c_date");
                String hour = rs.getString("c_hour");
                String raison = rs.getString("c_reason");
                int specIdInt = rs.getInt("s_id");
                String doctorLastName = rs.getString("d_last");
                String doctorFirstName = rs.getString("d_first");
                String specialtyName = rs.getString("s_name");
                String patientLastName = rs.getString("p_last");
                String patientFirstName = rs.getString("p_first");
                java.sql.Date pb = rs.getDate("p_birth");
                LocalDate patientBirthDate = (pb != null) ? pb.toLocalDate() : null;

                Patient patient = new Patient(idPatient, patientLastName, patientFirstName, patientBirthDate);

                Integer specialityId = rs.wasNull() ? null : specIdInt;
                Specialty specialty = (specialityId != null) ? new Specialty(specialityId, specialtyName) : null;

                Doctor doctor = new Doctor(idDoctor, specialty, doctorLastName, doctorFirstName);
                Consultation consultation = new Consultation(idConsul, doctor, patient, date.toLocalDate(), hour, raison);

                consultations.add(consultation);
            }
            stmt.close();
        } catch (SQLException ex){
            Logger.getLogger(ConsultationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            return consultations;
        }
    }

    public void save(Consultation consul)
    {
        try{
            String sql;

            if(consul != null)
            {
                if(consul.getIdConsultation() != null)
                {
                    if((consul.getDoctor() == null || consul.getDoctor().getIdDoctor() == null) && (consul.getPatient() == null || consul.getPatient().getIdPatient() == null))
                        return;


                    sql = "UPDATE consultations SET " +
                            "doctor_id = ?, " +
                            "patient_id = ?, " +
                            "date = ?, " +
                            "hour = ?, " +
                            "reason = ? " +
                            "WHERE id = ?";

                    PreparedStatement pStmt = connectDB.getConn().prepareStatement(sql);

                    pStmt.setInt(1, consul.getDoctor().getIdDoctor());
                    pStmt.setInt(2, consul.getPatient().getIdPatient());
                    pStmt.setDate(3, Date.valueOf(consul.getDateConsultation()));
                    pStmt.setString(4, consul.getHeureConsultation());
                    pStmt.setString(5, consul.getRaison());
                    pStmt.setInt(6, consul.getIdConsultation());

                    pStmt.executeUpdate();
                    pStmt.close();
                }
                else
                {
                    if((consul.getDoctor() == null || consul.getDoctor().getIdDoctor() == null) && (consul.getPatient() == null || consul.getPatient().getIdPatient() == null))
                        return;

                    sql = "INSERT INTO consultations ( doctor_id, patient_id, date, hour, reason) " +
                            " VALUES (?, ?, ?, ?, ?)";

                    PreparedStatement pStmt = connectDB.getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                    pStmt.setInt(1, consul.getDoctor().getIdDoctor());
                    pStmt.setInt(2, consul.getPatient().getIdPatient());
                    pStmt.setDate(3, Date.valueOf(consul.getDateConsultation()));
                    pStmt.setString(4, consul.getHeureConsultation());
                    pStmt.setString(5, consul.getRaison());

                    pStmt.executeUpdate();
                    ResultSet rs = pStmt.getGeneratedKeys();
                    if(rs.next())
                        consul.setIdConsultation(rs.getInt(1));

                    rs.close();
                    pStmt.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(Consultation consult)
    {
        if(consult != null && consult.getIdConsultation() != null)
            this.delete(consult.getIdConsultation());
    }

    public void delete(Integer idConsult)
    {
        if(idConsult != null)
            try {
                String sql = "DELETE FROM consultations WHERE id=?";
                PreparedStatement stmt = connectDB.getConn().prepareStatement(sql);
                stmt.setInt(1, idConsult);
                stmt.executeUpdate();
                stmt.close();
            }catch (SQLException ex) {
                Logger.getLogger(ConsultationDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
