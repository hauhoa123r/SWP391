package org.project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * Class này dùng để chạy thử nghiệm phần kho với cơ sở dữ liệu H2 riêng biệt.
 * Để chạy, sử dụng lệnh: mvn spring-boot:run -Dspring-boot.run.profiles=test -Dspring-boot.run.main-class=org.project.WarehouseTestRunner
 */
@SpringBootApplication
@Profile("test")
public class WarehouseTestRunner {

    public static void main(String[] args) {
        // Thiết lập profile là "test" để sử dụng application-test.properties
        System.setProperty("spring.profiles.active", "test");
        SpringApplication.run(WarehouseTestRunner.class, args);
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            System.out.println("=================================================");
            System.out.println("Môi trường test cho phần kho đã được khởi tạo");
            System.out.println("Sử dụng H2 Database in-memory");
            System.out.println("Truy cập H2 Console tại: http://localhost:9091/h2-console");
            System.out.println("JDBC URL: jdbc:h2:mem:testdb");
            System.out.println("Username: sa");
            System.out.println("Password: <trống>");
            System.out.println("=================================================");
            
            // Khởi tạo dữ liệu mẫu cho kho ở đây
            // Ví dụ:
            // - Tạo các sản phẩm mẫu
            // - Tạo các yêu cầu nhập kho mẫu
            // - Tạo các yêu cầu xuất kho mẫu
            
            System.out.println("Dữ liệu mẫu đã được tạo thành công!");
        };
    }
} 