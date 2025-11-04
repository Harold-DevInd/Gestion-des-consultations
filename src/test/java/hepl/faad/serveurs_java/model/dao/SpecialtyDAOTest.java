package hepl.faad.serveurs_java.model.dao;

import hepl.faad.serveurs_java.model.ConnectDB;
import hepl.faad.serveurs_java.model.entity.Specialty;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class SpecialtyDAOTest {

    private SpecialtyDAO dao;
    private Connection conn;

    @BeforeEach
    public void beforeEach() throws SQLException {
        dao = new SpecialtyDAO();
        conn = dao.getConnectDB().getConn();
        conn.setAutoCommit(false); // démarrer transaction pour rollback après test
    }

    @AfterEach
    public void afterEach() throws SQLException {
        if (conn != null) {
            conn.rollback();     // annuler toutes les opérations du test
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    @Test
    public void testLoadAndSaveAndDelete() {
        // ensure load returns a list (may be empty)
        List<Specialty> listBefore = dao.load();
        assertNotNull(listBefore);

        // create new specialty and insert
        Specialty s = new Specialty(null, "TestSpec-" + System.currentTimeMillis());
        dao.save(s);
        assertNotNull(s.getIdSpecialty()); // id should be set by save (generated key)

        // load and find by id
        List<Specialty> listAfter = dao.load();
        boolean found = listAfter.stream().anyMatch(x -> x.getIdSpecialty().equals(s.getIdSpecialty()));
        assertTrue(found, "Inserted specialty must be present after load()");

        // update the specialty
        s.setNom("TestSpecUpdated");
        dao.save(s); // update
        Specialty reloaded = dao.getSpecialtyById(s.getIdSpecialty());
        // getSpecialtyById searches in internal cache: call load() to refresh
        dao.load();
        Specialty reloaded2 = dao.getSpecialtyById(s.getIdSpecialty());
        assertNotNull(reloaded2);
        assertEquals("TestSpecUpdated", reloaded2.getNom());

        // delete
        dao.delete(s);
        dao.load();
        boolean stillPresent = dao.getSpecialtyById(s.getIdSpecialty()) != null;
        assertFalse(stillPresent, "Specialty should be deleted");
    }
}