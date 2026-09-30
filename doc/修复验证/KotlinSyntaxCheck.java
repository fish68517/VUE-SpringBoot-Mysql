import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles;
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment;
import org.jetbrains.kotlin.config.CompilerConfiguration;
import org.jetbrains.kotlin.psi.KtPsiFactory;
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer;
import org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement;
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil;

class KotlinSyntaxCheck {
    public static void main(String[] args) throws Exception {
        var disposable = Disposer.newDisposable();
        try {
            var env = KotlinCoreEnvironment.Companion.createForProduction(
                disposable, new CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES);
            var factory = new KtPsiFactory(env.getProject(), false);
            List<Path> files;
            try (var paths = Files.walk(Path.of(args[0]))) {
                files = paths.filter(p -> p.toString().endsWith(".kt")).toList();
            }
            int errors = 0;
            for (var path : files) {
                var file = factory.createFile(path.getFileName().toString(),
                    Files.readString(path, StandardCharsets.UTF_8).replace("\uFEFF", "").replace("\r\n", "\n"));
                for (var error : PsiTreeUtil.collectElementsOfType(file, PsiErrorElement.class)) {
                    System.out.println(path + ":" + error.getTextOffset() + " " + error.getErrorDescription());
                    errors++;
                }
            }
            System.out.println("Kotlin syntax parsed: " + files.size() + " files; errors: " + errors);
            System.out.println("PSI parsing only. No Android application compilation or type checking.");
            if (errors > 0) throw new IllegalStateException("Kotlin syntax errors");
        } finally { Disposer.dispose(disposable); }
    }
}
