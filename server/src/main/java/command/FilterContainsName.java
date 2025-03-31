package command;

import managers.CollectionManager;
import models.Movie;
import utility.Console;
import utility.ExecutionResponse;

public class FilterContainsName extends Command {
    private final Console console;
    private final CollectionManager manager;

    /**
     * Конструктор
     * @param console консоль
     * @param manager менеджер коллекции
     */
    public FilterContainsName(Console console, CollectionManager manager) {
        super("filter_contains_name name", "вывести элементы, значение поля name которых содержит заданную подстроку");
        this.console = console;
        this.manager = manager;
    }

    /**
     * Исполнение команды
     * @param arguments массив с аргументами
     * @return результат выполнения команды
     */
    @Override
    public ExecutionResponse apply(String[] arguments) {
        StringBuilder elements = new StringBuilder();

        for (Movie movie : manager.getCollection()){
            if (movie.getName().contains(arguments[1])){
                elements.append(" " + movie.toString() + "\n");
            }
        }

        return new ExecutionResponse(elements.toString());
    }
}
