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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        if (s.equals("")) {
            children.put("\0", null);
        } else {
            String first = Character.toString(s.charAt(0));
            String rest = s.substring(1);
            if (children.containsKey(first)) {
                children.get(first).add(rest);
            } else {
                TrieNode newNode = new TrieNode();
                children.put(first, newNode);
                newNode.add(rest);
            }
        }
    }

    public boolean isWord(String s) {
        if (s.isEmpty() && children.containsKey("\0")) {
            return true;
        } else if (!s.isEmpty()) {
            String first = Character.toString(s.charAt(0));
            String rest = s.substring(1);
            return children.containsKey(first) && children.get(first).isWord(rest);
        }
        return false;
    }

    private TrieNode getNodeStartingWith(String s) {
        if (s.isEmpty()) {
            return this;
        } else {
            String first = Character.toString(s.charAt(0));
            String rest = s.substring(1);
            if (children.containsKey(first)) {
                return children.get(first).getNodeStartingWith(rest);
            } else {
                return null;
            }
        }
    }

    private String getAnyWordStartingWithHelper() {
        Random random = new Random();
        List<String> keys = new ArrayList<>(children.keySet());
        String randomKey = keys.get(random.nextInt(keys.size()));
        TrieNode node = children.get(randomKey);

        if (randomKey.equals("\0")) {
            return "";
        }

        return randomKey + node.getAnyWordStartingWithHelper();
    }

    public String getAnyWordStartingWith(String s) {
        TrieNode nodeWithS = getNodeStartingWith(s);
        if (nodeWithS == null) {
            return null;
        }

        return s + nodeWithS.getAnyWordStartingWithHelper();
    }

    private String getGoodWordStartingWithHelper() {
        List<String> keys = new ArrayList<>(children.keySet());
        if (keys.size() > 0) {
            Random random = new Random();
            String randomKey = keys.get(random.nextInt(keys.size()));
            TrieNode node = children.get(randomKey);
            return randomKey + node.getAnyWordStartingWithHelper();
        }

        return "";
    }

    public String getGoodWordStartingWith(String s) {
        TrieNode nodeWithS = getNodeStartingWith(s);
        if (nodeWithS == null) {
            return null;
        }

        return s + nodeWithS.getGoodWordStartingWithHelper();
    }
}
