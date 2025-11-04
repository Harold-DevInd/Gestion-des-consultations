package hepl.faad.serveurs_java.model.dao;

import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Specialty;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DoctorDAOTest {

    private DoctorDAO dao;
    private SpecialtyDAO spDao;
    private Connection conn;

    @BeforeEach
    public void setUp() throws SQLException {
        dao = new DoctorDAO();
        spDao = new SpecialtyDAO();
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
        // Ensure there's a specialty to associate
        Specialty sp = new Specialty(null, "SpecForDoc_"+System.currentTimeMillis());
        spDao.save(sp);
        assertNotNull(sp.getIdSpecialty());

        // create doctor
        Doctor d = new Doctor(null, sp, "DocLast", "DocFirst");
        dao.save(d);
        assertNotNull(d.getIdDoctor());

        // load and check presence
        dao.load();
        Doctor loaded = dao.getDoctorById(d.getIdDoctor());
        assertNotNull(loaded);
        assertEquals("DocLast", loaded.getLastName());

        // update
        d.setFirstName("Changed");
        dao.save(d);
        dao.load();
        Doctor updated = dao.getDoctorById(d.getIdDoctor());
        assertEquals("Changed", updated.getFirstName());

        // delete
        dao.delete(d);
        dao.load();
        Doctor deleted = dao.getDoctorById(d.getIdDoctor());
        assertNull(deleted, "Doctor should be deleted");
    }
}