package xue.model;

import xue.XueException;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task in Xue's task list.
 */
public class Task {
    private final String type;
    private final String description;
    private final String from;
    private final String to;
    private final LocalDateTime fromDateTime;
    private final LocalDateTime toDateTime;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this("T", description, null, null);
    }

    /** Creates a dated task with the supplied display details. */
    public Task(String type, String description, String from, String to) {
        if (description == null || description.trim().isEmpty()) {
            throw new XueException("The description of a todo cannot be empty. I cannot read your mind!");
        }
        this.type = type;
        this.description = description;
        this.from = from;
        this.to = to;
        this.fromDateTime = parseDateTime(from);
        this.toDateTime = parseDateTime(to);
        this.isDone = false;
    }

    /** Creates a task while restoring its saved completion status. */
    public Task(String type, String description, String from, String to, boolean isDone) {
        this(type, description, from, to);
        this.isDone = isDone;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /** Returns the one-letter task type shown in the list. */
    public String getType() {
        return type;
    }

    /** Returns the start time of an event, if applicable. */
    public String getFrom() {
        return from;
    }

    /** Returns the end time of an event or deadline, if applicable. */
    public String getTo() {
        return to;
    }

    /** Returns whether this task is complete. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the optional date/time suffix shown in the list. */
    public String getDateTimeDescription() {
        if ("D".equals(type)) {
            return " (by: " + formatDateTime(to, toDateTime) + ")";
        }
        if ("E".equals(type)) {
            return " (from: " + formatDateTime(from, fromDateTime) + " to: "
                    + formatDateTime(to, toDateTime) + ")";
        }
        return "";
    }

    /** Returns the parsed deadline or event start, when the input used a numeric date. */
    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    /** Returns the parsed deadline or event end, when the input used a numeric date. */
    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    private static LocalDateTime parseDateTime(String value) {
        if (value == null || !value.matches("\\d{4}-\\d{2}-\\d{2}( \\d{4})?")) {
            return null;
        }
        try {
            return value.length() == 10
                    ? LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
                    : LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
        } catch (DateTimeParseException e) {
            throw new XueException("That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.");
        }
    }

    private static String formatDateTime(String original, LocalDateTime parsed) {
        if (parsed == null) {
            return original;
        }
        String date = parsed.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        return original.length() > 10
                ? date + " " + parsed.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
                        .toUpperCase(Locale.ENGLISH)
                : date;
    }

}
