package com.ecomweb.identity_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

// cau hinh replica set +config + annatation enabletransactionmanagement de co the bat transactional tren mongo
@Configuration
public class MongoConfig {

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}

//disk lưu dữ liệu theo dạng các block có kích thước cố định
// tuy nhiên dbms lại đọc dữ liệu theo nhiều cụm block 1 lúc gọi là page (địa chỉ (offset + size), số block tùy theo quy ước của db)
// db dựa vào page để truy cập vào disk lấy data và đẩy lên RAM
// thường dbms check xem page đó có trên RAM hay chưa rồi mới truy cập disk cho đỡ tốn IO
// các page thường có size hợp lý để tránh việc page lưu hết tất cả dữ liệu, khi đẩy vào RAM có thể sẽ không đủ dung lượng
// => RAM phải nạp, phóng page nhiều lần, dễ gây tốn IO nhiều



// các dbms thường chọn 1 ctdl để quản lý data, thường là B+Tree (dùng trong index, mysql, mongodb)
// thường BTree (giừo là B+Tree) sẽ được chọn
// Btree chi chứa dữ liệu key + value + pointer ở tất cả các node
// B+Tree chỉ chứa tất cả cái trên ở leaf node còn internal node chỉ chứa key + pointer
// mỗi khi tạo index trên 1 trường sẽ tạo ra 1 cây mới tương đương với 1 bảng mới để lưu trữ dữ liệu truy vấn nhanh
//  // số lượng key phu thuoc page size
// tuy nhien trong 1 so truogn hop nhu B+Tree có node leaf va internal node se co su khac biet giua so luong key toi da do dung luong moi key se khac nhau
// đối với BTree thì các node internal, node lá lưu cả pointer + key + value trên 1 disk riêng
// các node này k tham chiếu đến 1 cùng 1 disk vì các node internal có thể chứa ít dữ liệu hơn, tránh việc tham chiếu chung toàn bộ page thì sẽ khiến dung luognjw page giảm -> độ cao cây tăng
// B+Tree cải tiến hơn khi node không lưu thêm dữ liệu -> tăng được số key lưu thêm -> giảm độ cao cây
// mõi lần đọc node chính là 1 lần truy cập vào các block để đọc key value haowcj pointer
// các cây dùng index chỉ để lưu id PK thôi chứ khôgn lưu trữ dữ liệu thật, sau khi tổng hợp được các PK từ các bảng của index nó mới intersection và tra cứu trên table chính (id index, để lấy dữ liệu thật)