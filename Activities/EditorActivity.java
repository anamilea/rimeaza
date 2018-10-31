package com.example.anamaria.licentafirsttry.Activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.anamaria.licentafirsttry.DBOpenHelper;
import com.example.anamaria.licentafirsttry.PoemsProvider;
import com.example.anamaria.licentafirsttry.R;

import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    private ArrayList<String> rhymingWords = new ArrayList<>();
    private AlertDialog.Builder alertDialogBuilder;
    private EditText editor;
    private EditText editTextTitle;
    private TextSwitcher textSwitcher;
    private FloatingActionButton floatingActionButton;
    private String poemFilter; //pt where clause
    private String oldText; //textul existent
    private String oldTitle; //titlul existent
    private String action; //poezie noua sau poezie existenta
    private boolean flagLightbulb = false; //pt a aprinde/stinge in functie de actiunile userului
    private boolean isSelected = false; //pt a vedea daca e selectat un cuvant in text
    private int currentIndex = -1; //index pt TextSwitcher
    private boolean isWord = true; //flag pt cuvant de pe ultima linie
    private Intent intent;
    private DBOpenHelper dbOpenHelper;
    private String wordToFindRhyme = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editor = (EditText) findViewById(R.id.editText);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        textSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        createTextSwitcher();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor.getText().toString().length() == 0) {
                    try {
                        Toast.makeText(getApplicationContext(), R.string.errorMessageNoText, Toast.LENGTH_SHORT).show();
                        throw new Exception("User has no input text");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!flagLightbulb) {
                        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_idea_light, null));
                        flagLightbulb = true;
                    }
                    if (rhymingWords.size() == 0) {
                        if (!editor.hasSelection()) { //daca nu exista text selectat iau ultimul cuvant
                            String[] lines = editor.getText().toString().split("\n");
                            if (lines.length <= 1) { //daca nu exista decat o linie sau niciuna
                                try {
                                    Toast.makeText(getApplicationContext(), R.string.errorMessageNoText, Toast.LENGTH_SHORT).show();
                                    isWord = false;
                                    throw new Exception("User has no input text");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                wordToFindRhyme = getLastWord(lines);
                                editor.setSelection(editor.getText().length());
                            }
                        } else { //daca exista cuvant selectat
                            wordToFindRhyme = editor.getText().toString().substring(
                                    editor.getSelectionStart(), editor.getSelectionEnd());
                            isSelected = true;
                        }
                        try {
                            findRhymingWords(wordToFindRhyme);
                        } catch (Exception e) {
                        }
                    }
                    textSwitcher.setVisibility(View.VISIBLE);
                    currentIndex++;

                    if (currentIndex == rhymingWords.size())
                        currentIndex = 0;
                    if (rhymingWords.size() == 0 && isWord) {
                        Toast.makeText(getApplicationContext(), R.string.errorMessageNoRhymingWords, Toast.LENGTH_SHORT).show();
                        turnOffLightBulb();
                    } else if (rhymingWords.size() > 0) {
                        textSwitcher.setText(rhymingWords.get(currentIndex));
                    }
                }
            }
        });

        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFlag();
            }
        });

        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkFlag();

            }
        });

        textSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = " " + rhymingWords.get(currentIndex).toString();
                if (!isSelected) {
                    int start = Math.max(editor.getSelectionStart(), 0);
                    int end = Math.max(editor.getSelectionEnd(), 0);
                    editor.getText().replace(Math.min(start, end), Math.max(start, end),
                            word, 0, word.length());
                } else {
                    editor.append(word);
                    editor.setSelection(editor.getText().length());
                }
            }
        });

        textSwitcher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent2 = new Intent(EditorActivity.this, WebViewActivity.class);
                intent2.putExtra("search", rhymingWords.get(currentIndex).toString());
                startActivity(intent2);
                return true;
            }
        });

        intent = getIntent();
        final Uri uri = intent.getParcelableExtra(PoemsProvider.CONTENT_ITEM_TYPE);
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.newPoem));
            editor.requestFocus();
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.editPoem));
            poemFilter = DBOpenHelper.POEM_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.POEMS_ALL_COLUMNS,
                    poemFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.POEM_TEXT));
            oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.POEM_TITLE));
            editor.setText(oldText);
            editTextTitle.setText(oldTitle);
            cursor.close();
        }


        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (rhymingWords.size() > 0) {
                    String newText = editor.getText().toString();
                    String newTitle = editTextTitle.getText().toString();
                    ContentValues contentValues = new ContentValues();
                    boolean isModified = false;
                    if (!newText.equals(oldText)) {
                        contentValues.put(DBOpenHelper.POEM_TEXT, newText);
                        oldText = newText;
                        isModified = true;
                    } else if (!newTitle.equals(oldTitle)) {
                        contentValues.put(DBOpenHelper.POEM_TITLE, newTitle);
                        oldTitle = newTitle;
                        isModified = true;
                    }
                    if (isModified) {
                        getContentResolver().update(PoemsProvider.CONTENT_URI, contentValues, poemFilter, null);
                    }
                    Intent intent1 = new Intent(EditorActivity.this, RhymesListActivity.class);
                    intent1.putExtra("word", wordToFindRhyme);
                    intent1.putExtra(PoemsProvider.CONTENT_ITEM_TYPE, uri);
                    startActivityForResult(intent1, 1);
                }
                return true;
            }
        });
    }

    private void checkFlag() {
        if (flagLightbulb) {
            turnOffLightBulb();
            textSwitcher.setText("");
            textSwitcher.setVisibility(View.INVISIBLE);
            rhymingWords = new ArrayList<>();
            currentIndex = 0;
        }
    }

    private void turnOffLightBulb() {
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_idea, null));
        flagLightbulb = false;
    }

    private void createTextSwitcher() {
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView myText = new TextView(EditorActivity.this);
                myText.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(17);
                myText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                return myText;
            }
        });

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        textSwitcher.setInAnimation(in);
        textSwitcher.setOutAnimation(out);
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();
        String newTitle = editTextTitle.getText().toString();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertPoem(newText, newTitle);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deletePoem();
                } else if (oldText.equals(newText) && oldTitle.equals(newTitle)) {
                    setResult(RESULT_CANCELED);
                } else if (oldText.equals(newText) && !oldTitle.equals(newTitle)) {
                    updatePoemTitle(newTitle);
                } else {
                    updatePoem(newText, newTitle);
                }
        }
        finish();
    }

    private void updatePoemTitle(String newTitle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.POEM_TITLE, newTitle);
        getContentResolver().update(PoemsProvider.CONTENT_URI, contentValues, poemFilter, null);
        Toast.makeText(this, R.string.poemTitleUpdated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void updatePoem(String newText, String poemTitle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.POEM_TEXT, newText);
        if (!poemTitle.equals(oldTitle)) {
            contentValues.put(DBOpenHelper.POEM_TITLE, poemTitle);
        }
        getContentResolver().update(PoemsProvider.CONTENT_URI, contentValues, poemFilter, null);
        Toast.makeText(this, R.string.poemUpdated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertPoem(String poemText, String poemTitle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.POEM_TEXT, poemText);
        contentValues.put(DBOpenHelper.POEM_TITLE, poemTitle);
        getContentResolver().insert(PoemsProvider.CONTENT_URI, contentValues);
        setResult(RESULT_OK);
    }

    private void deletePoem() {
        alertDialogBuilder = new AlertDialog.Builder(EditorActivity.this, R.style.AlertTheme);

        alertDialogBuilder
                .setMessage(R.string.deletePoemMessage)
                .setPositiveButton(R.string.positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(PoemsProvider.CONTENT_URI, poemFilter, null);
                        Toast.makeText(EditorActivity.this, R.string.poemDeleted, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton(R.string.negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_action_delete)
                .show();

    }

    private void findRhymingWords(String wordToFindRhyme) {
        String letters = getLast3Letters(wordToFindRhyme);
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
            if (!dbWord.equals(wordToFindRhyme)) {
                rhymingWords.add(dbWord);
            }
            cursor.moveToNext();
        }
        cursor.close();
        dbOpenHelper.close();
    }

    public String getLastWord(String[] lines) {
        int i = lines.length - 2; //ma pozitionez pe penultima cu i-ul
        String lastLine = "";
        while (i >= 0) {

            if (!(lines[i].equals("")) && !(lines[i].trim().length() == 0)) { //verific ca linia pe care sa ma aflu sa nu fie empty
                lastLine = lines[i]; //daca nu e, o retin
                break;
            } else {
                i--; //altfel, trec cu o linie mai sus
            }
        }
        String[] words = lastLine.split(" "); //sparg linia
        String lastWord = words[words.length - 1]; //iau ultimul cuvant de pe ultima linie care nu e goala
        lastWord = lastWord.replaceAll("[^a-zA-z]", "");
        return lastWord;
    }

    public static String getLast3Letters(String word) {
        if (word.length() == 3) {
            return word;
        } else if (word.length() > 3) {
            return word.substring(word.length() - 3);
        } else {
            return word;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.top_menu_edit_poem, menu);
        } else if (action.equals(Intent.ACTION_INSERT)) {
            getMenuInflater().inflate(R.menu.top_menu_edit_poem, menu);
            menu.findItem(R.id.actionDelete).setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.actionDelete:
                deletePoem();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        intent = data;
        String selectedWord = intent.getStringExtra("selected");
        if (selectedWord != null) {
            editor.setText(oldText + " " + selectedWord);
            editor.setSelection(editor.getText().length());
        }
    }
}
