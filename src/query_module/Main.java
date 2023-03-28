package query_module;

import indexer.InvertedIndex;

public class Main {

    public static void main(String[] args) {

        // carregar índice invertido do ficheiro .ser
        Load load = new Load();
        InvertedIndex invertedIndex = load.loadInvertedIndexFromFile();

        System.out.println(invertedIndex.getInvertedIndex());
    }
}