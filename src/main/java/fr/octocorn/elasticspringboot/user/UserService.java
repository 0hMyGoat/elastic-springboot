package fr.octocorn.elasticspringboot.user;

import fr.octocorn.elasticspringboot.user.dto.UserCreateDTO;
import fr.octocorn.elasticspringboot.user.dto.UserDTO;
import fr.octocorn.elasticspringboot.user.dto.UserUpdateDTO;
import fr.octocorn.elasticspringboot.user.exception.UserNotFoundException;
import fr.octocorn.elasticspringboot.user.mapper.UserMapper;
import fr.octocorn.elasticspringboot.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDTO);
    }

    public UserDTO findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        User user = userMapper.toEntity(dto);
        user.setRegisteredAt(LocalDateTime.now());
        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO update(UUID id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userMapper.updateEntity(dto, user);
        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}

