package utility;

import command.Command;
import managers.CommandManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс исполениния программы
 */
public class Runner {
    private Console console;
    private final CommandManager commandManager;
    private final List<String> scriptStack = new ArrayList<>();
    private int lengthRecursion = -1;

    private final int MAX_RECURSION_DEEP = 500;
    private final int MIN_RECURSION_DEEP = 0;


    /**
     * Конструктор
     * @param console консоль
     * @param commandManager манеджер команд
     */
    public Runner(Console console, CommandManager commandManager) {
        this.commandManager = commandManager;
        this.console = console;
    }

    /**
     * Интерактивный режим
     */
    public void interactiveMode(){
        try{
            ExecutionResponse commandStatus;
            String[] userCommand = {"", ""};

            console.println("Добро пожаловать!\nВведите help для вывода доступных команд");

            while(true){
                console.prompt();
                userCommand = (console.readln().trim() + " ").split(" ",2);
                userCommand[1] = userCommand[1].trim();

                if (!userCommand[0].isEmpty()){
                    commandManager.addToHistory(userCommand[0]);
                }

                commandStatus = launchCommand(userCommand);

                if (commandStatus.getMessage().equals("exit")){
                    break;
                }
                console.println(commandStatus.getMessage());
            }
        } catch (NoSuchElementException e){
            console.printError("Пользовательский ввод не обнаружен");
        } catch (IllegalStateException e){
            console.printError("Непредвиденная ошибка");
        }
    }

    /**
     * Функция загрузки команды
     * @param userCommand загружаемая команда
     * @return результат об успешности
     */
    private ExecutionResponse launchCommand(String[] userCommand){
        if(userCommand[0].isEmpty()){
            return new ExecutionResponse(false, "Команда '" + userCommand[0] + "' не найден. Введите 'help' для справки");
        }

        Command command = commandManager.getCommands().get(userCommand[0]);
        if (command == null){
            return new ExecutionResponse(false, "Команда '" + userCommand[0] + "' не найден. Введите 'help' для справки");
        }

        switch (userCommand[0]){
            case "execute_script" -> {
                ExecutionResponse tmp = commandManager.getCommands().get("execute_script").apply(userCommand);
                if (!tmp.getExitCode()){
                    return tmp;
                }
                ExecutionResponse tmp2 = scriptMode(userCommand[1]);
                return new ExecutionResponse(tmp2.getExitCode(), tmp.getMessage() + "\n" + tmp2.getMessage().trim());
            }
            default -> {
                return command.apply(userCommand);
            }
        }
    }

    /**
     * Запуск скрипта
     * @param args имя файла
     * @return сообщение об успешности
     */
    private ExecutionResponse scriptMode(String args){
        String[] userCommand = {"", ""};
        StringBuilder executionOutput  = new StringBuilder();

        if (!new File(args).exists()){
            return  new ExecutionResponse(false, "Файл не существует!");
        }
        if (!Files.isReadable(Paths.get(args))){
            return new ExecutionResponse(false, "Нет прав для чтения!");
        }

        scriptStack.add(args);
        try (FileInputStream fileInputStream = new FileInputStream(args);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
             Scanner scriptScanner = new Scanner(inputStreamReader)){

            ExecutionResponse commandStatus;

            if (!scriptScanner.hasNext()){
                throw new NoSuchElementException();
            }
            console.selectFileScanner(scriptScanner);

            do{
                userCommand = (console.readln().trim() + " ").split(" ",2);
                userCommand[1] = userCommand[1].trim();
                while (console.isCanReadln() && userCommand[0].isEmpty()){
                    userCommand = (console.readln().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                }

                executionOutput.append(console.getPrompt() + String.join(" ", userCommand) + "\n");
                boolean needLaunch = true;

                if (userCommand[0].equals("execute_script")){
                    needLaunch = checkRecursion(userCommand[1], scriptScanner);
                }

                commandStatus = needLaunch ? launchCommand(userCommand) : new ExecutionResponse(true, "Превышена максимальная глубина рекурсии");

                if (userCommand[0].equals("execute_script")){
                    console.selectFileScanner(scriptScanner);
                }
                executionOutput.append(commandStatus.getMessage() + "\n");
            } while(commandStatus.getExitCode() && !commandStatus.getMessage().equals("exit") && console.isCanReadln());

            console.selectConsoleScanner();

            if (!commandStatus.getExitCode() && !(userCommand[0].equals("execute_script") && !userCommand[1].isEmpty())){
                executionOutput.append("Проверьте скрипт на корректность введённых данных!\n");
            }

            return new ExecutionResponse(commandStatus.getExitCode(), executionOutput.toString());
        } catch (FileNotFoundException e){
            return new ExecutionResponse(false, "Файл со скриптом не найден!");
        } catch (NoSuchElementException e){
            return new ExecutionResponse(false, "Файл со скриптом пуст!");
        } catch (IllegalStateException | IOException e){
            console.printError("Непредвиденная ошибка!");
            System.exit(0);
        } finally {
            scriptStack.remove(scriptStack.size()-1);
        }

        return new ExecutionResponse("");
    }

    /**
     * Функция проверки скрипта на рекурсию
     *
     * @param args имя запускаемого скрипта
     * @param scriptScanner сканер скрипта
     * @return True если скрипт можно запускать
     */
    private boolean checkRecursion(String args, Scanner scriptScanner){
        int recStart = -1;
        int i = 0;

        for (String script : scriptStack){
            i++;
            if (args.equals(script)){
                if (recStart < 0){
                    recStart = i;
                }
                if (lengthRecursion < MIN_RECURSION_DEEP){
                    console.selectConsoleScanner();
                    console.println("Была замечена рекурсия! Введите максимальную глубину рекурсии(0...500)");
                    while(lengthRecursion < 0 || lengthRecursion > 500){
                        try{
                            console.print("> ");
                            lengthRecursion = Integer.parseInt(console.readln().trim());
                            if (lengthRecursion < MIN_RECURSION_DEEP || lengthRecursion > MAX_RECURSION_DEEP) {
                                console.println("длина не распознана");
                            }
                        } catch (NumberFormatException e){
                            console.println("длина не распознана");
                        }
                    }
                    console.selectConsoleScanner();
                }
                if (i > recStart + lengthRecursion || i > MAX_RECURSION_DEEP) {
                    return false;
                }
            }
        }
        return true;
    }
}
