import java.util.Scanner;

/*
 * https://open.kattis.com/problems/deathstar
 */
 
public class deathstar {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        int N = s.nextInt();
        long[] ret = new long[N];

        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
            {
                ret[i] |= s.nextLong();
            }
        }

        for (int i = 0; i < N; i++)
        {
            System.out.print(ret[i]);

            if (i < (N - 1)) System.out.print(" ");
        }

        System.out.println();

        s.close();
    }
}