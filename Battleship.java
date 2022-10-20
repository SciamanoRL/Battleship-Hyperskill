
package battleship;

import java.util.Scanner;

public class Main {

    public static boolean rightEnter;
    public static boolean endGame;
    public static boolean sank;
    private final static String HITMSG = "You hit a ship!\n";
    private final static String SANKMSG = "You sank a ship! Specify a new target:\n";
    private final static String MISSMSG = "You missed.\n";

    public static void main(String[] args) {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        String turn = null;
        while (!endGame) {
            turn = player1.name;
            switch (turn) {
                case "Player 1":
                    rightEnter = false;
                    printFogField(player2.field.field);
                    System.out.println("---------------------");
                    player1.printField(player1.field.field);
                    System.out.printf("%s, it's your turn:\n", player1.name);
                    while (!rightEnter) {
                        enterShot(player2.field.field, player2);
                    }
                    if (endGame) {
                        break;
                    } else {
                        new Scanner(System.in).nextLine();
                        turn = player2.name;
                    }
                case "Player 2":
                    rightEnter = false;
                    printFogField(player1.field.field);
                    System.out.println("---------------------");
                    player2.printField(player2.field.field);
                    System.out.printf("%s, it's your turn:\n", player2.name);
                    while (!rightEnter) {
                        enterShot(player1.field.field, player1);
                    }
                    if (endGame) {
                        break;
                    } else {
                        new Scanner(System.in).nextLine();
                        turn = player1.name;
                    }
            }
        }
        System.out.printf("%s sank the last ship. You won. Congratulations!", turn);

    }

    public static void printFogField(String[][] field) {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (j == 0) {
                    System.out.print(field[i][j].equals("O") ? "~" : field[i][j]);
                } else {
                    System.out.print(" " + (field[i][j].equals("O") ? "~" : field[i][j]));
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void enterShot(String[][] field, Player player) {
        int line = 0;
        int column = 0;
        try {
            Scanner s = new Scanner(System.in);
            String shot = s.nextLine();
            column = Integer.parseInt(shot.substring(1));
            for (int i = 0; i < 11; i++) {
                if (shot.substring(0, 1).equals(field[i][0])) {
                    line = i;
                }
            }
            if ("O".equals(field[line][column])) {
                field[line][column] = "X";
                endGame = checkEndOfTheGame(field);
                sank = checkSank(line, column, field);
                if (sank && !endGame) {
                    System.out.println(SANKMSG);
                } else if (!endGame) {
                    System.out.println(HITMSG);
                }
                rightEnter = true;
            } else if ("~".equals(field[line][column]) || "M".equals(field[line][column])) {
                field[line][column] = "M";
                System.out.println(MISSMSG);
                rightEnter = true;
            } else if ("X".equals(field[line][column])) {
                System.out.println(HITMSG);
                rightEnter = true;
            } else {
                throw new Exception();
            }
            System.out.println("Press Enter and pass the move to another player");
        } catch (Exception e) {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
        }
    }

    public static boolean checkEndOfTheGame(String[][] field) {
        for (int i = 1; i < 11; i ++) {
            for (int j = 1; j < 11; j++) {
                if (field[i][j].equals("O")) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkSank(int line, int column, String[][] field) {
        int upperBound = Math.max(line - 1, 1);
        int lowerBound = Math.min(line + 1, 10);
        int leftBound = Math.max(column - 1, 1);
        int rightBound = Math.min(column + 1, 10);
        for (int i = upperBound; i <= lowerBound; i++) {
            for (int j = leftBound; j <= rightBound; j++) {
                if ("O".equals(field[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }
}


enum Ship {
    AIRCRAFT("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

    private final String name;
    private final int cell;


    Ship(String name, int cell) {
        this.name = name;
        this.cell = cell;
    }

    public int getCell() {
        return cell;
    }

    public String getName() {
        return name;
    }
}
class WrongShipLocationException extends RuntimeException { }

class WrongCellsException extends WrongShipLocationException { }

class ClosePositionException extends WrongCellsException { }

class WrongCoordinatesException extends ClosePositionException { }

class Player {
    public Field field;
    public String name;
    public boolean rightEnter;

    Player(String name) {
        this.name = name;
        this.field = new Field();
        System.out.printf("%s, place your ships on the game field\n\n", this.name);
        printField(field.field);
        for (Ship s : Ship.values()) {
            rightEnter = false;
            System.out.printf("Enter the coordinates of the %s (%d cells):\n", s.getName(), s.getCell());
            while (!rightEnter) {
                enterCoord(field, s.getCell());
            }
            printField(field.field);
        }
        System.out.println("Press Enter and pass the move to another player");
        new Scanner(System.in).nextLine();
    }

    public void enterCoord(Field field, int cell) {
        int columnIn;
        int columnOut;
        int lineIn = 0;
        int lineOut = 0;
        try {
            Scanner s = new Scanner(System.in);
            String coords = s.nextLine().toUpperCase().replace(" ", "");
            String[] numbers = coords.substring(1).split("[A-Z]");
            String[] literals = coords.replace(numbers[0], "").replace(numbers[1], "").split("");
            columnIn = Integer.parseInt(numbers[0]);
            columnOut = Integer.parseInt(numbers[1]);
            for (int i = 0; i < 11; i++) {
                if (literals[0].equals(field.field[i][0])) {
                    lineIn = i;
                }
                if (literals[1].equals(field.field[i][0])) {
                    lineOut = i;
                }
            }
            if (columnIn > columnOut) {
                int lastNumber = columnOut;
                columnOut = columnIn;
                columnIn = lastNumber;
            } else if (lineIn > lineOut) {
                int lastLiteral = lineOut;
                lineOut = lineIn;
                lineIn = lastLiteral;
            }
            checkCoordinates(columnIn, columnOut, lineIn, lineOut, field, cell);
            if (literals[0].equals(literals[1])) {
                for (int i = columnIn; i <= columnOut; i++) {
                    field.field[lineIn][i] = "O";
                }
            }
            if (numbers[0].equals(numbers[1])) {
                for (int i = lineIn; i <= lineOut; i++) {
                    field.field[i][columnIn] = "O";
                }
            }
            rightEnter = true;
        } catch (WrongCoordinatesException e) {
            System.out.println("Error! Wrong coordinates! Try again:");
        } catch (ClosePositionException e) {
            System.out.println("Error! You placed it too close to another one. Try again:");
        } catch (WrongCellsException e) {
            System.out.println("Error! Wrong length of the ship! Try again:");
        } catch (WrongShipLocationException e) {
            System.out.println("Error! Wrong ship location! Try again:");
        } catch (NumberFormatException e) {
            System.out.println("Error! Wrong input! Try again:");
        }
    }

    public void checkCoordinates(int columnIn, int columnOut, int lineIn, int lineOut, Field field, int cell) {
        int checkColumn = columnOut - columnIn + 1;
        int checkRow = lineOut - lineIn + 1;
        if (lineIn != lineOut && columnIn != columnOut) {
            throw new WrongShipLocationException();
        }

        if (columnIn < 1 || columnOut > 10 || lineIn < 1 || lineOut > 10) {
            throw new WrongCoordinatesException();
        }

        if (columnIn == columnOut) {
            if (checkRow < cell || checkRow > cell) {
                throw new WrongCellsException();
            }
        }
        if (lineIn == lineOut) {
            if (checkColumn < cell || checkColumn > cell) {
                throw new WrongCellsException();
            }
        }

        int upperBound = Math.max(lineIn - 1, 1);
        int lowerBound = Math.min(lineOut + 1, 10);
        int leftBound = Math.max(columnIn - 1, 1);
        int rightBound = Math.min(columnOut + 1, 10);
        for (int i = upperBound; i <= lowerBound; i++) {
            for (int j = leftBound; j <= rightBound; j++) {
                if ("O".equals(field.field[i][j])) {
                    throw new ClosePositionException();
                }
            }
        }
    }

    public void printField(String[][] field) {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (j == 0) {
                    System.out.print(field[i][j]);
                } else {
                    System.out.print(" " + field[i][j]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}

class Field {
    String[][] field = new String[11][11];
    Field() {
        char character = 'A';
        int num = 1;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (j == 0) {
                    if (i == 0) {
                        field[i][j] = " ";
                    } else {
                        field[i][j] = Character.toString(character);
                        character++;
                    }
                } else if (i == 0) {
                    if (j == 10) {

                    }
                    field[i][j] = Integer.toString(num);
                    num++;
                } else {
                    field[i][j] = "~";
                }
            }
        }
    }
}
