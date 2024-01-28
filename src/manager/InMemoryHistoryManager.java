package manager;

import module.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node next;
        Node prev;

        public Node(Node prev , Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    private final Map<Integer, Node> nodes = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodes.remove(id);
        if(node == null){
            return;
        }
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();

        Node node = head;
        while (node != null){
            history.add(node.task);
            node = node.next;
        }

        return history;
    }

    @Override
    public String toString() {
        List<Task> taskHistory = getHistory();
        StringBuilder sb = new StringBuilder();
        for (Task task : taskHistory) {
            sb.append(task.getId()).append(",");
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private void removeNode(Node node) {
        nodes.remove(node.task.getId());

        if(head == node) {
            head = node.next;
            if(head != null){
                head.prev = null;
            }
        }
        if(tail == node) {
            tail = node.prev;
            if(tail != null) {
                tail.next = null;
            }
        }
        else {
            if(node.prev != null) {
                node.prev.next = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }
        }
    }

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        }
        else {
            oldTail.next = newNode;
        }
        nodes.put(task.getId(), newNode);
    }
}