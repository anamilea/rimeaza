package com.example.anamaria.licentafirsttry;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class PoemsCursorAdapter extends CursorAdapter {

    public PoemsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.poem_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String poemTitle = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.POEM_TITLE));

        if (poemTitle.isEmpty()) {
            poemTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.POEM_TEXT));
            int pos = poemTitle.indexOf(10); //line =/n
            if (pos != -1) { //cazul de multi-line title
                poemTitle = poemTitle.substring(0, pos) + " ...";
            }
        }
        String date = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.POEM_CREATED));

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(poemTitle);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        SimpleDateFormat fromDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat myFormat = new SimpleDateFormat("EEE, d MMM");

        try {
            String showDate = myFormat.format(fromDB.parse(date));
            tvDate.setText(showDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}