package algorithms.binarySearch;

public class BooksMinMaxPages {

    public static boolean check(int[] bookPages, int k, int limit) {

        int students = 1;
        int currentSum = 0;

        for (int pages : bookPages) {
            if (currentSum + pages > limit) {
                students++;
                currentSum = pages;
            } else {
                currentSum += pages;
            }
        }
        return (students <= k);
    }

    public static int binarySearch(int[] bookPages, int k) {
        int left = 0;
        int right = 0;

        for (int pages : bookPages) {
            left = Math.max(left, pages);
            right += pages;
        }

        while (left < right) {
            int mid = (left + right) / 2;
            if (check(bookPages, k, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        int[] pages = {12, 34, 67, 90};
        int k = 2;
        System.out.println("Min Max Pages: " + binarySearch(pages, k));
    }
}
