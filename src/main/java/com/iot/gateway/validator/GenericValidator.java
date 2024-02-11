package com.iot.gateway.validator;

import com.iot.gateway.exception.GatewayException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenericValidator {
  private final Validator validator;

  @Autowired
  public GenericValidator(Validator validator) {
    this.validator = validator;
  }

  public <T> void validate(T object) {
    final Set<ConstraintViolation<T>> violations = validator.validate(object);
    if (!violations.isEmpty()) {
      Map<String, String> errors =
          violations.stream()
              .collect(
                  Collectors.toMap(
                      x -> x.getPropertyPath().toString(),
                      ConstraintViolation::getMessageTemplate,
                      (v1, v2) -> v1 + ", " + v2));
      throw new GatewayException("Invalid data", 400, errors);
    }
  }
}
