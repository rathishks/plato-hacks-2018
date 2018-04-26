import java.util.Scanner;

/*
 * https://open.kattis.com/problems/jez
 */

public class jez {
    private static int R, S, nd;
    private static long K, leaf;

    private static final int MAX_N = 20;

    private static int count(int x, int y_max) {
        int ret = 0;

        for (int k = MAX_N; k >= 0; k--) {
            if ((y_max & (1 << k)) != 0) {
                int j = Integer.bitCount(x & ((1 << k) - 1));
                ret += 1 << (k - j);

                if ((x & ( 1 << k)) != 0) {
                    break;
                }
            }
        }

        return ret;
    }

    private static int traverse(int r) {
        int s_max = nd - r, sr;

        if (nd % 2 == 0) {
            sr = nd - Math.max(0, nd - R + 1);
            s_max += (sr - r >= 0 && sr - r < leaf) ? 1 : 0;
        } else {
            sr = Math.max(0, nd - S + 1);
            s_max += (r - sr >= 0 && r - sr < leaf) ? 1 : 0;
        }
        s_max = Math.max(s_max, 0);
        s_max = Math.min(s_max, S);

        return count(r, s_max);
    }

    private static long add(long total, int x) {
        total += x;

        if (total > K) {
            return -1;
        }

        ++nd;
        leaf = K - total;

        return total;
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        R = s.nextInt();
        S = s.nextInt();
        K = s.nextLong();

        int m = Math.min(R, S);

        long total = 0;

        for (int i = 1; i <= m; i++) {
            total = add(total, i);

            if (total < 0) {
                break;
            }
        }

        if (total >= 0) {
            for (int i = 0; i < R + S - 2 * m; i++) {
                total = add(total, m);

                if (total < 0) {
                    break;
                }
            }
        }

        if (total >= 0) {
            for (int i = m - 1; i > 0; i--) {
                total = add(total, i);

                if (total < 0) {
                    break;
                }
            }
        }

        long ret = 0;

        for (int i = 0; i < R; i++) {
            ret += traverse(i);
        }

        System.out.println(ret);

        s.close();
    }
}
