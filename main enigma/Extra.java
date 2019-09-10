package enigma;

import static enigma.EnigmaException.*;
/** An Alphabet consisting of a string.
 *  @author Jacqueline Angelina. */

public class Extra extends Alphabet {

    /** An alphabet consisting of INPUT of all characters in the string. */
    Extra(String input) {
        _input = input;
    }
    @Override
    int size() {
        return _input.length();
    }

    @Override
    boolean contains(char ch) {
        return _input.contains(Character.toString(ch));
    }

    @Override
    char toChar(int index) {
        if (index > size()) {
            throw error("character index out of range");
        }
        return _input.charAt(index);
    }

    @Override
    int toInt(char ch) {
        if (!contains(ch)) {
            throw error("character out of range");
        }
        return _input.indexOf(ch);
    }

    /** Range of characters in this Alphabet. */
    private String _input;

}
