# 📦 MapStruct - Version LTS 1.6.0

## 🎯 À Propos de MapStruct

MapStruct est un processeur d'annotations qui génère automatiquement des implémentations de mappers pour convertir entre les objets Java (Entity, DTO, Document, etc.).

## 🚀 Avantages

✅ **Performance**: Code généré à la compilation, pas de réflexion à l'exécution
✅ **Type Safety**: Erreurs détectées à la compilation, pas à l'exécution
✅ **Zéro Configuration**: Fonctionnement immédiat avec Spring
✅ **Flexibilité**: Mappages complexes et personnalisés possibles
✅ **Maintenabilité**: Code généré lisible et debuggable

## 📋 Version 1.6.0 - LTS (Long-Term Support)

- Sortie: Février 2024
- Support long terme
- Compatible avec Java 8+
- Compatible avec Spring Boot 4.0+

### Nouvelles Fonctionnalités depuis la 1.5.x

- Support amélioré pour les records Java
- Meilleure gestion des génériques
- Performance optimisée
- Support étendu pour les types complexes

## 🛠️ Configuration dans pom.xml

```xml
<properties>
    <mapstruct.version>1.6.0</mapstruct.version>
</properties>

<dependencies>
    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${mapstruct.version}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>${mapstruct.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## 💡 Utilisation Basique

### 1. Créer une Interface Mapper

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    // Conversion simple
    UserDTO toDTO(User entity);
    
    User toEntity(UserDTO dto);
    
    // Collections
    List<UserDTO> toDTOList(List<User> entities);
    
    // Mapping personnalisé
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    UserDTO customMapping(User user);
    
    // Mise à jour (merge)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(UserDTO dto, @MappingTarget User entity);
}
```

### 2. Utiliser le Mapper

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserRepository repository;
    
    public UserDTO createUser(UserDTO dto) {
        User entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        return mapper.toDTO(entity);
    }
    
    public List<UserDTO> getAllUsers() {
        return mapper.toDTOList(repository.findAll());
    }
    
    public void updateUser(Long id, UserDTO dto) {
        User entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
        mapper.updateEntityFromDTO(dto, entity);
        repository.save(entity);
    }
}
```

## 🔄 Cas d'Usage Avancés

### 1. Mapping avec Transformations

```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    // Transformation avec méthode personnalisée
    @Mapping(target = "priceInCents", source = "price")
    ProductDTO toDTO(Product product);
    
    // Appeler une méthode personnalisée
    default Integer convertPrice(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(100)).intValue();
    }
}
```

### 2. Mapper Hérité (Parent Mapper)

```java
@Mapper(componentModel = "spring")
public abstract class EntityMapper<E, D> {
    
    public abstract D toDTO(E entity);
    
    public abstract E toEntity(D dto);
    
    public List<D> toDTOList(List<E> entities) {
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
```

### 3. Mapper Composé (utiliser plusieurs mappers)

```java
@Mapper(componentModel = "spring", uses = {AddressMapper.class, PhoneMapper.class})
public interface PersonMapper {
    PersonDTO toDTO(Person person);
    Person toEntity(PersonDTO dto);
}
```

### 4. Mapper avec Conditions

```java
@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    @Mapping(source = "customer", target = "customerName")
    OrderDTO toDTO(Order order);
    
    default String mapCustomer(Customer customer) {
        return customer != null ? customer.getName() : "Unknown";
    }
    
    // Ou avec @Condition
    @Mapping(source = "status", target = "statusCode", 
             conditionQualifiedByName = "mapStatus")
    OrderDTO toDTOWithCondition(Order order);
    
    @Named("mapStatus")
    default String mapStatus(OrderStatus status) {
        return status == null ? "UNKNOWN" : status.getCode();
    }
}
```

## 🔍 Debugging du Code Généré

MapStruct génère du code source que vous pouvez voir :

```
target/generated-sources/annotations/
```

Pour chaque mapper, MapStruct génère une classe `*Impl` :

```java
@Component
public class UserMapperImpl extends UserMapper {

    @Override
    public UserDTO toDTO(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId( entity.getId() );
        userDTO.setName( entity.getName() );
        // ... autres mappings

        return userDTO;
    }
    
    // ... autres méthodes
}
```

## ⚠️ Points Importants

### ✅ Bonnes Pratiques

- Toujours utiliser `componentModel = "spring"` pour que le mapper soit injecté en Spring
- Utiliser `@RequiredArgsConstructor` ou injection par constructeur
- Documenter les mappages complexes
- Faire des tests unitaires des mappers
- Utiliser l'héritage pour les mappers génériques

### ❌ Erreurs Courantes

1. **Oublier le componentModel**
   ```java
   @Mapper  // ❌ Incorrect
   public interface UserMapper { }
   
   @Mapper(componentModel = "spring")  // ✅ Correct
   public interface UserMapper { }
   ```

2. **Ne pas placer le mapper-processor dans les annotationProcessorPaths**
   → Le code ne sera pas généré !

3. **Mapper avec constructeurs privés**
   ```java
   public class UserDTO {
       private UserDTO() {}  // ❌ MapStruct ne peut pas instancier
   }
   ```

4. **Oublier de compiler après modification**
   ```bash
   # Toujours compiler après changements
   mvn clean compile
   ```

## 🧪 Tester les Mappers

```java
@Test
public class UserMapperTest {
    
    @InjectMocks
    private UserMapper mapper;
    
    @Before
    public void setup() {
        mapper = new UserMapperImpl();
    }
    
    @Test
    public void testToDTO() {
        User user = new User(1L, "John Doe", "john@example.com");
        UserDTO dto = mapper.toDTO(user);
        
        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
    }
    
    @Test
    public void testToEntity() {
        UserDTO dto = new UserDTO(1L, "Jane Doe", "jane@example.com");
        User user = mapper.toEntity(dto);
        
        assertEquals(1L, user.getId());
        assertEquals("Jane Doe", user.getName());
        assertEquals("jane@example.com", user.getEmail());
    }
    
    @Test
    public void testNullHandling() {
        assertNull(mapper.toDTO(null));
        assertNull(mapper.toEntity(null));
    }
}
```

## 📚 Ressources

- [MapStruct Documentation](https://mapstruct.org/)
- [Spring Integration Guide](https://mapstruct.org/documentation/stable/reference/html/)
- [GitHub Repository](https://github.com/mapstruct/mapstruct)
- [Stack Overflow Tag](https://stackoverflow.com/questions/tagged/mapstruct)

## 🔄 Compatibilité

| Version MapStruct | Java Min | Spring Boot Min | Statut |
|------------------|----------|-----------------|--------|
| 1.6.x LTS | 8 | 3.0 | ✅ Maintenue |
| 1.5.x | 8 | 2.5 | ⚠️ Ancien |
| 1.4.x | 8 | 2.0 | ⛔ EOL |

## ✅ Checklist de Mise en Place

- [x] MapStruct 1.6.0 ajoutée à pom.xml
- [x] mapstruct-processor en scope `provided`
- [x] Configuration du maven-compiler-plugin
- [x] annotationProcessorPaths configuré
- [x] Exemple de mapper créé
- [x] Tests possibles

Vous êtes prêt à utiliser MapStruct ! 🚀

