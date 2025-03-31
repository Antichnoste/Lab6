package command;

import utility.Console;
import utility.ExecutionResponse;

/**
 * Команда для выхода из интерактивного режима без сохранения
 */
public class Exit extends Command {
    private Console console;

    /**
     * Конструктор
     * @param console консоль
     */
    public Exit(Console console) {
        super("exit","завершить программу (без сохранения в файл)");
        this.console = console;
    }

    /**
     * Выполнение команды
     * @param arguments массив с аргументами
     * @return результат выполения команды
     */
    @Override
    public ExecutionResponse apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new ExecutionResponse(false, "Неправильное кол-во аргументов \nИспользование: '" + getName() + "'");
        }
        return new ExecutionResponse("exit");
    }
}
