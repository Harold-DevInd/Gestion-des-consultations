package hepl.faad.serveurs_java.model.dao;

import hepl.faad.serveurs_java.model.entity.Consultation;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Patient;
import hepl.faad.serveurs_java.model.entity.Specialty;
import hepl.faad.serveurs_java.model.viewmodel.ConsultationSearchVM;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ConsultationDAOTest {

    private ConsultationDAO dao;
    private DoctorDAO doctorDAO;
    private PatientDAO patientDAO;
    private SpecialtyDAO specialtyDAO;
    private Connection conn;

    @BeforeEach
    public void setUp() throws SQLException {
        dao = new ConsultationDAO();
        doctorDAO = new DoctorDAO();
        patientDAO = new PatientDAO();
        specialtyDAO = new SpecialtyDAO();

        conn = dao.getConnectDB().getConn();
        conn.setAutoCommit(false);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (conn != null) {
            conn.rollback();
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    @Test
    public void testLoadSaveUpdateDelete() {
        // create specialty
        Specialty sp = new Specialty(null, "SpecForCons_"+System.currentTimeMillis());
        specialtyDAO.save(sp);
        assertNotNull(sp.getIdSpecialty());

        // create doctor
        Doctor d = new Doctor(null, sp, "DocLast", "DocFirst");
        doctorDAO.save(d);
        assertNotNull(d.getIdDoctor());

        // create patient
        Patient p = new Patient(null, "PatLast", "PatFirst", LocalDate.of(1980,1,1));
        patientDAO.save(p);
        assertNotNull(p.getIdPatient());

        // create consultation (heure as string per your constructor)
        Consultation c = new Consultation(null, d, p, LocalDate.now(), "09:00:00", "Test reason");
        dao.save(c);
        assertNotNull(c.getIdConsultation());

        // load and check presence
        dao.load();
        Consultation loaded = dao.getConsultationById(c.getIdConsultation());
        assertNotNull(loaded);
        assertEquals("Test reason", loaded.getRaison());

        // update
        c.setRaison("Updated reason");
        dao.save(c);
        dao.load();
        Consultation updated = dao.getConsultationById(c.getIdConsultation());
        assertEquals("Updated reason", updated.getRaison());

        // search using VM
        ConsultationSearchVM vm = new ConsultationSearchVM();
        vm.setIdConsultation(c.getIdConsultation());
        List<Consultation> results = dao.load(vm);
        assertTrue(results.stream().anyMatch(x -> x.getIdConsultation().equals(c.getIdConsultation())));

        // delete
        dao.delete(c);
        dao.load();
        Consultation deleted = dao.getConsultationById(c.getIdConsultation());
        assertNull(deleted);
    }
}