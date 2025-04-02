package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext; // Нужен для констант
import picocli.CommandLine.Command;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static com.carpooling.constants.Constants.PREF_NODE_NAME; // Импортируем имя узла

@Command(name = "resetPrefs", description = "Сброс сохраненных настроек приложения (тип хранилища, ID пользователя)")
public class ResetPreferencesCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Попытка сброса настроек приложения...");
        try {
            Preferences prefs = Preferences.userRoot().node(PREF_NODE_NAME);

            // Проверяем, существует ли узел вообще
            if (!prefs.nodeExists("")) { // Проверка существования самого узла
                System.out.println("Узел настроек '" + PREF_NODE_NAME + "' не найден. Нечего сбрасывать.");
                return;
            }

            System.out.println("Удаление узла настроек: " + PREF_NODE_NAME);
            prefs.removeNode(); // Удаляем весь узел и все его содержимое
            prefs.flush();      // Сохраняем изменения (удаление узла)

            System.out.println("Настройки успешно сброшены.");
            System.out.println("Пожалуйста, перезапустите приложение, чтобы использовать настройки по умолчанию.");

            // Опционально: Можно попытаться обновить статические поля CliContext на дефолтные,
            // но это не повлияет на уже созданные синглтоны сервисов.
            // CliContext.resetToDefaults(); // Если бы был такой метод

        } catch (BackingStoreException e) {
            System.err.println("Ошибка при сбросе настроек: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // Может возникнуть, если узел уже удален другим процессом
            System.err.println("Ошибка состояния при доступе к настройкам (возможно, уже удалены): " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("Ошибка безопасности при доступе к настройкам: " + e.getMessage());
        }
    }
}