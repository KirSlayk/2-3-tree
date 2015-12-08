/**
 * Created by Кирилл Слайковский on 01.11.2015.
 */

import java.util.*;

// класс вершины, класс-шаблон наследует сравнимость
class   Node<T extends Comparable> {
    // отец
    private Node<T> parent;
    // три ребенка
    private Node<T> leftChild;
    private Node<T> middleChild;
    private Node<T> rightChild;

    private T leftVal;
    private T rightVal;

    private boolean twoNode; // когда false - это 2-3 ячейка

    protected Node() {}

    public static <T extends Comparable> Node<T> newTwoNode(T value) {
        Node<T> node = new Node<T>();
        node.leftVal = value;
        node.twoNode = true;
        return node;
    }


    public static <T extends Comparable> Node<T> newThreeNode(T leftVal, T rightVal) {
        Node<T> node = new Node<T>();
        if (rightVal.compareTo(leftVal) > 0) {
            node.rightVal = rightVal;
            node.leftVal = leftVal;
        } else {
            node.leftVal = rightVal;
            node.rightVal = leftVal;
        }
        node.twoNode = false;
        return node;
    }

    public static HoleNode newHole() {
        return new HoleNode();
    }

    public void setLeftChild(Node<T> leftChild) {
        this.leftChild = leftChild;
        if (leftChild != null)
            leftChild.setParent(this);
    }

    public void setRightChild(Node<T> rightChild) {
        this.rightChild = rightChild;
        if (rightChild != null)
            rightChild.setParent(this);
    }

    public void setMiddleChild(Node<T> middleChild) {
        assert isThreeNode();
        this.middleChild = middleChild;
        if (middleChild != null) {
            middleChild.setParent(this);
        }
    }

    public void removeChildren() {
        this.leftChild = null;
        this.rightChild = null;
    }


    public final Node<T> parent() {
        return parent;
    }

    public final void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public boolean isTerminal() {
        return leftChild == null && rightChild == null;
    }

    // возвращаем значение вершины, если это все еще 1-2-вершина
    public T val() {
        assert isTwoNode();
        return leftVal;
    }

    public void setVal(T val) {
        assert isTwoNode();
        leftVal = val;
    }

    // возвращаем левое значение, если эта 2-3-вершина
    public T leftVal() {
        assert isThreeNode();
        return leftVal;
    }

    public T rightVal() {
        assert isThreeNode();
        return rightVal;
    }

    public void setLeftVal(T leftVal) {
        assert isThreeNode();
        this.leftVal = leftVal;
    }

    public void setRightVal(T rightVal) {
        assert isThreeNode();
        this.rightVal = rightVal;
    }

    public boolean isTwoNode() {
        // return rightVal == null;
        return twoNode;
    }

    public boolean isThreeNode() {
        return !isTwoNode();
    }

    public Node<T> leftChild() {
        return leftChild;
    }

    public Node<T> rightChild() {
        return rightChild;
    }

    public Node<T> middleChild() {
        assert isThreeNode();
        return middleChild;
    }

    @SuppressWarnings("unchecked")
    public void replaceChild(Node currentChild, Node newChild) {
        if (currentChild == leftChild) {
            leftChild = newChild;
        } else if (currentChild == rightChild) {
            rightChild = newChild;
        } else {
            assert  middleChild == currentChild;
            middleChild = newChild;
        }
        newChild.setParent(this);
        currentChild.setParent(null);
    }
}


/**
 * этот класс не имеет никаких значений и у него только один дитятя
 * класс мне нужен тупо для удаления элемента из дерева
 */
final class HoleNode<T extends Comparable> extends Node {
    private Node<T> child;

    HoleNode() {
        super();
    }

    public boolean isTwoNode() {
        return false;
    }

    // брата возвращаем у вершины
    public Node brother() {
        if (parent() != null) {
            return parent().leftChild() == this ? parent().rightChild(): parent().leftChild();
        }
        return null;
    }

    @Override
    public void setLeftChild(Node leftChild) {}

    @Override
    public void removeChildren() {
        child = null;
    }

    @Override
    public void setRightChild(Node rightChild) {}

    public Node<T> child() {
        return child;
    }

    public void setChild(Node<T> child) {
        this.child = child;
    }
}

// главный класс, шаблон наследует сравнимость, класс-шаблон наследует AbstractSet, дабы было проще реализовать Set, и интерфейс сортировки
@SuppressWarnings("unchecked")
public class TwoThreeTree<T extends Comparable> extends AbstractSet<T> implements SortedSet<T> {

    Node<T> root;
    int size = 0;

    public boolean add(T value) {
        if (root == null)
            root = Node.newTwoNode(value);
        else {
                Node<T> result = insert(value, root);
                if (result != null) {
                    root = result;
                }
        }
        size++;
        return true;
    }


    public boolean contains(T value) {
        return findNode(root, value) != null;
    }


    private Node<T> findNode(Node<T> node, T value) {
        if (node == null) return null;

        if (node.isThreeNode()) {
            int leftComp = value.compareTo(node.leftVal());
            int rightComp = value.compareTo(node.rightVal());
            if (leftComp == 0 || rightComp == 0) {
                return node;
            }
            if (leftComp < 0) {
                return findNode(node.leftChild(), value);
            } else if (rightComp < 0) {
                return findNode(node.middleChild(), value);
            } else {
                return findNode(node.rightChild(), value);
            }
        } else {
            int comp = value.compareTo(node.val());
            if (comp == 0)
                return node;
            if (comp < 0)
                return findNode(node.leftChild(), value);
            else
                return findNode(node.rightChild(), value);
        }
    }

    /*
    Пример вставки (работа функции insert)

    8

    8|10

     8
    6 10

     8
  6|7 10

     8
  6|7 10|15

    8|10
 6|7 9  15

      8
   6    10
  5 7  9  15

     */


    private Node<T> insert(T value, Node<T> node){
        Node<T> returnValue = null;
        // если это вершина 1-2 то
        if (node.isTwoNode()) {
            /*
            сравниваем value и левое значение нашей вершины.
            если у вершины нет детей, то записываем в new_node 2-3-вершину со значениями value и значения нашей вершины
            в отца записываем отца вевшины. Если отец есть, то для отца подменим детишек с node на new_node
            иначе батя сам становится сынком new_node
            */
            int comp = value.compareTo(node.val());
            if (node.isTerminal()) {
                Node<T> new_node = Node.newThreeNode(value, node.val());
                Node<T> parent = node.parent();
                if (parent != null)
                    parent.replaceChild(node, new_node);
                else
                    root = new_node;
            }
            // иначе
            else {
                /*
                если value < node.leftValue и при этом у node есть дитя, то
                в result забиваем insert(value, node.leftChild());
                если результат есть, (returnValue!=null), тогда мутим 2-3-вершину из result.val(), node.val()
                 */
                if (comp < 0) {
                    Node<T> result = insert(value, node.leftChild());
                    // если норм забили то
                    if (result != null) {
                        // создаем 2-3 вершину
                        Node<T> threeNode = Node.newThreeNode(result.val(), node.val());
                        threeNode.setRightChild(node.rightChild());
                        threeNode.setMiddleChild(result.rightChild());
                        threeNode.setLeftChild(result.leftChild());
                        if (node.parent() != null) {
                            node.parent().replaceChild(node, threeNode);
                        } else {
                            root = threeNode;
                        }
                        unlinkNode(node);
                    }
                }
                else if (comp >= 0) {
                    Node<T> result = insert(value, node.rightChild());
                    if (result != null) {
                        Node<T> threeNode = Node.newThreeNode(result.val(), node.val());
                        threeNode.setLeftChild(node.leftChild());
                        threeNode.setMiddleChild(result.leftChild());
                        threeNode.setRightChild(result.rightChild());
                        if (node.parent() != null) {
                            node.parent().replaceChild(node, threeNode);
                        } else {
                            root = threeNode;
                        }
                        unlinkNode(node);
                    }
                }

            }

        }
        else { // если это 2-3-вершина
            Node<T> threeNode = node;

            /*
            то же самое, только больше
             */
            int leftComp = value.compareTo(threeNode.leftVal());
            int rightComp = value.compareTo(threeNode.rightVal());

            if (threeNode.isTerminal()) {

                returnValue = splitNode(threeNode, value);

            } else {
                if (leftComp < 0) {
                    Node<T> result = insert(value, threeNode.leftChild());
                    if (result != null) {
                        returnValue = splitNode(threeNode, result.val());
                        returnValue.leftChild().setLeftChild(result.leftChild());
                        returnValue.leftChild().setRightChild(result.rightChild());
                        returnValue.rightChild().setLeftChild(threeNode.middleChild());
                        returnValue.rightChild().setRightChild((threeNode.rightChild()));
                        unlinkNode(threeNode);
                    }
                } else if (rightComp < 0) {
                    Node<T> result = insert(value, threeNode.middleChild());
                    if (result != null) {
                        returnValue = splitNode(threeNode, result.val());
                        returnValue.leftChild().setLeftChild(threeNode.leftChild());
                        returnValue.leftChild().setRightChild(result.leftChild());
                        returnValue.rightChild().setLeftChild(result.rightChild());
                        returnValue.rightChild().setRightChild(threeNode.rightChild());
                        unlinkNode(threeNode);
                    }
                } else  {
                    Node<T> result = insert(value, threeNode.rightChild());
                    if (result != null) {
                        returnValue = splitNode(threeNode, result.val());
                        returnValue.leftChild().setLeftChild(threeNode.leftChild());
                        returnValue.leftChild().setRightChild(threeNode.middleChild());
                        returnValue.rightChild().setLeftChild(result.leftChild());
                        returnValue.rightChild().setRightChild(result.rightChild());
                        unlinkNode(threeNode);
                    }
                }
            }
        }
        return returnValue;
    }



    public boolean remove(T value) {
        if (value == null)
            return false;
        //  System.out.println("removing " + value);
        Node<T> node = findNode(root, value);
        if (node == null)
            return false;

        HoleNode hole = null;
        Node<T> terminalNode;
        T holeValue;
        if (node.isTerminal()) {
            terminalNode = node;
            holeValue = value;
        } else {
            if (node.isThreeNode()) {
                if (node.leftVal().equals(value)) {
                    Node<T> pred = predecessor(node, value);
                    holeValue = pred.isThreeNode() ? pred.rightVal() : pred.val();
                    node.setLeftVal(holeValue);
                    terminalNode = pred;
                } else {
                    Node<T> succ = successor(node, value);
                    holeValue = succ.isThreeNode() ? succ.leftVal() : succ.val();
                    node.setRightVal(holeValue);
                    terminalNode = succ;
                }
            } else {
                Node<T> succ = successor(node, value);
                holeValue = succ.isThreeNode() ? succ.leftVal() : succ.val();
                node.setVal(holeValue);
                terminalNode = succ;
            }
        }

        assert terminalNode.isTerminal();

        if (terminalNode.isThreeNode()) {
            T val = terminalNode.leftVal().equals(holeValue) ? terminalNode.rightVal() : terminalNode.leftVal();
            Node<T> twoNode = Node.newTwoNode(val);
            if (terminalNode.parent() != null) {
                terminalNode.parent().replaceChild(terminalNode, twoNode);
            } else {
                root = twoNode;
            }
        } else {
            if (terminalNode.parent() != null) {
                hole = Node.newHole();
                terminalNode.parent().replaceChild(terminalNode, hole);
            } else {
                root = null;
            }
        }

        while (hole != null) {
            // Вершина имеет 1-2-отца и 1-2-родственника (брата)
            if (hole.parent().isTwoNode() && hole.brother().isTwoNode()) {
                Node<T> parent = hole.parent();
                Node<T> brother = hole.brother();

                Node<T> threeNode = Node.newThreeNode(parent.val(), brother.val());
                if (parent.leftChild() == hole) {
                    threeNode.setLeftChild(hole.child());
                    threeNode.setMiddleChild(brother.leftChild());
                    threeNode.setRightChild(brother.rightChild());
                } else {
                    threeNode.setLeftChild(brother.leftChild());
                    threeNode.setMiddleChild(brother.rightChild());
                    threeNode.setRightChild(hole.child());
                }

                if (parent.parent() == null) {
                    unlinkNode(hole);
                    root = threeNode;
                    hole = null;
                } else {
                    hole.setChild(threeNode);
                    parent.parent().replaceChild(parent, hole);
                }
                unlinkNode(parent);
                unlinkNode(brother);

            }
            // hole имеет 1-2-отца и 2-3-брата
            else if (hole.parent().isTwoNode() && hole.brother().isThreeNode()) {
                Node<T> parent = hole.parent();
                Node<T> brother = hole.brother();

                if (parent.leftChild() == hole) {
                    Node<T> leftChild = Node.newTwoNode(parent.val());
                    Node<T> rightChild = Node.newTwoNode(brother.rightVal());
                    parent.setVal(brother.leftVal());
                    parent.replaceChild(hole, leftChild);
                    parent.replaceChild(brother, rightChild);
                    leftChild.setLeftChild(hole.child());
                    leftChild.setRightChild(brother.leftChild());
                    rightChild.setLeftChild(brother.middleChild());
                    rightChild.setRightChild(brother.rightChild());
                } else {
                    Node<T> leftChild = Node.newTwoNode(brother.leftVal());
                    Node<T> rightChild = Node.newTwoNode(parent.val());
                    parent.setVal(brother.rightVal()); // отцу ставим правое значение брата hole
                    parent.replaceChild(brother, leftChild); // меняем отцу детей на левое значение брата hole
                    parent.replaceChild(hole, rightChild); // и прошлое значение отца
                    leftChild.setLeftChild(brother.leftChild());
                    leftChild.setRightChild(brother.middleChild());
                    rightChild.setLeftChild(brother.rightChild());
                    rightChild.setRightChild(hole.child());
                }
                unlinkNode(hole);
                unlinkNode(brother);
                hole = null;
            }

            // hole имеет 2-3-отца и 1-2 брата
            else if (hole.parent().isThreeNode()) {
                Node<T> parent = hole.parent();

                // hole средний сын и его левый брат 1-2-ячейка
                if (parent.middleChild() == hole && parent.leftChild().isTwoNode()) {
                    //System.out.println("Case 3 (a) hole in the middle");
                    Node<T> leftChild = parent.leftChild();
                    Node<T> newParent = Node.newTwoNode(parent.rightVal());
                    Node<T> newLeftChild = Node.newThreeNode(leftChild.val(), parent.leftVal());
                    newParent.setLeftChild(newLeftChild);
                    newParent.setRightChild(parent.rightChild());
                    if (parent != root) {
                        parent.parent().replaceChild(parent, newParent);
                    } else {
                        root = newParent;
                    }

                    newLeftChild.setLeftChild(leftChild.leftChild());
                    newLeftChild.setMiddleChild(leftChild.rightChild());
                    newLeftChild.setRightChild(hole.child());

                    unlinkNode(parent);
                    unlinkNode(leftChild);
                    unlinkNode(hole);
                    hole = null;
                }
                // hole средний сын и его правый брат 1-2-ячейка
                else if (parent.middleChild() == hole && parent.rightChild().isTwoNode()) {
                    Node<T> rightChild = parent.rightChild();
                    Node<T> newParent = Node.newTwoNode(parent.leftVal());
                    Node<T> newRightChild = Node.newThreeNode(parent.rightVal(), rightChild.val());
                    newParent.setLeftChild(parent.leftChild());
                    newParent.setRightChild(newRightChild);
                    if (parent != root) {
                        parent.parent().replaceChild(parent, newParent);
                    } else {
                        root = newParent;
                    }
                    newRightChild.setLeftChild(hole.child());
                    newRightChild.setMiddleChild(rightChild.leftChild());
                    newRightChild.setRightChild(rightChild.rightChild());
                    unlinkNode(parent);
                    unlinkNode(rightChild);
                    unlinkNode(hole);
                    hole = null;
                }
                else if (parent.middleChild().isTwoNode()) {
                    Node<T> middleChild = parent.middleChild();

                    // hole левый сын
                    if (parent.leftChild() == hole) {
                        Node<T> newParent = Node.newTwoNode(parent.rightVal());
                        Node<T> leftChild = Node.newThreeNode(parent.leftVal(), middleChild.val());
                        newParent.setLeftChild(leftChild);
                        newParent.setRightChild(parent.rightChild());
                        if (parent != root) {
                            parent.parent().replaceChild(parent, newParent);
                        } else {
                            root = newParent;
                        }

                        leftChild.setLeftChild(hole.child());
                        leftChild.setMiddleChild(middleChild.leftChild());
                        leftChild.setRightChild(middleChild.rightChild());

                        unlinkNode(parent);
                        unlinkNode(hole);
                        unlinkNode(middleChild);
                        hole = null;
                    }
                    // hole правый сын
                    else if (parent.rightChild() == hole) {
                        Node<T> newParent = Node.newTwoNode(parent.leftVal());
                        Node<T> rightChild = Node.newThreeNode(middleChild.val(), parent.rightVal());
                        newParent.setRightChild(rightChild);
                        newParent.setLeftChild(parent.leftChild());
                        if (parent != root) {
                            parent.parent().replaceChild(parent, newParent);
                        } else {
                            root = newParent;
                        }

                        rightChild.setLeftChild(middleChild.leftChild());
                        rightChild.setMiddleChild(middleChild.rightChild());
                        rightChild.setRightChild(hole.child());

                        unlinkNode(parent);
                        unlinkNode(hole);
                        unlinkNode(middleChild);
                        hole = null;
                    }
                }

                // hole имеет 2-3-отца и 2-3-брата

                else if (parent.middleChild().isThreeNode()) {
                    Node<T> middleChild = parent.middleChild();
                    // hole левый сын
                    if (hole == parent.leftChild()) {
                        //System.out.println("Case 4 (a) hole is left child");
                        Node<T> newLeftChild = Node.newTwoNode(parent.leftVal());
                        Node<T> newMiddleChild = Node.newTwoNode(middleChild.rightVal());
                        parent.setLeftVal(middleChild.leftVal());
                        parent.setLeftChild(newLeftChild);
                        parent.setMiddleChild(newMiddleChild);
                        newLeftChild.setLeftChild(hole.child());
                        newLeftChild.setRightChild(middleChild.leftChild());
                        newMiddleChild.setLeftChild(middleChild.middleChild());
                        newMiddleChild.setRightChild(middleChild.rightChild());

                        unlinkNode(hole);
                        unlinkNode(middleChild);
                        hole = null;
                    }
                    // hole правый сын
                    else if (hole == parent.rightChild()) {
                        // System.out.println("Case 4 (b) hole is right child");
                        Node<T> newMiddleChild = Node.newTwoNode(middleChild.leftVal());
                        Node<T> newRightChild = Node.newTwoNode(parent.rightVal());
                        parent.setRightVal(middleChild.rightVal());
                        parent.setMiddleChild(newMiddleChild);
                        parent.setRightChild(newRightChild);
                        newMiddleChild.setLeftChild(middleChild.leftChild());
                        newMiddleChild.setRightChild(middleChild.middleChild());
                        newRightChild.setLeftChild(middleChild.rightChild());
                        newRightChild.setRightChild(hole.child());

                        unlinkNode(hole);
                        unlinkNode(middleChild);
                        hole = null;


                        //  hole средний сын, левый сын 2-3-вершина
                    } else if (hole == parent.middleChild() && parent.leftChild().isThreeNode()) {
                        Node<T> leftChild = parent.leftChild();
                        Node<T> newLeftChild = Node.newTwoNode(leftChild.leftVal());
                        Node<T> newMiddleChild = Node.newTwoNode(parent.leftVal());
                        parent.setLeftVal(leftChild.rightVal());
                        parent.setLeftChild(newLeftChild);
                        parent.setMiddleChild(newMiddleChild);
                        newLeftChild.setLeftChild(leftChild.leftChild());
                        newLeftChild.setRightChild(leftChild.middleChild());
                        newMiddleChild.setLeftChild(leftChild.rightChild());
                        newMiddleChild.setRightChild(hole.child());

                        unlinkNode(hole);
                        unlinkNode(leftChild);
                        hole = null;
                        // hole средний сын, правый сын 2-3-вершина
                    } else {
                        assert  (hole == parent.middleChild() && parent.rightChild().isThreeNode());
                        Node<T> rightChild = parent.rightChild();
                        Node<T> newRightChild = Node.newTwoNode(rightChild.rightVal());
                        Node<T> newMiddleChild = Node.newTwoNode(parent.rightVal());
                        parent.setRightVal(rightChild.leftVal());
                        parent.setMiddleChild(newMiddleChild);
                        parent.setRightChild(newRightChild);
                        newRightChild.setRightChild(rightChild.rightChild());
                        newRightChild.setLeftChild(rightChild.middleChild());
                        newMiddleChild.setRightChild(rightChild.leftChild());
                        newMiddleChild.setLeftChild(hole.child());

                        unlinkNode(hole);
                        unlinkNode(rightChild);
                        hole = null;
                    }
                }

            }
        }

        size--;
        return true;
    }

    // убирает все связи с другими вершинами
    private void unlinkNode(Node node) {
        node.removeChildren();
        node.setParent(null);
    }

    // возвращает левый лист самого левого ребенка среднего или же правого ребенка вершины node в случае, когда у
    // node есть дети. Иначе возвращает отца, у которого правый сын не равен node и который сам существует PS обожепомогите мне с формулировками
    private Node<T> successor(Node<T> node, T value) {
        if (node == null)
            return null;

        if (!node.isTerminal()) {
            Node<T> p;
            if (node.isThreeNode() && node.leftVal().equals(value)) {
                p = node.middleChild();
            } else {
                p = node.rightChild();
            }
            while (p.leftChild() != null) {
                p = p.leftChild();
            }
            return p;
        } else {
            Node<T> p = node.parent();
            if (p == null) return null;

            Node<T> ch = node;
            while (p != null && ch == p.rightChild()) {
                ch = p;
                p = p.parent();
            }
            return p != null ? p : null;
        }
    }

    // возвращает самый правы лист самого правого ребенка левого или среднего ребенка node
    private Node<T> predecessor(Node<T> node, T value) {
        if (node == null)
            return null;

        Node<T> p;
        if (!node.isTerminal()) {
            if (node.isThreeNode() && node.rightVal().equals(value)) {
                p = node.middleChild();
            } else {
                p = node.leftChild();
            }

            while (p.rightChild() != null) {
                p = p.rightChild();
            }
            return p;
        } else {
            throw new UnsupportedOperationException("Implement predecessor parent is not terminal node");
        }

    }

    // возвращает вершину с средним значением и два ребенка есть у нее
    private Node<T> splitNode(Node<T> threeNode, T value) {
        T min;
        T max;
        T middle;
        if (value.compareTo(threeNode.leftVal()) < 0) {
            min = value;
            middle = threeNode.leftVal();
            max = threeNode.rightVal();
        } else if (value.compareTo(threeNode.rightVal()) < 0) {
            min = threeNode.leftVal();
            middle = value;
            max = threeNode.rightVal();
        } else {
            min = threeNode.leftVal();
            max = value;
            middle = threeNode.rightVal();
        }

        Node<T> parent = Node.newTwoNode(middle);
        parent.setLeftChild(Node.newTwoNode(min));
        parent.setRightChild(Node.newTwoNode(max));
        return parent;
    }


    public interface Function<T> {
        void apply(T t);
    }


    // порядок обхода
    public  void inorderSearch(Node<T> node, Function<T> func) {
        if (node == null)
            return;
        inorderSearch(node.leftChild(), func);
        if (node.isThreeNode()) {
            Node<T> threeNode = node;
            func.apply(threeNode.leftVal());
            inorderSearch(threeNode.middleChild(), func);
            func.apply(threeNode.rightVal());
        } else {
            func.apply(node.val());
        }
        inorderSearch(node.rightChild(), func);
    }


    public Iterator<T> iterator() {

        return new Iterator<T>() {
            Node<T> nextNode;

            Deque<Node<T>> threeNodes = new ArrayDeque<Node<T>>();
            T next;
            {
                Node<T> node = root;
                while(node != null && node.leftChild() != null) {
                    node = node.leftChild();
                }
                nextNode = node;
            }
            public boolean hasNext() {
                return next != null || nextNode != null;
            }

            public T next() {
                T prev;
                if (next != null) {
                    prev = next;
                    next = null;
                    nextNode = successor(nextNode, prev);
                    return prev;
                }
                if (nextNode.isThreeNode()) {
                    if (nextNode.isTerminal()) {
                        next = nextNode.rightVal();
                        prev = nextNode.leftVal();
                    } else {
                        if (threeNodes.peekFirst() == nextNode) {
                            threeNodes.pollFirst();
                            prev = nextNode.rightVal();
                            nextNode = successor(nextNode, prev);
                        } else {
                            prev = nextNode.leftVal();
                            threeNodes.addFirst(nextNode);
                            nextNode = successor(nextNode, prev);
                        }
                    }
                } else {
                    prev = nextNode.val();
                    nextNode = successor(nextNode, prev);
                }
                return prev;
            }


            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

    }





    public Comparator<? super T> comparator() {
        return null;
    }

    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }

    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }


    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
    }

    // возвращает значение leftValue самого левого ребенка
    public T first() {
        Node<T> node = root;
        while (node.leftChild() != null) {
            node = node.leftChild();
        }
        return node.isThreeNode() ? node.leftVal() : node.val();
    }

    // бОльшее значение самого правого ребенка
    public T last() {
        Node<T> node = root;
        while (node.rightChild() != null) {
            node = node.rightChild();
        }
        return node.isThreeNode() ? node.rightVal() : node.val();
    }

    public int size() {
        return size;
    }


    @Override
    public boolean contains(Object o) {
        try {
            return contains ((T) o);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        try {
            return remove((T) o);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public void clear() {
        root = null;
    }


    @Override
    public Object[] toArray() {
        final Object arr[] = new Object[size];
        inorderSearch(root, new Function() {
            int index = 0;

            public void apply(Object t) {
                arr[index++] = (T) t;
            }
        });
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        T[] r = a.length >= size ? a : (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);

        return _toArray(r);
    }


    public <T> T[]  _toArray(final T[] a) {
        inorderSearch(root, new Function() {
            int index = 0;

            public void apply(Object t) {
                a[index++] = (T) t;
            }
        });
        return a;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removed = false;
        for (Object o : c) {
            removed |= remove(o);
        }
        return removed;
    }


    @Override
    public String toString() {
        if (size == 0)
            return "[]";
        final StringBuilder sb = new StringBuilder("[");
        inorderSearch(root, new Function<T>() {
            public void apply(T t) {
                sb.append(t);
                sb.append(", ");
            }
        });
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

}