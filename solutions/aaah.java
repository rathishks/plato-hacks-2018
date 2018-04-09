import java.util.Scanner;

/*
 * https://open.kattis.com/problems/aaah
 */
 
public class aaah {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        String john = s.nextLine();
        String doctor = s.nextLine();

        john = john.replaceAll("^h+", "");
        doctor = doctor.replaceAll("^h+", "");

        System.out.println(john.length() >= doctor.length() ? "go" : "no");

        s.close();
    }
}