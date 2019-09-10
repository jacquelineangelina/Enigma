package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collection;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Jacqueline Angelina
 */

public class MachineTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */
    private Machine _machine;

    private static Reflector reflector = new Reflector("B", new Permutation(
            "(AE) (BN) (CK) (DQ) (FU) (GY) (HW) (IJ) (LO) (MP) "
                    + "(RX) (SZ) (TV)", UPPER));
    private static FixedRotor fixed = new FixedRotor("Beta", new Permutation(
            "(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", UPPER));
    private static MovingRotor _moving1 = new MovingRotor("I", new Permutation(
            "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", UPPER), "Q");
    private static MovingRotor _moving2 = new MovingRotor("II", new Permutation(
            "(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)",
            UPPER), "E");
    private static MovingRotor _moving3 = new MovingRotor("III", new
            Permutation("(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", UPPER), "V");
    private static MovingRotor _moving4 = new MovingRotor("IV", new Permutation(
            "(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", UPPER), "J");
    private static MovingRotor _moving5 = new MovingRotor("V", new Permutation(
            "(AVOLDRWFIUQ)(BZKSMNHYC) (EGTJPX)", UPPER), "Z");

    private String[] insertedRotors = {"B", "BETA", "III", "IV", "I"};

    private static Rotor[] rotors = {reflector, fixed, _moving1, _moving2,
        _moving3, _moving4, _moving5};

    static ArrayList<Rotor> _allRotors = new ArrayList<>();
    static {
        for (Rotor i : rotors) {
            _allRotors.add(i);
        }
    }

    private void createMachine(Alphabet alpha, int numRotors, int pawls,
                               Collection<Rotor> allRotors) {
        _machine = new Machine(alpha, numRotors, pawls, _allRotors);
    }

    /* ***** TESTS ***** */
    @Test
    public void testInsertRotors() {
        createMachine(UPPER, 5, 3, _allRotors);
        _machine.insertRotors(insertedRotors);
        assertEquals("Wrong rotor at position 0",
                _allRotors.get(0), _machine.getRotor()[0]);
        assertEquals("Wrong rotor at position 1",
                _allRotors.get(1), _machine.getRotor()[1]);
        assertEquals("Wrong rotor at position 2",
                _allRotors.get(4), _machine.getRotor()[2]);
        assertEquals("Wrong rotor at position 3",
                _allRotors.get(5), _machine.getRotor()[3]);
        assertEquals("Wrong rotor at position 4",
                _allRotors.get(2), _machine.getRotor()[4]);
    }

    @Test
    public void testSetRotors() {
        createMachine(UPPER, 5, 3, _allRotors);
        _machine.insertRotors(insertedRotors);
        _machine.setRotors("AXLE");
        assertEquals("Wrong setting on position 1",
                0, _machine.getRotor()[1].setting());
        assertEquals("Wrong setting on position 2",
                23, _machine.getRotor()[2].setting());
        assertEquals("Wrong setting on position 3",
                11, _machine.getRotor()[3].setting());
        assertEquals("Wrong setting on position 3",
                4, _machine.getRotor()[4].setting());
    }

    @Test
    public void testConvert() {
        createMachine(UPPER, 5, 3, _allRotors);
        _machine.insertRotors(insertedRotors);
        _machine.setRotors("AXLE");
        _machine.setPlugboard(new Permutation(
                "(HQ) (EX) (IP) (TR) (BY)", UPPER));
        assertEquals("Wrong conversion.",
                "QVPQ", _machine.convert("FROM"));
        _machine.setRotors("AXLE");
        assertEquals("Wrong convert",
                "FROM", _machine.convert("QVPQ"));
    }
}
