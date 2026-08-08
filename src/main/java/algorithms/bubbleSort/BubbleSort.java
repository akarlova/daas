package algorithms.bubbleSort;

public class BubbleSort {

    static void bubbleSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false; // флажок: были ли обмены
            for (int j = 0; j < n - 1 - i; j++) { // -i: хвост уже отсортирован
                if (a[j] > a[j + 1]) {
                    int tmp = a[j]; // меняем соседей местами
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                    swapped = true;
                }
            }if
            (!swapped) break; // проход без обменов → всё готово
        }
    }
}
