import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String sentence = sc.nextLine();

        if(sentence.matches(".*\\d.*")){
            System.out.println("Digit Found in the Sentence\n"+sentence);
        }else{
            System.out.println(sentence.replaceAll(" ",""));
        }

    }
}
