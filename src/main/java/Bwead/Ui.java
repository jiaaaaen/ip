package Bwead;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles input commands from the system and parses it using Parsers,
 * then provides outputs back to the user.
 */
public class Ui {

    private Scanner scanner;
    private TaskList taskList;
    private History history;

    /**
     * sets a reference to History and TaskList.
     *
     * @param taskList taskList with all the current tasks.
     * @param history handles saving the tasks locally.
     */
    public void set(History history, TaskList taskList) {
        this.history = history;
        this.taskList = taskList;
    }

    /**
     * Prints the error message when loading.
     *
     * @param msg error message.
     */
    public void showLoadingError(String msg) {
        System.out.println(msg);
    }

    /**
     * Handles the command inputs by parsing it using a Parser.
     *
     * @param input string command.
     * @return String output to be returned.
     * @throws IOException when a failure occurs while performing scanning or write operations.
     */
    public String handleCommands(String input) throws IOException {
        Parser parser = new Parser(input);
        try {
            if (parser.isOneWord()) {
                if (input.equals("todo")) {
                    throw new BweadException("OOPS!!! The description of a todo cannot be empty.");
                } else if (input.equals("deadline")) {
                    throw new BweadException("OOPS!!! The description of a deadline cannot be empty.");
                } else if (input.equals("event")) {
                    throw new BweadException("OOPS!!! The description of an event cannot be empty.");
                } else if (!input.equals("bye") && !input.equals("list")) {
                    throw new BweadException("i don't know what that means :(");
                }
            } else {
                if (parser.isInvalidMultiWord()) {
                    throw new BweadException("i don't know what that means");
                }
            }
        } catch (BweadException e) {
            return e.getMessage();
        }
        try {
            if (input.equals("bye")) {
                return ("Bye. Hope to see you again soon!");
            } else if (input.equals("list")) {
                return taskList.printlist();
            } else if (input.startsWith("mark")) {
                return handleMark(parser);
            } else if (input.startsWith("unmark")) {
                return handleUnmark(parser);
            } else if (input.startsWith("todo ")) {
                return handleTodo(parser);
            } else if (input.startsWith("deadline ")) {
                return handleDeadline(parser);
            } else if (input.startsWith("event ")) {
                return handleEvent(parser);
            } else if (input.startsWith("delete")) {
                return handleDelete(parser);
            } else if (input.startsWith("find")) {
                return handleFind(parser);
            }
        } catch (BweadException e) {
            return (e.getMessage());
        }
        return "";
    }

    /**
     * Handles a mark command.
     *
     * @param parser
     * @return string to return to user.
     * @throws IOException
     */
    public String handleMark(Parser parser) throws IOException {
        int toadd = parser.getTaskToMark();
        Task task = taskList.getCurrentList().get(toadd - 1);
        task.setDone(true);
        history.updateFile(taskList.getCurrentList());
        return ("Nice! I've marked this task as done: " + task.getText());
    }

    /**
     * Handles an unmark command.
     *
     * @param parser
     * @return string to return to user.
     * @throws IOException
     */
    public String handleUnmark(Parser parser) throws IOException {
        int toadd = parser.getTaskToMark();
        Task task = taskList.getCurrentList().get(toadd - 1);
        task.setDone(false);
        history.updateFile(taskList.getCurrentList());
        return ("OK, I've marked this task as not done yet: " + task.getText());
    }

    /**
     * Handles a todo command by adding the todo task.
     *
     * @param parser
     * @return string to return to user.
     * @throws IOException
     */
    public String handleTodo(Parser parser) throws IOException {
        String todoName = parser.getTodoName();
        Todo task = new Todo(todoName);
        taskList.getCurrentList().add(task);
        history.updateFile(taskList.getCurrentList());
        return ("Got it. I've added this task: " + task.toString() + "\n" + "Now you have "
                + taskList.getCurrentList().size() + " tasks in the list.");
    }

    /**
     * Handles a deadline command by adding the deadline task.
     *
     * @param parser
     * @return string to return to user.
     * @throws IOException
     */
    public String handleDeadline(Parser parser) throws IOException {
        Deadline task = new Deadline(parser.getDeadlineName(), parser.getDeadlineDate(),
                parser.getDeadlineTime());
        taskList.getCurrentList().add(task);
        history.updateFile(taskList.getCurrentList());
        return ("Got it. I've added this task: " + task.toString() + "\n" + "Now you have "
                + taskList.getCurrentList().size() + " tasks in the list.");
    }

    /**
     * Handles an event command by adding the event task.
     *
     * @param parser
     * @return string to return to user.
     * @throws IOException
     */
    public String handleEvent(Parser parser) throws IOException {
        Event task = new Event(parser.getEventName(), parser.getEventStart(), parser.getEventEnd(),
                parser.getEventStartTime(), parser.getEventEndTime());
        taskList.getCurrentList().add(task);
        history.updateFile(taskList.getCurrentList());
        return ("Got it. I've added this task: " + task.toString() + "\n" + "Now you have "
                + taskList.getCurrentList().size() + " tasks in the list.");
    }

    /**
     * Handles a delete command by deleting the task.
     *
     * @param parser
     * @return string to return to user.
     * @throws IOException
     */
    public String handleDelete(Parser parser) throws IOException {
        Task toremove = taskList.getCurrentList().get(parser.getDeleteIndex() - 1);
        taskList.getCurrentList().remove(toremove);
        history.updateFile(taskList.getCurrentList());
        return ("Noted. I've removed this task: " + toremove.toString() + "\n" + "Now you have "
                + taskList.getCurrentList().size() + " tasks in the list.");
    }

    /**
     * Handles a find command by searching the list.
     *
     * @param parser
     * @return string to return to user.
     */
    public String handleFind(Parser parser) {
        String keyword = parser.getKeyword();
        ArrayList<String> matches = new ArrayList<>();
        for (int i = 0; i < taskList.getCurrentList().size(); i++) {
            if (taskList.getCurrentList().get(i).getName().contains(keyword)) {
                matches.add(taskList.getCurrentList().get(i).toString());
            }
        }
        if (matches.size() == 0) {
            throw new BweadException("no matching tasks found");
        }
        String matchingTasksString = "Here are the matching tasks in your list:" + "\n";
        for (int i = 0; i < matches.size(); i++) {
            matchingTasksString = matchingTasksString + (i + 1) + "." + matches.get(i) + "\n";
        }
        return matchingTasksString;
    }
}

