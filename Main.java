import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Кирилл on 01.11.2015.
 */

/*
вводить в cmd javac Main.java java Main ent.txt и файл вывода по желанию
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        TwoThreeTree<Integer> tree = new TwoThreeTree<>();
        if (args.length == 1 || args.length == 2 || args.length == 3 ) {
            String outFile = "";
            if (args.length == 2)
                outFile = args[1];
            else outFile = "ans.txt";
            File file = new File(outFile);
            try {
                if(!file.exists()){
                    file.createNewFile();
                }
                PrintWriter out = new PrintWriter(file.getAbsoluteFile());


                char[] str = FileWorker.read(args[0]).toCharArray();
                for (int i = 0; i < str.length; ++i) {
                    String buffer = "";
                    int num_of_operation = 100; // 1 - add; -1 - del; 0 - find; -2 - min; 2 - max
                    switch (str[i]) {
                        case '+':
                            num_of_operation = 1;
                            break;
                        case '-':
                            num_of_operation = -1;
                            break;
                        case '?':
                            num_of_operation = 0;
                            break;
                        case 'm':
                            num_of_operation = -2;
                            break;
                        case 'M':
                            num_of_operation = 2;
                            break;
                        default:
                            break;
                    }
                    while (i + 1 < str.length && (str[++i] != ' ' && str[i] != '\0' && str[i] != '\n'))
                        buffer += str[i];
                    switch (num_of_operation) {
                        case 1:
                            tree.add(Integer.parseInt(buffer));
                            FileWorker.write(out, tree.toString());
                            break;
                        case -1:
                            tree.remove(Integer.parseInt(buffer));
                            FileWorker.write(out, tree.toString());
                            break;
                        case 0:
                            if (tree.contains(Integer.parseInt(buffer)) == true)
                                FileWorker.write(out, buffer + " существует в дереве");
                            else FileWorker.write(out, buffer + " не существует в дереве");
                            break;
                        case -2:
                            FileWorker.write(out, tree.min());
                            break;
                        case 2:
                            FileWorker.write(out, tree.max());
                            break;
                        default:
                            break;
                    }
                }
                out.close();

            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        } else throw new FileNotFoundException();

    }
}