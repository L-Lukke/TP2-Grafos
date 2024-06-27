import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KCentrosAproximado {

    private static int K; // Variável para armazenar o valor de K

    // Método para ler uma instância da OR-Library
    public static int[][] lerInstancia(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) { // Utilizando try-with-resources para garantir o fechamento do BufferedReader
            String primeiraLinha = br.readLine().trim();

            if (primeiraLinha.isEmpty()) { // Verificação de formato válido
                throw new IOException("Arquivo vazio ou formato inválido na primeira linha.");
            }

            String[] partesPrimeiraLinha = primeiraLinha.split("\\s+");
            if (partesPrimeiraLinha.length < 3) { // Verificação de formato válido
                throw new IOException("Formato inválido na primeira linha.");
            }

            int n = Integer.parseInt(partesPrimeiraLinha[0]); // Número de vértices
            int m = Integer.parseInt(partesPrimeiraLinha[1]); // Número de arestas
            K = Integer.parseInt(partesPrimeiraLinha[2]); // Valor de K

            int[][] grafo = new int[n][n]; // Inicializa o grafo com valores de infinito
            for (int[] row : grafo) {
                Arrays.fill(row, Integer.MAX_VALUE);
            }

            for (int i = 0; i < n; i++) {
                grafo[i][i] = 0; // Distância para si mesmo é 0
            }

            String linha;
            while ((linha = br.readLine()) != null) { // Leitura das arestas do grafo
                linha = linha.trim();
                if (linha.isEmpty()) {
                    continue; // Ignorar linhas em branco
                }
                String[] partes = linha.split("\\s+");
                if (partes.length != 3) {
                    System.err.println("Linha mal formatada ignorada: " + linha);
                    continue; // Ignorar linhas mal formatadas
                }
                try {
                    int u = Integer.parseInt(partes[0]) - 1;
                    int v = Integer.parseInt(partes[1]) - 1;
                    int w = Integer.parseInt(partes[2]);
                    grafo[u][v] = w; // Adiciona a aresta ao grafo
                    grafo[v][u] = w; // Adiciona a aresta inversa, pois o grafo é não-direcionado
                } catch (NumberFormatException e) {
                    System.err.println("Número inválido na linha: " + linha);
                }
            }

            System.out.println("Valor de K: " + K); // Exibir o valor de K
            return grafo;
        }
    }

    // Algoritmo de Floyd-Warshall para encontrar todas as distâncias mínimas
    public static int[][] floydWarshall(int[][] grafo) {
        int n = grafo.length;
        int[][] dist = new int[n][n];

        for (int i = 0; i < n; i++) {
            System.arraycopy(grafo[i], 0, dist[i], 0, n); // Copia o grafo inicial para a matriz de distâncias
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != Integer.MAX_VALUE && dist[k][j] != Integer.MAX_VALUE) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]); // Atualiza a distância mínima entre i e j
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
    

    // Inicializa o mapa de distâncias com valores máximos
    static Map<Integer, Integer> inicializarDistancias(int numVertices) {
        Map<Integer, Integer> distancias = new HashMap<>();
        for (int i = 0; i < numVertices; i++) distancias.put(i, Integer.MAX_VALUE);
        return distancias;
    }

    // Encontra um índice inicial aleatório para começar a seleção de centros
    static int encontrarIndiceInicial(Map<Integer, Integer> distancias) {
        return 0; // Poderia ser escolhido de forma mais elaborada, dependendo da lógica desejada
    }

    // Atualiza as distâncias de todos os vértices ao centro mais próximo
    static void atualizarDistancias(Map<Integer, Integer> distancias, int[][] pesos, int indiceCentro) {
        for (int j = 0; j < pesos.length; j++) distancias.put(j, Math.min(distancias.get(j), pesos[indiceCentro][j]));
    }

    // Encontra o índice com a maior distância mínima no mapa de distâncias
    static int encontrarIndiceMaximo(Map<Integer, Integer> distancias) {
        int indiceMax = -1;
        int maxDistancia = Integer.MIN_VALUE;

        for (Map.Entry<Integer, Integer> entry : distancias.entrySet()) {
            if (entry.getValue() > maxDistancia) {
                maxDistancia = entry.getValue();
                indiceMax = entry.getKey();
            }
        }

        return indiceMax;
    }


    // Método principal para selecionar k vértices como centros e imprimir a maior distância mínima
    static void selecionarKVértices(int[][] pesos, int numCentros, int numVertices) {
        Map<Integer, Integer> distancias = inicializarDistancias(numVertices); // Inicializa o mapa de distâncias
        Set<Integer> centros = new HashSet<>(); // Conjunto para armazenar os índices dos vértices escolhidos como centros
        int indiceMax = encontrarIndiceInicial(distancias); // Encontra o índice inicial aleatório

        for (int i = 0; i < numCentros; i++) {
            centros.add(indiceMax); // Adiciona o vértice atual ao conjunto de centros
            atualizarDistancias(distancias, pesos, indiceMax); // Atualiza as distâncias ao centro mais próximo
            indiceMax = encontrarIndiceMaximo(distancias); // Encontra o próximo centro com maior distância mínima
        }

        System.out.println(distancias.get(indiceMax)); // Imprime a maior distância mínima de um vértice a um centro
    }

    // Método principal
    public static void main(String[] args) {
        try {

            String filePath = "teste.txt"; // Caminho do arquivo da instância

            int[][] grafo = lerInstancia(filePath); // Ler a instância do arquivo

            int[][] distancias = floydWarshall(grafo); // Calcular a matriz de distâncias mínimas usando Floyd-Warshall

            long startTime = System.currentTimeMillis();

            selecionarKVértices(distancias, K, distancias.length); // Encontrar e imprimir os k-centros usando o método guloso

            long endTime = System.currentTimeMillis();

            long duration = (endTime - startTime);

            System.out.println(duration);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
