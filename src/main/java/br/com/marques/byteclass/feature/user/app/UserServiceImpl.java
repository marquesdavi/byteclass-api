package br.com.marques.byteclass.feature.user.app;

import br.com.marques.byteclass.common.exception.AlreadyExistsException;
import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.common.util.PageableRequest;
import br.com.marques.byteclass.config.resilience.Resilient;
import br.com.marques.byteclass.feature.user.adapter.repository.UserRepository;
import br.com.marques.byteclass.feature.user.domain.User;
import br.com.marques.byteclass.feature.user.port.UserPort;
import br.com.marques.byteclass.feature.user.port.dto.UserDetailsInternal;
import br.com.marques.byteclass.feature.user.port.dto.UserRequest;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;
import br.com.marques.byteclass.feature.user.port.mapper.UserRequestMapper;
import br.com.marques.byteclass.feature.user.port.mapper.UserResponseMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserPort {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserRequestMapper requestMapper;
    private final UserResponseMapper summaryMapper;

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public Long create(@Valid UserRequest dto) {
        if (userRepository.existsByEmail(dto.email()))
            throw new AlreadyExistsException(String.format("User with email %s already exists!", dto.email()));

        User instance = requestMapper.toEntity(dto);
        instance.setPassword(encoder.encode(dto.password()));
        userRepository.save(instance);
        return instance.getId();
    }

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void update(@Min(value = 1, message = "Id must be greater than 0") Long id, @Valid UserRequest dto) {
        User instance = findByIdOrElseThrow(id);

        if (!instance.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email()))
            throw new AlreadyExistsException(String.format("User with email %s already exists!", dto.email()));

        requestMapper.updateEntityFromDto(dto, instance);
        instance.setPassword(encoder.encode(dto.password()));
        userRepository.save(instance);
    }

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public UserSummary getById(@Min(value = 1, message = "Id must be greater than 0") Long id) {
        User instance = findByIdOrElseThrow(id);
        return summaryMapper.toDto(instance);
    }

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public Page<UserSummary> list(PageableRequest pageable) {
        return userRepository.findAll(pageable.toPageable())
                .map(summaryMapper::toDto);
    }

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void delete(@Min(value = 1, message = "Id must be greater than 0") Long id) {
        User instance = findByIdOrElseThrow(id);
        userRepository.delete(instance);
    }

    @Override
    public UserDetailsInternal findByEmail(String email) {
        User instance = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(String.format("User with email %s not found!", email)));
        return summaryMapper.toInternalDto(instance);
    }

    private User findByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
