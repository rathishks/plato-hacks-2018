import java.util.Scanner;

/*
 * https://open.kattis.com/problems/temperature
 */
 
public class temperature {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        int x = s.nextInt();
        int y = s.nextInt();

        if (x == 0)
        {
            System.out.println(y == 1 ? "ALL GOOD" : "0");
        }
        else
        {
            if (y == 1) System.out.println("IMPOSSIBLE");
            else System.out.println((double)x / ((double)-y + 1D));
        }

        s.close();
    }
}