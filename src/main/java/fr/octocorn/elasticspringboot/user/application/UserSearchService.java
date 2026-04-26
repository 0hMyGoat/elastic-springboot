package fr.octocorn.elasticspringboot.user.application;

import fr.octocorn.elasticspringboot.user.application.view.UserView;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class UserSearchService {
    public Page<UserView> search(@Valid UserSearchCriteria criteria) {
        return null;
    }
}
