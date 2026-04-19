package fr.octocorn.elasticspringboot.example;

import org.mapstruct.Mapper;

/**
 * Exemple de mapper MapStruct
 *
 * À copier et adapter à vos entités et DTOs
 */

// ============ ENTITÉ JPA ============
// @Entity
// @Table(name = "users")
// public class User {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
//
//     @Column(nullable = false)
//     private String name;
//
//     @Column(unique = true, nullable = false)
//     private String email;
//
//     // getters and setters...
// }

// ============ DOCUMENT ELASTICSEARCH ============
// @Document(indexName = "users")
// public class UserDocument {
//     @Id
//     private Long id;
//
//     @Field(type = FieldType.Text)
//     private String name;
//
//     @Field(type = FieldType.Keyword)
//     private String email;
//
//     // getters and setters...
// }

// ============ DTO ============
// public class UserDTO {
//     private Long id;
//     private String name;
//     private String email;
//
//     // getters and setters...
// }

// ============ MAPPER ============
@Mapper(componentModel = "spring")
public interface UserMapper {

    // Conversion Entity -> DTO
    UserDTO toDTO(User user);

    // Conversion DTO -> Entity
    User toEntity(UserDTO userDTO);

    // Conversion Document -> DTO
    UserDTO documentToDTO(UserDocument userDocument);

    // Conversion DTO -> Document
    UserDocument dtoToDocument(UserDTO userDTO);
}

// ============ UTILISATION DANS UN SERVICE ============
// @Service
// public class UserService {
//     private final UserRepository userRepository;
//     private final UserSearchRepository userSearchRepository;
//     private final UserMapper userMapper;
//
//     public UserService(UserRepository userRepository,
//                       UserSearchRepository userSearchRepository,
//                       UserMapper userMapper) {
//         this.userRepository = userRepository;
//         this.userSearchRepository = userSearchRepository;
//         this.userMapper = userMapper;
//     }
//
//     public UserDTO createUser(UserDTO userDTO) {
//         // Convertir DTO -> Entity
//         User user = userMapper.toEntity(userDTO);
//
//         // Sauvegarder en base de données
//         user = userRepository.save(user);
//
//         // Convertir en Document Elasticsearch
//         UserDocument userDocument = userMapper.dtoToDocument(userMapper.toDTO(user));
//         userSearchRepository.save(userDocument);
//
//         // Retourner le DTO
//         return userMapper.toDTO(user);
//     }
//
//     public UserDTO getUserById(Long id) {
//         User user = userRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
//         return userMapper.toDTO(user);
//     }
// }

