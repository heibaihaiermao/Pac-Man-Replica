import java.awt.*;

// PriorityQueue class: a queue of elements with values, smallest in the head, biggest in the tail
public class PriorityQueue {
    private LNode tail;
    private LNode head;
    public PriorityQueue() {
        tail = null;
        head = null;
    }

    // add an element
    public void add(Point currentPos, int v){
        if(head==null){
            tail = new LNode(currentPos,v, null, null);
            head = tail;
        }
        // smaller than head
        else if(v<=head.getVal()){
            LNode newNode = new LNode(currentPos,v,null,head);
            head.setPrevious(newNode);
            head = newNode;
        }
        // bigger than tail
        else if(v>=tail.getVal()){
            LNode newNode = new LNode(currentPos,v,tail,null);
            tail.setNext(newNode);
            tail = newNode;
        }
        else {
            LNode temp = head.getNext();
            while (temp != null) {
                if(v <= temp.getVal()){
                    LNode newNode = new LNode(currentPos,v,temp.getPrevious(),temp);
                    temp.getPrevious().setNext(newNode);
                    temp.setPrevious(newNode);
                    break;
                }
                temp = temp.getNext();
            }
        }
    }

    // remove the smallest element from the head of the queue and return the tile's position.
    public Point dequeue() {
        LNode temp = head;
        if(head.getNext()==null){
            head=null;
            tail=null;
            return temp.getPos();
        }
        else {
            head = temp.getNext();
            head.setPrevious(null);
            return temp.getPos();
        }
    }

    // remove the smallest element from the head of the queue and return the LNode.
    public LNode dequeueNode() {
        LNode temp = head;
        if(head.getNext()==null){
            head=null;
            tail=null;
            return temp;
        }
        else {
            head = temp.getNext();
            head.setPrevious(null);
            return temp;
        }
    }

    public boolean empty(){
        return head==null;
    }

    // output string
    @Override
    public String toString() {
        String ans = "";
        LNode tmp = head;
        while (tmp != null) {
            ans += tmp.getVal() + ", ";
            tmp = tmp.getNext();
        }
        if (!ans.equals("")) {
            ans = ans.substring(0, ans.length() - 2);
        }
        return "[" + ans + "]";
    }
}
