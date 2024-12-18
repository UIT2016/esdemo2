package com.city.esdemo2.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.city.esdemo2.DTO.User;
import com.city.esdemo2.Esdemo2Application;
import com.city.esdemo2.util.ConnectElasticsearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class HelloElasticSearch {
    public static void main(String[] args) throws IOException {


        ConnectElasticsearch.connect(esClient -> {
            try {
                BooleanResponse user2ExistRep = esClient.indices().exists(ExistsRequest.of(i -> i.index("user2")));
                if (!user2ExistRep.value()) {

                    //创建索引user2
                    CreateIndexResponse response = esClient.indices().create(c -> c
                            .index("user2").mappings(m -> m
                                    .properties("name", p -> p.text(t -> t.index(true)))
                                    .properties("sex", p -> p.keyword(k -> k.index(true)))
                                    .properties("tel", p -> p.keyword(k -> k.index(false))))
                    );
                    System.out.printf("创建索引%s,分配索引%s", response.acknowledged(), response.shardsAcknowledged());
                }
                else{
                    System.out.println("索引已存在，跳过");
                }
                //删除索引
                /*DeleteIndexResponse deleteIndexResponse = esClient.indices().delete(DeleteIndexRequest.of(i -> i.index("user2")));
                System.out.println("删除索引" + deleteIndexResponse.acknowledged());*/
                //添加数据
               /* Map<String, Object> map = Map.of("name", "小米", "sex", "女的", "tel", "123");
                IndexResponse indexResponse = esClient.index(i -> i.index("user2").id("1").document(map));
                System.out.println("插入数据" + indexResponse.result().jsonValue());
               */
//                searchData(esClient);
                //删除其中一条,已经删除之后返回notfound
//                DeleteResponse deleteResponse = esClient.delete(i -> i.index("user2").id("O7zmLJEBEx2ZMrs91r0S"));
//                System.out.println("删除结果"+deleteResponse.result());
                //批量插入
                List<User> users = List.of(
                        new User("John Doe", "male", "123-456-7890"),
                        new User("Jane Smith", "female", "098-765-4321"),
                        new User("Alice Johnson", "female", "111-222-3333")
                );
//                batchInsert(esClient, users);

                searchData(esClient);
                conditionSearch(esClient);
//                List<String> userIds = List.of("10010", "10011", "10012");
//                batchDelete(esClient,userIds);
//                searchData(esClient);
                fuzzySearch(esClient);
                aggregationSearch(esClient);
            } catch (IOException e) {
                log.error("",e.getCause());
            }
        });


    }

    private static void conditionSearch(ElasticsearchClient esClient) throws IOException {
        System.out.println("条件查询---------------");
       /* SearchResponse<User> response = esClient.search(s -> s
                        .index("user2")
                        .query(q -> q.bool(b -> b
                                .must(m -> m.match(t -> t.field("name").query("John Doe")))
                        )),
                User.class
        );*/
        SearchResponse<User> userSearchResponse = esClient.search(s -> s.index("user2").query(
                q->q.bool(
                        b->b.must(
                                m->m.match(
                                        t->t.field("name").query("小米")
                                )
                        ).filter(
                                f->f.term(
                                        t->t.field("sex").value("女的")
                                )
                        )
                )
        ).sort(
                so->so.field(
                        f->f.field("tel").order(SortOrder.Desc)
                )
        ).from(0).size(3), User.class);
        List<Hit<User>> hits = userSearchResponse.hits().hits();
        for (Hit<User> hit : hits) {
            User User = hit.source();
            log.info("Found User " + User.getName() + ", score " + hit.score() + "id " + hit.id());
        }

    }

    private static void batchDelete(ElasticsearchClient esClient, List<String> users) throws IOException {
        // Build the bulk request
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (int i = 0; i < users.size(); i++) {
            int finalI = i;
            br.operations(op -> op
                    .delete(idx ->
                            idx.index("user2")
                                    .id(users.get(finalI)) // Optional: specify a unique ID

                    )
            );
        }
        BulkResponse bulk = esClient.bulk(br.refresh(Refresh.True).build());
        System.out.println("批量删除"+!bulk.errors());
    }

    private static void batchInsert(ElasticsearchClient esClient, List<User> users) throws IOException {
        // Build the bulk request
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (int i = 0; i < users.size(); i++) {
            int finalI = i;
            br.operations(op -> op
                    .index(idx ->
                        idx.index("user2")
                                .id("200" + 1 + finalI) // Optional: specify a unique ID
                                .document(users.get(finalI))
                    )
            );
        }
        BulkResponse bulk = esClient.bulk(br.refresh(Refresh.True).build());
        System.out.println("批量插入"+!bulk.errors());
    }

    private static void searchData(ElasticsearchClient esClient) throws IOException {
        //查询插入的数据
        SearchResponse<User> userSearchResponse = esClient.search(t -> t.index("user2").query(q -> q.matchAll(v-> v)), User.class);
        TotalHits total = userSearchResponse.hits().total();
        Optional.ofNullable(total).ifPresent(t -> {
            boolean isExactResult = t.relation() == TotalHitsRelation.Eq;

            if (isExactResult) {
                log.info("There are " + t.value() + " results");
            } else {
                log.info("There are more than " + t.value() + " results");
            }
        });
        List<Hit<User>> hits = userSearchResponse.hits().hits();
        for (Hit<User> hit : hits) {
            User User = hit.source();
            log.info("Found User " + User.getName() + "sex: "+User.getSex() + " score " + hit.score() + "id " + hit.id());
        }
    }
    private static void fuzzySearch(ElasticsearchClient esClient) throws IOException {
        System.out.println("模糊查询---------------");

        SearchResponse<User> userSearchResponse = esClient.search(s -> s
                        .index("user2")
                        .query(q -> q
                                .fuzzy(f -> f
                                        .field("name")            // 需要模糊查询的字段
                                        .value("小米")         // 模糊查询的值，允许一定程度的拼写错误
                                        .fuzziness("1")        // 自动确定模糊度，允许一定范围内的编辑距离
                                )
                        )
                        .from(0)
                        .size(10),                       // 分页，返回前10个结果
                User.class
        );

        // 输出查询结果
        userSearchResponse.hits().hits().forEach(hit -> {
            User user = hit.source();
            System.out.println("Found user: " + user.getName() + ", " + user.getSex() + ", " + user.getTel());
        });
    }
    private static void aggregationSearch(ElasticsearchClient esClient) throws IOException {
        System.out.println("分组查询-------------------");
        SearchResponse<User> response = esClient.search(s -> s
                .index("user2")
                .size(0)  // 设置 size 为 0，只返回聚合结果，不返回实际文档
                .aggregations("by_sex", a -> a
                        .terms(t -> t
                                .field("sex")  // 按 "sex.keyword" 字段进行分组
                                .size(10)              // 返回前 10 个分组
                        )
                ),User.class
        );

        // 处理聚合结果
        List<StringTermsBucket> buckets = response.aggregations()
                .get("by_sex")
                .sterms()
                .buckets()
                .array();

        for (StringTermsBucket bucket : buckets) {
            System.out.println("Sex: " + bucket.key().stringValue() + ", Count: " + bucket.docCount());
        }
    }

}
