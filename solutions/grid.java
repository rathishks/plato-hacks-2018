import java.util.*;

/*
 * https://open.kattis.com/problems/grid
 */
 
public class grid {
    private static int infinity = Integer.MAX_VALUE;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        int n = s.nextInt(),
            m = s.nextInt(),
            t = n * m;

        List<List<Integer>> adjacencyList = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();

        for (int i = 0; i < t; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        initGraph(adjacencyList, s, m, n);

        Integer[][] result = Dijkstra(adjacencyList, 0);
        int cost = result[0][t - 1];

        if (cost == infinity)
        {
            System.out.println(-1);
        }
        else
        {
            int steps = 0, cur = result[1][t - 1];

            while (cur >= 0 && cur != 0)
            {
                steps++;
                cur = result[1][cur];
            }

            steps++;

            System.out.println(steps);
        }

        s.close();
    }

    private static void initGraph(List<List<Integer>> adjacencyList, Scanner s, int m, int n) {
        int counter = 0;

        for (int i = 0; i < n; i++)
        {
            char[] chars = s.next().toCharArray();
            int[] line = new int[chars.length];

            for (int k = 0; k < chars.length; k++) {
                line[k] = chars[k] - '0';
            }
            
            for (int j = 0; j < m; j++)
            {
                int ti = i * m + j, tj = -1;

                int x = line[j];

                if (x > 0)
                {
                    tj = j - x;

                    if (tj >= 0)
                    {
                        tj = i * m + tj;

                        adjacencyList.get(ti).add(tj);
                    }

                    tj = j + x;

                    if (tj < m)
                    {
                        tj = i * m + tj;

                        adjacencyList.get(ti).add(tj);
                    }

                    tj = i - x;

                    if (tj >= 0)
                    {
                        tj = tj * m + j;

                        adjacencyList.get(ti).add(tj);
                    }

                    tj = i + x;

                    if (tj < n)
                    {
                        tj = tj * m + j;

                        adjacencyList.get(ti).add(tj);
                    }
                }

                counter++;
            }
        }
    }

    private static Integer[][] Dijkstra(List<List<Integer>> adjacencyList, int start) {
        int n = adjacencyList.size();

        Integer[] distance = new Integer[n];
        Integer[] previous = new Integer[n];
        
        Queue<Integer> queue = new LinkedList<>();
        
        queue.add(start);

        for (int i = 0; i < n; i++)
        {
            distance[i] = infinity;
            previous[i] = -1;
        }

        distance[start] = 0;

        while (queue.size() > 0)
        {
            int u = queue.remove();

            for (int i = 0; i < adjacencyList.get(u).size(); i++)
            {
                int v = adjacencyList.get(u).get(i);
                int newDistance = distance[u] + 1;

                if (distance[v] > newDistance)
                {
                    distance[v] = newDistance;
                    previous[v] = u;

                    queue.add(v);
                }
            }
        }
        
        return new Integer[][] { distance, previous };
    }
}