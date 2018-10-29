package com.mathrace.mathrace.Utilities;


import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author amr
 */
public class MathRaceQuestionsGenerator {

    public static double[][] operatorProbs =
            {{0.60, 0.90, 0.95, 1.00},
                    {0.55, 0.85, 0.90, 1.00},
                    {0.50, 0.80, 0.90, 1.00},
                    {0.45, 0.65, 0.85, 1.00},
                    {0.45, 0.60, 0.80, 1.00},
                    {0.50, 0.55, 0.75, 1.00},
                    {0.45, 0.50, 0.75, 1.00},
                    {0.35, 0.45, 0.70, 1.00},
                    {0.30, 0.40, 0.70, 1.00},
                    {0.20, 0.30, 0.60, 1.00},
            };


    public static String[] operatorsList = {"+", "-", "\u00D7", "\u00F7"};
    public static int[] oprandsList = {10, 15, 20, 25, 35, 40, 45, 50, 60, 80};

    //level are 0 based
    public static ArrayList<String> getQuestion(int level) {
        //set level
        if (level > 9) {
            level = 9;
        }
        //select operator
        double rand = Math.random();
        int index = 0;
        for (int i = 0; i < 4; i++) {
            if (rand <= operatorProbs[level][i]) {
                index = i;
                break;
            }

        }
        String operator = operatorsList[index];

        //select oprands
        index = level;
        int baseNumber = oprandsList[index];

        int oprand1 = (int) (Math.random() * baseNumber + 1);
        int oprand2 = (int) (Math.random() * baseNumber + 1);
        int answer = 0;

        if (operator.equals("+")) {
            answer = oprand1 + oprand2;
        } else if (operator.equals("-")) {
            answer = oprand1 - oprand2;
        } else if (operator.equals("\u00D7")) {
            answer = oprand1 * oprand2;
        } else {
            answer = oprand1;
            oprand1 = oprand2 * answer;
        }

        //build answers array
        ArrayList<Integer> choices = new ArrayList<Integer>();
        int ch1 = (int) (Math.random() * answer * 2);
        while (choices.contains(ch1) || ch1 == answer) ch1 += 3;
        choices.add(ch1);

        int ch2 = (int) (Math.random() * answer * 2);
        while (choices.contains(ch2) || ch2 == answer) ch2 += 3;
        choices.add(ch2);

        int ch3 = (int) (Math.random() * answer * 2);
        while (choices.contains(ch3) || ch3 == answer) ch3 += 3;
        choices.add(ch3);

        int answerIndex = (int) (Math.random() * 4);
        choices.add(answerIndex, answer);

        String[] result = new String[9];
        result[0] = Integer.toString(oprand1);
        result[1] = operator;
        result[2] = Integer.toString(oprand2);
        result[3] = "=";
        result[4] = Integer.toString(choices.get(0));
        result[5] = Integer.toString(choices.get(1));
        result[6] = Integer.toString(choices.get(2));
        result[7] = Integer.toString(choices.get(3));
        result[8] = Integer.toString(answerIndex);


        return new ArrayList<>(Arrays.asList(result));


    }

    public static ArrayList<String> getQuizQuestion(int level) {
        //set level
        if (level > 9) {
            level = 9;
        }
        //select operator
        double rand = Math.random();
        int index = 0;
        for (int i = 0; i < 4; i++) {
            if (rand <= operatorProbs[level][i]) {
                index = i;
                break;
            }

        }
        String operator = operatorsList[index];

        //select oprands
        index = level;
        int baseNumber = oprandsList[index];

        int oprand1 = (int) (Math.random() * baseNumber) + 1;
        int answer = (int) (Math.random() * baseNumber) + 1;
        int oprand2 = 0;

        if (operator.equals("+")) {
            oprand2 = answer - oprand1;
        } else if (operator.equals("-")) {
            oprand2 = oprand1 - answer;
        } else if (operator.equals("\u00D7")) {
            oprand2 = answer;
            answer = oprand2 * oprand1;
        } else {
            oprand2 = oprand1;
            oprand1 = oprand2 * answer;
        }

        //build answers array
        ArrayList<Integer> choices = new ArrayList<Integer>();
        int ch1 = (int) (Math.random() * answer * 2);
        while (choices.contains(ch1) || ch1 == oprand2) ch1 += 3;
        choices.add(ch1);

        int ch2 = (int) (Math.random() * answer * 2);
        while (choices.contains(ch2) || ch2 == oprand2) ch2 += 3;
        choices.add(ch2);

        int ch3 = (int) (Math.random() * answer * 2);
        while (choices.contains(ch3) || ch3 == oprand2) ch3 += 3;
        choices.add(ch3);

        int answerIndex = (int) (Math.random() * 4);
        choices.add(answerIndex, oprand2);

        String[] result = new String[10];
        result[0] = Integer.toString(oprand1);
        result[1] = operator;
        result[2] = "?";
        result[3] = "=";
        result[4] = Integer.toString(answer);
        result[5] = Integer.toString(choices.get(0));
        result[6] = Integer.toString(choices.get(1));
        result[7] = Integer.toString(choices.get(2));
        result[8] = Integer.toString(choices.get(3));
        result[9] = Integer.toString(answerIndex);


        return new ArrayList<>(Arrays.asList(result));


    }

    public static void main(String[] args) {
        Log.e("Generator", "Quiz >  " + MathRaceQuestionsGenerator.getQuizQuestion(1));
        Log.e("Generator", "Game >  " + MathRaceQuestionsGenerator.getQuestion(1));
        System.out.println(MathRaceQuestionsGenerator.getQuizQuestion(1));
        System.out.println(MathRaceQuestionsGenerator.getQuizQuestion(1));
        System.out.println(MathRaceQuestionsGenerator.getQuizQuestion(1));
        System.out.println(MathRaceQuestionsGenerator.getQuizQuestion(1));
        System.out.println(MathRaceQuestionsGenerator.getQuizQuestion(1));
        System.out.println(MathRaceQuestionsGenerator.getQuizQuestion(1));
        System.out.println(MathRaceQuestionsGenerator.getQuizQuestion(1));
        System.out.println(MathRaceQuestionsGenerator.getQuizQuestion(1));
    }

}
