package com.cf.util;

/**
 * Created by saurabh.jaluka on 10/8/15.
 */

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
/**
 * Created by saurabh.jaluka on 10/7/15.
 */
public class SynonymUtil {

    /*
     * Level e.g. :
     * 1 - car,auto
     * 2 - cell phone, mobile phone
     * 3 - a b c, x y z
     */
    private static final int MAX_LEVEL = 3;


    /*
     * if true will generate all the possibilities (starting from max to min level) of the synonym string
     * else will only look for a level synonyms (starting from max, if found it will stop looking for lower levels)
     *
     * e.g.
     *
     * content = a b c d
     * synonymSet = ["a b, x y", "a b c, r s t"]
     *
     * true:
     * result: ["r s t d","x y c d"]
     *
     * false:
     * result: ["r s t d"]
     */
    private static final boolean GENERATE_ALL = false;

    /*
     * Generate ngram of the given content string and given the value of n
     *
     * e.g.
     * content = "a b c d"
     * n = 2
     *
     * o/p: ["a b","b c","c d"]
     */
    public static List<String> generateNGram(String content, int n)
    {
        String[] tokens = content.split(" ");

        List<String> result = new ArrayList<>();

        for(int i = 0; i < tokens.length; i++)
        {
            if(i+n <= tokens.length)
            {
                StringBuilder string = new StringBuilder();
                for(int j = 0; j < n; j++)
                {
                    string.append(tokens[i+j]);
                    string.append(" ");
                }
                string.setLength(string.length()-1);
                result.add(string.toString());
            }
        }
        return result;
    }

    /*
     * Generate ngram of the given content string and given the value of n
     * this function will generate all the ngram from 1 to n.
     */
    public static List<List<String>> generate1ToNGram(String content, int n)
    {
        List<List<String>> results = new ArrayList<>();
        for(int i = n; i > 0 ; i--)
        {
            results.add(generateNGram(content,i));
        }
        return results;
    }

    /*
     * Generates the synonymStrings
     * i/p: content(search string), list of synonym set
     *
     * e.g.
     * content = "a b c d"
     * synonymSets = [["a b","x y"],["c d","r s"]]
     *
     * o/p: ["a b c d","a b r s","x y c d", "x y r s"]
     */
    public static Set<String> generateSynonymStrings(String content, List<Set<String>> synonymSets)
    {

        List<List<List<String>>> finalList = new ArrayList<>();
        String[] words = content.split(" ");

        for(int level = MAX_LEVEL; level > 0; level--)
        {

            boolean found = false;

            for(int i = 0; i < words.length; i++)
            {
                String left = subStringFromArray(words, 0, i);
                String right = subStringFromArray(words, i, words.length);

                List<String> ngrams = generateNGram(right,level);

                List<List<String>> tmpList = new ArrayList<>();

                List<List<String>> rightLists = getGreedySynonymList(right, ngrams, synonymSets);

                if(rightLists != null)
                {
                    found = true;

                    if(left.length() > 0)
                    {
                        List<List<String>> leftList = getListOfWords(left);
                        tmpList.addAll(leftList);

                    }

                    if(rightLists.size() > 0)
                    tmpList.addAll(rightLists);

                    finalList.add(tmpList);
                }
            }

            if(!GENERATE_ALL && found)
            {
                break;
            }

        }

        Set<String> queries = new HashSet<>();

        if(finalList.size() != 0)
        {
            for(List<List<String>> listsOfString: finalList)
            generatePermutations(listsOfString,queries,0,"");
        }

        return queries;
    }

    /*
     * This function puts individual word into a list and finally returns list of list of word.
     */
    private static List<List<String>> getListOfWords(String string)
    {

        List<List<String>> result = new ArrayList<>();
        String[] words = string.split(" ");
        for(String word: words)
        {
            List<String> list = new ArrayList<>();
            list.add(word);
            result.add(list);
        }
        return result;
    }

    /*
     * This function generates the string from array of words, given start index and end index of the subString we need
     */
    private static String subStringFromArray(String[] array, int index, int n)
    {
        StringBuilder string = new StringBuilder();
        for(int i = index; i < n ; i++)
        {
            string.append(array[i]);
            string.append(" ");
        }

        if(string.length() > 0)
            string.setLength(string.length() - 1);

        return string.toString();
    }

    /*
     * This function generate synonymlist for the given ngram greedily
     *
     * e.g
     *
     * synonymSets = [["a b","x y"],["b c","r s"]]
     * ngrams = ["a b","b c","c d"]
     *
     * i/p 1.
     * content: "a b c d"
     * o/p: [["a b","x y"],[c],[d]]
     *
     * note: after matching "a b" it skips to c.
     *
     * i/p 2.
     * content: "b c d"
     * o/p: [["b c","r s"],[d]]
     *
     *
     */
    public static List<List<String>> getGreedySynonymList(String content,List<String> ngrams, List<Set<String>> synonymSets)
    {
        List<List<String>> result = new ArrayList<>();

        boolean match = false;

        int movedWords = 0;

        for(int i = 0; i < ngrams.size();)
        {

            Set<String> set = getMatchingSet(ngrams.get(i), synonymSets);
            List<String> string = new ArrayList<>();
            String[] words = ngrams.get(i).split(" ");
            if(set.size() > 0)
            {
                string.addAll(set);
                movedWords+=words.length;
                i+=words.length;
                match = true;
            }
            else
            {
                string.add(words[0]);
                i++;
                movedWords++;
            }
            result.add(string);
        }
        if(match)
        {
            for(int y = movedWords; y < content.split(" ").length; y++)
            {
                List<String> word = new ArrayList<>();
                word.add(content.split(" ")[y]);
                result.add(word);
            }
            return result;
        }
        return null;
    }

    /*
     * This function compare the string to the list of sets.
     * Matched set items are put into a single set.
     */
    public static Set<String> getMatchingSet(String string, List<Set<String>> synonymSets) {

        Set<String> result = new HashSet<>();

        for (Set<String> set:synonymSets)
        {
            if(set.contains(string))
            {
                result.addAll(set);
            }
        }

        return result;
    }

    /*
     * generate permutations
     *
     * e.g.
     *
     * i/p: [["a b","x y"],[c],[d]]
     *
     * value in result:
     * ["a b c d","x y c d"]
     */
    private static void generatePermutations(List<List<String>> Lists, Set<String> result, int depth, String current)
    {
        if(depth == Lists.size())
        {
            result.add(current.trim());
            return;
        }

        for(int i = 0; i < Lists.get(depth).size(); ++i)
        {
            generatePermutations(Lists, result, depth + 1, current + " " + Lists.get(depth).get(i));
        }
    }

}
