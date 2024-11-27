import java.awt.*;

public class LNode {
    private int val;
    private Point pos;
    private LNode next;
    private LNode previous;

    public LNode(Point posi, int v, LNode pre, LNode nex) {
        pos = posi;
        val = v;
        next = nex;
        previous = pre;
    }

    public int getVal() {
        return val;
    }

    public Point getPos(){return pos;}

    public LNode getNext() {
        return next;
    }

    public LNode getPrevious() {
        return previous;
    }

    public void setNext(LNode n) {
        next = n;
    }

    public void setPrevious(LNode n) {
        previous = n;
    }

    public LNode self(){
        return this;
    }

    @Override
    public String toString() {
        return "" + getVal();
    }
}
