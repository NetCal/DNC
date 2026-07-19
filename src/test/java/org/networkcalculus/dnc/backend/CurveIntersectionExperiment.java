package org.networkcalculus.dnc.backend;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.networkcalculus.dnc.backend.number.Number;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CurveIntersectionExperiment {

    // ----- Test Intersection accuracy parameters ----------
    private static final double TARGET_r = 2.0;
    private static final double START_R = 4.0;
    private static final double JUMP = 0.05;
    private static final int DECIMAL_PLACES = 2;

    private static final String OUTPUT_DIR = "target/test-outputs/";
    private static String fileName;

    @BeforeAll
    static void setupExperimentFile() throws IOException {
        Files.createDirectories(Paths.get(OUTPUT_DIR));

        // Group the file name by the static parameter suite configuration
        fileName = String.format("%sinter_r%s_startR%s_j%s_p%d.csv",
                OUTPUT_DIR, TARGET_r, START_R, JUMP, DECIMAL_PLACES);

        // Overwrite/initialize the single grouped file with metadata and headers
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            writer.println("# EXPERIMENT CONFIGURATION");
            writer.println("# Target r: " + TARGET_r);
            writer.println("# Start R: " + START_R);
            writer.println("# Jump size: " + JUMP);
            writer.println("# Decimal Places: " + DECIMAL_PLACES);
            writer.println();
            writer.println("Current_R,Intersection_Result");
        }
    }

    @ParameterizedTest(name = "Sweep R = {0}")
    @MethodSource("generateRates")
    void runIntersectionSimulation(Number currentR) {
        Curve a = CurveFactory.createTokenBucket(new Number(TARGET_r), new Number(1));
        Curve b = CurveFactory.createRateLatency(currentR, Number.ONE);

        Number result = CurveUtils.getFirstIntersection(a, b);
        assertNotNull(result);

        // Append this iteration directly to the grouped file
        appendResultToFile(currentR, result);
    }

    private static void appendResultToFile(Number rateR, Number intersectionResult) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.printf("%s,%s\n", rateR, intersectionResult);
        } catch (IOException e) {
            System.err.println("Failed to append to file " + fileName + ": " + e.getMessage());
        }
    }

    private static Stream<Number> generateRates() {
        double scale = Math.pow(10, DECIMAL_PLACES);

        return Stream.iterate(START_R, val -> val - JUMP)
                .map(val -> Math.round(val * scale))
                .takeWhile(scaledVal -> (scaledVal / scale) > TARGET_r)
                .map(scaledVal -> new Number((long) scaledVal, (long) scale));
    }
}