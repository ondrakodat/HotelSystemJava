package com.kodat.complexhotelsystem;

import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.entities.UserSession;
import com.kodat.complexhotelsystem.interfaces.Irepository.IUserSessionRepository;
import com.kodat.complexhotelsystem.jpaIRepository.IUserSessionJpa;
import com.kodat.complexhotelsystem.services.jwt.JwtRefreshTokenGeneration;
import com.kodat.complexhotelsystem.services.jwt.RefreshTokenReuseService;
import com.kodat.complexhotelsystem.services.jwt.RefreshTokenRotationTransaction;
import com.kodat.complexhotelsystem.services.jwt.RefreshTokenUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = RefreshTokenRotationConcurrencyTest.TestApplication.class,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:refresh-token-tests;DB_CLOSE_DELAY=-1",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.open-in-view=false"
        }
)
class RefreshTokenRotationConcurrencyTest {
    @Autowired EntityManager entityManager;
    @Autowired IUserSessionRepository repository;
    @Autowired JwtRefreshTokenGeneration refreshTokens;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired RefreshTokenUtils utils;

    @Test
    void productionJpaQueryDeclaresPessimisticWriteLock() throws Exception {
        Method queryMethod = IUserSessionJpa.class.getMethod("findByTokenHashWithLock", String.class);
        assertEquals(LockModeType.PESSIMISTIC_WRITE, queryMethod.getAnnotation(Lock.class).value());
        assertNotNull(queryMethod.getAnnotation(Query.class));
    }

    @Test
    void concurrentRefreshWaitsForFirstRotationThenTreatsSecondUseAsReplayAndRevokesFamily() throws Exception {
        String rawToken = "concurrent-refresh-token";
        UUID familyId = UUID.randomUUID();
        String email = "rotation-" + UUID.randomUUID() + "@example.test";
        TransactionTemplate transactions = new TransactionTemplate(transactionManager);
        transactions.executeWithoutResult(status -> {
            Person person = new Person();
            person.setFirstname("Refresh");
            person.setLastname("Test");
            person.setEmail(email);
            person.setSecret("not-a-real-password");
            person.setPhone("+420" + Math.abs(email.hashCode()));
            entityManager.persist(person);

            UserSession session = new UserSession();
            session.setPerson(person);
            session.setTokenHash(utils.createHashToken(rawToken));
            session.setFamilyId(familyId);
            session.setCreatedAt(Instant.now());
            session.setExpiresAt(Instant.now().plusSeconds(3600));
            entityManager.persist(session);
        });

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            List<Future<Object>> results = java.util.stream.IntStream.range(0, 2)
                    .mapToObj(ignored -> executor.submit(() -> {
                        ready.countDown();
                        if (!start.await(5, TimeUnit.SECONDS)) {
                            throw new IllegalStateException("Concurrent requests did not start");
                        }
                        try {
                            return refreshTokens.refresh(rawToken);
                        } catch (IllegalArgumentException expectedReplay) {
                            return expectedReplay;
                        }
                    }))
                    .toList();

            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            List<Object> outcomes = results.stream().map(future -> {
                try {
                    return future.get(10, TimeUnit.SECONDS);
                } catch (Exception exception) {
                    throw new AssertionError("Refresh request failed unexpectedly", exception);
                }
            }).toList();

            assertEquals(
                    1,
                    outcomes.stream().filter(value -> !(value instanceof IllegalArgumentException)).count(),
                    () -> "Expected one successful rotation, got: " + outcomes.stream()
                            .map(value -> value instanceof Exception exception
                                    ? exception.getClass().getSimpleName() + ": " + exception.getMessage()
                                    : value.getClass().getSimpleName())
                            .toList()
            );
            assertEquals(1, outcomes.stream().filter(IllegalArgumentException.class::isInstance).count());

            List<UserSession> sessions = transactions.execute(status -> repository.findAllSessionsInOneFamily(familyId));
            assertNotNull(sessions);
            assertEquals(2, sessions.size(), "The original token and one replacement must be committed");
            assertTrue(sessions.stream().allMatch(session -> session.getRevokedAt() != null),
                    "REQUIRES_NEW replay handling revokes the original and the legitimate concurrent replacement");
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = DataJpaRepositoriesAutoConfiguration.class)
    @EntityScan(basePackageClasses = UserSession.class)
    static class TestApplication {
        @Bean
        RefreshTokenUtils refreshTokenUtils() {
            return new RefreshTokenUtils();
        }

        @Bean
        IUserSessionRepository userSessionRepository(EntityManager entityManager) {
            return new EntityManagerSessionRepository(entityManager);
        }

        @Bean
        JwtRefreshTokenGeneration jwtRefreshTokenGeneration(
                IUserSessionRepository repository,
                @Lazy RefreshTokenRotationTransaction transaction,
                RefreshTokenReuseService reuseService,
                RefreshTokenUtils utils
        ) {
            return new JwtRefreshTokenGeneration(repository, transaction, reuseService, utils);
        }

        @Bean
        RefreshTokenRotationTransaction refreshTokenRotationTransaction(
                IUserSessionRepository repository,
                RefreshTokenUtils utils
        ) {
            return new RefreshTokenRotationTransaction(repository, utils);
        }

        @Bean
        RefreshTokenReuseService refreshTokenReuseService(IUserSessionRepository repository) {
            return new RefreshTokenReuseService(repository);
        }
    }

    static class EntityManagerSessionRepository implements IUserSessionRepository {
        private final EntityManager entityManager;

        EntityManagerSessionRepository(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

        @Override
        public void saveRefreshSession(UserSession session) {
            if (session.getId() == null) entityManager.persist(session);
            else entityManager.merge(session);
        }

        @Override
        public List<UserSession> findAllSessionsInOneFamily(UUID familyId) {
            return entityManager.createQuery(
                            "select s from UserSession s where s.familyId = :familyId", UserSession.class)
                    .setParameter("familyId", familyId)
                    .getResultList();
        }

        @Override
        public Optional<UserSession> findSessionByHashToken(String tokenHash) {
            return findByTokenHash(tokenHash, false);
        }

        @Override
        public Optional<UserSession> findByTokenHashWithLock(String tokenHash) {
            return findByTokenHash(tokenHash, true);
        }

        private Optional<UserSession> findByTokenHash(String tokenHash, boolean lock) {
            try {
                var query = entityManager.createQuery(
                                "select s from UserSession s where s.tokenHash = :tokenHash", UserSession.class)
                        .setParameter("tokenHash", tokenHash);
                if (lock) query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                return Optional.of(query.getSingleResult());
            } catch (NoResultException ignored) {
                return Optional.empty();
            }
        }

        @Override
        public int revokeAllByFamilyId(UUID familyId, Instant now) {
            return entityManager.createQuery(
                            "update UserSession s set s.revokedAt = :now where s.familyId = :familyId and s.revokedAt is null")
                    .setParameter("familyId", familyId)
                    .setParameter("now", now)
                    .executeUpdate();
        }

        @Override
        public int revokeAllByPersonId(long id, Instant now) {
            return 0;
        }
    }
}
