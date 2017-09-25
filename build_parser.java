/**
 * Created by philipp on 5/23/17.
 */
public class build_parser {
    public static void main(String args[]) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("output_file"));
            String line;
            double tests = 0;
            int success = 0;
            while ((line = br.readLine()) != null) {
                if (line.matches(".*Tests.*\\d+, Failures: \\d+, Errors: \\d+, Skipped: \\d+, Time.*\\d+.\\d+.*")) {
                    System.out.println(line);
                    String sp[] = line.split(" ");
                    int tests2 = Integer.parseInt(sp[7].substring(0, sp[7].length() - 1));
                    tests += tests2;
                    //System.out.println(sp[7].substring(0, sp[7].length() - 1));
                    int fail = Integer.parseInt(sp[9].substring(0, sp[9].length() - 1));
                    //System.out.println(sp[9].substring(0, sp[9].length() - 1));
                    int err = Integer.parseInt(sp[11].substring(0, sp[11].length() - 1));
                    //System.out.println(sp[11].substring(0, sp[11].length() - 1));
                    int skip = Integer.parseInt(sp[13].substring(0, sp[13].length() - 1));
                    //System.out.println(sp[13].substring(0, sp[13].length() - 1));
                    success = success + (tests2 - err - skip - fail);
                    br.readLine();
                }

            }
            //System.out.println(tests + " " + fail + " " + err);
            //Output must me: "(xx.xx%) covered"
            double result = 100 * success / tests;
            //System.out.println(result);
            String out = "" + result;
            System.out.println("(" + out.substring(0, Math.min(5, out.length())) + "%) covered");
            PrintWriter w = new PrintWriter("num_tests", "UTF-8");
            w.println(tests);
            w.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
