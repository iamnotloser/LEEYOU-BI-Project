package com.yupi.springbootinit;

public class singletonTest {
    private static singletonTest instance;

    private singletonTest(){};

    public static synchronized singletonTest getinstance(){
        if(instance == null){
            instance = new singletonTest();
            return instance;
        }
        return instance;
    }
}
class Test{
    public static void main(String[] args) {
        singletonTest instance = singletonTest.getinstance();
        singletonTest instance1 = singletonTest.getinstance();
        System.out.println(instance == instance1);
    }
}
