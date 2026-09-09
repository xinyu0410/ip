package xue;

import org.junit.jupiter.api.Test;
import xue.storage.Storage;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests command processing independently from console and GUI presentation. */
class CommandProcessorTest {
    private CommandProcessor createProcessor() throws Exception {
        Path directory = Files.createTempDirectory("xue-test");
        return new CommandProcessor(new Storage(directory.resolve("duke.txt")));
    }

    @Test
    void process_todo_returnsAddedTask() throws Exception {
        assertTrue(createProcessor().process("todo learn JavaFX").contains("learn JavaFX"));
    }

    @Test
    void process_deadlineAndEvent_returnsDateDetails() throws Exception {
        CommandProcessor processor = createProcessor();
        assertTrue(processor.process("deadline submit report /by Friday").contains("(by: Friday)"));
        assertTrue(processor.process("event meeting /from 2pm /to 3pm").contains("from: 2pm to: 3pm"));
    }

    @Test
    void process_listAndFind_returnsExpectedTasks() throws Exception {
        CommandProcessor processor = createProcessor();
        processor.process("todo read book");
        processor.process("todo buy milk");
        assertTrue(processor.process("list").contains("1.[T][ ] read book"));
        assertTrue(processor.process("find BOOK").contains("1.[T][ ] read book"));
    }

    @Test
    void process_markAndUnmark_updatesTaskStatus() throws Exception {
        CommandProcessor processor = createProcessor();
        processor.process("todo read book");
        assertTrue(processor.process("mark 1").contains("[X] read book"));
        assertTrue(processor.process("unmark 1").contains("[ ] read book"));
    }

    @Test
    void process_delete_removesTask() throws Exception {
        CommandProcessor processor = createProcessor();
        processor.process("todo remove me");
        assertTrue(processor.process("delete 1").contains("Now you have 0 tasks"));
        assertTrue(processor.process("list").endsWith(":\n"));
    }

    @Test
    void process_invalidCommand_returnsError() throws Exception {
        assertTrue(createProcessor().process("unknown").startsWith("OOPS!!!"));
    }

    @Test
    void processor_persistsTasksAcrossInstances() throws Exception {
        Path file = Files.createTempDirectory("xue-test").resolve("duke.txt");
        new CommandProcessor(new Storage(file)).process("todo saved task");
        CommandProcessor restored = new CommandProcessor(new Storage(file));
        assertTrue(restored.process("list").contains("saved task"));
        assertNull(restored.getLoadError());
    }

    @Test
    void processor_exposesStorageLoadError() throws Exception {
        Path directory = Files.createTempDirectory("xue-test");
        CommandProcessor processor = new CommandProcessor(new Storage(directory));
        assertEquals("I could not read your saved tasks.", processor.getLoadError());
    }
}
