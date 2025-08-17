package com.qhuyns.ecomweb.entity.key;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PrimaryKeyClass
public class MessagePrimaryKey implements Serializable {
    // dung nosql cho các entity cần truy vấn nhanh, tuy nhiên chỉ truy vấn theo 1 số field cho trc trong partition va cluster
    // voi cac entity co nhieu quan he voi cac bang khac thi dung sql, vi neu dung nosql phai find tung quan he rat mat thoi gian
   // dung cho entity co data lon va truy xuat nhanh, it quan he
    // xay dung bang theo truy van chu khong phai theo entity -> 1 entity co the nhieu bang va phai cap nhat nhieu
    // co the chuyen sang scylladb de dang chi can doi config connection
    // >7 querry thi xem xet chuyen entity sang sql
    @PrimaryKeyColumn(name = "room_id", type = PrimaryKeyType.PARTITIONED)
    private String roomId;

    @PrimaryKeyColumn(name = "sent_at", type = PrimaryKeyType.CLUSTERED, ordinal = 0)
    private LocalDateTime sentAt;

    @PrimaryKeyColumn(name = "message_id", type = PrimaryKeyType.CLUSTERED, ordinal = 1)
    private UUID messageId;
}