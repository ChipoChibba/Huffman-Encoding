import java.util.Comparator;

/**
 * @author Chipo Chibbamulilo
 * @author Chikwanda Chisha
 * ,making a priority tree that stores tree elements and releases the one
 * with the least frequency
 * Don't forget to make comparator
 */
public class Comparable<T> implements Comparator<BinaryTree<CodeTreeElement>> {
    //making a compare method to suit the data given

    @Override
    public int compare(BinaryTree<CodeTreeElement> o1, BinaryTree<CodeTreeElement> o2) {
        return (int) (o1.data.getFrequency()-o2.data.getFrequency());
    }
}
