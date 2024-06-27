import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class KCentrosForcaBruta {

    private static int K; // Variável para armazenar o valor de K

    // Método para ler uma instância da OR-Library
    public static int[][] lerInstancia(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String primeiraLinha = br.readLine().trim();

        if (primeiraLinha.isEmpty()) {
            throw new IOException("Arquivo vazio ou formato inválido na primeira linha.");
        }

        String[] partesPrimeiraLinha = primeiraLinha.split(" ");
        if (partesPrimeiraLinha.length < 3) {
            throw new IOException("Formato inválido na primeira linha.");
        }

        int n = Integer.parseInt(partesPrimeiraLinha[0]); // Número de vértices
        int m = Integer.parseInt(partesPrimeiraLinha[1]); // Número de arestas
        K = Integer.parseInt(partesPrimeiraLinha[2]); // Terceiro valor da primeira linha

        // Inicializar o grafo com valores de infinito
        int[][] grafo = new int[n][n];
        for (int[] row : grafo) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        for (int i = 0; i < n; i++) {
            grafo[i][i] = 0;
        }

        String linha;
        while ((linha = br.readLine()) != null) {
            linha = linha.trim();
            if (linha.isEmpty()) {
                continue; // Ignorar linhas em branco
            }
            String[] partes = linha.split(" ");
            if (partes.length != 3) {
                System.err.println("Linha mal formatada ignorada: " + linha);
                continue; // Ignorar linhas mal formatadas
            }
            int u, v, w;
            try {
                u = Integer.parseInt(partes[0]) - 1;
                v = Integer.parseInt(partes[1]) - 1;
                w = Integer.parseInt(partes[2]);
            } catch (NumberFormatException e) {
                System.err.println("Número inválido na linha: " + linha);
                continue; // Ignorar linhas com números inválidos
            }
            grafo[u][v] = w;
            grafo[v][u] = w;
        }

        br.close();
        System.out.println("Valor de K: " + K); // Exibir o valor de K
        return grafo;
    }

    // Algoritmo de Floyd-Warshall para encontrar todas as distâncias mínimas
    public static int[][] floydWarshall(int[][] grafo) {
        int n = grafo.length;
        int[][] dist = new int[n][n];

        for (int i = 0; i < n; i++) {
            dist[i] = Arrays.copyOf(grafo[i], n);
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != Integer.MAX_VALUE && dist[k][j] != Integer.MAX_VALUE) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }

        return dist;
    }

    // Método para encontrar a melhor solução de k-centros usando força bruta
    public static void encontrarKCentros(int[][] distancias, int k) {
        int n = distancias.length;
        int[] centros = new int[k];
        int[] melhorCentros = new int[k];
        int[] melhorRaio = { Integer.MAX_VALUE };

        // Gerar todas as combinações possíveis de k vértices
        combinar(centros, 0, 0, n, k, distancias, melhorCentros, melhorRaio);

        // Imprimir a melhor solução
        System.out.println("Melhor combinação de centros: " + Arrays.toString(melhorCentros));
        System.out.println("Melhor raio: " + melhorRaio[0]);
    }

    private static void combinar(int[] centros, int pos, int start, int n, int k, int[][] distancias, int[] melhorCentros, int[] melhorRaio) {
        if (pos == k) {
            int raio = calcularRaio(centros, distancias);
            if (raio < melhorRaio[0]) {
                melhorRaio[0] = raio;
                System.arraycopy(centros, 0, melhorCentros, 0, k);
            }
            return;
        }

        for (int i = start; i < n; i++) {
            centros[pos] = i;
            combinar(centros, pos + 1, i + 1, n, k, distancias, melhorCentros, melhorRaio);
        }
    }

    private static int calcularRaio(int[] centros, int[][] distancias) {
        int n = distancias.length;
        int maxDist = 0;

        for (int i = 0; i < n; i++) {
            int minDist = Integer.MAX_VALUE;
            for (int centro : centros) {
                minDist = Math.min(minDist, distancias[i][centro]);
            }
            maxDist = Math.max(maxDist, minDist);
        }

        return maxDist;
    }

    public static void main(String[] args) {
        try {
            // Caminho do arquivo da instância
            String filePath = "teste.txt"; // Altere para o caminho do seu arquivo

            // Ler a instância do arquivo
            int[][] grafo = lerInstancia(filePath);

            // Calcular a matriz de distâncias mínimas usando Floyd-Warshall
            int[][] distancias = floydWarshall(grafo);

            /*// Imprimir a matriz de distâncias mínimas
            System.out.println("Matriz de Distâncias Mínimas:");
            for (int i = 0; i < distancias.length; i++) {
                for (int j = 0; j < distancias[i].length; j++) {
                    if (distancias[i][j] == Integer.MAX_VALUE) {
                        System.out.print("INF ");
                    } else {
                        System.out.print(distancias[i][j] + " ");
                    }
                }
                System.out.println();
            }*/

            // Encontrar a melhor solução de k-centros

            long startTime = System.currentTimeMillis();

            encontrarKCentros(distancias, K);

            long endTime = System.currentTimeMillis();

            long duration = (endTime - startTime);

            System.out.println(duration);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
