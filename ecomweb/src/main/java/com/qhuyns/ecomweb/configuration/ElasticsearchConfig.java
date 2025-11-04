package com.qhuyns.ecomweb.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

// viec sync sang ES nen dung message broker de giam latenncy
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.qhuyns.ecomweb.ES.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .withBasicAuth("elastic", "changeme")
                .build();
    }
}
// kien truc tong quan
// 1 index là 1 schema và chứa nhiều document (table)
// 1 cluster bao gom nhieu node, moi node la 1 instance cua ES (scale)
// mỗi node chứa 1 mảnh shard bất kì + 1 replica shard (khác shard hiện tại)
// các node p2p tương tự cassandra
// khi request save : đến 1 node, tính toán vị trí shard chứa dữ liệu -> gọi đến node chứa shard đó qua giao thức riêng và lưu
// khi read: đến 1 node, gửi rq tương tự đến tất cả node, tổng hợp kết quả trả lại node tiếp nhận rq -> trả kq cuối cùng
// -> nhanh do xử lý song song các yêu cầu: chia nhỏ phần việc cho các node rồi tổng hợp kết quả, mỗi node đều có thể tiếp nhận yêu cầu

// ctdl sử dụng : invert index - đảo ngược các từ khóa tách từ name thành document ID
// VD : 1-iphone 13 | 2-iphone 14 -> "iphone"-[1,2] | "13"-[1] | "14"-[2]

// ES sử dugnj nhiều ctdl để lưu các field chứ kông chỉ inverse index, lưu thêm 1 trường source để lưu data gốc để trả ra