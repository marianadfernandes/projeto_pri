package query_module;

import objects.InvertedIndex;
import objects.IdsMap;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // objeto scanner para ler da consola
        Scanner read = new Scanner(System.in);

        // carregar índice invertido do ficheiro .ser e também o mapa de ficheiros do ficheiro .ser
        Load load = new Load();
        InvertedIndex invertedIndex = load.loadInvertedIndexFromFile();
        IdsMap idsMap = load.loadIdsMapFromFile();

        Search search = new Search();
        QueryProcess processer = new QueryProcess();

        Integer opt = 1;
        while (opt != 0) {
            System.out.println("\n--------- MENU ---------" +
                    "\n1 - Pesquisa por termo" +
                    "\n2 - Pesquisa ANDNOT entre 2 termos" +
                    "\n3 - Pesquisa NOT de um termo" +
                    "\n4 - Pesquisa AND entre 2 termos" +
                    "\n5 - Pesquisa OR entre 2 termos" +
                    "\n6 - Pesquisa por query complexa" +
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
                    while(true) {
                        // pesquisa por termo (termo: posting list)
                        System.out.println("\nIntroduza o termo a procurar (0 para voltar):");
                        String term = read.nextLine();
                        if (term.equals("0")) {
                            break;
                        }
                        term = processer.normalizeTerm(term);
                        processer.searchTerm(term, invertedIndex);
                    }
                    break;
                case 2:
                    read.nextLine();
                    while(true) {
                        // pesquisa por ANDNOT entre 2 termos
                        System.out.println("\nIntroduza os termos entre um espaço (0 para voltar):");
                        String terms = read.nextLine();
                        if (terms.equals("0")) {
                            break;
                        }
                        String postLists = processer.processQueryTermsInPostLists(terms, invertedIndex);
                        ArrayList<ArrayList<Integer>> result = processer.processStringPLToArray(postLists);
                        search.searchANDNOT(result.get(0), result.get(1));
                    }
                    break;
                case 3:
                    read.nextLine();
                    while(true) {
                        // pesquisa por NOT de 1 termo
                        System.out.println("\nIntroduza o termo que pretende negar (0 para voltar):");
                        String term = read.nextLine();
                        if (term.equals("0")) {
                            break;
                        }
                        String postList = processer.processQueryTermsInPostLists(term, invertedIndex);
                        ArrayList<ArrayList<Integer>> result = processer.processStringPLToArray(postList);
                        search.searchNegation(result.get(0), idsMap);
                    }
                    break;
                case 4:
                    read.nextLine();
                    while(true) {
                        // pesquisa por AND entre 2 termos
                        System.out.println("\nIntroduza os termos entre um espaço (0 para voltar):");
                        String terms = read.nextLine();
                        if (terms.equals("0")) {
                            break;
                        }
                        String postLists = processer.processQueryTermsInPostLists(terms, invertedIndex);
                        ArrayList<ArrayList<Integer>> result = processer.processStringPLToArray(postLists);
                        search.searchIntersection(result.get(0), result.get(1));
                    }
                    break;
                case 5:
                    read.nextLine();
                    while(true) {
                        // pesquisa por OR entre 2 termos
                        System.out.println("\nIntroduza os termos entre um espaço (0 para voltar):");
                        String terms = read.nextLine();
                        if (terms.equals("0")) {
                            break;
                        }
                        String postLists = processer.processQueryTermsInPostLists(terms, invertedIndex);
                        ArrayList<ArrayList<Integer>> result = processer.processStringPLToArray(postLists);
                        search.searchUnion(result.get(0), result.get(1));
                    }
                    break;
                case 6:
                    read.nextLine();
                    while(true) {
                        // pesquisa por Query complexa
                        System.out.println("\nIntroduza uma query (0 para voltar):");
                        String query = read.nextLine();
                        if (query.equals("0")) {
                            break;
                        }
                        try {
                            search.querySolver(query, idsMap, invertedIndex);
                        } catch (Exception e) {
                            continue;
                        }
                    }
                    break;
                default: break;
            }
        }
    }
}