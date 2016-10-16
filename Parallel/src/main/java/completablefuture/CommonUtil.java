package completablefuture;
import java.util.LinkedList;
import java.util.List;

public class CommonUtil {
    public static <T> List<List<T>> group(List<T> input, int bulkSize) {
        List<List<T>> result = new LinkedList<>();
        int from = 0;
        int to = bulkSize;
        int size = input.size();

        while (to < size) {
            result.add(input.subList(from, to));
            from = to;
            to += bulkSize;
        }

        if (from < size) {
            result.add(input.subList(from, size));
        }
        return result;
    }

}
