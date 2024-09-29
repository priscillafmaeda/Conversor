import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConversorMoeda {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    private static final Map<Integer, String> MOEDAS = new HashMap<>();
    private static final Map<String, String> DESCRICOES = new HashMap<>();

    static {
        MOEDAS.put(1, "USD");
        MOEDAS.put(2, "EUR");
        MOEDAS.put(3, "GBP");
        MOEDAS.put(4, "CLP");
        MOEDAS.put(5, "JPY");
        MOEDAS.put(6, "THB");
        MOEDAS.put(7, "BRL");

        DESCRICOES.put("USD", "Dólar Americano (USD)");
        DESCRICOES.put("EUR", "Euro (EUR)");
        DESCRICOES.put("GBP", "Libra Esterlina (GBP)");
        DESCRICOES.put("CLP", "Peso Chileno (CLP)");
        DESCRICOES.put("JPY", "Iene Japonês (JPY)");
        DESCRICOES.put("THB", "Baht Tailandês (THB)");
        DESCRICOES.put("BRL", "Real Brasileiro (BRL)");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo à casa de câmbio");
        System.out.print("Digite a quantia de dinheiro: ");
        double quantia = scanner.nextDouble();

        System.out.println("Qual a moeda de origem:");
        mostrarOpcoesMoeda();
        int opcaoOrigem = scanner.nextInt();
        String moedaOrigem = MOEDAS.get(opcaoOrigem);

        System.out.println("Qual a moeda final:");
        mostrarOpcoesMoeda();
        int opcaoDestino = scanner.nextInt();
        String moedaDestino = MOEDAS.get(opcaoDestino);

        System.out.printf("Você deseja converter %.2f %s para %s?\n", quantia, DESCRICOES.get(moedaOrigem), DESCRICOES.get(moedaDestino));

        double resultado = converterMoeda(quantia, moedaOrigem, moedaDestino);

        if (resultado != -1) {
            System.out.printf("Resultado: %.2f %s\n", resultado, moedaDestino);
        } else {
            System.out.println("Conversão inválida.");
        }

        scanner.close();
    }

    public static void mostrarOpcoesMoeda() {
        for (Map.Entry<Integer, String> entrada : MOEDAS.entrySet()) {
            System.out.printf("%d: %s\n", entrada.getKey(), DESCRICOES.get(entrada.getValue()));
        }
    }

    public static double converterMoeda(double quantia, String moedaOrigem, String moedaDestino) {
        try {

            String url = API_URL + moedaOrigem;
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            double taxaCambio = json.getAsJsonObject("rates").get(moedaDestino).getAsDouble();

            return quantia * taxaCambio;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
