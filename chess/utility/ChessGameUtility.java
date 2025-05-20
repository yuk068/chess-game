package chess.utility;

import chess.board.Board;
import chess.board.ChessGame;
import chess.board.Square;
import chess.piece.*;

import java.util.*;

public class ChessGameUtility {


    public ChessGameUtility() {
    }

    public static boolean haveThisMove(List<Integer[]> moves, Integer[] move) {
        for (Integer[] arr : moves) {
            if (Arrays.equals(arr, move)) {
                return true;
            }
        }
        return false;
    }

    public static void printMoves(List<Integer[]> moves) {
        StringBuilder print = new StringBuilder("{");
        for (Integer[] move : moves) {
            print.append("[").append(move[0]).append(", ").append(move[1]).append("]").append("; ");
        }
        System.out.println(print.append("}"));
    }

    public static boolean isSameBoard(Square[][] current, Square[][] another) {
        for (int i = 0; i < current.length; i++) {
            for (int j = 0; j < current[0].length; j++) {
                if (!current[i][j].isSameSquare(another[i][j])) return false;
            }
        }
        return true;
    }

    public static List<Integer[]> removeMoves(List<Integer[]> list, List<Integer[]> target) {
        List<Integer[]> newList = new ArrayList<>(list);

        for (Integer[] element : target) {
            newList.removeIf(current -> areArraysEqual(current, element));
        }
        return newList;
    }

    public static List<Integer[]> removeMove(List<Integer[]> list, int posX, int posY) {
        List<Integer[]> newList = new ArrayList<>(list);
        Integer[] target = new Integer[]{posX, posY};
        newList.removeIf(current -> areArraysEqual(current, target));
        return newList;
    }

    private static boolean areArraysEqual(Integer[] array1, Integer[] array2) {
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (!array1[i].equals(array2[i])) {
                return false;
            }
        }
        return true;
    }

    public static List<Integer[]> trimMoves(List<Integer[]> list, Integer[] target, boolean inclusive) {
        List<Integer[]> trimmedList = new ArrayList<>();
        boolean found = false;

        for (Integer[] array : list) {
            if (Arrays.equals(array, target)) {
                if (!inclusive) {
                    found = true;
                    continue;
                } else {
                    found = true;
                }
            }

            if (!found) {
                trimmedList.add(array);
            }
        }
        return trimmedList;
    }

    public static boolean containsAny(List<Integer[]> moves, List<Integer[]> prevention) {
        for (Integer[] move : prevention) {
            for (Integer[] innerMove : moves) {
                if (move.length == innerMove.length && equalsArrayValues(move, innerMove)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean equalsArrayValues(Integer[] arr1, Integer[] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (!arr1[i].equals(arr2[i])) {
                return false;
            }
        }
        return true;
    }

    public static List<Integer[]> removeDuplicates(List<Integer[]> list) {
        List<Integer[]> result = new ArrayList<>();
        Set<List<Integer>> set = new HashSet<>();

        for (Integer[] arr : list) {
            if (!containsArray(set, arr)) {
                result.add(arr);
                set.add(List.of(arr));
            }
        }

        return result;
    }

    private static boolean containsArray(Set<List<Integer>> set, Integer[] arr) {
        for (List<Integer> list : set) {
            if (equalsArrayValues(list.toArray(new Integer[0]), arr)) {
                return true;
            }
        }
        return false;
    }

    public static List<Integer[]> copyMoves(List<Integer[]> original) {
        List<Integer[]> copy = new ArrayList<>();
        for (Integer[] array : original) {
            Integer[] newArray = new Integer[array.length];
            System.arraycopy(array, 0, newArray, 0, array.length);
            copy.add(newArray);
        }
        return copy;
    }

    public static String convertToChessNotation(int x, int y) {
        char file = (char) ('A' + y);
        int rank = 8 - x;
        return file + Integer.toString(rank);
    }

    public static Integer[] convertFromChessNotation(String chessNotation) {
        int x;
        int y;
        try {
            char file = chessNotation.toUpperCase().charAt(0);
            int rank = Character.getNumericValue(chessNotation.charAt(1));
            x = 8 - rank;
            y = file - 'A';
        } catch (Exception e) {
            x = -1;
            y = -1;
        }
        return new Integer[]{x, y};
    }

}
