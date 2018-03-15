package intisolutions.com.co.pruebamerqueo.Model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class RestClient {
    private OkHttpClient client;
    public Realm realm;

    public List<Companies> getCompanies() throws IOException, JSONException {
        try {
            realm = Realm.getDefaultInstance();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.themoviedb.org/3/movie/550").newBuilder();
            urlBuilder.addQueryParameter("api_key", "b33ac356c5357a9301ffda1a3ead2f30");
            Request request = new Request.Builder().url(urlBuilder.build()).build();
            Response response = getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                List<Companies> list = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray listArray = jsonObject.getJSONArray("production_companies");
                for (int i = 0; i < listArray.length(); i++) {
                    final JSONObject value = listArray.getJSONObject(i);
                    realm.beginTransaction();
                    Company result = realm.where(Company.class).equalTo("id", Integer.parseInt(value.getString("id"))).findFirst();
                    if (result != null) {
                        result.removeFromRealm();
                    }
                    realm.commitTransaction();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            try {
                                Company company = realm.createObject(Company.class);
                                company.setId(Integer.parseInt(value.getString("id")));
                                company.setName(value.getString("name"));
                                company.setImgUrl("https://image.tmdb.org/t/p/w500/" + value.getString("logo_path"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    list.add(new Companies(value.getString("id"), value.getString("name"), "https://image.tmdb.org/t/p/w500/" + value.getString("logo_path")));
                }
                return list;
            } else {
                throw new IOException();
            }
        } catch (Exception ex) {
            System.out.println("Excepcion ... " + ex.toString());
            throw new IOException();
        }
    }

    private OkHttpClient getClient() {
        if (client == null) {
            try {
                client = createClient();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    private OkHttpClient createClient() throws NoSuchAlgorithmException, KeyManagementException {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        okHttpClientBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        okHttpClientBuilder.addInterceptor(
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        );
        return okHttpClientBuilder.build();
    }

    public class Companies {
        private String name;
        private String id;
        private String image;

        public Companies() {
        }

        public Companies(String name, String id, String image) {
            this.name = name;
            this.id = id;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
