package com.example.alexander.fastreading.shulte;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Alexander on 27.07.2016.
 */
public class ShulteGenerator {

    private static final int ITEM_COUNT = 25;

    public static final String FIRST_NUMBER = "1";
    public static final String FIRST_LETTER = "A";

    private static final Random random = new Random();

    private static final List<String> numbers = new ArrayList<>(ITEM_COUNT);
    private static final List<String> letters = new ArrayList<>(ITEM_COUNT);

    static {
        numbers.add("1");
        numbers.add("2");
        numbers.add("3");
        numbers.add("4");
        numbers.add("5");
        numbers.add("6");
        numbers.add("7");
        numbers.add("8");
        numbers.add("9");
        numbers.add("10");
        numbers.add("11");
        numbers.add("12");
        numbers.add("13");
        numbers.add("14");
        numbers.add("15");
        numbers.add("16");
        numbers.add("17");
        numbers.add("18");
        numbers.add("19");
        numbers.add("20");
        numbers.add("21");
        numbers.add("22");
        numbers.add("23");
        numbers.add("24");
        numbers.add("25");

        letters.add("A");
        letters.add("B");
        letters.add("C");
        letters.add("D");
        letters.add("E");
        letters.add("F");
        letters.add("G");
        letters.add("H");
        letters.add("I");
        letters.add("J");
        letters.add("K");
        letters.add("L");
        letters.add("M");
        letters.add("N");
        letters.add("O");
        letters.add("P");
        letters.add("Q");
        letters.add("R");
        letters.add("S");
        letters.add("T");
        letters.add("U");
        letters.add("V");
        letters.add("W");
        letters.add("X");
        letters.add("Y");
    }

    private static List<String> getRandomList(List<String> randomList){

        for (int i = 0; i < randomList.size(); i++){
            int randomNumber = random.nextInt(randomList.size());
            if (i != randomNumber){
                String firstBuffer = randomList.get(randomNumber);
                String secondBuffer = randomList.get(i);

                randomList.set(i, firstBuffer);
                randomList.set(randomNumber, secondBuffer);
            }
        }

        return randomList;
    }

    public static List<String> getRandomNumbers(){
        List<String> randomNumbers = new ArrayList<>(numbers);
        return getRandomList(randomNumbers);
    }

    public static List<String> getRandomLetters(){
        List<String> randomLetters = new ArrayList<>(letters);
        return getRandomList(randomLetters);
    }

    public static String getNextNumberItem(String currentItem){
        int indexCurrentItem = numbers.indexOf(currentItem);
        if (indexCurrentItem == numbers.size() - 1){
            return null;
        }

        return numbers.get(indexCurrentItem + 1);
    }

    public static String getNextLetterItem(String currentItem){
        int indexCurrentItem = letters.indexOf(currentItem);
        if (indexCurrentItem == letters.size() - 1){
            return null;
        }

        return letters.get(indexCurrentItem + 1);
    }

}
