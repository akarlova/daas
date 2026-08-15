package dataStructures;

public class CustomNode<T extends Comparable<T>>{

    private T data;
    private CustomNode<T> nextNode;

    public CustomNode(T data) {
        this.data = data;
        this.nextNode = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public CustomNode<T> getNextNode() {
        return nextNode;
    }

    public void setNextNode(CustomNode<T> nextNode) {
        this.nextNode = nextNode;
    }

    @Override
    public String toString() {
        if (this.nextNode != null) {
            return data + " -> ";
        } else {
            return data + " -> null";
        }
    }

}
