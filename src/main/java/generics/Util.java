package generics;

import java.util.ArrayList;
import java.util.List;

public class Util {

    // <T> перед возвращаемым типом — объявление параметра метода
    public static <T> List<T> listOf(T... elements) {
        List<T> list = new ArrayList<>();
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }


    // Использование — компилятор выводит тип автоматически
    List<String> names = Util.listOf("Alice", "Bob", "Carol");
    List<Integer> numbers = Util.listOf(1, 2, 3);

    // Можно указать тип явно (редко нужно)
   // List<String> names = Util.<String>listOf("Alice", "Bob");
}
