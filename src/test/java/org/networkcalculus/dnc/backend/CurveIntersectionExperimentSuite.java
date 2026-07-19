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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("experiment")
public class CurveIntersectionExperimentSuite {

    private static final String OUTPUT_DIR = "target/test-outputs/";

    // Data holder representing a distinct test file run
    record ExperimentSuiteConfig(double target_r, double start_R, double jump, int decimalPlaces) {
    }

    @BeforeAll
    static void setupOutputDirectory() throws IOException {
        Files.createDirectories(Paths.get(OUTPUT_DIR));
    }

    @ParameterizedTest(name = "Suite Run: r={0}, R_start={1}, jump={2}")
    @MethodSource("generateSuiteConfigurations")
    void runExperimentSuite(ExperimentSuiteConfig config) throws IOException {
        // 1. Initialize the unique file for this specific parameter combination
        String fileName = String.format("%sinter_r%s_startR%s_j%s_p%d.csv",
                OUTPUT_DIR, config.target_r(), config.start_R(), config.jump(), config.decimalPlaces());

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            writer.println("# EXPERIMENT CONFIGURATION");
            writer.println("# Target r: " + config.target_r());
            writer.println("# Start R: " + config.start_R());
            writer.println("# Jump size: " + config.jump());
            writer.println("# Decimal Places: " + config.decimalPlaces());
            writer.println();
            writer.println("Current_R,Intersection_Result");
        }

        // 2. Generate the internal rate progression for this specific suite item
        double scale = Math.pow(10, config.decimalPlaces());

        Stream.iterate(config.start_R(), val -> val - config.jump())
                .map(val -> Math.round(val * scale))
                .takeWhile(scaledVal -> (scaledVal / scale) > config.target_r())
                .map(scaledVal -> new Number((long) scaledVal, (long) scale))
                .forEach(currentR -> {
                    Curve a = CurveFactory.createTokenBucket(new Number(config.target_r()), new Number(1));
                    Curve b = CurveFactory.createRateLatency(currentR, Number.ONE);

                    Number result = CurveUtils.getFirstIntersection(a, b);
                    assertNotNull(result);

                    // 3. Append row execution to this configuration's unique file
                    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
                        writer.printf("%s,%s\n", currentR, result);
                    } catch (IOException e) {
                        System.err.println("Failed appending to " + fileName + ": " + e.getMessage());
                    }
                });
    }

    private static Stream<ExperimentSuiteConfig> generateSuiteConfigurations() {
        // Define your sweep ranges or distinct values you want to analyze
        double[] target_r_values = {1.5, 2.0, 3.0};
        double[] start_R_values = {4.0, 5.0};
        double[] jump_values = {0.05, 0.01};
        int decimalPlaces = 2;

        List<ExperimentSuiteConfig> configurations = new ArrayList<>();

        // Generate the cross-product (combinatorial combination) of all your testing variables
        for (double r : target_r_values) {
            for (double R : start_R_values) {
                for (double jump : jump_values) {
                    if (R > r) { // Business rule safety check before spinning up the run
                        configurations.add(new ExperimentSuiteConfig(r, R, jump, decimalPlaces));
                    }
                }
            }
        }
        return configurations.stream();
    }
}