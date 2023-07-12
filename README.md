# java-kanban

## Описание проекта
Приложение - трекер задач. С помощью трекера задач можно создавать простые задачи, эпик задачи, подзадачи – 
и работать с ними. 

Задача состоит из имени, описания, статуса («NEW», «IN PROGRESS», «DONE»), времени начала и – длительности.
Task задачи являются одиночными, а Epic задачи - составными, так как для Epic задач назначаются подзадачи - Subtask.
Start time, End time и статус Эпик задачи рассчитываются исходя из данных её подзадач.

В приложении есть 4 менеджера задач:
1. InMemoryTaskManager - содержит основную логику работы с задачами. Хранит данные в оперативной памяти.
   От InMemoryTaskManager наследуются FileBackedTaskManager и HTTPTaskManage. 
2. FileBackedTaskManager - расширяет функциональность InMemoryTaskManager тем, что сохраняет данные в csv файл 
   и загружает данные из него – при следующем запуске приложения.
3. HTTPTaskManager - сохраняет данные через HTTP запросы на импровизированном сервере (KVServer) и загружает 
   данные из него.
4. InMemoryHistoryManager - работает с историей просмотренных задач. 
   В менеджере истории реализована работа с кастомным двусвязным списком.

## Инструкция по развёртыванию и системные требования.
JDK 17 и выше. Добавить в Project structure -> Libraries библиотеку Gson 2.9.0 и Junit 5.7.0
