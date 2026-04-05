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

    private static final Random random = new Random();

    private static final StringBuilder HIDDEN_WORD = new StringBuilder();
    private static final List<Character> USED_LETTERS = new ArrayList<>();

    private static final String WORDS_FILE_PATH = "words.txt";
    private static final String HIDE_SYMBOL = "_";
    private static final int MAX_MISTAKES = 6;

    private static int mistakesCount;
    private static char myGuess;
    private static List<String> listOfWords = new ArrayList<>();
    private static String randomSecretWord;
    private static int letterToGuess;

    public static void main(String[] args) {
        readFile();
        try (Scanner scanner = new Scanner(System.in)) {
            while (isGameStarted(scanner)) {
                startGameLoop(scanner);
            }
        }
    }

    private static void readFile() {
        try {
            listOfWords = Files.readAllLines(Paths.get(WORDS_FILE_PATH));
        } catch (IOException e) {
            System.out.println("no such file...");
        }
    }

    private static String getRandomWord() {
        return listOfWords.get(random.nextInt(listOfWords.size())).trim().toLowerCase();
    }

    private static boolean isGameStarted(Scanner scanner) {
        while (true) {
            System.out.println(" ");
            System.out.println("HANGMAN GAME: TRY to guess an english word or DIE");
            System.out.println("WANNA PLAY WITH ME?" + " \n" + " 1 - YES    0 - NO ");

            String choice = scanner.next();
            switch (choice) {
                case "1":
                    System.out.println(" ");
                    System.out.println("NEW GAME STARTED");
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
        mistakesCount = 0;
        letterToGuess = randomSecretWord.length();
        USED_LETTERS.clear();
        hideSecretWord(randomSecretWord);
    }

    private static void startGameLoop(Scanner scanner) {
        randomSecretWord = getRandomWord();
        clearStats();
        guessingLoop(scanner);
    }

    private static boolean isGameStillPlaying() {
        if (mistakesCount == MAX_MISTAKES) {
            System.out.println("GAME OVER :( ");
            System.out.println("the word was: " + randomSecretWord);
            return false;
        } else if (letterToGuess == 0) {
            System.out.println(" :) WIN ! ");
            System.out.println("the word was: " + randomSecretWord);
            return false;
        }
        return true;
    }

    private static void guessingLoop(Scanner scanner) {
        while (true) {
            printHangmanStage(mistakesCount);
            printHiddenWord();
            if (!isGameStillPlaying()) {
                break;
            }
            getUserInput(scanner);
            if (!isInputValid(myGuess)) {
                continue;
            }
            USED_LETTERS.add(myGuess);
            if (randomSecretWord.contains(String.valueOf(myGuess))) {
                guessIsRight();
            } else {
                guessIsNotRight();
            }
        }
    }

    private static void printHangmanStage(int mistakes) {
        System.out.println(HANGMAN_STAGES[mistakes]);
    }

    private static void hideSecretWord(String randomWord) {
        HIDDEN_WORD.setLength(0);
        HIDDEN_WORD.append(HIDE_SYMBOL.repeat(randomWord.length()));
    }

    private static void printHiddenWord() {
        System.out.println("the word: " + HIDDEN_WORD);
    }

    private static void getUserInput(Scanner scanner) {
        System.out.println("type your guess, one letter:");
        myGuess = scanner.next().toLowerCase().charAt(0);
    }

    private static boolean isInputValid(char userGuess) {
        if (USED_LETTERS.contains(userGuess)) {
            System.out.println("you already used this letter: " + userGuess + "!");
            return false;
        } else if (!Character.isLetter(userGuess) || userGuess < 'a' || userGuess > 'z') {
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
                "number of mistakes: " + mistakesCount + "\n" +
                "number of tries left: " + (MAX_MISTAKES - mistakesCount));
    }

    private static void guessIsRight() {
        for (int i = 0; i < randomSecretWord.length(); i++) {
            if (myGuess == randomSecretWord.charAt(i)) {
                HIDDEN_WORD.setCharAt(i, myGuess);
                letterToGuess--;
            }
        }
        rightGuessMessage(mistakesCount);
    }

    private static void guessIsNotRight() {
        mistakesCount++;
        mistakeMessage();
    }
}
