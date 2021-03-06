import java.util.*;
import java.io.*;

/*
 * https://open.kattis.com/problems/subwayplanning
 */

class CPointComparator implements Comparator {
    public int compare(Object a, Object b) {
        double angleX = ((CPoint)a).angle;
        double angleY = ((CPoint)b).angle;

        if (angleX > angleY) return 1;
        if (angleX < angleY) return -1;
        
        return 0;
    }
}

class CPoint {
    public int x;
    public int y;
    public int d;
    public double angle;
    
    CPoint(int x, int y, int d) {
        this.x = x;
        this.y = y;
        this.d = d;
        angle = Math.atan2(y, x);
    }

    boolean needsSubway() {
        return this.r2() > (d * d);
    }

    int r2() {
        return ((x * x) + (y * y));
    }
    
    boolean overlaps(CPoint o) {
        if (!this.needsSubway() && !o.needsSubway()) {
            return true;
        }
        
        int tempLeft = (x * o.x) + (y * o.y) + (d * d);

        if (tempLeft < 0) {
            return false;
        }

        int leftSide = (tempLeft * tempLeft);
        int rightSide = (this.r2() - (d * d)) * (o.r2() - (d*d));

        return leftSide >= rightSide;
    }
}

class SubwaySystem {
    int[] a;
    int[] solution;

    CPoint[] p;
    boolean[][] overlaps;
    
    int n;
    
    SubwaySystem(int dimension) {
        n = 0;
        a = new int[dimension];
        solution = new int[dimension];
        p = new CPoint[dimension];
        overlaps = new boolean[dimension][dimension];
    }
    
    void addPoint(CPoint newPoint) {
        p[n++] = newPoint;
    }
    
    void reset() {
        for (int i = 0; i < n; i++) {
            a[i] = -1;
        }
    }

    void makeOverlaps() {
        for (int i = 0; i < n; i++) {
            boolean stillOverlapping = true;

            overlaps[i][i] = true;

            for (int j = (i + n - 1) % n; j != i; j = (j + n - 1) % n) {
                if (stillOverlapping) {
                    stillOverlapping = p[j].overlaps(p[i]);
                }

                overlaps[j][i] = stillOverlapping;
            }
        }
    }

    boolean canShareLines(int first, int last) {
        int i = first;

        for (i = first; i != last; i = (i + 1) % n) {
            if (!(p[last].overlaps(p[i]))) {
                return false;
            }
        }

        return true;
    }
    
    int getNeededLines(int starter, int current) {
        if (a[current] >= 0) {
            return a[current];
        }

        int i = current;
        int best, lastIncluded, candidate;

        if ((i + 1) % n == starter) {
            best = 1;
            lastIncluded = current;
        } else {
            lastIncluded = i;
            i = (i + 1) % n;
            best = this.getNeededLines(starter, i) + 1;
        
            while ((i != starter) && (overlaps[current][i])) {
                if ((i + 1) % n == starter) {
                    best = 1;
                    lastIncluded = i;

                    break;
                } else {
                    candidate = this.getNeededLines(starter, (i + 1) % n) + 1;
                    if (candidate < best) {
                        best = candidate;
                        lastIncluded = i;
                    }

                    i=(i + 1) % n;
                }
            }
        }

        a[current] = best;
        solution[current] = (lastIncluded + 1) % n;

        return best;
    }

    int minSubways() {
        if (n == 0) {
            return 0;
        }
        
        int bestSoFar = -1;
        
        Arrays.sort(p, 0, n, new CPointComparator());

        this.makeOverlaps();

        for (int i = 0; i < n; i++) {
            this.reset();

            int neededLines = this.getNeededLines(i,i);
            if (neededLines < bestSoFar || bestSoFar == -1) {
                bestSoFar = neededLines;
            }
        }

        return bestSoFar;
    }
}

public class subwayplanning {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        int N = s.nextInt();

        for (int i = 0; i < N; i++) {
            int n = s.nextInt();
            int d = s.nextInt();

            SubwaySystem subway = new SubwaySystem(n);

            for (int j = 0; j < n; j++) {
                CPoint pointCandidate = new CPoint(s.nextInt(), s.nextInt(), d);
                
                if (pointCandidate.needsSubway()) {
                    subway.addPoint(pointCandidate);
                }
            }
        
            System.out.println(subway.minSubways());
        }

        s.close();
    }
}