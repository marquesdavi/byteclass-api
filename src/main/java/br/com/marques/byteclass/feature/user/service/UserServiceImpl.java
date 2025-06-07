package br.com.marques.byteclass.feature.user.service;

import br.com.marques.byteclass.feature.user.api.UserApi;
import br.com.marques.byteclass.feature.user.api.dto.UserRequest;
import br.com.marques.byteclass.feature.user.api.dto.UserSummary;
import br.com.marques.byteclass.feature.user.api.dto.UserDetailsInternal;
import br.com.marques.byteclass.feature.user.api.mapper.UserRequestMapper;
import br.com.marques.byteclass.feature.user.api.mapper.UserSummaryMapper;
import br.com.marques.byteclass.feature.user.entity.User;
import br.com.marques.byteclass.feature.user.repository.UserRepository;
import br.com.marques.byteclass.common.exception.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserApi {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserRequestMapper requestMapper;
    private final UserSummaryMapper summaryMapper;

    @Override
    @Transactional
    public Long create(@Valid UserRequest dto) {
        if (userRepository.existsByEmail(dto.email()))
            throw new AlreadyExistsException(String.format("User with email %s already exists!", dto.email()));

        User u = requestMapper.toEntity(dto);
        u.setPassword(encoder.encode(dto.password()));
        userRepository.save(u);
        return u.getId();
    }

    @Override
    @Transactional
    public void update(Long id, UserRequest dto) {
        User instance = findByIdOrElseThrow(id);

        if (!instance.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email()))
            throw new AlreadyExistsException(String.format("User with email %s already exists!", dto.email()));

        requestMapper.updateEntityFromDto(dto, instance);
        instance.setPassword(encoder.encode(dto.password()));
        userRepository.save(instance);
    }

    @Override
    public UserSummary getById(@Min(1) Long id) {
        User instance = findByIdOrElseThrow(id);
        return summaryMapper.toDto(instance);
    }

    @Override
    public List<UserSummary> list() {
        return userRepository.findAll().stream()
                .map(summaryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User u = findByIdOrElseThrow(id);
        userRepository.delete(u);
    }

    @Override
    public UserDetailsInternal findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(String.format("User with email %s not found!", email)));
        return summaryMapper.toInternalDto(user);
    }

    private User findByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
