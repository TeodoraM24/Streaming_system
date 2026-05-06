package org.example.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AccountValidation {

    private static final String NAME_REGEX = "^[A-ZÆØÅ][a-zæøå]{0,29}$";
    private static final String PHONE_NUMBER_PREFIX_REGEX = "^(2|30|31|40|41|42|50|51|52|53|60|61|71|81|91|92|93|342|34[4-9]|35[6-7]|359|362|36[5-6]|389|398|431|441|462|466|468|472|474|476|478|48[5-6]|48[8-9]|49[3-6]|49[8-9]|54[2-3]|545|55[1-2]|556|57[1-4]|577|579|584|58[6-7]|589|59[7-8]|627|629|641|649|658|66[2-5]|667|69[2-4]|697|77[1-2]|78[2-3]|78[5-6]|78[8-9]|82[6-7]|829).*";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9]+@[A-Za-z]+\\.[A-Za-z]{2,3}$";

    public List<String> validateName(String name) {
        List<String> errors = new ArrayList<>();

        if (name == null || name.isBlank()) {
            errors.add("Name must be filled in.");
            return errors;
        }

        if (!Pattern.matches(NAME_REGEX, name)) {
            errors.add("Name must be 1-30 characters, start with an uppercase letter, and contain only Danish letters with remaining letters lowercase.");
        }

        return errors;
    }

    public List<String> validatePhoneNumber(String phoneNumber) {
        List<String> errors = new ArrayList<>();

        if (phoneNumber == null || phoneNumber.isBlank()) {
            errors.add("Phone number must be filled in.");
            return errors;
        }

        if (!Pattern.matches("\\d+", phoneNumber)) {
            errors.add("Phone number must contain only digits.");
        }

        if (phoneNumber.length() != 8) {
            errors.add("Phone number must be 8 digits.");
        }

        if (!Pattern.matches(PHONE_NUMBER_PREFIX_REGEX, phoneNumber)) {
            errors.add("Phone number must start with an allowed prefix.");
        }

        return errors;
    }

    public List<String> validateEmail(String email) {
        List<String> errors = new ArrayList<>();

        if (email == null || email.isBlank()) {
            errors.add("Email must be filled in.");
            return errors;
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            errors.add("Email must use the format text@domain.tld.");
        }

        return errors;
    }
}
