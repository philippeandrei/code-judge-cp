import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SimpleJudge {
    public static void main(String[] args) throws IOException {

        try {
            File workingDirectory = new File("/Users/Philippe/Documents/code-judge-cp/src/main/java");
            File sourceFile = new File("/Users/Philippe/Documents/code-judge-cp/src/main/java/TestJudge.java");

            System.out.println("Compiling...");
            ProcessBuilder compiler = new ProcessBuilder("javac", "TestJudge.java");
            compiler.directory(workingDirectory);
            Process compileProcess = compiler.start();
            compileProcess.waitFor();

            System.out.println("Running...");
            ProcessBuilder runner = new ProcessBuilder("java", "TestJudge");
            runner.directory(workingDirectory);
            Process runProcess = runner.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            runProcess.waitFor();


            System.out.println("Finished.");


        } catch (IOException e) {

        } catch (InterruptedException e) {

        } catch (Exception e) {

        }
    }
}
