import java.util.*;

/*
 * https://open.kattis.com/problems/najkraci
 */

class Edge {
    int u;
    int v;
    int weight;

    int ret;
    Edge next;
}

public class najkraci {
    private static final int MAX_V = 3000;
    private static final int MAX_E = 10000;
    private static final int MOD = 1000000007;
    private static final int INFINITY = 1000000000;

    private static int n;
    private static Edge[] e = new Edge[MAX_E];
    private static Edge[] adj = new Edge[MAX_V];
    private static int[] pathsTo = new int[MAX_V];
    private static int[] pathsFrom = new int[MAX_V];
    private static int[] distance = new int[MAX_V];
    private static int[] settled = new int[MAX_V];

    private static void solve (int source) {
        for (int i = 0; i < n; i++) {
            distance[i] = INFINITY;
        }

        SortedSet<Integer> pq = new TreeSet<Integer>(new Comparator<Integer>() {
            public int compare(Integer x, Integer y) {
                if (distance[x] != distance[y]) {
                    return distance[x] - distance[y];
                }
                return x - y;
            }
        });

        distance[source] = 0;
        pathsTo[source] = 1;

        int nSettled = 0;
        pq.add(source);

        while (!pq.isEmpty()) {
            int u = pq.first();
            pq.remove(u);

            settled[nSettled++] = u;

            Edge e = adj[u];

            while (e != null) {
                if (distance[e.v] >= distance[u] + e.weight) {
                    if (distance[e.v] > distance[u] + e.weight) {
                        pathsTo[e.v] = 0;

                        if (distance[e.v] != INFINITY) {
                            pq.remove(e.v);
                        }

                        distance[e.v] = distance[u] + e.weight;
                        pq.add(e.v);
                    }

                    pathsTo[e.v] = (pathsTo[e.v] + pathsTo[u]) & MOD;
                }

                e = e.next;
            }
        }

        for (int i = nSettled - 1; i >= 0; i--) {
            int u = settled[i];

            pathsFrom[u] = 1;

            Edge e = adj[u];

            while (e != null) {
                if (distance[e.v] >= distance[u] + e.weight) {
                    pathsFrom[u] = (pathsFrom[u] + pathsFrom[e.v]) % MOD;
                    e.ret = (int)(((long)e.ret + ((long)pathsTo[u] * (long)pathsFrom[e.v])) % MOD);

                }

                e = e.next;
            }
        }
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        n = s.nextInt();
        int m = s.nextInt();

        for (int i = 0; i < m; i++) {
            e[i] = new Edge();

            e[i].u = s.nextInt();
            e[i].v = s.nextInt();
            e[i].weight = s.nextInt();

            e[i].u--;
            e[i].v--;

            e[i].next = adj[e[i].u];
            adj[e[i].u] = e[i];
        }

        for (int i = 0; i < n; i++) {
            solve(i);
        }

        for (int i = 0; i < m; i++) {
            System.out.println(e[i].ret);
        }

        s.close();
    }
}