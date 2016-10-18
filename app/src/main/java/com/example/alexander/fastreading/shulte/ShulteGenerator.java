package com.example.alexander.fastreading.shulte;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Alexander on 27.07.2016.
 */
public class ShulteGenerator {

    private final Random random = new Random();

    private final int itemsCount;
    private final List<String> randomNumberList;

    public ShulteGenerator(int countLines) {
        itemsCount = countLines * countLines;
        randomNumberList = new ArrayList<>(itemsCount);

        for (int i = 0; i < itemsCount; i++) {
            randomNumberList.add(String.valueOf(i + 1));
        }
    }

    /*
    private List<String> randomizeList(List<String> randomList){

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
    */

    public List<String> getRandomNumbers() {
        for (int i = 0; i < itemsCount; i++){
            int randomNumber = random.nextInt(itemsCount);
            if (i != randomNumber){
                String firstBuffer = randomNumberList.get(randomNumber);
                String secondBuffer = randomNumberList.get(i);

                randomNumberList.set(i, firstBuffer);
                randomNumberList.set(randomNumber, secondBuffer);
            }
        }

        return randomNumberList;
    }

    public String getNextNumberItem(String currentItem){
        int number = Integer.valueOf(currentItem);

        if (number < itemsCount) {
            return String.valueOf(number + 1);
        } else {
            return null;
        }
    }

}
