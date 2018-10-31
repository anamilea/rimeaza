package com.example.anamaria.licentafirsttry.Activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anamaria.licentafirsttry.R;

import java.util.ArrayList;
import java.util.Collections;

public class WordAdapter extends ArrayAdapter<Word> {
    private ArrayList<Word> rhymingWords;

    private int diff;

    public WordAdapter(Context context, int resource, ArrayList<Word> objects) {
        super(context, resource, objects);
        this.rhymingWords = objects;
        int max = Collections.max(rhymingWords).getFrequency();
        int min = Collections.min(rhymingWords).getFrequency();
        this.diff = max - min;
        if (this.diff == 0) {
            this.diff = 1;
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textViewWord;
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final View view = layoutInflater.inflate(R.layout.word_list_item, null);

        String word = rhymingWords.get(position).getText();
        textViewWord = (TextView) view.findViewById(R.id.textViewWord);
        textViewWord.setText(word);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageFrequency);
        int freq = rhymingWords.get(position).getFrequency();
        int proc = (freq * 100) / this.diff;
        if (proc >= 0 && proc < 30) {
            imageView.setBackgroundResource(R.drawable.low);
        } else if (proc >= 30 && proc < 60) {
            imageView.setBackgroundResource(R.drawable.half);
        } else {
            imageView.setBackgroundResource(R.drawable.full);
        }
        return view;
    }
}
