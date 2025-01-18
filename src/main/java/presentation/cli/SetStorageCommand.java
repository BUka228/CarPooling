package presentation.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import presentation.context.CliContext;

@Command(name = "setStorage", description = "Выбор типа хранилища для данных")
public class SetStorageCommand implements Runnable {
    @Option(names = {"-t", "--type"}, description = "Тип хранилища (XML, CSV, MONGO, POSTGRES)", required = true)
    private String storageType;

    @Override
    public void run() {
        try {
            CliContext.StorageType type = CliContext.StorageType.valueOf(storageType.toUpperCase());
            CliContext.setCurrentStorageType(type);
            System.out.println("Тип хранилища установлен: " + type);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: Неверный тип хранилища. Допустимые значения: XML, CSV, MONGO, POSTGRES.");
        }
    }
}