import java.util.Scanner;

/**
 * The main entry point for Xue.
 */
public class Xue {
    /**
     * Starts Xue and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String separator = "________________________________________________________________________________";
        String banner = "██   ██  ██   ██  ███████\n"
                + " ██ ██   ██   ██  ██\n"
                + "  ███    ██   ██  █████\n"
                + " ██ ██   ██   ██  ██\n"
                + "██   ██   █████   ███████";
        String[] todo = new String[100];
        int counter = 0;

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Hello! I'm Xue.");
        System.out.println("What can I do for you?");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            if (command.equals("list")) {
                for (int i = 0; i < counter; i++) {
                    System.out.println((i + 1) + ". " + todo[i]);
                }
                System.out.println(separator);
                continue;
            }

            todo[counter] = command;
            counter++;
            System.out.println("added: " + command);
            System.out.println(separator);
        }
    }
}
