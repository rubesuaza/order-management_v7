package com.example.order_management.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for Address Value Object following TDD approach.
 */
@DisplayName("Address Value Object Tests")
class AddressTest {

    @Test
    @DisplayName("Should create Address with all required fields")
    void shouldCreateAddressWithAllFields() {
        // Given & When
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        
        // Then
        assertThat(address.getStreet()).isEqualTo("123 Main St");
        assertThat(address.getCity()).isEqualTo("New York");
        assertThat(address.getZipCode()).isEqualTo("10001");
        assertThat(address.getCountry()).isEqualTo("USA");
    }

    @Test
    @DisplayName("Should throw exception when street is null")
    void shouldThrowExceptionWhenStreetIsNull() {
        // When & Then
        assertThatThrownBy(() -> new Address(null, "New York", "10001", "USA"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("street");
    }

    @Test
    @DisplayName("Should throw exception when city is null")
    void shouldThrowExceptionWhenCityIsNull() {
        // When & Then
        assertThatThrownBy(() -> new Address("123 Main St", null, "10001", "USA"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("city");
    }

    @Test
    @DisplayName("Should throw exception when zipCode is null")
    void shouldThrowExceptionWhenZipCodeIsNull() {
        // When & Then
        assertThatThrownBy(() -> new Address("123 Main St", "New York", null, "USA"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("zipCode");
    }

    @Test
    @DisplayName("Should throw exception when country is null")
    void shouldThrowExceptionWhenCountryIsNull() {
        // When & Then
        assertThatThrownBy(() -> new Address("123 Main St", "New York", "10001", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("country");
    }

    @Test
    @DisplayName("Should be equal when Address objects have same values")
    void shouldBeEqualWhenSameValues() {
        // Given
        Address address1 = new Address("123 Main St", "New York", "10001", "USA");
        Address address2 = new Address("123 Main St", "New York", "10001", "USA");
        
        // When & Then
        assertThat(address1).isEqualTo(address2);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when Address objects have different street")
    void shouldNotBeEqualWhenDifferentStreet() {
        // Given
        Address address1 = new Address("123 Main St", "New York", "10001", "USA");
        Address address2 = new Address("456 Oak Ave", "New York", "10001", "USA");
        
        // When & Then
        assertThat(address1).isNotEqualTo(address2);
    }
}
