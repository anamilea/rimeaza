package com.example.anamaria.licentafirsttry.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.anamaria.licentafirsttry.DBOpenHelper;
import com.example.anamaria.licentafirsttry.PoemsProvider;
import com.example.anamaria.licentafirsttry.R;

import java.util.ArrayList;

public class RhymesListActivity extends AppCompatActivity {
    private ArrayList<Word> rhymingWords = new ArrayList<>();
    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase database;
    private ListView listView;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_rhymes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(R.string.rhymesList));

        Intent intent = getIntent();
        String word = intent.getStringExtra("word");
        listView = (ListView) findViewById(R.id.listViewRhymes);

        getList(word);
        WordAdapter wordAdapter = new WordAdapter(this.getApplicationContext(), R.layout.word_list_item, rhymingWords);
        listView.setAdapter(wordAdapter);

        uri = intent.getParcelableExtra(PoemsProvider.CONTENT_ITEM_TYPE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word selected = (Word) listView.getItemAtPosition(position);
                String selectedWord = selected.getText();
                returnToEditor(selectedWord);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RhymesListActivity.this, WebViewActivity.class);
                Word word = (Word) listView.getItemAtPosition(position);
                intent.putExtra("search", word.getText());
                startActivity(intent);
                return true;
            }
        });
    }

    private void getList(String word) {
        String letters = EditorActivity.getLast3Letters(word);
        letters = new StringBuilder(letters).reverse().toString();

        dbOpenHelper = new DBOpenHelper(this);
        database = dbOpenHelper.getReadableDatabase();
        String selection;
        String orderBy = DBOpenHelper.WORD_LETTERS + " ," + DBOpenHelper.WORD_FREQUENCY + " DESC";

        if (letters.length() == 3) {
            selection = DBOpenHelper.WORD_LETTERS + "= '" + letters + "'"
                    + " OR " + DBOpenHelper.WORD_LETTERS + " LIKE '" + letters + "%'";
        } else {
            selection = DBOpenHelper.WORD_LETTERS + " LIKE '" + letters + "%'";
        }

        Cursor cursor = database.query(DBOpenHelper.TABLE_WORDS, DBOpenHelper.WORDS_ALL_COLUMNS,
                selection, null, null, null, orderBy);
        cursor.moveToFirst();

        String dbWord;
        while (!cursor.isAfterLast()) {
            dbWord = cursor.getString(cursor.getColumnIndex(DBOpenHelper.WORD_TEXT));
            if (!dbWord.equals(word)) {
                rhymingWords.add(
                        new Word(dbWord, cursor.getInt(cursor.getColumnIndex(DBOpenHelper.WORD_FREQUENCY))));
            }
            cursor.moveToNext();
        }
        cursor.close();
        dbOpenHelper.close();
    }

    private void returnToEditor(String selectedWord) {
        Intent data = new Intent();
        data.putExtra(PoemsProvider.CONTENT_ITEM_TYPE, uri);
        if (selectedWord != null) {
            data.putExtra("selected", selectedWord);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnToEditor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                returnToEditor(null);
        }
        return true;
    }
}
