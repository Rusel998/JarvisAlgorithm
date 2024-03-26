import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int numberOfSets = 50 + (int) (Math.random() * 51); // Генерация случайного числа от 50 до 100
        String filePath = "input_data.txt"; // Имя временного файла
        try {
            System.out.println("Сгенерировать новые данные? (да/нет)");
            String s = scan.nextLine();
            if(Confirm(s)){
                generateRandomDataToFile(filePath, numberOfSets);
            }
            List<List<Point>> allSets = readDataFromFile(filePath);

            for (List<Point> points : allSets) {
                List<Point> convexHull = convexHullJarvis(points);

                System.out.println("Точки выпуклой оболочки размера " + points.size() + ":");
                for (Point point : convexHull) {
                    System.out.println("(" + point.x + ", " + point.y + ")");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean Confirm(String s){
        if (s.equals("yes") || s.equals("да") || s.equals("y") || s.equals("Да") || s.equals("ДА")){
            return true;
        }else{
            return false;
        }
    }

    public static void generateRandomDataToFile(String filePath, int numberOfSets) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        Random random = new Random();

        for (int i = 0; i < numberOfSets; i++) {
            int setSize = 100 + (int) (Math.random() * 9901); // Генерация случайного размера от 100 до 10000
            writer.write(setSize + "\n"); // Записываем количество точек в наборе

            for (int j = 0; j < setSize; j++) {
                int x = random.nextInt(1000); // Adjust range as needed
                int y = random.nextInt(1000);
                writer.write(x + " " + y + "\n");
            }
        }

        writer.close();
    }

    public static List<List<Point>> readDataFromFile(String filePath) throws IOException {
        List<List<Point>> allSets = new ArrayList<>();
        Scanner scanner = new Scanner(new File(filePath));

        while (scanner.hasNextLine()) {
            int setSize = Integer.parseInt(scanner.nextLine());
            List<Point> points = new ArrayList<>();

            for (int i = 0; i < setSize; i++) {
                String[] line = scanner.nextLine().split(" ");
                points.add(new Point(Integer.parseInt(line[0]), Integer.parseInt(line[1])));
            }

            allSets.add(points);
        }

        scanner.close();
        return allSets;
    }

    // Функция для определения направления поворота точек p, q, r
    private static int orientation(Point p, Point q, Point r) {
        int val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (val == 0) return 0; // Коллинеарны
        return (val > 0) ? 1 : 2; // 1 - По часовой стрелке, 2 - Против часовой стрелки
    }

    public static List<Point> convexHullJarvis(List<Point> points) {
        int iterations = 0;
        long startTime = System.nanoTime();
        int n = points.size();
        if (n < 3) return null; // Не может быть выпуклой оболочки с менее чем 3 точками
        List<Point> convexHull = new ArrayList<>();
        // Найдем крайнюю правую точку
        int rightmost = 0;
        for (int i = 1; i < n; i++) {
            if (points.get(i).x > points.get(rightmost).x) {
                rightmost = i;
            }
            iterations++;
        }
        int p = rightmost, q;
        do {
            convexHull.add(points.get(p));
            q = (p + 1) % n;
            for (int i = 0; i < n; i++) {
                if (orientation(points.get(p), points.get(i), points.get(q)) == 2) {
                    q = i;
                }
                iterations++;
            }
            p = q;
            iterations++;
        } while (p != rightmost);
        long endTime = System.nanoTime();
        System.out.println("Алгоритм выполнялся " + (endTime-startTime) + " наносекунд");
        System.out.println("Кол-во итераций = " + iterations);
        return convexHull;
    }
}