package service.codeRunner;

import java.io.IOException;

public interface LanguageRunner {
    CompilationResult compile(String sourceFilePath, String workDir) throws Exception;
    ExecutionResult execute(String workDir, String input, long timeoutMs) throws IOException, InterruptedException;
    String getSupportedLanguage();
}
