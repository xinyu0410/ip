package xue.ui;

import java.util.Scanner;

/** Handles console input and shared user-facing messages. */
public class Ui {
    /** Divider printed between console interactions. */
    private static final String SEPARATOR = "________________________________________________________________________________";
    /** Reads commands entered by the user. */
    private final Scanner scanner = new Scanner(System.in);

    /** Displays Xue's welcome message. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println("██   ██  ██   ██  ███████\n"
                + " ██ ██   ██   ██  ██\n"
                + "  ███    ██   ██  █████\n"
                + " ██ ██   ██   ██  ██\n"
                + "██   ██   █████   ███████");
        System.out.println("Hello, I'm Xue. Try not to make this difficult.");
        System.out.println("What do you want? I have work to do.");
    }

    /** Reads the next command, or returns null at end of input. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Displays the standard divider. */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Displays an error message. */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }
}
