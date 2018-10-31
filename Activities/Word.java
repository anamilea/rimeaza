package com.example.anamaria.licentafirsttry.Activities;

import java.io.Serializable;

public class Word implements Serializable, Comparable<Word> {
    private String text;
    private int frequency;

    @Override
    public String toString() {
        return this.text;
    }

    public Word(String text, int frequency) {
        this.text = text;
        this.frequency = frequency;
    }

    public String getText() {
        return text;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(Word o) {
        return o.frequency < this.frequency ? 1 : -1;
    }
}
