package ru.yandex.practicum;

import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Уфффф!");

        TaskManager taskManager = Managers.getDefault();
        testProgram(taskManager);
    }

    private static void testProgram(TaskManager taskManager) {
        System.out.println("Метод ушёл на перекур.");
    }
}

    /*
     * Привет, Эркин 😀
     *
     * Как бы я не старался всё проверить, но я все равно налажал.
     *
     * В методе calculateEpicTime(Epic epic, Subtask subtask) я не предусмотрел ситуацию, когда subtask у Эпика
     * самый первый. Если subtask у Эпика самый первый, то его время сразу же нужно присваивать Эпику,
     * а не проверять его через isBefore и isAfter. Так как я этого не сделал, то время Эпика могло не измениться
     * при его расчётах.
     *
     * Например, я создаю Эпик с startTime «NOW», endTime +10 дней, а потом создаю его первую подзадачу –
     * с startTime +1 день, duration 2 дня. И в этот момент произойдет печальное событие – подзадача у Эпика появилась,
     * а время Эпика не поменяется :(
     *
     * Но есть хорошая новость, – я все исправил! :))) Я ведь обещал тебе, поэтому это было для меня очень важно.
     * Также я написал еще один тест, который прорабатывает эту ситуацию. Так что можешь посмотреть ☺
     *
     * Для твоего удобства я отметил строчки кода комментарием ТОДО – и закладки ещё там поставил. Они находятся в
     * InMemoryTaskManager.calculateEpicTime() и в TaskManagerTest - 657 строчка.
     *
     * Спасибо, что указал мне на эту ошибку. Я не понимаю, как ты это так быстро делаешь.
     * Я тысячу раз всё перепроверял, но так и не увидел этот косяк, а ты раз и нашёл за 5 сек)))
     */