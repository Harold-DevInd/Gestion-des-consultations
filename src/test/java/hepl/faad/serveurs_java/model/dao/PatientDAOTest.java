package hepl.faad.serveurs_java.model.dao;

import hepl.faad.serveurs_java.model.entity.Patient;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PatientDAOTest {

    private PatientDAO dao;
    private Connection conn;

    @BeforeEach
    public void setUp() throws SQLException {
        dao = new PatientDAO();
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
        List<Patient> before = dao.load();
        assertNotNull(before);

        // create patient
        Patient p = new Patient(null, "TestLast", "TestFirst", LocalDate.of(1990,1,1));
        dao.save(p);
        assertNotNull(p.getIdPatient());

        // load and verify presence
        dao.load();
        Patient found = dao.getPatientById(p.getIdPatient());
        assertNotNull(found);
        assertEquals("TestLast", found.getLastName());

        // update
        p.setFirstName("NewFirst");
        dao.save(p);
        dao.load();
        Patient updated = dao.getPatientById(p.getIdPatient());
        assertEquals("NewFirst", updated.getFirstName());

        // delete
        dao.delete(p);
        dao.load();
        Patient deleted = dao.getPatientById(p.getIdPatient());
        assertNull(deleted, "Patient should be deleted after delete()");
    }
}