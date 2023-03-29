package br.com.celestialvip.mercadopago;

import br.com.celestialvip.models.entities.PayamentStatus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import okio.BufferedSink;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class MercadoPagoAPI {

    private final String BASE_URL = "https://api.mercadopago.com";
    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final String ACCESS_TOKEN;

    public MercadoPagoAPI(FileConfiguration config) {
        CLIENT_ID = config.getString("config.mercadopago.CLIENT_ID");
        CLIENT_SECRET = config.getString("config.mercadopago.CLIENT_SECRET");
        ACCESS_TOKEN = getAccessToken();
    }

    public PayamentStatus getPaymentStatus(String paymentId) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/v1/payments/search?id=" + paymentId)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .build();
        Response response = client.newCall(request).execute();

        try {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonObject result = jsonObject.get("results").getAsJsonArray().get(0).getAsJsonObject();
                String status = result.get("status").getAsString();
                String externalReference = result.get("external_reference").getAsString();
                return new PayamentStatus(paymentId, status, externalReference);
            }
        } catch (Exception e) {
            return new PayamentStatus();
        }
        return new PayamentStatus();
    }

    private String getAccessToken() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/oauth/token?grant_type=client_credentials&client_id="
                        + CLIENT_ID + "&client_secret=" + CLIENT_SECRET)
                .method("POST", new RequestBody() {
                    @Nullable
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink bufferedSink) throws IOException {

                    }
                })
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                return jsonObject.get("access_token").getAsString();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


