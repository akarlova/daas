package dataStructures;

public class Main {

    public static void main(String[] args) {
        CustomSinglyLinkedList<Integer> list = new CustomSinglyLinkedList<>();
//        System.out.println("Size is: " + list.size());
//        System.out.println("Is list empty? " + list.isEmpty());

        list.addLast(1);
        list.addLast(2);

        System.out.println(list.toString());

    }
}
