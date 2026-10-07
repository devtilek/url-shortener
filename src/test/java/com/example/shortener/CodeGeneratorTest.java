package com.example.shortener;

import com.example.shortener.repository.LinkRepository;
import com.example.shortener.service.CodeGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeGeneratorTest {

    @Mock
    LinkRepository linkRepository;

    @InjectMocks
    CodeGenerator codeGenerator;

    @Test
    void generateUniqueCode_returnsCodeOfConfiguredLength() {
        ReflectionTestUtils.setField(codeGenerator, "codeLength", 7);
        ReflectionTestUtils.setField(codeGenerator, "maxRetries", 5);
        when(linkRepository.existsByCode(anyString())).thenReturn(false);

        String code = codeGenerator.generateUniqueCode();

        assertThat(code).hasSize(7).matches("[A-Za-z0-9]{7}");
    }
}