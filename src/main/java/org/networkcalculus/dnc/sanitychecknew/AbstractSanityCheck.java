package org.networkcalculus.dnc.sanitychecknew;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractSanityCheck implements Sanity_Check {

    protected void writeResultToFile(String checkName, String content) throws IOException {
        try {
            Path resultsDir = Paths.get("src/main/java/org/networkcalculus/dnc/sanitychecknew/resultsnew"
            );
            Files.createDirectories(resultsDir);
            Path resultFile = resultsDir.resolve(checkName + ".txt");
            Files.writeString(resultFile, content, StandardCharsets.UTF_8);
            System.out.println("Sanity check written to: " + resultFile.toAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException("Failed to write sanity check result", e);
        }
    }
}

