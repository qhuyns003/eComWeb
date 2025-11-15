package com.qhuyns.ecomweb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
@Configuration
public class RedisConfig {
// redis luudu lieu kieu key-value
    // key mac dinh la string
    // value co nhieu ctdl ho tro nhu String List Set Map
    // thuong luu String (JSON)

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setJmxEnabled(false); // tắt JMX
        return new JedisPool(poolConfig, "localhost", 6379);
    }
}

// redis vẫn ghi dữ liệu ra disk, tuy nhiên nó được thực hiện trên 1 process cn tách biệt với process chính -> isolation, khôgn ảnh hưởng đến flow đọc ghi cơ bản
// có 2 cách ghi dữ liệu của redis :
// 1. RDB (Redis database file)
// - là 1 file nhị phân lưu dữ liệu redis trong RAM tại 1 thời điểm cụ thể, snapshot này được chụp lại khi thỏa mãn điều kiện nhất định
// - khi có luồng ghi update trong khi process con đang thực hiện ghi, cơ chế COW (copy on write ) được kich hoạt
// - COW kiểm tra xem page nào trên RAM bị ghi, thay đổi, nó chụp lại page đó và gửi sang process con nơi ghi dữ liệu disk để update
// - ghi RDB tự động khi thỏa mãn 1 số diều kiện được config
// - RDB update sẽ xóa toàn bộ fil cũ thay bằng file RDB mới
// - chỉ BGSAVE mới phục vụ COW do nso phục vụ đa luồng, còn SAVE để ghi disk thì nó chặn process đọc ghi chính cả redis xong mới ghi

// 2. AOF (append on file)
// - là cơ chế ghi tuần tự các lệnh được thao tác thay đổi trên RAM, như update insert delete
// - ghi tuần tự, không ghi đè lệnh
// - ban đầu AOF sẽ lưu tạm trong RAM sau đó sẽ được ghi xuống disk :
// - các câu lệnh này được ghi xuống đĩa theo mỗi 1 khaonrg thời gian (mặc định), haowcj sau mỗi câu lệnh, để hệ điều hành quản lí.
// - khi file bị phình to, sẽ có cơ chế rewrite
// - rewrite tạo 1 process con độc lập và đọc dữ liệu trong RAM hiện tại, sau đó sinh câu lệnh tuần tự tương đương (nhưung gọn hơn) và thay thế file cũ
// - điều kiện rewrite thường theo ngưỡng dữ liệu cố định trong file hoặc theo độ tăng % so với lần rewrite trước (theo dung lượng)
// - cơ chế COW phụcvuj cho việc rewwrite ở AOF k phục vụ việc ghi AOF từ RAM xuống disk vì:

// => khi ị crash nó sẽ đọc lại file rdb hoặc aof để khôi phục data

// redis sử dụng single thread để đảm bảo luồng an toàn tránh xung đột
// single thread là 1 luồng duy nhất truy cập dữ liệu (dữ liệu trên RAM - k lien quan dữ liệu disk)
// => trong 1 process có nhiều thread, còn single thread chỉ là cho phép 1 luồng duy nhất truy cậ dât
// việc ghi aof chỉ là ghi lại lệnh, luồng riêng biệt vì k đôgnj chạm đến dữ liệu
// việc ghi rdb bắt buộc pahri sử dungj fork do nó cần chụp lại dữ liệu mà redis k phục vụ multithread
// process idolation trên RAM

// Sơ qua về hdh
// process thì tách biệt về mặt bộ nhứo so với process khác
// trong process có nhiều thread và chúng sử dugnj chung 1 bộ nhớ
// bộ nhớ dùng chung là RAM
// process isolation do chúng sử dugnj virtual memmory- địa chỉ giũa 2 process có thể giống nhau nhưng chúng ánh xạ đén địa chỉ vật lsy trên RAM khác nhau -> isolation
// hdh có quyền cho phép 2 tiến trình chỏ cùng vùng RAM nhưng vẫn đảm bảo isolation bằng 1 số cơ chế tiêu biểu là COW
// COW cho phép process con đọc cùng 1 vùng nhớ process cha, nhưung khi ghi dữ liệu sẽ phải ghi ra vùng nhớ khác -> k gây conflict
// -> COW giúp tối ưu bộ nhớ tánh bị trùng lặp dữ liệu