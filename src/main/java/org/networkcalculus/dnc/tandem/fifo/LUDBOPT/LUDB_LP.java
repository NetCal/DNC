package org.networkcalculus.dnc.tandem.fifo.LUDBOPT;

import org.apache.commons.math3.util.Pair;
import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.num.Num;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Alexander Scheffler
 */
public class LUDB_LP {
    static boolean check_constant_constraints=true; //enables pre-filtering of infeasible constraints;
    static int fileId = 0;
    public static long cplex_time = 0;
    public static long terms_simplify_time = 0; // total time which our method takes to simplify the expressions (all expressions simplified so far)
    private static final boolean keepLPsOnDrive = false;

    public  static Pair<Double, Map<Integer, Double>> simplify_first_then_save_and_solve_lp_w_cplex(Expression_LUDB delay_term, List<Expression_LUDB> constraints, int number_vars) throws Exception {


        long start = System.nanoTime();
        Pair<Num, Map<Integer, Num>> pair_delay = delay_term.simplifyExpression();
        long end = System.nanoTime();
        long duration = end- start;
        terms_simplify_time = terms_simplify_time + duration;

        Num minus_one = Num.getUtils(Calculator.getInstance().getNumBackend()).create(-1);

        fileId++;
        File file_lp = new File(AnalysisConfig.path_to_lp_dir + "lp_" + fileId );
        Writer w_lp = new OutputStreamWriter(new FileOutputStream(file_lp), StandardCharsets.UTF_8);
        PrintWriter pw_lp = new PrintWriter(w_lp);
        pw_lp.println("Minimize");

        StringBuffer delay_str_buffer = new StringBuffer();

        Num constant_of_objective = pair_delay.getFirst();
        Map<Integer, Num> var_coeffs_map = pair_delay.getSecond();

        for(int var_id : var_coeffs_map.keySet())
        {
            Num var_coeff = var_coeffs_map.get(var_id);
            if(var_coeff.geqZero())
            {
                if (delay_str_buffer.length() == 0) {
                    delay_str_buffer.append(var_coeff + " s_" + var_id);
                } else {
                    delay_str_buffer.append(" + " + var_coeff + " s_" + var_id);
                }
            }
            else{
                if (delay_str_buffer.length() == 0) {
                    delay_str_buffer.append("- " + (Num.getUtils(Calculator.getInstance().getNumBackend()).mult(minus_one, var_coeff)) + " s_" + var_id);
                } else {
                    delay_str_buffer.append(" - " + (Num.getUtils(Calculator.getInstance().getNumBackend()).mult(minus_one, var_coeff)) + " s_" + var_id);
                }
            }
        }



        // costant term has to come last in the lp format...
        if (constant_of_objective.geqZero()) {
            if (delay_str_buffer.length() == 0) {
                delay_str_buffer.append(constant_of_objective);
            } else {
                delay_str_buffer.append(" + " + constant_of_objective);
            }
        } else {
            if (delay_str_buffer.length() == 0) {
                delay_str_buffer.append("- " + (Num.getUtils(Calculator.getInstance().getNumBackend()).mult(minus_one, constant_of_objective)));
            } else {
                delay_str_buffer.append(" - " + (Num.getUtils(Calculator.getInstance().getNumBackend()).mult(minus_one, constant_of_objective)));
            }
        }


        pw_lp.println(delay_str_buffer.toString());
        pw_lp.println("Subject To");

        // We have to adhere to the cplex lp file format restrictions:
        // * The left-side of the inequality is not allowed to hold constant(s) => Put the constant to the right-side (compute a new constant: r_constant - l_constant)
        // * The right-side of the inequality is not allowed to hold variables  => Put the vars of the right-side with their coefficients to the left-side (swap algebraic sign) -- we added code that also considers that the same variable could be on both sides
        // * In case both sides only hold constants, we use the pseudo-variable c_0 which represents 0 and put it on the left side and compute the new constant for the right side as mentioned above -- for this we will add 0 <= c_0 <= 0 to the bounds section
        // in the end (even if we do not use it -- cplex just gives a warning in that case which we then simply ignore)

        for (Expression_LUDB constraint : constraints) {
            start = System.nanoTime();
            Pair pair_constraint = constraint.simplifyConstraint();
            end = System.nanoTime();
            duration = end- start;
            terms_simplify_time = terms_simplify_time + duration;

            Pair<Num, Map<Integer, Num>> constraint_left_side = (Pair<Num, Map<Integer, Num>>) pair_constraint.getFirst();
            Pair<Num, Map<Integer, Num>> constraint_right_side = (Pair<Num, Map<Integer, Num>>) pair_constraint.getSecond();


            Num constant_left = constraint_left_side.getFirst();
            Num constant_right = constraint_right_side.getFirst();

            Map<Integer, Num> var_coeffs_left_map = constraint_left_side.getSecond();
            Map<Integer, Num> var_coeffs_right_map = constraint_right_side.getSecond();


            Num constant_new = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(constant_right, constant_left);

            List<Integer> common_var_ids = new ArrayList<>();
            for (int i : var_coeffs_left_map.keySet())
            {
                if(var_coeffs_right_map.containsKey(i))
                {
                    common_var_ids.add(i);
                }
            }

            for (Integer id_common : common_var_ids) {
                Num var_coeff_common_left = var_coeffs_left_map.get(id_common);
                Num var_coeff_common_right = var_coeffs_right_map.get(id_common);
                Num var_coeff_common_left_new = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(var_coeff_common_left, var_coeff_common_right);
                var_coeffs_left_map.put(id_common, var_coeff_common_left_new);
            }


            // Test for special case (both sides only hold constants)
            boolean constant_case = true;

            for(int i : var_coeffs_left_map.keySet())
            {
                if(!var_coeffs_left_map.get(i).eqZero())
                {
                    constant_case = false;
                    break;
                }
            }

            if(constant_case)
            {
                for(int i : var_coeffs_right_map.keySet())
                {
                    if(!common_var_ids.contains(i)) {
                        if (!var_coeffs_right_map.get(i).eqZero())
                        {
                            constant_case = false;
                            break;
                        }
                    }
                    // else: already in the updated left expression
                }
            }



            StringBuffer constraint_str_buffer = new StringBuffer();
            if (constant_case) {
                // The geq constraint was simplified and only constant terms are left
                constraint_str_buffer.append("c_0 >= " + constant_new);
                if (check_constant_constraints) {
                    double constant = Double.parseDouble(constant_new.toString());
                    if (constant > 0) {
                        // The constraint is violated. Hence, we can safely return the current LP as infeasible.
                        Double result = Double.NaN;
                        Map<Integer, Double>opt_s_values = new HashMap<>();
                        if(!keepLPsOnDrive)
                        {
                            file_lp.delete();
                        }
                        return new Pair<>(result, opt_s_values);
                    }
                }
            } else {
                // We have at least one variable with a non-zero coefficient

                for (int var_id : var_coeffs_left_map.keySet()) {
                    Num coeff_left_new = var_coeffs_left_map.get(var_id);
                    if (!coeff_left_new.eqZero()) {

                        if (coeff_left_new.geqZero()) {
                            if (constraint_str_buffer.length() == 0) {
                                constraint_str_buffer.append(coeff_left_new + " s_" + var_id);
                            } else {
                                constraint_str_buffer.append(" + " + coeff_left_new + " s_" + var_id);
                            }
                        } else {
                            if (constraint_str_buffer.length() == 0) {
                                constraint_str_buffer.append("- " + (Num.getUtils(Calculator.getInstance().getNumBackend()).mult(minus_one, coeff_left_new)) + " s_" + var_id);
                            } else {
                                constraint_str_buffer.append(" - " + (Num.getUtils(Calculator.getInstance().getNumBackend()).mult(minus_one, coeff_left_new)) + " s_" + var_id);
                            }

                        }
                    }
                }


                for (int var_id : var_coeffs_right_map.keySet()) {
                    Num coeff_right = var_coeffs_right_map.get(var_id);
                    if (!coeff_right.eqZero()) {

                        coeff_right = Num.getUtils(Calculator.getInstance().getNumBackend()).mult(minus_one, coeff_right); // swap algebraic sign bc we will put coeff_right * var_right to the left side


                        if (!common_var_ids.contains(var_id)) {
                            if (coeff_right.geqZero()) {
                                if (constraint_str_buffer.length() == 0) {
                                    constraint_str_buffer.append(coeff_right + " s_" + var_id);
                                } else {
                                    constraint_str_buffer.append(" + " + coeff_right + " s_" + var_id);
                                }
                            } else {
                                if (constraint_str_buffer.length() == 0) {
                                    constraint_str_buffer.append("- " + (Num.getUtils(Calculator.getInstance().getNumBackend()).mult(minus_one, coeff_right)) + " s_" + var_id);
                                } else {
                                    constraint_str_buffer.append(" - " + (Num.getUtils(Calculator.getInstance().getNumBackend()).mult(minus_one, coeff_right)) + " s_" + var_id);
                                }

                            }
                        }
                        // else: already handled by var_coeffs_left
                    }
                }
                constraint_str_buffer.append(" >= " + constant_new);
            }

            pw_lp.println(constraint_str_buffer.toString());
        }

        pw_lp.println("Bounds");
        pw_lp.println("0 <= c_0 <= 0");
        pw_lp.println("End");
        pw_lp.flush();
        pw_lp.close();


        long start_cplex_time = System.nanoTime();
        InputStream terminal_output_cplex;
        Process process_cplex;

        ProcessBuilder pb_cplex = new ProcessBuilder("./cplex", "-c", "read", AnalysisConfig.path_to_lp_dir  + "lp_" + fileId , "lp", "optimize", "Display solution variables -", "quit");
        pb_cplex.directory(new File(AnalysisConfig.path_to_cplex));

        process_cplex = pb_cplex.start();

        terminal_output_cplex = process_cplex.getInputStream();


        Pair<Double, Map<Integer, Double>> result_cplex_pair = cplexDelayAndVarOpt(terminal_output_cplex, number_vars);


        long end_cplex_time = System.nanoTime();
        long duration_cplex_time_current_interval = end_cplex_time - start_cplex_time;
        cplex_time = cplex_time + duration_cplex_time_current_interval;

        if(!keepLPsOnDrive)
        {
            file_lp.delete();
        }
        return result_cplex_pair;
    }

    private static Pair<Double, Map<Integer, Double>> cplexDelayAndVarOpt(InputStream inputStream, int number_vars) throws IOException {
        BufferedReader br = null;

        Double result = Double.NaN;

        Map<Integer, Double>opt_s_values = new HashMap<>();
        br = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.contains("Infeasible")) {
                break;
            }

            if (line.contains("Objective = ")) {
                try {
                    result = Double.parseDouble(line.substring(line.indexOf("Objective =") + 12));
                } catch (Exception e) {
                    e.printStackTrace();
                    result = Double.NaN;
                }
            }
            if (line.startsWith("s_")) {
                String[] splited = line.split("\\s+");
                String var_name = splited[0];
                String[] var_name_splitted = var_name.split("s_");
                int var_id = Integer.parseInt(var_name_splitted[1]);
                Double var_value_opt = Double.parseDouble(splited[1]);
                opt_s_values.put(var_id, var_value_opt);
            }
        }
        if(opt_s_values.size() != number_vars && !result.equals(Double.NaN))
        {
            // have to set some vars specifically to zero (cplex outputs with "Display solution variables -" the vars with their optimal setting if
            // that setting is != 0; it justs outputs that all the other vars are set to 0)
            for(int i = 0; i < number_vars; i++)
            {
                if(!opt_s_values.containsKey(i))
                {
                    // have to explicitly list s_i* = 0
                    opt_s_values.put(i, 0.0);
                }
            }
        }
        // else{  // all vars found or infeasible: nothing to do }
        return new Pair<>(result, opt_s_values);
    }
}



