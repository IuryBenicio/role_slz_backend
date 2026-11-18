package com.example.roleslz_backend.Utills;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // O Spring passará a monitorar suas entidades que possuem o AuditingEntityListener.
}
