package lu.uni;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named("numberleBean")
@SessionScoped
public class NumberleBean implements Serializable {
    private static final Logger logger = Logger.getLogger(NumberleBean.class.getName());

    private List<String> numbersList;
    private String secretNumber;
    private String guess;
    private int tryCounter = 0;
    private String attempts;
    private ArrayList<String> currentAttempt;
    private boolean won;

    @PostConstruct
    public void init() {
        loadNumbers();
        selectRandomNumber();
        this.attempts = "";
        this.tryCounter = 0;
        this.currentAttempt = new ArrayList<>();
        this.won = false;
    }

    private void loadNumbers() {
        numbersList = new ArrayList<>();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("numbers.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            String line;
            while ((line = reader.readLine()) != null) {
                numbersList.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectRandomNumber() {
        Random rand = new Random();
        this.secretNumber = numbersList.get(rand.nextInt(numbersList.size()));
        logger.info(secretNumber);
    }

    /**
     * small function to check that every character in the guess is a digit
     * returns false as soon as anything other than a digit is encountered 
     */ 
    public boolean isValidGuess(){
        for (int i = 0; i < guess.length(); i++){
            char ch = guess.charAt(i);
            if (!Character.isDigit(ch)) return false;
        }
        return true;
    }
   
    /**
     * function to check the current guess
     */
    public void checkGuess() {
        // store the attempt in form of a string
        String attempt = "";
        //in order to visualize a number in a single line, a div with a flexbox is used
        attempt += "<div class ='number'>";
        //loop through the digits of the guess
        for (int i = 0; i < currentAttempt.size(); i++) {
            //retrieve the char of the digit and the position of the digit in the secret number
            char guessDigit = currentAttempt.get(i).charAt(0);
            int digitPosition = getDigitPosition(guessDigit);

            //if a digit is not found in the target number, put the digit in a red box
            if (digitPosition == -1){
                 attempt += "<div class = 'digit' style='background-color:red'>" +guessDigit + "</div>";
            }
            //when the digit is to the left  of its actual position --> blue box
            else if (i < digitPosition && guessDigit != secretNumber.charAt(i)) {
                attempt +="<div class = 'digit' style='background-color:blue'>"+guessDigit+"</div>";
            }
            //when the digit is to the right of its actual position --> orange box
            else if (i > digitPosition && guessDigit != secretNumber.charAt(i)){
                    attempt += "<div class = 'digit' style='background-color:orange'>" + guessDigit + "</div>";
            }
            // when the digit is at the right position --> green box
            else {
                attempt += "<div class = 'digit' style='background-color:green'>" + guessDigit + "</div>";
            }
        }
        attempt += "</div>";
        //add the attempt to the attempts string
        attempts += (attempt);
        tryCounter++;
        if (getStringFromCurrentAttempt().equals(secretNumber)) {
            won = true;
        }
        if (tryCounter >= 6) {
            
        }
        currentAttempt.clear();
    }
    /**
     * 
     * @param ch
     * @return position of the digit in the secret number
     * @return -1 if the digit is not found
     */
    private int getDigitPosition(char ch){
        for (int i = 0; i < secretNumber.length(); i++){
            if (ch == secretNumber.charAt(i)){
                return i;
            }
        }
        return -1;
    }
    private String getStringFromCurrentAttempt(){
        String output = "";
        for (String digit: currentAttempt){
            output += digit;
        }
        return output;
    }

    public boolean isOver(){
        return won || tryCounter >= 6;
    }
    // Getters and Setters
    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        //exit function if an invalid character is entered
        if (guess.isBlank() || Character.isAlphabetic(guess.charAt(0))) return;
        currentAttempt.add(guess);
        if (currentAttempt.size() >= 6) {
            checkGuess();
        }
    }
    public String getAttempts() {
        return attempts;
    }
    public String getSecretNumber(){
        return secretNumber;
    }
    public int getTryCounter() {
        return tryCounter;
    }

    public String getCurrentAttempt(){
        if (currentAttempt.isEmpty() ) return "";
        String output = "<div class= 'number'>";
        for (String digit: currentAttempt){
            output += "<div class ='digit'>";
            output += digit;
            output += "</div>";
        }
        output += "</div";
        return output;
    }
    public String resetGame(){
        init();
        return "index";
    }

    public boolean isWon() {
        return won;
    }
}
