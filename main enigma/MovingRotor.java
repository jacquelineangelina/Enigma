package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Jacqueline Angelina
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    void advance() {
        int next = this.setting() + 1;
        this.set(next);
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return _notches.contains(Character.toString(alphabet().
                toChar(this.setting())));
    }

    /** Notches of the moving rotor. */
    private String _notches;

}
