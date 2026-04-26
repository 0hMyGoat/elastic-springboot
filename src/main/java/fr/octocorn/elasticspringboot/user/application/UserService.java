package fr.octocorn.elasticspringboot.user.application;

import fr.octocorn.elasticspringboot.user.application.command.CreateUserCommand;
import fr.octocorn.elasticspringboot.user.application.command.UpdateUserCommand;
import fr.octocorn.elasticspringboot.user.application.view.UserView;
import fr.octocorn.elasticspringboot.user.domain.event.UserSavedEvent;
import fr.octocorn.elasticspringboot.user.domain.model.User;
import fr.octocorn.elasticspringboot.user.domain.UserRepository;
import fr.octocorn.elasticspringboot.user.domain.exception.UserNotFoundException;
import fr.octocorn.elasticspringboot.user.infrastructure.mapper.UserMapper;
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
    private final UserEventPublisher eventPublisher;

    /**
     * Retourne la liste paginée des utilisateurs.
     */
    public Page<UserView> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toView);
    }

    /**
     * Retourne un utilisateur par son identifiant.
     *
     * @param id identifiant de l'utilisateur
     * @return la vue de l'utilisateur
     * @throws UserNotFoundException si l'utilisateur est introuvable
     */
    public UserView findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toView)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Crée un nouvel utilisateur.
     *
     * @param command commande de création
     * @return la vue de l'utilisateur créé
     */
    @Transactional
    public UserView create(CreateUserCommand command) {
        User user = userMapper.toEntity(command);
        user.setRegisteredAt(LocalDateTime.now());
        eventPublisher.onUserSaved(new UserSavedEvent(user.getId()));
        return userMapper.toView(userRepository.save(user));
    }

    /**
     * Met à jour un utilisateur existant.
     *
     * @param id      identifiant de l'utilisateur
     * @param command commande de mise à jour
     * @return la vue de l'utilisateur mis à jour
     * @throws UserNotFoundException si l'utilisateur est introuvable
     */
    @Transactional
    public UserView update(UUID id, UpdateUserCommand command) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userMapper.updateEntity(command, user);
        eventPublisher.onUserSaved(new UserSavedEvent(user.getId()));
        return userMapper.toView(userRepository.save(user));
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @throws UserNotFoundException si l'utilisateur est introuvable
     */
    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        eventPublisher.publishDelete(id);
    }
}

