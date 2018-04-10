import java.util.*;

/*
 * https://open.kattis.com/problems/paintball
 */
 
public class paintball {
    private static int[] par;
    private static boolean[] bol;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        int n = s.nextInt(), 
            m = s.nextInt();

        List<List<Integer>> g = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            g.add(new ArrayList<Integer>());
        }

        for (int i = 0; i < m; i++)
        {
            int a = s.nextInt(), 
                b = s.nextInt();

            a--; b--;

            g.get(a).add(b);
            g.get(b).add(a);
        }

        int cnt = match(g, n, n);

        if (cnt < n) {
            System.out.println("Impossible");
        } else {
            for (int i = 0; i < n; i++) {
                System.out.println(par[i] + 1);
            }
        }

        s.close();
    }

    private static boolean find(int j, List<List<Integer>> g) {
        if (par[j] == -1) {
            return true;
        }

        int di = par[j];

        bol[j] = true;

        for (int i = 0; i < g.get(di).size(); i++) {
            int u = g.get(di).get(i);

            if (!bol[u] && find(u, g))
            {
                par[u] = di;
                par[j] = -1;

                return true;
            }
        }

        return false;
    }

    private static int match(List<List<Integer>> g, int n, int m) {
        par = new int[m];
        
        for (int i = 0; i < m; i++) {
            par[i] = -1;
        }

        bol = new boolean[m];

        int cnt = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                bol[j] = false;
            }

            for (int j = 0; j < g.get(i).size(); j++) {
                if (find(g.get(i).get(j), g))
                {
                    cnt++;
                    par[g.get(i).get(j)] = i;
                    break;
                }
            }
        }

        return cnt;
    }
}