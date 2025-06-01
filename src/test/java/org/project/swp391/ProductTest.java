package org.project.swp391;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.project.entity.Product;
import org.project.repository.ProductRepository;

import jakarta.persistence.*;

public class ProductTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    public static void init() {
        emf = Persistence.createEntityManagerFactory("testPU");
    }

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @AfterAll
    public static void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    public void testAddProduct() {
        ProductRepository repo = new ProductRepository(); // your class
        Product p = new Product();
        p.setName("Test Product");
        p.setType(ProductType.MEDICINE);

        Product result = repo.addProduct(p);
        assertNotNull(result.getId());
        assertEquals("Test Product", result.getName());
    }

    @Test
    public void testDeleteProduct() {
        ProductRepository repo = new ProductRepository();
        Product p = new Product();
        p.setName("To Delete");
        p.setType(ProductType.SERVICE);

        // Save
        Product saved = repo.addProduct(p);
        assertNotNull(saved.getId());

        // Delete
        boolean deleted = repo.deleteProduct(saved);
        assertEquals(saved.getId(), deleted.getId());

        // Verify it's deleted
        Product found = em.find(Product.class, deleted.getId());
        assertNull(found);
    }
}
