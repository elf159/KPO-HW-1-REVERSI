import java.util.ArrayList;

public class Field {
    private static ZeroCell[][] field = new ZeroCell[8][8];

    public static ZeroCell[][] getField() {
        return field;
    }

    private static ArrayList<ZeroCell[][]> history = new ArrayList<>();
    private static int firstCount = 2;
    private static int secondCount = 2;

    public static int getSizeOfHistory() {
        return history.size();
    }

    public static int getFirstCount() {
        return firstCount;
    }

    public static int getSecondCount() {
        return secondCount;
    }

    public Field() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                field[i][j] = new ZeroCell();
            }
        }
        field[3][3] = new Cell(true);
        field[3][4] = new Cell(false);
        field[4][4] = new Cell(true);
        field[4][3] = new Cell(false);
    }
    public static void saveField() {
        var arr = copyField();
        history.add(arr);
    }

    private static ZeroCell[][] copyField() {
        ZeroCell[][] arr = new ZeroCell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (field[i][j] instanceof Cell cell) {
                    arr[i][j] = new Cell(cell);
                } else {
                    arr[i][j] = new ZeroCell();
                }
            }
        }
        return arr;
    }
    public static void printField(ZeroCell[][] field) {
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        for (int i = 0; i < 8; i++) {
            System.out.print(i + "  ");
            for (int j = 0; j < 8; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    public static void printPossibleTurns(boolean turn) {
        var matrix = copyField();
        var potenTurns = possibleTurns(turn);
        for (int[] potenTurn : potenTurns) {
            matrix[potenTurn[1]][potenTurn[0]].str("+");
        }
        printField(matrix);
    }

    public static void returnMove(int key) {
        field = history.get(history.size() - key);
        var score = getScore();
        firstCount = score[0];
        secondCount = score[1];
        while (key != 0) {
            history.remove(history.size() - 1);
            key--;
        }
    }

    public static int[] inputXY(ArrayList<int[]> turns) {
        boolean flag = true;
        int x = 0, y = 0;
        while (flag) {
            System.out.println("Введите x, который желаете поставить от 0 до 7");
            x = Main.inputInt();
            System.out.println("Введите y, который желаете поставить от 0 до 7");
            y = Main.inputInt();
            if (field[y][x] instanceof Cell || !checkPossibleTurns(x, y, turns) || x > 7 || y > 7 || x < 0 || y < 0) {
                System.out.println("Уже была эта позиция или вы указали некорретные координаты! Введите другие координаты.");
            } else {
                flag = false;
            }
        }
        return new int[]{x, y};
    }

    public void addCell(Boolean player, int x, int y) {
        Cell cell = new Cell(player);
        field[y][x] = cell;
        boardScore(cell, y, x);
        System.out.println("Клетка успешно добавлена.");
        if (player)
            firstCount++;
        else
            secondCount++;
    }

    private static void Add(ArrayList<int[]> turns, int[] arr) {
        boolean flag = true;
        for (int[] turn : turns) {
            if (turn[0] == arr[0] && turn[1] == arr[1]) {
                flag = false;
                break;
            }
        }
        if (flag) {
            turns.add(arr);
        }
    }

    public static void printPossibleTurns(ArrayList<int[]> turns) {
        System.out.println("Всего доступно ходов: " + turns.size());
        System.out.println("Потенциальные ходы:");
        for (var turn : turns) {
            System.out.println(turn[0] + " - " + turn[1]);
        }
    }
    public static ArrayList<int[]> possibleTurns(boolean turn) {
        ArrayList<int[]> turns = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (field[i][j] instanceof Cell cell && cell.getPlayer() != turn) {
                    int up = i - 1;
                    int down = i + 1;
                    int left = j - 1;
                    int right = j + 1;
                    if (up > -1 && !(field[up][j] instanceof Cell)) {
                        int[] arr = {j, up};
                        Add(turns, arr);
                    }
                    if (down < 8 && !(field[down][j] instanceof Cell)) {
                        int[] arr = {j, down};
                        Add(turns, arr);
                    }
                    if (left > -1) {
                        if (!(field[i][left] instanceof Cell)) {
                            int[] arr = {left, i};
                            Add(turns, arr);
                        }
                        if (up > -1 && !(field[up][left] instanceof Cell)) {
                            int[] arr2 = {left, up};
                            Add(turns, arr2);
                        }
                        if (down < 8 && !(field[down][left] instanceof Cell)) {
                            int[] arr2 = {left, down};
                            Add(turns, arr2);
                        }
                    }
                    if (right < 8 ) {
                        if (!(field[i][right] instanceof Cell)) {
                            int[] arr = {right, i};
                            Add(turns, arr);
                        }
                        if (up > -1 && !(field[up][right] instanceof Cell)) {
                            int[] arr2 = {right, up};
                            Add(turns, arr2);
                        }
                        if (down < 8 && !(field[down][right] instanceof Cell)) {
                            int[] arr2 = {right, down};
                            Add(turns, arr2);
                        }
                    }
                }
            }
        }
        turns = rowDefeat(turns, turn);
        return turns;
    }

    public static boolean checkPossibleTurns(int x, int y, ArrayList<int[]> turns) {
        for (int[] turn : turns) {
            if (turn[0] == x && turn[1] == y) {
                return true;
            }
        }
        return false;
    }

    private static ArrayList<int[]> rowDefeat(ArrayList<int[]> turns, boolean turn) {
        ArrayList<int[]> correctTurns = new ArrayList<>();
        for (int[] ints : turns) {
            boolean flag = false;
            int x = ints[0];
            int y = ints[1];
            for (int i = y + 1; i < 8; i++) {
                if (field[i][x] instanceof Cell cell1) {
                    if (cell1.getPlayer() == turn) {
                        if (i > y + 1) {
                            correctTurns.add(new int[]{x, y});
                            flag = true;
                        }
                        break;
                    }
                } else {
                    break;
                }
            }
            if (flag) {
                continue;
            }
            for (int i = y - 1; i > -1; i--) {
                if (field[i][x] instanceof Cell cell1) {
                    if (cell1.getPlayer() == turn) {
                        if (i < y - 1) {
                            correctTurns.add(new int[]{x, y});
                            flag = true;
                        }
                        break;
                    }
                } else {
                    break;
                }
            }
            if (flag) {
                continue;
            }
            for (int i = x + 1; i < 8; i++) {
                if (field[y][i] instanceof Cell cell1) {
                    if (cell1.getPlayer() == turn) {
                        if (i > x + 1) {
                            correctTurns.add(new int[]{x, y});
                            flag = true;
                        }
                        break;
                    }
                } else {
                    break;
                }
            }
            if (flag) {
                continue;
            }
            for (int i = x - 1; i > -1; i--) {
                if (field[y][i] instanceof Cell cell1) {
                    if (cell1.getPlayer() == turn) {
                        if (i < x - 1) {
                            correctTurns.add(new int[]{x, y});
                            flag = true;
                        }
                        break;
                    }
                } else {
                    break;
                }
            }
            if (flag) {
                continue;
            }
            int h = y - 1;
            for (int i = x + 1; i < 8 && h > -1; i++) {
                if (field[h][i] instanceof Cell cell1) {
                    if (cell1.getPlayer() == turn) {
                        if (i > x + 1) {
                            correctTurns.add(new int[]{x, y});
                            flag = true;
                        }
                        break;
                    }
                } else {
                    break;
                }
                h--;
            }
            if (flag) {
                continue;
            }

            h = y + 1;
            for (int i = x - 1; h < 8 && i > -1; i--) {
                if (field[h][i] instanceof Cell cell1) {
                    if (cell1.getPlayer() == turn) {
                        if (i < x - 1) {
                            correctTurns.add(new int[]{x, y});
                            flag = true;
                        }
                        break;
                    }
                } else {
                    break;
                }
                h++;
            }
            if (flag) {
                continue;
            }

            h = y - 1;
            for (int i = x - 1; i > -1 && h > -1; i--) {
                if (field[h][i] instanceof Cell cell1) {
                    if (cell1.getPlayer() == turn) {
                        if (i < x - 1) {
                            correctTurns.add(new int[]{x, y});
                            flag = true;
                        }
                        break;
                    }
                } else {
                    break;
                }
                h--;
            }
            if (flag) {
                continue;
            }

            h = y + 1;
            for (int i = x + 1; i < 8 && h < 8; i++) {
                if (field[h][i] instanceof Cell cell1) {
                    if (cell1.getPlayer() == turn) {
                        if (i > x + 1) {
                            correctTurns.add(new int[]{x, y});
                        }
                        break;
                    }
                } else {
                    break;
                }
                h++;
            }
        }
        return correctTurns;
    }

    public static double evaluateTurn(int x, int y) {
        if ((x == 0 && y == 0) || (x == 7 && y == 0) || (x == 0 && y == 7) || (x == 7 && y == 7))
            return 0.8;
        if (x == 0 || y == 0 || x == 7 || y == 7)
            return 0.4;
        else
            return 0;
    }

    private static int evaluateCell(int y, int x) {
        if (x == 0 || y == 0 || x == 7 || y == 7)
            return 2;
        return 1;
    }

    public static int turnScore(int x, int y, Boolean turn) {
        int sum = 0;
        for (int i = y + 1; i < 8; i++) {
            if (field[i][x] instanceof Cell cell1) {
                if (cell1.getPlayer() == turn) {
                    for (int j = y + 1; j < i; j++) {
                        sum += evaluateCell(j,x);
                    }
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = y - 1; i > -1; i--) {
            if (field[i][x] instanceof Cell sell1) {
                if (sell1.getPlayer() == turn) {
                    for (int j = y - 1; j > i; j--) {
                        sum += evaluateCell(j,x);
                    }
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = x + 1; i < 8; i++) {
            if (field[y][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == turn) {
                    for (int j = x + 1; j < i; j++) {
                        sum += evaluateCell(y,j);
                    }
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = x - 1; i > -1; i--) {
            if (field[y][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == turn) {
                    for (int j = x - 1; j > i; j--) {
                        sum += evaluateCell(y,j);
                    }
                    break;
                }
            } else {
                break;
            }
        }

        int k = y - 1;
        for (int i = x + 1; i < 8 && k > -1; i++) {
            if (field[k][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == turn) {
                    int l = y - 1;
                    for (int j = x + 1; j < i && l > k; j++) {
                        sum += evaluateCell(l,j);
                        l--;
                    }
                    break;
                }
            } else {
                break;
            }
            k--;
        }

        k = y + 1;
        for (int i = x - 1; k < 8 && i > -1; i--) {
            if (field[k][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == turn) {
                    int l = y + 1;
                    for (int j = x - 1; j > i && l < k; j--) {
                        sum += evaluateCell(l,j);
                        l++;
                    }
                    break;
                }
            } else {
                break;
            }
            k++;
        }

        k = y - 1;
        for (int i = x - 1; i > -1 && k > -1; i--) {
            if (field[k][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == turn) {
                    int l = y - 1;
                    for (int j = x - 1; j > i && l > k; j--) {
                        sum += evaluateCell(l,j);
                        l--;
                    }
                    break;
                }
            } else {
                break;
            }
            k--;
        }

        k = y + 1;
        for (int i = x + 1; i < 8 && k < 8; i++) {
            if (field[k][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == turn) {
                    int l = y + 1;
                    for (int j = x + 1; j < i && l < k; j++) {
                        sum += evaluateCell(l,j);
                        l++;
                    }
                    break;
                }
            } else {
                break;
            }
            k++;
        }
        return sum;
    }

    public static void printScore() {
        System.out.println("Первый игрок: " + firstCount + " и второй игрок: " + secondCount);
    }

    private static int[] getScore() {
        int[] score = new int[2];
        int firstCounter = 0;
        int secondCounter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (field[i][j] instanceof Cell cell) {
                    if (cell.getPlayer())
                        firstCounter++;
                    else
                        secondCounter++;
                }
            }
        }
        score[0] = firstCounter;
        score[1] = secondCounter;
        return score;
    }

    private static void score(int y, int x) {
        ((Cell) field[y][x]).changePlayer();
        if (((Cell) field[y][x]).getPlayer()) {
            firstCount++;
            secondCount--;
        } else {
            firstCount++;
            secondCount--;
        }
    }

    public static void boardScore(Cell cell, int y, int x) {
        int k = y - 1;
        for (int i = x + 1; i < 8 && k > -1; i++) {
            if (field[k][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == cell.getPlayer()) {
                    int l = y - 1;
                    for (int j = x + 1; j < i && l > k; j++) {
                        score(l, j);
                        l--;
                    }
                    break;
                }
            } else {
                break;
            }
            k--;
        }

        k = y + 1;
        for (int i = x - 1; k < 8 && i > -1; i--) {
            if (field[k][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == cell.getPlayer()) {
                    int l = y + 1;
                    for (int j = x - 1; j > i && l < k; j--) {
                        score(l, j);
                        l++;
                    }
                    break;
                }
            } else {
                break;
            }
            k++;
        }

        k = y - 1;
        for (int i = x - 1; i > -1 && k > -1; i--) {
            if (field[k][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == cell.getPlayer()) {
                    int l = y - 1;
                    for (int j = x - 1; j > i && l > k; j--) {
                        score(l, j);
                        l--;
                    }
                    break;
                }
            } else {
                break;
            }
            k--;
        }

        k = y + 1;
        for (int i = x + 1; i < 8 && k < 8; i++) {
            if (field[k][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == cell.getPlayer()) {
                    int l = y + 1;
                    for (int j = x + 1; j < i && l < k; j++) {
                        score(l, j);
                        l++;
                    }
                    break;
                }
            } else {
                break;
            }
            k++;
        }

        for (int i = y + 1; i < 8; i++) {
            if (field[i][x] instanceof Cell stone1) {
                if (stone1.getPlayer() == cell.getPlayer()) {
                    for (int j = y + 1; j < i; j++) {
                        score(j, x);
                    }
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = y - 1; i > -1; i--) {
            if (field[i][x] instanceof Cell cell1) {
                if (cell1.getPlayer() == cell.getPlayer()) {
                    for (int j = y - 1; j > i; j--) {
                        score(j, x);
                    }
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = x + 1; i < 8; i++) {
            if (field[y][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == cell.getPlayer()) {
                    for (int j = x + 1; j < i; j++) {
                        score(y, j);
                    }
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = x - 1; i > -1; i--) {
            if (field[y][i] instanceof Cell cell1) {
                if (cell1.getPlayer() == cell.getPlayer()) {
                    for (int j = x - 1; j > i; j--) {
                        score(y, j);
                    }
                    break;
                }
            } else {
                break;
            }
        }
    }
}