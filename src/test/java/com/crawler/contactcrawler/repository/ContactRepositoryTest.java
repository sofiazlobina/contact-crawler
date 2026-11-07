package com.crawler.contactcrawler.repository;

import com.crawler.contactcrawler.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContactRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContactRepository repository;

    @BeforeEach
    void setUp() {
        Contact contact = new Contact();
        contact.setSourceUrl("https://test.com");
        contact.setPhone("+79991234567");
        contact.setEmail("test@example.com");
        contact.setCompanyName("Test Company");
        entityManager.persist(contact);
        entityManager.flush();
    }

    @Test
    void shouldFindBySourceUrl() {
        List<Contact> found = repository.findBySourceUrl("https://test.com");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindByPhoneContaining() {
        List<Contact> found = repository.findByPhoneContaining("999");
        assertThat(found).hasSize(1);
    }
}