package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Jacqueline Angelina
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigma = readConfig();
        while (_input.hasNext()) {
            String setting = _input.nextLine();
            while (setting.matches("\\s+") || setting.length() == 0) {
                setting = _input.nextLine();
                _output.println();
            }
            setUp(enigma, setting);
            while (!_input.hasNext("\\*") && _input.hasNextLine()) {
                if (_input.hasNext("\\s+")) {
                    _input.nextLine();
                    _output.println();
                }
                String result = enigma.convert(_input.nextLine()
                        .replaceAll(" ", "").toUpperCase());
                printMessageLine(result);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphabet = _config.next();
            if (alphabet.length() == 3) {
                _alphabet = new CharacterRange(alphabet.charAt(0),
                        alphabet.charAt(2));
            } else {
                _alphabet = new Extra(alphabet);
            }
            if (!_config.hasNextInt()) {
                throw error("wrong configuration format");
            }
            int numRotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw error("wrong configuration format");
            }
            int numPawls = _config.nextInt();
            while (_config.hasNext()) {
                Rotor read = readRotor();
                if (allRotors.contains(read)) {
                    throw error("wrong configuration rotors");
                } else {
                    allRotors.add(read);
                }
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config.
     * E.g. I MQ      (AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S) */
    private Rotor readRotor() {
        try {
            nameRotor = _config.next();
            type = _config.next();
            perm = "";
            while (_config.hasNext("\\(.*\\)")) {
                perm = perm.concat(_config.next());
            }
            if (type.charAt(0) == 'M') {
                return new MovingRotor(nameRotor,
                        new Permutation(perm, _alphabet), type.substring(1));
            } else if (type.charAt(0) == 'N') {
                return new FixedRotor(nameRotor,
                        new Permutation(perm, _alphabet));
            } else if (type.charAt(0) == 'R') {
                return new Reflector(nameRotor,
                        new Permutation(perm, _alphabet));
            } else {
                return null;
            }
        } catch (NoSuchElementException excp) {
            System.out.println(excp);
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment.
     *  E.g. * B BETA III IV I AXLE (HQ) (EX) (IP) (TR) (BY)*/
    private void setUp(Machine M, String settings) {
        if (settings.charAt(0) != '*') {
            throw error("Wrong setting format");
        }
        String[] setting = settings.split(" ");
        if (setting.length - 1 < M.numRotors()) {
            throw new EnigmaException("Setting doesn't have enough arguments.");
        }
        String[] insertedRotors = new String[M.numRotors()];
        for (int i = 1; i < M.numRotors() + 1; i += 1) {
            insertedRotors[i - 1] = setting[i];
        }
        for (int i = 0; i < insertedRotors.length - 1; i += 1) {
            for (int j = 1; j < insertedRotors.length; j += 1) {
                if (insertedRotors[i].equals(insertedRotors[j]) && i != j) {
                    throw error("Repeating rotors.");
                }
            }
        }

        M.insertRotors(insertedRotors);
        M.setRotors(setting[M.numRotors() + 1]);
        String pairs = "";
        if (setting.length <= M.numRotors() + 2) {
            M.setPlugboard(new Permutation("", _alphabet));
            return;
        }
        for (int i = M.numRotors() + 2; i < setting.length; i += 1) {
            pairs = pairs + setting[i] + " ";
        }
        M.setPlugboard(new Permutation(pairs, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i += 5) {
            if (msg.length() - i <= 5) {
                _output.print(msg.substring(i));
            } else {
                _output.print(msg.substring(i, i + 5) + " ");
            }
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** A String containing each rotor's cycles. */
    private String perm;

    /** Name of rotor. */
    private String nameRotor;

    /** Name of rotor. */
    private String type;

    /** An ArrayList containing all available rotors. */
    private ArrayList<Rotor> allRotors = new ArrayList<>();
}
