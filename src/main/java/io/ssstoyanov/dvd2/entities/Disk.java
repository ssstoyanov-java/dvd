package io.ssstoyanov.dvd2.entities;

import com.fasterxml.jackson.annotation.JsonView;
import io.ssstoyanov.dvd2.configurations.View;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "disks")
@TypeAlias("disk")
public class Disk {
    @Id
    @JsonView(View.ExtendedPublic.class)
    private ObjectId id;

    @JsonView({View.ExtendedPublic.class, View.Public.class})
    private String name;

    @JsonView(View.ExtendedPublic.class)
    private User originalOwner;

    @JsonView(View.ExtendedPublic.class)
    private User currentOwner;
}
