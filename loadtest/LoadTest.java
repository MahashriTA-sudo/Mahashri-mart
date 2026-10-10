import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple load test for MahashriMart.
 * 10 concurrent users, 60 seconds, each user sends requests one after another.
 * Run: java loadtest\LoadTest.java http://localhost:8080
 */
public class LoadTest {

    public static void main(String[] args) throws Exception {
        String base = args.length > 0 ? args[0] : "http://localhost:8080";
        int users = 10;
        int seconds = 60;
        String[] paths = {"/", "/api/v1/health", "/login"};

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        List<Long> times = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger errors = new AtomicInteger();
        long startAll = System.currentTimeMillis();
        long end = startAll + seconds * 1000L;

        System.out.println("Load test: " + users + " users, " + seconds + " seconds, target " + base);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < users; i++) {
            Thread t = new Thread(() -> {
                int n = 0;
                while (System.currentTimeMillis() < end) {
                    String path = paths[n++ % paths.length];
                    long start = System.nanoTime();
                    try {
                        HttpRequest req = HttpRequest.newBuilder(URI.create(base + path))
                                .timeout(Duration.ofSeconds(10))
                                .GET()
                                .build();
                        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
                        if (res.statusCode() != 200) {
                            errors.incrementAndGet();
                        }
                    } catch (Exception ex) {
                        errors.incrementAndGet();
                    }
                    times.add((System.nanoTime() - start) / 1_000_000);
                }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        double elapsed = (System.currentTimeMillis() - startAll) / 1000.0;
        List<Long> sorted = new ArrayList<>(times);
        Collections.sort(sorted);
        int total = sorted.size();
        long sum = 0;
        for (long v : sorted) {
            sum += v;
        }
        long avg = total == 0 ? 0 : sum / total;
        long p95 = total == 0 ? 0 : sorted.get((int) Math.ceil(0.95 * total) - 1);
        long max = total == 0 ? 0 : sorted.get(total - 1);

        System.out.println("----- RESULT -----");
        System.out.println("Concurrent users : " + users);
        System.out.printf("Duration (s)     : %.1f%n", elapsed);
        System.out.println("Total requests   : " + total);
        System.out.println("Errors           : " + errors.get());
        System.out.printf("Requests/second  : %.1f%n", total / elapsed);
        System.out.println("Average time ms  : " + avg);
        System.out.println("95th percentile  : " + p95 + " ms");
        System.out.println("Slowest request  : " + max + " ms");
    }
}