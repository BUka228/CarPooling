package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "setStorage", description = "Выбор типа хранилища для данных (ТРЕБУЕТ ПЕРЕЗАПУСКА)")
public class SetStorageCommand implements Runnable {
    @Option(names = {"-t", "--type"}, description = "Тип хранилища (XML, CSV, MONGO, POSTGRES)", required = true)
    private String storageType;

    @Override
    public void run() {
        try {
            CliContext.StorageType oldType = CliContext.getCurrentStorageType();
            CliContext.StorageType newType = CliContext.StorageType.valueOf(storageType.toUpperCase());

            if (newType == oldType) {
                System.out.println("Тип хранилища уже установлен: " + newType);
                return;
            }

            CliContext.setCurrentStorageType(newType);

            System.out.println("Тип хранилища установлен на: " + newType);
            System.out.println("********************************************************************");
            System.out.println("!!! ВАЖНО: Пожалуйста, ПЕРЕЗАПУСТИТЕ приложение (выйдите и ");
            System.out.println("!!!         запустите java -jar ... снова), чтобы изменения ");
            System.out.println("!!!         вступили в силу для всех операций с данными.");
            System.out.println("********************************************************************");

        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: Неверный тип хранилища '" + storageType + "'.");
            System.err.println("Допустимые значения: XML, CSV, MONGO, POSTGRES (регистр не важен).");
        } catch (Exception e) {
            System.err.println("Произошла ошибка при установке типа хранилища: " + e.getMessage());
            e.printStackTrace();
        }
    }
}