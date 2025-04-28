public class OneMapApproach {
    // Base command interface
    interface Command {
        void execute();
    }

    // Implementation of the command
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
            // Implementation for the Command interface
        }
    }

    public static void main(String[] args) {
        Command cmd = new AppendToCurrentLibrary("test");

        // This is the method we want to analyze in bytecode
        processCommand(cmd);
    }

    public static String processCommand(Command command) {
        return java.util.stream.Stream.of(command)
                                      .filter(AppendToCurrentLibrary.class::isInstance)
                                      .map(cmd -> ((AppendToCurrentLibrary) cmd).toAppend())
                                      .filter(java.util.Objects::nonNull)
                                      .findAny()
                                      .orElse(null);
    }
}