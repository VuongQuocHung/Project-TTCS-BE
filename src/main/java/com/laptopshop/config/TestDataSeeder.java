package com.laptopshop.config;

import com.laptopshop.entity.*;
import com.laptopshop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3) // Run after AdminBootstrap (1) and DataBootstrap (2)
public class TestDataSeeder implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        if (branchRepository.count() > 1) {
            log.info("Test data already exists. Skipping seeding.");
            return;
        }

        log.info("Seeding test data...");

        // 1. Seed Branches
        List<Branch> branches = seedBranches();

        // 2. Seed Users (Customers & Managers)
        List<User> users = seedUsers(branches);

        // 3. Seed Vouchers
        seedVouchers();

        // 4. Seed Orders & Reviews (Only if products exist)
        if (productRepository.count() > 0) {
            seedOrdersAndReviews(branches, users);
        }

        log.info("Test data seeding completed successfully!");
    }

    private List<Branch> seedBranches() {
        String[] cities = {"Hanoi", "Ho Chi Minh", "Da Nang", "Can Tho", "Hai Phong", "Bien Hoa", "Nha Trang", "Hue", "Vinh", "Vung Tau"};
        List<Branch> branches = new ArrayList<>();
        for (int i = 0; i < cities.length; i++) {
            Branch branch = Branch.builder()
                    .name("Laptop Shop " + cities[i])
                    .address((i + 10) + " Main St, " + cities[i])
                    .phone("098765432" + i)
                    .build();
            branches.add(branchRepository.save(branch));
        }
        return branches;
    }

    private List<User> seedUsers(List<Branch> branches) {
        List<User> seededUsers = new ArrayList<>();
        // Seed 5 Managers
        for (int i = 1; i <= 5; i++) {
            User manager = User.builder()
                    .username("manager" + i)
                    .email("manager" + i + "@laptopshop.com")
                    .password(passwordEncoder.encode("password123"))
                    .fullName("Manager " + i)
                    .role(Role.MANAGER)
                    .branch(branches.get(i % branches.size()))
                    .enabled(true)
                    .build();
            seededUsers.add(userRepository.save(manager));
        }

        // Seed 15 Customers
        for (int i = 1; i <= 15; i++) {
            User customer = User.builder()
                    .username("user" + i)
                    .email("user" + i + "@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .fullName("Customer Name " + i)
                    .role(Role.CUSTOMER)
                    .enabled(true)
                    .build();
            seededUsers.add(userRepository.save(customer));
        }
        return seededUsers;
    }

    private void seedVouchers() {
        for (int i = 1; i <= 10; i++) {
            Voucher voucher = Voucher.builder()
                    .code("GiamGia" + i * 10)
                    .discountType(i % 2 == 0 ? DiscountType.PERCENTAGE : DiscountType.FIXED_AMOUNT)
                    .discountValue(i % 2 == 0 ? 10.0 : 500000.0)
                    .minOrderValue(1000000.0)
                    .maxDiscountValue(1000000.0)
                    .startDate(LocalDateTime.now().minusDays(5))
                    .endDate(LocalDateTime.now().plusMonths(1))
                    .usageLimit(100)
                    .status(VoucherStatus.ACTIVE)
                    .build();
            voucherRepository.save(voucher);
        }
    }

    private void seedOrdersAndReviews(List<Branch> branches, List<User> users) {
        List<User> customers = users.stream()
                .filter(u -> u.getRole() == Role.CUSTOMER)
                .toList();
        List<ProductVariant> variants = variantRepository.findAll();
        List<Product> products = productRepository.findAll();

        if (customers.isEmpty() || variants.isEmpty()) return;

        // Seed 15 Orders
        for (int i = 1; i <= 15; i++) {
            User customer = customers.get(random.nextInt(customers.size()));
            ProductVariant variant = variants.get(random.nextInt(variants.size()));
            Branch branch = branches.get(random.nextInt(branches.size()));

            com.laptopshop.entity.Order order = com.laptopshop.entity.Order.builder()
                    .user(customer)
                    .branch(branch)
                    .totalPrice(variant.getPrice())
                    .status(OrderStatus.values()[random.nextInt(OrderStatus.values().length)])
                    .paymentMethod(PaymentMethod.COD)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();
            
            order = orderRepository.save(order);

            // Create Order Item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .variant(variant)
                    .quantity(1)
                    .price(variant.getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // Seed 15 Reviews
        for (int i = 0; i < 15; i++) {
            User customer = customers.get(random.nextInt(customers.size()));
            Product product = products.get(random.nextInt(products.size()));

            // Use try-catch or check if review already exists due to unique constraint
            if (reviewRepository.findByUserIdAndProductId(customer.getId(), product.getId()).isEmpty()) {
                Review review = Review.builder()
                        .user(customer)
                        .product(product)
                        .rating(random.nextInt(3) + 3) // 3-5 stars
                        .content("Sản phẩm rất tuyệt vời, tôi rất hài lòng với chất lượng của Laptop này.")
                        .build();
                reviewRepository.save(review);
            }
        }
    }
}
