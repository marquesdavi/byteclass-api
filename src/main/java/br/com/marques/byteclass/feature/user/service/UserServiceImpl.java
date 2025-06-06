package br.com.marques.byteclass.feature.user.service;

import br.com.marques.byteclass.common.exception.AlreadyExistsException;
import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.config.resilience.Resilient;
import br.com.marques.byteclass.feature.user.dto.UserRequest;
import br.com.marques.byteclass.feature.user.dto.UserSummary;
import br.com.marques.byteclass.feature.user.entity.Role;
import br.com.marques.byteclass.feature.user.entity.User;
import br.com.marques.byteclass.feature.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService<User, UserRequest, UserSummary> {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void create(UserRequest dto) {
        log.info("Creating new user with email: {}", dto.email());
        existsByEmail(dto.email());

        User user = User.fromRequest(dto);
        user.setRole(Role.STUDENT);

        userRepository.save(user);
        log.info("User with email {} successfully created.", dto.email());
    }

    public void existsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("User with email {} already exists!", email);
            throw new AlreadyExistsException("User already exists");
        }
    }

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public List<User> list() {
        return userRepository.findAll();
    }

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User findByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found")
        );
    }

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void update(Long id, UserRequest dto) {
        log.info("Updating user id={} with email: {}", id, dto.email());
        User existing = findByIdOrElseThrow(id);

        if (!existing.getEmail().equals(dto.email())) {
            existsByEmail(dto.email());
            existing.setEmail(dto.email());
        }

        existing.setName(dto.name());
        String encodedPassword = passwordEncoder.encode(dto.password());
        existing.setPassword(encodedPassword);

        userRepository.save(existing);
        log.info("User id={} successfully updated.", id);
    }

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public User getByID(Long id) {
        log.info("Fetching user by id={}", id);
        return findByIdOrElseThrow(id);
    }

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void deleteAccount(Long id) {
        log.info("Deleting user id={}", id);
        User existing = findByIdOrElseThrow(id);
        userRepository.delete(existing);
        log.info("User id={} successfully deleted.", id);
    }
}
