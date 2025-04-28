import java.util.Arrays;

public class MapPerformanceTest {
    static class AppendCommand {
        private final String value;

        public AppendCommand(String value) {
            this.value = value;
        }

        public String toAppend() {
            return value;
        }
    }

    interface Command {
        void execute();
    }

    static class AppendToCurrentLibrary implements Command {
        private final String value;

        public AppendToCurrentLibrary(String value) {
            this.value = value;
        }

        public String toAppend() {
            return value;
        }

        @Override
        public void execute() {
        }
    }

    public static void main(String[] args) {
        final int SIZE = 40000000;
        Command[] commands = new Command[SIZE];

        for (int i = 0; i < SIZE; i++) {
            if (i % 4 == 0) {
                commands[i] = new AppendToCurrentLibrary(null);
            } else if (i % 2 == 0) {
                commands[i] = new AppendToCurrentLibrary("Value" + i);
            } else {
                commands[i] = () -> {};
            }
        }

        System.out.println("Starting performance test with " + SIZE + " elements");
        System.out.println("Warming up JVM...");

        // Warm-up runs to allow JIT compilation
        for (int i = 0; i < 5; i++) {
            twoMapOperations(commands);
            oneMapOperation(commands);
        }

        System.out.println("\nRunning actual tests...");

        // Test runs
        final int RUNS = 10;
        long twoMapTotal = 0;
        long oneMapTotal = 0;

        for (int i = 0; i < RUNS; i++) {
            twoMapTotal += twoMapOperations(commands);
            oneMapTotal += oneMapOperation(commands);
        }

        System.out.println("\nResults averaged over " + RUNS + " runs:");
        System.out.println("Two map operations: " + (twoMapTotal / RUNS) + "ms");
        System.out.println("One map operation : " + (oneMapTotal / RUNS) + "ms");
        System.out.println("Difference       : " + ((twoMapTotal - oneMapTotal) / RUNS) + "ms");

        if (twoMapTotal > oneMapTotal) {
            double percentDiff = (double)(twoMapTotal - oneMapTotal) / twoMapTotal * 100;
            System.out.printf("One map is %.2f%% faster than two maps\n", percentDiff);
        } else if (oneMapTotal > twoMapTotal) {
            double percentDiff = (double)(oneMapTotal - twoMapTotal) / oneMapTotal * 100;
            System.out.printf("Two maps are %.2f%% faster than one map\n", percentDiff);
        } else {
            System.out.println("Both approaches perform equally");
        }

        // Verify that both methods return the same results
        verifyResults(commands);
    }

    private static long twoMapOperations(Command[] commands) {
        long start = System.currentTimeMillis();

        long count = Arrays.stream(commands)
                           .filter(AppendToCurrentLibrary.class::isInstance)
                           .map(AppendToCurrentLibrary.class::cast)
                           .map(AppendToCurrentLibrary::toAppend)
                           .filter(java.util.Objects::nonNull)
                           .count();

        long end = System.currentTimeMillis();
        return end - start;
    }

    private static long oneMapOperation(Command[] commands) {
        long start = System.currentTimeMillis();

        long count = Arrays.stream(commands)
                           .filter(AppendToCurrentLibrary.class::isInstance)
                           .map(command -> ((AppendToCurrentLibrary) command).toAppend())
                           .filter(java.util.Objects::nonNull)
                           .count();

        long end = System.currentTimeMillis();
        return end - start;
    }

    private static void verifyResults(Command[] commands) {
        System.out.println("\nVerifying results are identical...");

        java.util.List<String> resultsFromTwoMaps = java.util.Arrays.stream(commands)
                                                                    .filter(AppendToCurrentLibrary.class::isInstance)
                                                                    .map(AppendToCurrentLibrary.class::cast)
                                                                    .map(AppendToCurrentLibrary::toAppend)
                                                                    .filter(java.util.Objects::nonNull)
                                                                    .toList();

        java.util.List<String> resultsFromOneMap = java.util.Arrays.stream(commands)
                                                                   .filter(command -> command instanceof AppendToCurrentLibrary)
                                                                   .map(command -> ((AppendToCurrentLibrary) command).toAppend())
                                                                   .filter(java.util.Objects::nonNull)
                                                                   .toList();

        boolean resultsMatch = resultsFromTwoMaps.equals(resultsFromOneMap);
        System.out.println("Results match: " + resultsMatch);
        System.out.println("Found " + resultsFromOneMap.size() + " non-null values");
    }
}