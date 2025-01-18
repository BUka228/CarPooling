@echo off
chcp 65001 > nul


REM Указываем путь к конфигурационному файлу
set CONFIG_FILE=src\main\resources\environment.yaml

REM Запускаем приложение с указанием конфигурационного файла и кодировки UTF-8
java -Dconfig.file=%CONFIG_FILE% -jar target\CarPooling-1.0-SNAPSHOT-jar-with-dependencies.jar

pause