package indexer;

import java.util.*;
import objects.InvertedIndex;
import objects.DocMap;
import objects.IdsMap;

public class Main {

    public static void main(String[] args) {

        // objeto scanner para ler da consola
        Scanner read = new Scanner(System.in);


        // caminho para a pasta a percorrer e a guardar informação
        System.out.println("\nIntroduza o caminho da pasta a analisar:");
        String path = read.nextLine();


        // criação de um objeto FilesRetriever, para implementar os seus métodos
        FilesRetriever filesRet = new FilesRetriever();

        // obtenção dos ficheiros na pasta escolhida para percorrer
        ArrayList<String> files = new ArrayList<>();
        files = filesRet.listFilesForFolder(path);
        System.out.println("\nOs ficheiros .txt e .xml contidos na pasta com o caminho " + path + " são: \n" + files);

        // obtenção do texto contido em cada ficheiro txt encontrado anteriormente
        ArrayList<String> listText = new ArrayList<>();
        //System.out.println("\nTexto contido nos ficheiros:");
        listText = filesRet.listTextFromFiles(files);


        // criação de um objeto DocumentManager, para implementar os seus métodos
        DocumentManager doc = new DocumentManager();

        // atribuição de um ID a cada ficheiro .txt encontrado na pasta inicial
        IdsMap filesIds;
        //System.out.println("\nAtribuição de ID a cada ficheiro da pasta:");
        filesIds = doc.attributeFileId(files);

        // criação de um mapa que inclui DocID - DocName - Texto corrido
        DocMap filesMap;
        //System.out.println("\nMapa DocID - DocPath - Texto corrido");
        filesMap = doc.filesIdsAndText(filesIds, listText);


        // criação do índice direto de doc id - termos
        HashMap<Integer, ArrayList<String>> directIdx = new HashMap<>();
        System.out.println("\nÍndice Direto de DocID - Termos (processados):");
        directIdx = doc.createDirectIndex(filesMap);

        // criação do índice invertido
        InvertedIndex invertedIdx = new InvertedIndex();
        System.out.println("\nÍndice Invertido:");
        invertedIdx = doc.createInvertedIndex(directIdx);
        System.out.println("\nTamanho do índice invertido: " + invertedIdx.getInvertedIndex().size());

        // escrita do índice serializado e do mapa de documentos em ficheiros
        doc.saveIndexToFile(invertedIdx);
        doc.saveDocMapToFile(filesIds);


        Integer opt = 1;
        while (opt != 0) {
            System.out.println("\n\n--------- MENU ---------" +
                    "\n1 - Procura por DOC. ID" +
                    "\n2 - Procura por termo" +
                    "\n3 - Escrita no ficheiro" +
                    "\n0 - Terminar");
            System.out.println("\nIntroduza uma opção: ");

            while (!read.hasNextInt()) {
                System.out.println("Introduza uma opção válida:");
                read.next();
            }
            opt = read.nextInt();

            switch (opt) {
                case 1:
                    while(true) {
                        // pesquisa por Doc ID
                        System.out.println("\nIntroduza o doc id a procurar (0 para voltar):");
                        Integer docid = read.nextInt();
                        if (docid == 0) {
                            break;
                        }
                        doc.searchDocID(filesMap, docid, directIdx);
                    }
                    break;
                case 2:
                    read.nextLine();
                    while(true) {
                        // pesquisa por termo
                        System.out.println("\nIntroduza o termo a procurar (0 para voltar):");
                        String term = read.nextLine().toLowerCase();
                        if (term.equals("0")) {
                            break;
                        }
                        doc.searchTerm(invertedIdx, term);
                    }
                    break;
                case 3:
                    // escrita do índice invertido global num ficheiro de texto "invertedIndex.txt"
                    doc.dumpData(invertedIdx, filesIds);
                    break;
                default: break;
            }
        }
    }
}