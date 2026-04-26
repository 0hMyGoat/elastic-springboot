package fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;
import java.util.UUID;

@Document(indexName = "users")
@Getter
@Setter
@Builder
public class UserDocument {

    @Id
    private UUID id;

    @Field(type = FieldType.Text)
    private String firstName;

    @Field(type = FieldType.Text)
    private String lastName;

    @Field(type = FieldType.Keyword, normalizer = "lowercase")
    private String city;

    @Field(type = FieldType.Keyword, normalizer = "lowercase")
    private String postalCode;

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Keyword)
    private String jobName;

    @Field(type = FieldType.Keyword)
    private String sectorName;

    @Field(type = FieldType.Keyword)
    private List<String> emails;

    @Field(type = FieldType.Keyword)
    private List<String> phones;
}