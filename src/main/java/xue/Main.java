package xue;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import xue.storage.Storage;
import xue.ui.Ui;

/** JavaFX user interface for Xue, backed by the reusable command processor. */
public class Main extends Application {
    private final CommandProcessor processor = new CommandProcessor(new Storage());
    private final VBox conversation = new VBox(8);
    /** Multiline command editor; Enter submits while Shift+Enter inserts a newline. */
    private final TextArea commandInput = new TextArea();
    private final ScrollPane chatScrollPane = new ScrollPane(conversation);

    /** Builds and displays the main window. */
    @Override
    public void start(Stage stage) {
        conversation.setFillWidth(true);
        addXueMessage(Ui.WELCOME_MESSAGE);
        if (processor.getLoadError() != null) {
            addXueMessage("OOPS!!! " + processor.getLoadError());
        }
        commandInput.setPromptText("Type your message…");
        commandInput.setWrapText(true);
        commandInput.setPrefRowCount(1);
        commandInput.setMinHeight(42);
        commandInput.setMaxHeight(100);
        commandInput.setStyle("-fx-background-color: white; -fx-background-radius: 20;"
                + " -fx-border-color: #b7cad5; -fx-border-radius: 20; -fx-padding: 8 14 8 14;"
                + " -fx-text-fill: #203040; -fx-prompt-text-fill: #6b7c86;");
        Button sendButton = new Button("Send");
        sendButton.setMinWidth(72);
        sendButton.setMinHeight(40);
        sendButton.setStyle("-fx-background-color: #4f8fc4; -fx-text-fill: white;"
                + " -fx-background-radius: 18; -fx-font-weight: bold;");
        sendButton.setOnAction(event -> submitCommand());
        commandInput.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                submitCommand();
                event.consume();
            }
        });

        HBox inputRow = new HBox(8, commandInput, sendButton);
        inputRow.setAlignment(Pos.CENTER);
        inputRow.setPadding(new Insets(10, 14, 12, 14));
        inputRow.setStyle("-fx-background-color: rgba(255,255,255,0.96);"
                + " -fx-border-color: #c6d7df; -fx-border-width: 1 0 0 0;");
        HBox.setHgrow(commandInput, Priority.ALWAYS);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        conversation.setPadding(new Insets(8));
        conversation.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> chatScrollPane.setVvalue(1.0)));

        StackPane chatArea = new StackPane();
        chatArea.setStyle("-fx-background-color: #e9f3f7;");
        ImageView background = new ImageView();
        var imageStream = getClass().getResourceAsStream("/images/xue-mascot.png");
        if (imageStream != null) {
            background.setImage(new Image(imageStream));
        }
        background.setOpacity(0.24);
        background.setPreserveRatio(false);
        background.setMouseTransparent(true);
        background.fitWidthProperty().bind(chatArea.widthProperty());
        background.fitHeightProperty().bind(chatArea.heightProperty());
        chatArea.getChildren().addAll(background, chatScrollPane);
        BorderPane root = new BorderPane(chatArea);
        root.setBottom(inputRow);

        stage.setTitle("Xue");
        stage.setScene(new Scene(root, 600, 450));
        stage.show();
        commandInput.requestFocus();
    }

    /** Adds both the command and response to the conversation history. */
    private void submitCommand() {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        addUserMessage(command);
        addXueMessage(processor.process(command));
        commandInput.clear();
    }

    /** Adds a right-aligned user message bubble. */
    private void addUserMessage(String message) {
        conversation.getChildren().add(createMessageBubble(message, true));
    }

    /** Adds a left-aligned Xue message bubble. */
    private void addXueMessage(String message) {
        conversation.getChildren().add(createMessageBubble(message, false));
    }

    /** Creates a readable, wrapping message bubble with the requested alignment. */
    private HBox createMessageBubble(String message, boolean isUserMessage) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(420);
        label.setStyle(isUserMessage
                ? "-fx-background-color: #4f8fc4; -fx-text-fill: white;"
                + " -fx-background-radius: 16; -fx-padding: 9 13 9 13;"
                : "-fx-background-color: rgba(255,255,255,0.92); -fx-text-fill: #203040;"
                + " -fx-background-radius: 16; -fx-padding: 9 13 9 13;");
        HBox bubble = new HBox(label);
        bubble.setMaxWidth(Double.MAX_VALUE);
        bubble.setAlignment(isUserMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        VBox.setVgrow(bubble, Priority.NEVER);
        return bubble;
    }
}
