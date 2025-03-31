package command;

import managers.CollectionManager;
import models.Movie;
import utility.Console;
import utility.ExecutionResponse;

/**
 * Команда по выводу всех фильмов, у которых слоган начинается с определённой строки
 */
public class FilterStartsWithTagline extends Command {
    private final Console console;
    private final CollectionManager manager;

    /**
     * Конструктор
     * @param console консоль
     * @param collectionManager менеджер коллекции
     */
    public FilterStartsWithTagline(Console console, CollectionManager collectionManager) {
        super("filter_starts_with_tagline tagline", "вывести элементы, значение поля tagline которых начинается с заданной подстроки");
        this.console = console;
        this.manager = collectionManager;
    }


    /**
     * Исполнение команды
     *
     * @param arguments массив с аргументами
     * @return результат выполнения команды
     */
    @Override
    public ExecutionResponse apply(String[] arguments) {
        StringBuilder correctMovie = new StringBuilder();

        for (Movie movie : manager.getCollection()){
            if (movie.getTagline().startsWith(arguments[1])){
                correctMovie.append(" " + movie.toString() + "\n");
            }
        }

        return new ExecutionResponse(correctMovie.toString());
    }
}
