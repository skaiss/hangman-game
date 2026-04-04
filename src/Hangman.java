import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Hangman {
    private static final String[] HANGMAN_STAGES = {
            // 0 mistakes
            """
      __________
      |/    |
      |
      |
      |
      ===========""",

            // 1 mistake
            """
      __________
      |/    |
      |     O
      |
      |
      ===========""",

            // 2 mistakes
            """
      __________
      |/    |
      |     O
      |     П
      |
      ===========""",

            // 3 mistakes
            """
      __________
      |/    |
      |     O
      |    /П
      |
      ===========""",

            // 4 mistakes
            """
      __________
      |/    |
      |     O
      |    /П\\
      |
      ===========""",

            // 5 mistakes
            """
      __________
      |/    |
      |     O
      |    /П\\
      |     L
      ===========""",

            // 6 mistakes
            """
      __________
      | /   |
      |     O
      |    /П\\
      |     LL
      ==========="""
    };

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    private static final StringBuilder HIDDEN_WORD = new StringBuilder();
    private static final List<Character> USED_LETTERS = new ArrayList<>();

    private static final String WORDS_FILE_PATH = "words.txt";
    private static final String HIDE_SYMBOL = "_";
    private static final int MAX_MISTAKES = 6;
    private static final int ALL_LETTERS_GUESSED = 0;

    private static int MISTAKES_COUNT;
    private static char MY_GUESS;

    private static List<String> LIST_OF_WORDS = new ArrayList<>();

    private static String RANDOM_WORD;
    private static int LETTERS_TO_GUESS;


    public static void main(String[] args) {
        readFile();
        while (isGameStarted()) {
            startGameLoop();
        }
    }

    private static void readFile() {
        try {
            LIST_OF_WORDS = Files.readAllLines(Paths.get(WORDS_FILE_PATH));
        } catch (IOException e) {
            System.out.println("no such file...");
        }
    }

    private static String getRandomWord() {
        return LIST_OF_WORDS.get(random.nextInt(LIST_OF_WORDS.size())).trim().toLowerCase();
    }

    private static boolean isGameStarted() {
        while (true) {
            System.out.println(" ");
            System.out.println("try to guess an english word or die");
            System.out.println("WANNA PLAY HANGMAN?" + " \n" + " 1 - YES    0 - NO ");

            String choice = scanner.next();
            switch (choice) {
                case "1":
                    return true;
                case "0":
                    System.out.println("bye...");
                    return false;
                default:
                    System.out.println("choose 1 for YES and 0 for NO");
            }
        }
    }

    private static void clearStats() {
        MISTAKES_COUNT = 0;
        LETTERS_TO_GUESS = RANDOM_WORD.length();
        USED_LETTERS.clear();
        hideTheWord(RANDOM_WORD);
    }

    private static void startGameLoop() {
        System.out.println(" ");
        System.out.println("NEW GAME STARTED");
        RANDOM_WORD = getRandomWord();
        clearStats();
        guessingLoop();
    }

    private static boolean isGameStillPlaying() {
        if (MISTAKES_COUNT == MAX_MISTAKES) {
            System.out.println("GAME OVER :( ");
            System.out.println("the word was: " + RANDOM_WORD);
            return false;
        } else if (LETTERS_TO_GUESS == ALL_LETTERS_GUESSED) {
            System.out.println(" :) WIN ! ");
            System.out.println("the word was: " + RANDOM_WORD);
            return false;
        }
        return true;
    }

    private static void guessingLoop() {
        while (true) {
            printHangmanStage(MISTAKES_COUNT);
            printHiddenWord();
            if (!isGameStillPlaying()) {
                break;
            }
            getUserInput();
            if (!isInputValid(MY_GUESS)) {
                continue;
            }
            USED_LETTERS.add(MY_GUESS);
            if (RANDOM_WORD.contains(String.valueOf(MY_GUESS))) {
                guessIsRight();
            } else {
                guessIsNotRight();
            }
        }
    }

    private static void printHangmanStage(int mistakes) {
        System.out.println(HANGMAN_STAGES[mistakes]);
    }

    private static void hideTheWord(String randomWord) {
        HIDDEN_WORD.setLength(0);
        HIDDEN_WORD.append(HIDE_SYMBOL.repeat(randomWord.length()));
    }

    private static void printHiddenWord() {
        System.out.println("the word: " + HIDDEN_WORD);
    }

    private static void getUserInput() {
        System.out.println("type your guess, one letter:");
        MY_GUESS = scanner.next().toLowerCase().charAt(0);
    }

    private static boolean isInputValid(char myGuess) {
        if (USED_LETTERS.contains(myGuess)) {
            System.out.println("you already used this letter: " + myGuess + "!");
            return false;
        } else if (!Character.isLetter(myGuess) || myGuess < 'a' || myGuess > 'z') {
            System.out.println("type a correct english letter. no numbers or symbols");
            return false;
        }
        return true;
    }

    private static void rightGuessMessage(int mistakes) {
        System.out.println("guess is right!  " + "\n" +
                "used letters: " + USED_LETTERS + "\n" +
                "number of mistakes: " + mistakes + "\n" +
                "number of tries left: " + (MAX_MISTAKES - mistakes));
    }

    private static void mistakeMessage() {
        System.out.println("oops MISTAKE!\n" +
                "used letters: " + USED_LETTERS + "\n" +
                "number of mistakes: " + MISTAKES_COUNT + "\n" +
                "number of tries left: " + (MAX_MISTAKES - MISTAKES_COUNT));
    }

    private static void guessIsRight() {
        for (int i = 0; i < RANDOM_WORD.length(); i++) {
            if (MY_GUESS == RANDOM_WORD.charAt(i)) {
                HIDDEN_WORD.setCharAt(i, MY_GUESS);
                LETTERS_TO_GUESS--;
            }
        }
        rightGuessMessage(MISTAKES_COUNT);
    }

    private static void guessIsNotRight() {
        MISTAKES_COUNT++;
        mistakeMessage();
    }
}
