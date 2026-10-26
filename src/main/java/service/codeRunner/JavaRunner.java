package service.codeRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class JavaRunner implements LanguageRunner {

    @Override
    public String getSupportedLanguage() {
        return "java";
    }

    @Override
    public CompilationResult compile(String sourceFilePath, String workDir) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File workingDirectory = new File(workDir);
        String fileName = sourceFile.getName();

        ProcessBuilder compiler = new ProcessBuilder("javac", fileName);
        compiler.directory(workingDirectory);
        Process compileProcess = compiler.start();

        String compilationErrors = new String(
                compileProcess.getErrorStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        int exitCode = compileProcess.waitFor();
        if (exitCode != 0) {
            return new CompilationResult(false, compilationErrors);
        }

        return new CompilationResult(true, "");
    }

    @Override
    public ExecutionResult execute(String workDir, String input, long timeoutMs) throws IOException, InterruptedException {
        File workingDirectory = new File(workDir);

        String className = "Solution";

        ProcessBuilder runner = new ProcessBuilder("java", className);
        runner.directory(workingDirectory);
        Process runProcess = runner.start();

        try (BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream(), StandardCharsets.UTF_8))) {
            stdin.write(input);
            stdin.flush();
        }

        boolean finished = runProcess.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
        if (!finished) {
            runProcess.destroyForcibly();
            return new ExecutionResult("", "Time Limit Exceeded", -1, true);
        }

        String output = new String(runProcess.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        String errorOutput = new String(runProcess.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        int exitCode = runProcess.exitValue();

        return new ExecutionResult(output, errorOutput, exitCode, false);
    }
}
