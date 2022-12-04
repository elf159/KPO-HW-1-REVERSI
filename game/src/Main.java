import java.util.Objects;
import java.util.Scanner;

public class Main {
    /**
     * // чей сейчас ход, true - первый, false - второй
     */
    static Boolean turn = false;
    /**
     * лучший результат
     */
    static int result = 0;
    /**
     * поле.
     */
    static Field field = new Field();

    /**
     * Метод main
     */
    public static void main(String[] args) {
        String playAgain = "1";
        while (playAgain.equals("1")) {
            System.out.println("\nВыберите режим игры в Реверси:\n" +
                    "1 - Игра с ботом\n" +
                    "2 - Игра против игрока\n" +
                    "3 - Вывод лучшего результата за сессию\n");
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            if (Objects.equals(input, "1")) {
                System.out.println("Выбрана игра против бота.");
                PvE();
            } else if (Objects.equals(input, "2")) {
                System.out.println("Выбрана игра против игрока");
                PvP();
            } else {
                printResult();
            }
            System.out.println("Вернуться в главное меню?\n" +
                    "1 - да\n" +
                    "любая другая строка - завершить работу программы");
            playAgain = in.nextLine();
        }
    }
    /**
     * Режим игры - игрок против игрока, вызывает другие игровые методы.
     */
    public static void PvP() {
        turn = !turn;
        while (!winCondition()) {
            Field.printPossibleTurns(turn);
            Field.saveField();
            var turns = Field.possibleTurns(turn);
            if (turns.size() == 0) {
                System.out.println("Нет доступных ходов, игрок пропускает ход.\n");
                turn = !turn;
                continue;
            } else {
                Field.printPossibleTurns(turns);
            }
            var coordinates = Field.inputXY(turns);
            field.addCell(turn, coordinates[0], coordinates[1]);
            Field.printScore();
            turn = !turn;
            badDecision(true);
        }
        System.out.println("Игра закончилась! ");
        Field.printField(Field.getField());
        if (Field.getFirstCount() < Field.getSecondCount()) {
            System.out.println("Второй победил.");
            result = Field.getFirstCount();
        } else {
            System.out.println("Первый победил.");
            result = Field.getSecondCount();
        }
    }

    /**
     * @return boolean значение - окончилась игра или еще нет
     */
    public static boolean winCondition() {
        if ((Field.possibleTurns(true).size() == 0 && Field.possibleTurns(false).size() == 0) || Field.getSecondCount() <= 0 || Field.getFirstCount() <= 0) {
            return true;
        }
        return false;
    }
    /**
     * Спрашивает пользователя о желании откать ход(ы), вызывает другие методы.
     * @param pvp вызван из режима игрок против игрока или игрок против компьютера
     */
    public static void badDecision(boolean pvp) {
        System.out.println("Сделали ход случайно? Введите '1', если да. Если нет, то введите любую другую строку.");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        if (Objects.equals(input, "1")) {
            System.out.println("На сколько назад хотите вернуться?");
            int turnBack = 1;
            boolean flag = true;
            while (flag) {
                input = in.nextLine();
                while(!tryParseInt(input)) {
                    System.out.println("Вводить можно только числа!");
                    input = in.nextLine();
                }
                turnBack = Integer.parseInt(input);
                if (turnBack <= Field.getSizeOfHistory()) {
                    flag = false;
                } else {
                    System.out.println("На данное количество ходов вернуться невозможно. Введите количество заного!");
                }
            }
            Field.returnMove(turnBack);
            if (pvp) {
                if (turnBack % 2 != 0)
                    turn = !turn;
            } else {
                turn = false;
            }
        }
    }

    /**
     * Смотрит, можно ли преобразовать строку в число
     * @param value число, которое нужно преобразовать
     * @return boolean значение - можно ли преобразовать строку в число
     */
    public static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Даёт ввести число в виде строки
     * @return введенное пользователем число в виде int
     */
    public static int inputInt() {
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        while (!Main.tryParseInt(input)) {
            System.out.println("Вводить только числа!");
            input = in.nextLine();
        }
        return Integer.parseInt(input);
    }

    /**
     * режим игры игрок против бота.
     */
    public static void PvE() {
        turn = !turn;
        while(!winCondition()) {
            Field.printScore();
            Field.printPossibleTurns(turn);
            Field.saveField();
            var posTurns = Field.possibleTurns(turn);
            if (posTurns.size() != 0) {
                Field.printPossibleTurns(posTurns);
                var coordinates = Field.inputXY(posTurns);
                field.addCell(turn, coordinates[0], coordinates[1]);
                System.out.println("Вы сделали ход:");
                Field.printField(Field.getField());
                Field.printScore();
            } else {
                System.out.println("Нет доступных ходов, вы пропускаете ход.\n");
            }
            turn = !turn;
            BotTurn();
            turn = !turn;
            if (posTurns.size() != 0) {
                badDecision(false);
            }
        }
        System.out.println("Игра закончилась! ");
        Field.printField(Field.getField());
        if (Field.getSecondCount() > Field.getFirstCount()) {
            System.out.println("Второй победил.");
            result = Field.getSecondCount();
        } else {
            System.out.println("Первый победил.");
            result = Field.getFirstCount();
        }
    }

    /**
     * Бот ходит
     */
    public static void BotTurn() {
        var posTurns = Field.possibleTurns(turn);
        if (posTurns.size() == 0) {
            System.out.println("У компьютера нет ходов!");
            return;
        }
        double bestTurnSum = 0;
        int x = posTurns.get(0)[0];
        int y = posTurns.get(0)[1];
        for (int[] posTurn : posTurns) {
            double sum = 0;
            sum += Field.evaluateTurn(posTurn[0], posTurn[1]);
            sum += Field.turnScore(posTurn[0], posTurn[1], turn);
            if (sum >= bestTurnSum) {
                x = posTurn[0];
                y = posTurn[1];
                bestTurnSum = sum;
            }
        }
        field.addCell(turn, x,y);
    }
    /**
     * Вывод лучшего результата
     */
    private static void printResult() {
        if (result == 0) {
            System.out.println("Вы еще не сыграли ни одной игры.");
        } else {
            System.out.println("Лучший результат: " + result);
        }
    }
}