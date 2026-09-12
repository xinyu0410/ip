package xue;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import xue.storage.Storage;
import xue.ui.Ui;

/** JavaFX user interface for Xue, backed by the reusable command processor. */
public class Main extends Application {
    private final CommandProcessor processor = new CommandProcessor(new Storage());
    private final TextArea conversation = new TextArea();
    private final TextField commandInput = new TextField();

    /** Builds and displays the main window. */
    @Override
    public void start(Stage stage) {
        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.appendText(Ui.WELCOME_MESSAGE + "\n\n");
        if (processor.getLoadError() != null) {
            conversation.appendText("Xue: OOPS!!! " + processor.getLoadError() + "\n\n");
        }
        commandInput.setPromptText("Enter a command");
        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> submitCommand());
        commandInput.setOnAction(event -> submitCommand());

        HBox inputRow = new HBox(8, commandInput, sendButton);
        BorderPane root = new BorderPane(conversation);
        root.setBottom(inputRow);
        BorderPane.setMargin(conversation, new Insets(8));

        stage.setTitle("Xue");
        stage.setScene(new Scene(root, 600, 450));
        stage.show();
    }

    /** Adds both the command and response to the conversation history. */
    private void submitCommand() {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        conversation.appendText("You: " + command + "\n");
        conversation.appendText("Xue: " + processor.process(command) + "\n\n");
        commandInput.clear();
    }
}
