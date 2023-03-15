public class Main {
    public static void main(String[] args) {

        Document doc = new Document();
        Document doc2 = new Document();

        String text = doc.readFile("teste.txt");
        String text2 = doc2.readFile("teste2.txt");

        String [] splitText = doc.splitData(text);
        String [] splitText2 = doc2.splitData(text2);

        doc.addEntry(splitText);
        doc2.addEntry(splitText2);

        doc.invertEntry();
        doc2.invertEntry();

        doc.printDirectIndex();

        doc.printInvertedIndex();
    }
}