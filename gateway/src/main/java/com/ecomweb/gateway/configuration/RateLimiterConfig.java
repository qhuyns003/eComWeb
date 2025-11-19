package com.ecomweb.gateway.configuration;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
// sinh key cho redis, dua tren ip ng gui
public class RateLimiterConfig {

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange ->
                Mono.just(exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress());
    }
}

// event-loop: cơ chế giúp duy trì 1 thread có thể phục vụ nhiều request 1 lúc
// bản chất là vẫn xử lý tuần tự các event, tuy nhiên khi phải đợi kết quả từ db thì nó đăng kí với hdh để nhận kết quả sau và tiếp nhận request khác
// các event được xếp trong hàng đợi, kể cả thông báo kết quả đc trả về đợi xử lý cũng nằm trong hàng đợi này
// event có thể là các request hoặc thông báo từ hdh

// redis và nginx hoạt đôgnj với cơ chế eventloop
// ngĩnx gồm 1 master process và nhiều worker process, mỗi worker process hoạt động với 1 thread với cơ chế eventloop
// master process chỉ quản lý các worker, fork chúng haowcj xử lý kki worker crash, k tham gia điều phối request
// sử dugnj nhiều worker process để tận dugnj tối đa cpu, tăng khả năng tải hệ thống
// hdh không phân phối các request tới cho các worker mà worker pahri tự động lắng nghe trên cổng để lấy requeest
// nginx có cơ chế accept mutex giúp chỉ 1 worker tiếp nhận yêu cầu tại 1 thời điểm, tránh tranh nhau request



