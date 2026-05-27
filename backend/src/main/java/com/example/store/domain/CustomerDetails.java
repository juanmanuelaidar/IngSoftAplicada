package com.example.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CustomerDetails.
 */
@Entity
@Table(name = "customer_details")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "phone")
    private String phone;

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JsonIgnoreProperties(value = { "customer", "orders" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "customer")
    private ShoppingCart shoppingCart;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CustomerDetails id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public CustomerDetails phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return this.address;
    }

    public CustomerDetails address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CustomerDetails user(User user) {
        this.setUser(user);
        return this;
    }

    public ShoppingCart getShoppingCart() {
        return this.shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        if (this.shoppingCart != null) {
            this.shoppingCart.setCustomer(null);
        }
        if (shoppingCart != null) {
            shoppingCart.setCustomer(this);
        }
        this.shoppingCart = shoppingCart;
    }

    public CustomerDetails shoppingCart(ShoppingCart shoppingCart) {
        this.setShoppingCart(shoppingCart);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerDetails)) {
            return false;
        }
        return getId() != null && getId().equals(((CustomerDetails) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerDetails{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", address='" + getAddress() + "'" +
            "}";
    }
}
