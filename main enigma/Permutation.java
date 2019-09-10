package enigma;

import static enigma.EnigmaException.*;
import java.util.ArrayList;
import java.util.Arrays;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Jacqueline Angelina
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String eliminate = cycles.replace("(", " ");
        eliminate = eliminate.replace(")", " ");
        _cycles = new ArrayList<String>(Arrays.asList(eliminate.split("\\s+")));
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. Go from "(abc) (d)" to {"abc", "d"}*/
    private void addCycle(String cycle) {
        String cycle2 = cycle.replace("(", "");
        cycle2 = cycle2.replace(")", "");
        _cycles.add(cycle2);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int contact = wrap(p);
        char start = _alphabet.toChar(contact);
        char end = _alphabet.toChar(0);
        for (int i = 0; i < _cycles.size(); i += 1) {
            for (int j = 0; j < _cycles.get(i).length(); j += 1) {
                if (_cycles.get(i).charAt(j) == start) {
                    if (j != _cycles.get(i).length() - 1) {
                        end = _cycles.get(i).charAt(j + 1);
                    } else {
                        end = _cycles.get(i).charAt(0);
                    }
                    return _alphabet.toInt(end);
                }
            }
        }
        return contact;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int contact = wrap(c);
        char start = _alphabet.toChar(contact);
        char end = _alphabet.toChar(0);
        for (int i = 0; i < _cycles.size(); i += 1) {
            for (int j = 0; j < _cycles.get(i).length(); j += 1) {
                if (_cycles.get(i).charAt(j) == start) {
                    if (j != 0) {
                        end = _cycles.get(i).charAt(j - 1);
                    } else {
                        end = _cycles.get(i).charAt(_cycles.
                                get(i).length() - 1);
                    }
                    return _alphabet.toInt(end);
                }
            }
        }
        return contact;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int index = _alphabet.toInt(p);
        int pResult = permute(index);
        return _alphabet.toChar(pResult);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        int index = _alphabet.toInt(c);
        int pResult = invert(index);
        return _alphabet.toChar(pResult);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i += 1) {
            if (permute(_alphabet.toChar(i)) != _alphabet.toChar(i)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** An array list of strings, where each string is a cycle. */
    private ArrayList<String>  _cycles;
}
