import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ParallelWithCompleteable {
    static ExecutorService es = Executors.newFixedThreadPool(20);

    public Integer doubleIt(Integer v) {
        try {
//            Thread.currentThread().sleep(1);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return v * 2;
    }

    public List<Integer> process(List<Integer> input) {
        return input.parallelStream().map(this::doubleIt).collect(Collectors.toList());
    }

    public void sequentialProcessing(List<List<Integer>> group) {
        List<Integer> finalResult = new ArrayList<>();
        long start = System.currentTimeMillis();
        List<List<Integer>> collect = group.stream().map(list -> process(list)).collect(Collectors.toList());
        for (List<Integer> e : collect) {
            finalResult.addAll(e);
        }
        System.out.println(finalResult + ". Size=" + finalResult.size() + ". It took: " + (System.currentTimeMillis() - start));
    }

    public void parallelProcess(List<List<Integer>> group) {
        long start = System.currentTimeMillis();
        List<Integer> finalResult = new ArrayList<>();
        group.parallelStream().forEach(list -> {
            CompletableFuture<List<Integer>> cf = CompletableFuture.supplyAsync(() -> process(list), es);
            finalResult.addAll(cf.join());
        });
        System.out.println(finalResult + ". Size=" + finalResult.size() + ". It took: " + (System.currentTimeMillis() - start));
    }

    public void parallelProcess2(List<List<Integer>> group) {
        long start = System.currentTimeMillis();
        List<Integer> finalResult = new ArrayList<>();
        List<CompletableFuture<List<Integer>>> cfs = group.stream().map(list -> CompletableFuture.supplyAsync(() -> process(list), es))
                .collect(Collectors.toList());
        CompletableFuture<List<List<Integer>>> sequence = sequence(cfs);
        try {
            List<List<Integer>> list = sequence.get();
            for (List<Integer> e : list) {
                finalResult.addAll(e);
            }
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(finalResult + "\nSize=" + finalResult.size() + ". It took: " + (System.currentTimeMillis() - start));
    }

    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v -> futures.stream().map(future -> future.join()).collect(Collectors.<T>toList()));
    }

    public static void main(String[] args) {
        long startInit = System.currentTimeMillis();
        List<Integer> orginal = new LinkedList<>();
        for (int k = 1; k <= 1000000; k++) {
            orginal.add(k);
        }
        System.out.println("Init took: " +(System.currentTimeMillis() - startInit));
        long startGroup = System.currentTimeMillis();
        List<List<Integer>> group = CommonUtil.group(orginal, 500);
        System.out.println("Group took: " +(System.currentTimeMillis() - startGroup));
        ParallelWithCompleteable pwc = new ParallelWithCompleteable();
//        for (int i = 0; i < 1; i++) {
//            pwc.sequentialProcessing(group);
//        }

        for (int i = 0; i < 1; i++) {
            pwc.parallelProcess2(group);
        }
        es.shutdown();
    }
}
