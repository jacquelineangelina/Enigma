package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Jacqueline Angelina
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = new ArrayList<>(allRotors);
        _rotors = new Rotor[numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Gets the array of rotors in the machine.
     * @return List of rotors. */
    Rotor[] getRotor() {
        return _rotors;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i += 1) {
            for (int j = 0; j < _allRotors.size(); j += 1) {
                if (rotors[i].equals(_allRotors.get(j).name().toUpperCase())) {
                    _rotors[i] = _allRotors.get(j);
                }
            }
            if (_rotors[i] == null) {
                throw error("Rotor doesn't exist.");
            }
        }
        if (!_rotors[0].reflecting()) {
            throw error("First rotor is not a reflector.");
        }
        if (rotors.length != _numRotors) {
            throw error("Not enough number of rotor slots.");
        }
        for (int i = 0; i < numRotors() - numPawls(); i += 1) {
            if (_rotors[i].rotates()) {
                throw error("Too many moving rotors.");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw error("Incorrect setting length.");
        }
        for (int i = 1; i < _rotors.length; i += 1) {
            if (!_alphabet.contains(setting.charAt(i - 1))) {
                throw error("Initial setting is not in alphabet");
            }
            _rotors[i].set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        boolean[] moves;
        moves = new boolean[numRotors()];
        for (int i = numRotors() - 1; i >= 0; i -= 1) {
            if (i == numRotors() - 1) {
                moves[i] = true;
            } else if (_rotors[i].rotates()) {
                moves[i] = _rotors[i + 1].atNotch();
                if (moves[i]) {
                    moves[i + 1] = true;
                }
            } else {
                moves[i] = false;
            }
        }
        for (int i = numRotors() - 1; i >= numRotors() - numPawls(); i -= 1) {
            if (moves[i]) {
                _rotors[i].advance();
            }
        }
        int result = _plugboard.permute(c);
        for (int i = numRotors() - 1; i >= 0; i -= 1) {
            result = _rotors[i].convertForward(result);
        }
        for (int i = 1; i < numRotors(); i += 1) {
            result = _rotors[i].convertBackward(result);
        }
        result = _plugboard.permute(result);
        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i += 1) {
            int converted = convert(_alphabet.toInt(msg.charAt(i)));
            char convertedChar = _alphabet.toChar(converted);
            result += convertedChar;
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots. */
    private int _numRotors;

    /** Number of rotor pawls. */
    private int _pawls;

    /** Collection of all available rotors. */
    private ArrayList<Rotor> _allRotors;

    /** Array of rotors in the machine. */
    private Rotor[] _rotors;

    /** Plugboard containing connected pairs of letters. */
    private Permutation _plugboard;
}
