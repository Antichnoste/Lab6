import command.*;
import managers.Ask;
import managers.CollectionManager;
import managers.CommandManager;
import managers.DumpManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.Runner;
import utility.StandartConsole;

import java.io.IOException;


/**
 * Главный класс для серверного приложения
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Ask.AskBreak, IOException {
        StandartConsole console = new StandartConsole();

        String fileName = System.getenv("FILE_NAME");

        if (fileName == null) {
            System.out.println("Переменная окружения FILE_NAME не задана");
            System.exit(1);
        }

        DumpManager dumpManager = new DumpManager(fileName, console);
        CollectionManager collectionManager = new CollectionManager(dumpManager);

        if (!collectionManager.loadCollection()){
            System.exit(1);
        }

        CommandManager commandManager = new CommandManager() {
            {
            register("add", new Add(console, collectionManager)); //OK
            register("help", new Help(console, this)); //OK
            register("exit", new Exit(console)); //OK
            register("average_of_oscars_count", new AverageOfOscarsCount(console, collectionManager));
            register("clear", new Clear(console, collectionManager)); //OK
            register("execute_script", new ExecuteScript(console)); //OK
            register("filter_contains_name", new FilterContainsName(console, collectionManager));
            register("history", new History(console, this));// OK
            register("filter_starts_with_tagline", new FilterStartsWithTagline(console, collectionManager));
            register("info", new Info(console, collectionManager)); // OK
            register("remove_by_id", new RemoveById(console,collectionManager )); // OK
            register("remove_first", new RemoveFirst(console, collectionManager));
            register("remove_greater", new RemoveGreater(console, collectionManager));
            register("save", new Save(console, collectionManager)); // OK
            register("show", new Show(console, collectionManager)); // OK
            register("update", new UpdateID(console, collectionManager));
        }
        };


        new Runner(console, commandManager).interactiveMode();
    }
}