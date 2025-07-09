package com.marketplace.catalogue.seeder;

import com.marketplace.catalogue.model.Category;
import com.marketplace.catalogue.model.Product;
import com.marketplace.catalogue.model.ProductStatus;
import com.marketplace.catalogue.repository.CategoryRepository;
import com.marketplace.catalogue.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DatabaseSeeder(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (categoryRepository.count() > 0 || productRepository.count() > 0) {
            System.out.println("Database already seeded. Skipping seeding process.");
            return;
        }

        System.out.println("Starting database seeding...");

        // Create categories
        List<Category> categories = createCategories();
        categoryRepository.saveAll(categories);
        System.out.println("Created " + categories.size() + " categories");

        // Create products
        List<Product> products = createProducts(categories);
        productRepository.saveAll(products);
        System.out.println("Created " + products.size() + " products");

        System.out.println("Database seeding completed successfully!");
    }

    private List<Category> createCategories() {
        return List.of(
            createCategory("Electroménager"),
            createCategory("Informatique"),
            createCategory("Vêtements & Chaussures"),
            createCategory("Sports & Loisirs"),
            createCategory("Beauté & Santé"),
            createCategory("Librairie"),
            createCategory("Auto & Moto"),
            createCategory("Brico & Jardin")
        );
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(name);
        category.setIsDeleted(false);
        return category;
    }

    private List<Product> createProducts(List<Category> categories) {
        return List.of(
            // Electroménager
            createProduct("Réfrigérateur Samsung Smart",
                "Réfrigérateur intelligent avec écran tactile et technologie de refroidissement rapide. Volume de 450L.",
                categories.get(0).getId(), 12499.99),

            createProduct("Machine à Laver LG",
                "Machine à laver automatique de 8kg avec technologie vapeur et connexion WiFi.",
                categories.get(0).getId(), 6999.99),

            // Informatique
            createProduct("MacBook Pro 16 pouces",
                "Ordinateur portable haute performance avec puce M2 Pro, 16Go RAM et SSD 512Go. Idéal pour les professionnels.",
                categories.get(1).getId(), 24999.99),

            createProduct("Smartphone Samsung Galaxy S23",
                "Dernier smartphone haut de gamme avec appareil photo 108MP et écran AMOLED 6.8 pouces.",
                categories.get(1).getId(), 9999.99),

            // Vêtements & Chaussures
            createProduct("Djellaba Traditionnelle",
                "Djellaba marocaine en coton premium avec broderie artisanale. Disponible en plusieurs couleurs.",
                categories.get(2).getId(), 899.99),

            createProduct("Babouches en Cuir",
                "Babouches marocaines authentiques en cuir véritable, faites à la main par des artisans de Fès.",
                categories.get(2).getId(), 399.99),

            // Sports & Loisirs
            createProduct("Vélo de Montagne",
                "Vélo tout-terrain robuste avec suspension intégrale et freins à disque hydrauliques.",
                categories.get(3).getId(), 4599.99),

            createProduct("Set de Tennis Professionnel",
                "Ensemble complet avec raquette professionnelle, balles et accessoires pour joueurs de tout niveau.",
                categories.get(3).getId(), 1299.99),

            // Beauté & Santé
            createProduct("Huile d'Argan Pure",
                "Huile d'argan 100% naturelle du Maroc pour cheveux et peau. Production artisanale et équitable.",
                categories.get(4).getId(), 249.99),

            createProduct("Hammam Set Traditionnel",
                "Ensemble complet pour le rituel du hammam avec savon noir, gant kessa et pierre d'alun naturelle.",
                categories.get(4).getId(), 349.99),

            // Librairie
            createProduct("Le Pain Nu - Mohamed Choukri",
                "Autobiographie célèbre de l'écrivain marocain Mohamed Choukri, un classique de la littérature marocaine.",
                categories.get(5).getId(), 129.99),

            createProduct("La Nuit Sacrée - Tahar Ben Jelloun",
                "Roman de l'écrivain marocain Tahar Ben Jelloun, lauréat du Prix Goncourt en 1987.",
                categories.get(5).getId(), 159.99),

            // Auto & Moto
            createProduct("Casque Moto Intégral",
                "Casque moto homologué avec visière anti-buée et système de ventilation avancé.",
                categories.get(6).getId(), 1999.99),

            createProduct("Dashcam Haute Définition",
                "Caméra embarquée 4K avec vision nocturne et détection de mouvement pour votre sécurité routière.",
                categories.get(6).getId(), 899.99),

            // Brico & Jardin
            createProduct("Perceuse Sans Fil Bosch",
                "Perceuse-visseuse 18V avec 2 batteries lithium-ion et mallette de rangement.",
                categories.get(7).getId(), 1499.99),

            createProduct("Set de Jardinage Complet",
                "Ensemble d'outils de jardinage comprenant pelle, râteau, sécateurs et plus encore.",
                categories.get(7).getId(), 799.99)
        );
    }

    private Product createProduct(String name, String description, UUID categoryId, 
                                double basePrice) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setDescription(description);
        product.setCategoryId(categoryId);
        product.setBasePrice(basePrice);
        product.setStatus(ProductStatus.available);
        // Note: Thumbnail media is now handled by SCENA service, not stored in Product entity
        return product;
    }
}
