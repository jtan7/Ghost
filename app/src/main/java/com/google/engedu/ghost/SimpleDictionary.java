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

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    private Random rand = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    public int findIndexOfWordWithPrefix(ArrayList<String> words, String prefix, int start, int end) {
        int range = end - start;
        if (range == 0) {
            return -1;
        } else {
            int mid = (start + end) / 2;
            String word = words.get(mid);
            if (word.equals(prefix)) {
                if (mid + 1 < words.size()) {
                    if (words.get(mid + 1).startsWith(prefix)) {
                        return mid + 1;
                    }
                }
                return -1;
            } else if (words.get(mid).compareTo(prefix) > 0) {
                // if word at index mid lexicographically follows the prefix
                if (word.startsWith(prefix)) {
                    return mid;
                }
                return findIndexOfWordWithPrefix(words, prefix, start, mid);
            } else {
                // if word at index mid lexicographically precedes the prefix
                return findIndexOfWordWithPrefix(words, prefix, mid + 1, end);
            }
        }
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if (prefix.equals("")) {
            int randomNum = rand.nextInt(words.size());
            return words.get(randomNum);
        } else {
            int index = findIndexOfWordWithPrefix(words, prefix, 0, words.size());
            if (index == -1) {
                return null;
            } else {
                return words.get(index);
            }
        }
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        int startIndex = findIndexOfWordWithPrefix(words, prefix, 0, words.size());

        if (startIndex == -1) {
            return null;
        }

        int endIndex = startIndex;
        while (endIndex < words.size()) {
            if (words.get(endIndex).startsWith(prefix)) {
                endIndex += 1;
            } else {
                break;
            }
        }

        ArrayList<String> odds = new ArrayList<>();
        ArrayList<String> evens = new ArrayList<>();

        for (int i = startIndex; i < endIndex; i += 1) {
            String word = words.get(i);
            if (word.length() % 2 == 0) {
                evens.add(word);
            } else {
                odds.add(word);
            }
        }

        int randomNum;
        int evensSize = evens.size();
        int oddsSize = odds.size();

        if ((GhostActivity.firstPlayer == "user" && GhostActivity.currentPlayer == "user") ||
            (GhostActivity.firstPlayer == "computer" && GhostActivity.currentPlayer == "computer")) {
            if (evensSize > 0) {
                randomNum = rand.nextInt(evens.size());
                return evens.get(randomNum);
            } else {
                randomNum = rand.nextInt(odds.size());
                return odds.get(randomNum);
            }

        } else {
            if (oddsSize > 0) {
                randomNum = rand.nextInt(odds.size());
                return odds.get(randomNum);
            } else {
                randomNum = rand.nextInt(evens.size());
                return evens.get(randomNum);
            }
        }
    }
}
