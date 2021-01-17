package io.ssstoyanov.dvd2.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "disks")
@TypeAlias("disk")
public class Disk {
    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private ObjectId id;

    private String name;

    private User originalOwner;

    private User currentOwner;

}
