package br.com.fgto.customers;

import br.com.fgto.customers.validation.CpfCnpjValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CpfCnpjValidatorTests {
    @Test
    void invalidCpf() {
        CpfCnpjValidator validator = new CpfCnpjValidator();
        Assertions.assertFalse(validator.isValid("252.032.665-35", null));
        Assertions.assertFalse(validator.isValid("830.139.280-10", null));
    }

    @Test
    void validCpf() {
        CpfCnpjValidator validator = new CpfCnpjValidator();
        Assertions.assertTrue(validator.isValid("830.139.280-00", null));
    }
}
