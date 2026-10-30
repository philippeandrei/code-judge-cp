package service.codeRunner;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class LanguageRunnerFactory {
  private final Map<String, LanguageRunner> registry = new HashMap<>();

  @Inject
  public LanguageRunnerFactory() {
    register(new JavaRunner());
    register(new CppRunner());
  }

  public void register(LanguageRunner runner) {
    registry.put(runner.getSupportedLanguage().toLowerCase(), runner);
  }

  public LanguageRunner getRunner(String language) {
    if (language == null) {
      throw new IllegalArgumentException("Language cannot be null");
    }

    LanguageRunner runner = registry.get(language.toLowerCase());
    if (runner == null) {
      throw new IllegalArgumentException("Unsupported programming language: " + language);
    }
    return runner;
  }
}
