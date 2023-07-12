import java.util.HashMap;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        HashMap<Integer, Integer> val2index = new HashMap<>();
        int[] num = new int[100005];
        int[] bin = new int[100005];
        int[] visited = new int[100005];
        int n, a, b;
        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();
        a = scanner.nextInt();
        b = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            num[i] = scanner.nextInt();
            val2index.put(num[i], i);
        }
        for (int i = 0; i < n; i++) {
            if (visited[i] == 0) {
                if (a > num[i]) {
                    Integer index = val2index.get(a - num[i]);
                    if (index != null && visited[index] == 0) {
                        visited[i] = 1;
                        visited[index] = 1;
                        bin[i] = 0;
                        bin[index] = 0;
                        continue;
                    }
                }
                if (b > num[i]) {
                    Integer index = val2index.get(b - num[i]);
                    if (index != null && visited[index] == 0) {
                        visited[i] = 1;
                        visited[index] = 1;
                        bin[i] = 1;
                        bin[index] = 1;
                        continue;
                    }
                    System.out.println("NO");
                    return;
                }
            }
        }
        System.out.println("YES");
        for (int i = 0; i < n; i++) {
            System.out.print(bin[i] + " ");
        }
    }
}
