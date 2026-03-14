package br.com.celestialvip.mercadopago;

import br.com.celestialvip.config.PluginConfig;
import br.com.celestialvip.models.entities.PaymentStatus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class MercadoPagoAPI {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoAPI.class);
    private static final String BASE_URL = "https://api.mercadopago.com";
    private static final MediaType EMPTY_JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final PluginConfig config;
    private volatile String accessToken;
    private final ReentrantLock tokenLock = new ReentrantLock();

    public MercadoPagoAPI(PluginConfig config) {
        this.config = config;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.accessToken = fetchAccessToken();
    }

    public PaymentStatus getPaymentStatus(String paymentId) throws IOException {
        String token = getValidAccessToken();
        if (token == null) {
            logger.warn("Mercado Pago: sem access token válido");
            return new PaymentStatus();
        }
        Request request = new Request.Builder()
                .url(BASE_URL + "/v1/payments/search?id=" + paymentId)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 401) {
                refreshToken();
                return getPaymentStatus(paymentId);
            }
            if (!response.isSuccessful() || response.body() == null) {
                return new PaymentStatus();
            }
            String responseBody = response.body().string();
            JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
            if (jsonObject == null || !jsonObject.has("results") || jsonObject.getAsJsonArray("results").isEmpty()) {
                return new PaymentStatus();
            }
            JsonObject result = jsonObject.getAsJsonArray("results").get(0).getAsJsonObject();
            String status = result.has("status") ? result.get("status").getAsString() : null;
            String externalReference = result.has("external_reference") ? result.get("external_reference").getAsString() : null;
            return new PaymentStatus(paymentId, status, externalReference);
        } catch (Exception e) {
            logger.debug("Erro ao buscar pagamento {}: {}", paymentId, e.getMessage());
            return new PaymentStatus();
        }
    }

    private String getValidAccessToken() {
        if (accessToken != null) {
            return accessToken;
        }
        tokenLock.lock();
        try {
            if (accessToken == null) {
                accessToken = fetchAccessToken();
            }
            return accessToken;
        } finally {
            tokenLock.unlock();
        }
    }

    private void refreshToken() {
        tokenLock.lock();
        try {
            accessToken = fetchAccessToken();
        } finally {
            tokenLock.unlock();
        }
    }

    @Nullable
    private String fetchAccessToken() {
        String clientId = config.getMercadoPagoClientId();
        String clientSecret = config.getMercadoPagoClientSecret();
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            logger.warn("Mercado Pago: CLIENT_ID ou CLIENT_SECRET não configurados");
            return null;
        }
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/oauth/token"))
                .newBuilder()
                .addQueryParameter("grant_type", "client_credentials")
                .addQueryParameter("client_id", clientId)
                .addQueryParameter("client_secret", clientSecret)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", EMPTY_JSON))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                logger.warn("Mercado Pago: falha ao obter token, status {}", response.code());
                return null;
            }
            String body = response.body().string();
            JsonObject json = new Gson().fromJson(body, JsonObject.class);
            return json != null && json.has("access_token") ? json.get("access_token").getAsString() : null;
        } catch (IOException e) {
            logger.error("Mercado Pago: erro ao obter token: {}", e.getMessage());
            return null;
        }
    }
}
