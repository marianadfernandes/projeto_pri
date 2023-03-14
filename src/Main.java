public class Main {
    public static void main(String[] args) {

        Document doc = new Document();

        String text = doc.readFile("teste.txt");

        doc.splitData(text);
    }
}