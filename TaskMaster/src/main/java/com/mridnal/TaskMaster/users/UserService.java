package com.mridnal.TaskMaster.users;

import com.mridnal.TaskMaster.users.dto.UserResponse;
import com.mridnal.TaskMaster.users.dto.UserUpdateRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Users getCurrentUser(Authentication authentication) {
        Users user = findUserByEmail(authentication.getName());

        return user;
    }

    @Transactional
    public Users updateUserDetails(Authentication authentication, UserUpdateRequest request) {
        Users user = findUserByEmail(authentication.getName());

        if (request.name() != null)
            user.setName(request.name());
        if (request.designation() != null)
            user.setDesignation(request.designation());
        if (request.bio() != null)
            user.setBio(request.bio());
        if (request.yearOfExp() != null && request.yearOfExp() >= 0)
            user.setYearOfExp(request.yearOfExp());

        try{
            return userRepository.save(user);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Users findUserByEmail(String email) {

        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));
    }

    public Users findByUserId(Long id){
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }
}
