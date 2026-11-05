/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beansproduction.mathrace.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author amr
 */
public class MathRaceQuestionsGenerator {

    public static double[][] operatorProbs=
    {{0.80,1.00,1.00,1.00},
     {0.60,1.00,1.00,1.00},
     {0.45,1.00,1.00,1.00},
     {0.45,0.90,1.00,1.00},
     {0.45,0.75,1.00,1.00},
     {0.45,0.75,0.95,1.00},
     {0.45,0.65,0.90,1.00},
     {0.35,0.50,0.80,1.00},
     {0.30,0.40,0.70,1.00},
     {0.20,0.30,0.60,1.00},
    };
    
    
       
    public static String [] operatorsList={"+","-","\u00D7","\u00F7"};
    public static int[] oprandsList={5,10,15,15,15,15,20,20,25,50};
    //level are 0 based
    public static ArrayList<String> getQuestion(int level)
    {
        level-=1;
        //set level
        if(level>9)
        {
            level=9;
        }
        //select operator
        double rand=Math.random();
        int index=0;
        for(int i=0;i<4;i++)
        {
            if(rand<=operatorProbs[level][i])
            {
                index=i;
                break;
            }
            
        }
        String operator=operatorsList[index];
        
        //select oprands
        index=level;
        int baseNumber=oprandsList[index];
        
        int oprand1=(int)(Math.random()*baseNumber+1);
        int oprand2=(int)(Math.random()*baseNumber+1);
        int answer=0;
        
        if(operator.equals("+"))
        {
            answer=oprand1+oprand2;
        }
        else if(operator.equals("-"))
        {
            answer=oprand1-oprand2;
        }
        else if(operator.equals("\u00D7"))
        {
            oprand1=oprand1/2+1;
            oprand2=oprand2/2+1;
            answer=oprand1*oprand2;
        }
        else
        {
            oprand1=oprand1/3+1;
            oprand2=oprand2/3+1;
            answer=oprand1;
            oprand1=oprand2*answer;
        }
        
        //build answers array
        ArrayList<Integer> choices=new ArrayList<Integer>();
        int ch1=(int)(Math.random()*answer*2);
        while(choices.contains(ch1) || ch1==answer) ch1+=3;
        choices.add(ch1);
        
        int ch2=(int)(Math.random()*answer*2);
        while(choices.contains(ch2) || ch2==answer) ch2+=3;
        choices.add(ch2);
        
        int ch3=(int)(Math.random()*answer*2);
        while(choices.contains(ch3) || ch3==answer) ch3+=3;
        choices.add(ch3);
        
        int answerIndex=(int)(Math.random()*4);
        choices.add(answerIndex, answer);
        
        String[] result=new String[9];
        result[0]=Integer.toString(oprand1);
        result[1]=operator;
        result[2]=Integer.toString(oprand2);
        result[3]="=";
        result[4]=Integer.toString(choices.get(0));
        result[5]=Integer.toString(choices.get(1));
        result[6]=Integer.toString(choices.get(2));
        result[7]=Integer.toString(choices.get(3));
        result[8]=Integer.toString(answerIndex);
        
        
        return new ArrayList<>(Arrays.asList(result));
        
        
        
        
    }

    public static ArrayList<String> getCustomQuestion(int add,int sub,int mul,int div,int level)
    {
        level-=1;
        //set level
        if(level>9)
        {
            level=9;
        }

        int size=add+sub+mul+div;
        int[] oprandsList={5,7,10,12,15,20,25,30,40,50};
        double[] operatorProbs=new double[size];
        for(int i=0;i<size;i++) operatorProbs[i]=1.0/size*(i+1);


        String [] operatorsList=new String[size];
        int idx=0;
        if(add==1) {operatorsList[idx]="+"; idx++;}
        if(sub==1) {operatorsList[idx]="-"; idx++;}
        if(mul==1) {operatorsList[idx]="\u00D7"; idx++;}
        if(div==1) {operatorsList[idx]="\u00F7"; idx++;}

        //select operator
        double rand=Math.random();
        int index=0;
        for(int i=0;i<size;i++)
        {
            if(rand<=operatorProbs[i])
            {
                index=i;
                break;
            }

        }
        String operator=operatorsList[index];

        //select oprands
        index=level;
        int baseNumber=oprandsList[index];

        int oprand1=(int)(Math.random()*baseNumber+1);
        int oprand2=(int)(Math.random()*baseNumber+1);
        int answer=0;

        if(operator.equals("+"))
        {
            answer=oprand1+oprand2;
        }
        else if(operator.equals("-"))
        {
            answer=oprand1-oprand2;
            if(answer<0 && level<4)
            {
                int tmp=oprand1;
                oprand1=oprand2;
                oprand2=tmp;
                answer=oprand1-oprand2;
            }
        }
        else if(operator.equals("\u00D7"))
        {
            oprand1=oprand1/2+1;
            oprand2=oprand2/2+1;
            answer=oprand1*oprand2;
        }
        else
        {
            oprand1=oprand1/3+1;
            oprand2=oprand2/3+1;
            answer=oprand1;
            oprand1=oprand2*answer;
        }

        //build answers array
        ArrayList<Integer> choices=new ArrayList<Integer>();
        int ch1=(int)(Math.random()*answer*2);
        while(choices.contains(ch1) || ch1==answer) ch1+=3;
        choices.add(ch1);

        int ch2=(int)(Math.random()*answer*2);
        while(choices.contains(ch2) || ch2==answer) ch2+=3;
        choices.add(ch2);

        int ch3=(int)(Math.random()*answer*2);
        while(choices.contains(ch3) || ch3==answer) ch3+=3;
        choices.add(ch3);

        int answerIndex=(int)(Math.random()*4);
        choices.add(answerIndex, answer);

        String[] result=new String[9];
        result[0]=Integer.toString(oprand1);
        result[1]=operator;
        result[2]=Integer.toString(oprand2);
        result[3]="=";
        result[4]=Integer.toString(choices.get(0));
        result[5]=Integer.toString(choices.get(1));
        result[6]=Integer.toString(choices.get(2));
        result[7]=Integer.toString(choices.get(3));
        result[8]=Integer.toString(answerIndex);


        return new ArrayList<>(Arrays.asList(result));




    }
    
    public static ArrayList<String> getQuizQuestion(int level)
    {
        level-=1;
        //set level
        if(level>9)
        {
            level=9;
        }
        //select operator
        double rand=Math.random();
        int index=0;
        for(int i=0;i<4;i++)
        {
            if(rand<=operatorProbs[level][i])
            {
                index=i;
                break;
            }
            
        }
        String operator=operatorsList[index];
        
        //select oprands
        index=level;
        int baseNumber=oprandsList[index];
        
        int oprand1=(int)(Math.random()*baseNumber)+1;
        int answer=(int)(Math.random()*baseNumber)+1;
        int oprand2=0;
        
        if(operator.equals("+"))
        {
            oprand2=answer-oprand1;
        }
        else if(operator.equals("-"))
        {
            oprand2=oprand1-answer;
        }
        else if(operator.equals("\u00D7"))
        {
            oprand2=answer;
            answer=oprand2*oprand1;
        }
        else
        {
            oprand2=oprand1;
            oprand1=oprand2*answer;
        }
        
        //build answers array
        ArrayList<Integer> choices=new ArrayList<Integer>();
        int ch1=(int)(Math.random()*answer*2);
        while(choices.contains(ch1) || ch1==oprand2) ch1+=3;
        choices.add(ch1);
        
        int ch2=(int)(Math.random()*answer*2);
        while(choices.contains(ch2) || ch2==oprand2) ch2+=3;
        choices.add(ch2);
        
        int ch3=(int)(Math.random()*answer*2);
        while(choices.contains(ch3) || ch3==oprand2) ch3+=3;
        choices.add(ch3);
        
        int answerIndex=(int)(Math.random()*4);
        choices.add(answerIndex, oprand2);
        
        String[] result=new String[10];
        result[0]=Integer.toString(oprand1);
        result[1]=operator;
        result[2]="?";
        result[3]="=";
        result[4]=Integer.toString(answer);
        result[5]=Integer.toString(choices.get(0));
        result[6]=Integer.toString(choices.get(1));
        result[7]=Integer.toString(choices.get(2));
        result[8]=Integer.toString(choices.get(3));
        result[9]=Integer.toString(answerIndex);
        
        
        return new ArrayList<>(Arrays.asList(result));
        
        
        
        
    }
    
    public static void main(String[] args) {
        System.out.println(MathRaceQuestionsGenerator.getCustomQuestion(0, 1, 1, 0, 3));
        System.out.println(MathRaceQuestionsGenerator.getCustomQuestion(0, 1, 1, 0, 3));
        System.out.println(MathRaceQuestionsGenerator.getCustomQuestion(0, 1, 1, 0, 3));
        System.out.println(MathRaceQuestionsGenerator.getCustomQuestion(0, 1, 1, 0, 3));
        System.out.println(MathRaceQuestionsGenerator.getCustomQuestion(0, 1, 1, 0, 3));
        System.out.println(MathRaceQuestionsGenerator.getCustomQuestion(0, 1, 1, 0, 3));
        System.out.println(MathRaceQuestionsGenerator.getCustomQuestion(0, 1, 1, 0, 3));
    }
    
}
