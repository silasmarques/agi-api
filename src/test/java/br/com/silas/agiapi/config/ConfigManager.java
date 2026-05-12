package br.com.silas.agiapi.config;

import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Properties PROPERTIES = new Properties();

    static {
        String env = System.getProperty("env", "hml");
        String fileName = "environments/" + env + ".properties";

        try (InputStream input = ConfigManager.class
                .getClassLoader()
                .getResourceAsStream(fileName)) {

            if (input == null) {
                throw new IllegalStateException(
                        "Arquivo de configuracao nao encontrado no classpath: " + fileName);
            }
            PROPERTIES.load(input);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao carregar configuracoes do ambiente '" + env + "'", e);
        }
    }

    private ConfigManager() { }

    public static String baseUrl() { return require("base.url"); }
    public static int connectTimeout() { return Integer.parseInt(require("http.timeout.connect")); }
    public static int readTimeout() { return Integer.parseInt(require("http.timeout.read")); }

    private static String require(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Propriedade obrigatoria ausente: " + key);
        }
        return value;
    }
}
