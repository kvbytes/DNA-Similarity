/*
* DNA Similarity BST
* Math 140 - Data Structures
* Spring 2018
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.io.Serializable;
import static java.lang.System.in;
import java.util.ArrayList;
import java.util.Scanner;

public class BST<Key extends Comparable<Key>, Value>
        implements Iterable<Key>, Serializable {

    private Node root;
    public String word;
    public int counter;
    public int total;
    public ArrayList<String> foundKey = new ArrayList<>();

    public void put(Key key, Value val) //insert
    {
        root = put(root, key, val);
    }

    private Node put(Node x, Key key, Value val) {
        if (x == null) {
            return new Node(key, val);
        }
        int cmp = key.compareTo(x.key);
        if (cmp == 0) {
            x.val = val;
        } else if (cmp < 0) {
            x.left = put(x.left, key, val);
        } else {
            x.right = put(x.right, key, val);
        }
        return x;
    }

    public Value get(Key key) {  //search  
        Node x = root;
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp == 0) {
                return x.val;
            } else if (cmp < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        return null;
    }

    public Node getRootNode() {
        return root;
    }

    public void visit() {
        visit(root);
    }

    private void visit(Node x) {
        if (x == null) {
            return;
        }
        counter++;
        visit(x.left);
        visit(x.right);
    }

    public void search(String word) {
        search(root, word);
    }

    private void search(Node x, String word) {
        int w = word.length();
        int a[] = new int[w]; // holds word positions
        int j = 0;  // index for word[]
        int i = 0;  // index for value[]
        for (int k = 0; i < w; i++) { //initializing a
            a[i] = i;
        }
        if (x == null) {
            return;
        }
        search(x.left, word);

        {
            String s = (String) x.val;
            int v = s.length();
            int found = 0;
            while (i < v) {
                if (word.charAt(j) == s.charAt(i)) {
                    j++;
                    i++;
                }
                if (j == w) {
                    found++;
                    j = a[j - 1];
                } // mismatch after j matches
                else if (i < v && word.charAt(j) != s.charAt(i)) {
                    if (j != 0) {
                        j = a[j - 1];
                    } else {
                        i = i + 1;
                    }
                }
            }
            if (found != 0) {
                foundKey.add("    Found in " + x.key + " (" + found + " times)");
            }
            total += found;
        }
        search(x.right, word);

    }// end of search

    public int getNodesVisited() {
        return this.counter;
    }

    public Iterator<Key> iterator() {
        return new BSTIterator();
    }

    private class BSTIterator implements Iterator<Key>, Serializable {

        BSTIterator() {
        }

        public boolean hasNext() {
            return true;
        }

        public Key next() {
            Key k = null;
            return k;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        File f = new File("projc4.dat");
        String s = ""; //holds the search word
        BST<String, String> myTree;

        if (f.exists()) //if the file exists, read it in. Otherwise create the file
        {
            System.out.println("file exists, so will read existing tree in...\n\n");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
            myTree = (BST) in.readObject();
            in.close();
        } else //create the tree
        {
            System.out.println("file not found, so will create tree and add items...\n\n");
            myTree = new BST<String, String>();
            ArrayList<String> data = new ArrayList<>();
            BufferedReader in = new BufferedReader(new FileReader(args[0]));
            String str, key, value;
            int n = 0;
            value = "";
            while ((str = in.readLine()) != null) {
                if (str.length() > 0 && str.charAt(0) == '>') { // if it's a key
                    key = "";
                    for (int j = 1; j < str.length(); j++) {
                        key += str.charAt(j);
                    }
                    data.add(key);
                } else {
                    value += str;
                    if (str.length() == 0) {
                        data.add(value);
                        value = "";
                    }
                }

            }//end of while
            n = data.size();
            for (int j = 0; j < n - 1; j += 2) {
                myTree.put(data.get(j), data.get(j + 1));
            }

        }
        //The visit will simply be an inorder traversal that counts how many nodes are visited
        myTree.visit();  //make a wrapper class. use visit() which calls visit( Node)
        Scanner sc = new Scanner(new File(args[1])); // reading the search word from a file
        while (sc.hasNext()) {
            s = sc.nextLine(); // my search word
        }
        myTree.search(s);
        // writing on a file named results.txt
        try {
            FileWriter outFile = new FileWriter("results.txt");
            PrintWriter out = new PrintWriter(outFile);

            out.println("Target used: " + s);
            out.println("Number of nodes visited: " + myTree.getNodesVisited());
            out.println("Number of target instances found: " + myTree.total);
            out.println("Keys containing target:");
            for (int i = 0; i < myTree.foundKey.size(); i++) {
                out.println(myTree.foundKey.get(i));
            }
            myTree.counter = 0;
            myTree.total = 0;

            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        in.close();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
        out.writeObject(myTree);
        out.close();
    } // End of main

    private class Node implements Serializable {

        Key key;
        Value val;
        Node left, right;

        Node(Key key, Value val) {
            this.key = key;
            this.val = val;
        }
    }//end Node class
}// End of class
