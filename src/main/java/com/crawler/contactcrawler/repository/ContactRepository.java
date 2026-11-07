package com.crawler.contactcrawler.repository;

import com.crawler.contactcrawler.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    List<Contact> findBySourceUrl(String sourceUrl);
    
    List<Contact> findByPhoneContaining(String phone);
    
    List<Contact> findByEmailContaining(String email);
}