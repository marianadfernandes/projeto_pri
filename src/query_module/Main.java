package query_module;

import indexer.InvertedIndex;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // objeto scanner para ler da consola
        Scanner read = new Scanner(System.in);

        // carregar índice invertido do ficheiro .ser
        Load load = new Load();
        InvertedIndex invertedIndex = load.loadInvertedIndexFromFile();

        Search search = new Search();

        //System.out.println("inverted: " + invertedIndex.getInvertedIndex());

//        search.searchIntersection("available", "have", invertedIndex);
//
//        ArrayList<String> terms = new ArrayList<>();
//        terms.add("available");
//        terms.add("have");
//        terms.add("even");
//        search.searchIntersections(terms, invertedIndex);

        Integer opt = 1;
        while (opt != 0) {
            System.out.println("\n\n--------- MENU ---------" +
                    "\n1 - Pesquisa por termo" +
                    "\n2 - Pesquisa AND" +
                    "\n3 - Pesquisa termos" +
                    "\n0 - Terminar");
            System.out.println("\nIntroduza uma opção: ");

            while (!read.hasNextInt()) {
                System.out.println("Introduza uma opção válida:");
                read.next();
            }
            opt = read.nextInt();

            switch (opt) {
                case 1:
                    read.nextLine();
                    // pesquisa por Doc ID
                    System.out.println("\nIntroduza o termo:");
                    String term = read.nextLine().toLowerCase();
                    if (term.equals("0")) {
                        break;
                    }
                    search.searchTerm(term, invertedIndex);
                    break;
                case 2:
                    read.nextLine();
                    int count = 0;
                    ArrayList<String> termsInp = new ArrayList<>();
                    while(count<2) {
                        // pesquisa por termo
                        System.out.println("\nIntroduza um termo:");
                        termsInp.add(read.nextLine().toLowerCase());
                        count++;
                        if (termsInp.equals("0")) {
                            break;
                        }
                    }
                    search.searchIntersection(termsInp.get(0), termsInp.get(1), invertedIndex);

                    break;
                case 3:
                    read.nextLine();
                    ArrayList<String> termsListInp = new ArrayList<>();
                    while(true) {
                        // pesquisa por termo
                        System.out.println("\nIntroduza um termo ou 0 para terminar:");
                        String term1 = read.nextLine().toLowerCase();
                        if (term1.equals("0")) {
                            break;
                        }
                        termsListInp.add(term1);
                    }
                    search.searchIntersections(termsListInp, invertedIndex);
                default: break;
            }
        }


        //System.out.println(invertedIndex.getInvertedIndex());
    }
}