package com.example.relatives.config;

import com.example.relatives.model.Location;
import com.example.relatives.model.Relative;
import com.example.relatives.model.Role;
import com.example.relatives.model.User;
import com.example.relatives.repository.LocationRepository;
import com.example.relatives.repository.RelativeRepository;
import com.example.relatives.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDefaultData(
            UserRepository userRepository,
            LocationRepository locationRepository,
            RelativeRepository relativeRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // === Пользователь-администратор ===
            User admin = userRepository.findByUsername("1").orElse(null);
            if (admin == null) {
                admin = new User();
                admin.setUsername("1");
                admin.setPassword(passwordEncoder.encode("1"));
                admin.setRole(Role.ADMIN);
                admin.setOwner(null);
                userRepository.save(admin);
                System.out.println("✅ Создан администратор: 1 / 1");
            }

            // === Локации ===
            if (locationRepository.count() == 0) {
                locationRepository.saveAll(List.of(
                        new Location(null, "Алматы, Казахстан", "Казахстан", "Алматинская область", "Алматы", null, null, null),
                        new Location(null, "Астана, Казахстан", "Казахстан", "Акмолинская область", "Астана", null, null, null),
                        new Location(null, "Москва, Россия", "Россия", "Московская область", "Москва", null, null, null)
                ));
                System.out.println("✅ Добавлены локации");
            }

            List<Location> locations = locationRepository.findAll();
            Location almaty = locations.get(0);
            Location astana = locations.get(1);
            Location moscow = locations.get(2);

            if (relativeRepository.count() == 0) {

                // ===== ПОКОЛЕНИЕ 1 — дедушки и бабушки =====
                Relative grandfather = new Relative();
                grandfather.setFirstName("Николай");
                grandfather.setLastName("Плеханов");
                grandfather.setGender("MALE");
                grandfather.setBirthDate("1935-04-10");
                grandfather.setOccupation("Строитель");
                grandfather.setCurrentLocation(moscow);
                grandfather.setOwner(admin);

                Relative grandmother = new Relative();
                grandmother.setFirstName("Анна");
                grandmother.setLastName("Плеханова");
                grandmother.setGender("FEMALE");
                grandmother.setBirthDate("1938-07-22");
                grandmother.setOccupation("Медсестра");
                grandmother.setCurrentLocation(moscow);
                grandmother.setOwner(admin);

                grandfather.setSpouse(grandmother);
                grandmother.setSpouse(grandfather);

                // ===== ПОКОЛЕНИЕ 2 — родители =====
                Relative father = new Relative();
                father.setFirstName("Александр");
                father.setLastName("Плеханов");
                father.setGender("MALE");
                father.setBirthDate("1960-05-12");
                father.setOccupation("Инженер");
                father.setCurrentLocation(moscow);
                father.setFather(grandfather);
                father.setMother(grandmother);
                father.setOwner(admin);

                Relative mother = new Relative();
                mother.setFirstName("Марина");
                mother.setLastName("Плеханова");
                mother.setGender("FEMALE");
                mother.setBirthDate("1963-09-20");
                mother.setOccupation("Учитель");
                mother.setCurrentLocation(moscow);
                mother.setOwner(admin);

                father.setSpouse(mother);
                mother.setSpouse(father);

                // ===== ПОКОЛЕНИЕ 3 — дети =====
                Relative child1 = new Relative();
                child1.setFirstName("Алексей");
                child1.setLastName("Плеханов");
                child1.setGender("MALE");
                child1.setBirthDate("1990-03-15");
                child1.setOccupation("Разработчик");
                child1.setCurrentLocation(almaty);
                child1.setFather(father);
                child1.setMother(mother);
                child1.setOwner(admin);

                Relative child2 = new Relative();
                child2.setFirstName("Екатерина");
                child2.setLastName("Плеханова");
                child2.setGender("FEMALE");
                child2.setBirthDate("1995-11-02");
                child2.setOccupation("Дизайнер");
                child2.setCurrentLocation(astana);
                child2.setFather(father);
                child2.setMother(mother);
                child2.setOwner(admin);

                // ===== ПОКОЛЕНИЕ 4 — внуки =====
                Relative grandchild = new Relative();
                grandchild.setFirstName("Иван");
                grandchild.setLastName("Плеханов");
                grandchild.setGender("MALE");
                grandchild.setBirthDate("2022-06-01");
                grandchild.setOccupation("Малыш :)");
                grandchild.setCurrentLocation(almaty);
                grandchild.setFather(child1);
                grandchild.setOwner(admin);

                relativeRepository.saveAll(List.of(
                        grandfather, grandmother,
                        father, mother,
                        child1, child2,
                        grandchild
                ));
                System.out.println("✅ Добавлены тестовые родственники для сложного древа");
            }

        };
    }
}
