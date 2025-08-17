package com.qhuyns.ecomweb.entity.key;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PrimaryKeyClass
public class PrivateChatKey implements Serializable {
    //id1<id2
    @PrimaryKeyColumn(name = "user1", type = PrimaryKeyType.PARTITIONED)
    private String user1;
    @PrimaryKeyColumn(name = "user2", type = PrimaryKeyType.PARTITIONED)
    private String user2;
}