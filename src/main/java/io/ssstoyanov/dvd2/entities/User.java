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

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "users")
@TypeAlias("user")
public class User {
    @Id
    @JsonView(View.ExtendedPublic.class)
    private ObjectId id;

    @JsonView({View.ExtendedPublic.class, View.Internal.class})
    private String username;

    @JsonView(View.Internal.class)
    private String password;

    @JsonView(View.ExtendedPublic.class)
    private List<Disk> disks;

}
