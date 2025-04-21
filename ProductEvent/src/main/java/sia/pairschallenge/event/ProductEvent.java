package sia.pairschallenge.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductEvent {

    String message;

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ProductEvent(String message, Long id, String name, String description, BigDecimal price, Integer quantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.message = message;
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public String getMessage() {
        return message;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
