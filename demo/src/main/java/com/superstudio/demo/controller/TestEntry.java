package com.superstudio.demo.controller;

/**
 * Created by T440P on 2016-6-20.
 */
public class TestEntry {
    private String name;

    private  int Age;

    private  int Gender;

    private  TestEntry father;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public int getGender() {
        return Gender;
    }

    public void setGender(int gender) {
        Gender = gender;
    }

    public TestEntry getFather() {
        return father;
    }

    public void setFather(TestEntry father) {
        this.father = father;
    }
}
