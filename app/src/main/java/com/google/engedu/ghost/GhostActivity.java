/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    static final String GAME_STATUS = "gameStatus";
    static final String WORD_FRAGMENT = "wordFragment";
    static final String SAVED_USER_TURN = "savedUserTurn";

    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    public static String firstPlayer = "user";
    public static String currentPlayer = "user";
    TextView gameStatus;
    TextView ghostText;
    Button challenge;
    Button restart;
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private String wordFragment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();

        try {
            InputStream inputStream = assetManager.open("words.txt");
            // dictionary = new SimpleDictionary(inputStream);
            dictionary = new FastDictionary(inputStream);
        } catch(Exception e) {
            e.printStackTrace();
        }

        gameStatus = (TextView) findViewById(R.id.gameStatus);
        ghostText = (TextView) findViewById(R.id.ghostText);
        restart = (Button) findViewById(R.id.restart);
        challenge = (Button) findViewById(R.id.challenge);

        restart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onStart(v);
            }
        });

        onStart(null);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        gameStatus.setText(savedInstanceState.getString(GAME_STATUS));
        wordFragment = savedInstanceState.getString(WORD_FRAGMENT);
        ghostText.setText(wordFragment);
        userTurn = savedInstanceState.getBoolean(SAVED_USER_TURN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        restart.setClickable(true);
        challenge.setClickable(true);

        wordFragment = "";
        userTurn = random.nextBoolean();
        ghostText.setText("");
        if (userTurn) {
            gameStatus.setText(USER_TURN);
            firstPlayer = "user";
            currentPlayer = "user";
        } else {
            gameStatus.setText(COMPUTER_TURN);
            firstPlayer = "computer";
            currentPlayer = "computer";
            computerTurn();
        }
        return true;
    }

    public void challenge(View v) {
        /*
        If the current word fragment has at least 4 characters and is a valid word, declare victory for the user
        otherwise if a word can be formed with the fragment as prefix, declare victory for the computer and display a possible word
        If a word cannot be formed with the fragment, declare victory for the user
         */
        if(wordFragment.length() >= 4 && dictionary.isWord(wordFragment)) {
            gameStatus.setText(R.string.user_wins);
            challenge.setClickable(false);
            return;
        }
        String word = dictionary.getGoodWordStartingWith(wordFragment);
//        String word = dictionary.getAnyWordStartingWith(wordFragment);
        if (word == null) {
            gameStatus.setText(R.string.user_wins);
            challenge.setClickable(false);
        } else {
            gameStatus.setText(R.string.computer_wins);
            challenge.setClickable(false);
        }
    }

    private void computerTurn() {
        currentPlayer = "computer";

        // Do computer turn stuff then make it the user's turn again
        if(wordFragment.length() >= 4 && dictionary.isWord(wordFragment)) {
            gameStatus.setText(R.string.computer_wins);
            challenge.setClickable(false);
            return;
        }
        String word = dictionary.getGoodWordStartingWith(wordFragment);
//        String word = dictionary.getAnyWordStartingWith(wordFragment);
        if (word == null) {
            gameStatus.setText(R.string.computer_wins);
            challenge.setClickable(false);
        } else {
            wordFragment += word.substring(wordFragment.length(), wordFragment.length() + 1);
            ghostText.setText(wordFragment);
        }
        userTurn = true;
        currentPlayer = "user";
        gameStatus.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char c = (char) event.getUnicodeChar();
        if (!Character.isLetter(c)) {
            return super.onKeyUp(keyCode, event);
        }
        wordFragment += String.valueOf(c);
        ghostText.setText(wordFragment);
        if (dictionary.isWord(wordFragment)) {
            gameStatus.setText(R.string.computer_wins);
            challenge.setClickable(false);
        }
        computerTurn();
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        String status = gameStatus.getText().toString();
        savedInstanceState.putString(GAME_STATUS, status);
        savedInstanceState.putString(WORD_FRAGMENT, wordFragment);
        savedInstanceState.putBoolean(SAVED_USER_TURN, userTurn);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
