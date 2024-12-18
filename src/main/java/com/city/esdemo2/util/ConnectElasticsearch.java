package com.city.esdemo2.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
public class ConnectElasticsearch{

    public static void connect(Consumer<ElasticsearchClient> task ){
        // URL and API key
        String serverUrl = "https://49.233.52.4:9200";
        String apiKey = "UEx6VVNaRUJFeDJaTXJzOWtiMlg6Wlk0S2V0R2RUdEt0SnNfZmdLa2dhUQ==";

// Create the low-level client
        RestClient restClient = RestClient.builder(HttpHost.create(serverUrl)).setDefaultHeaders(new Header[]{new BasicHeader("Authorization", "ApiKey " + apiKey)}).build();

// Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

// And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);



        try {
            task.accept(esClient);
            restClient.close();
        } catch (IOException e) {
           log.error(e.getMessage());
        }
    }
}
