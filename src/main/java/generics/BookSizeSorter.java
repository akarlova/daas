package generics;

public class BookSizeSorter<T> {

    public <T extends Book & Comparable<T>> T maxBook(T book1, T book2) {
        if (book1.compareTo(book2) < 0) {
            return book2;
        } else {
            return book1;
        }
    }

    public static void main(String[] args) {
        BookSizeSorter sorter = new BookSizeSorter();
        sorter.maxBook(new PocketBook(), new PocketBook());
    }
}
