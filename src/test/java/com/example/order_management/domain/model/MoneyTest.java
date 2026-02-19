package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for Money Value Object following TDD approach.
 */
@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create Money with valid amount and default currency USD")
    void shouldCreateMoneyWithDefaultCurrency() {
        // Given & When
        Money money = new Money(new BigDecimal("100.50"));
        
        // Then
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(money.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should create Money with custom currency")
    void shouldCreateMoneyWithCustomCurrency() {
        // Given & When
        Money money = new Money(new BigDecimal("50.00"), "EUR");
        
        // Then
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(money.getCurrency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should add two Money objects with same currency")
    void shouldAddMoneyWithSameCurrency() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"));
        Money money2 = new Money(new BigDecimal("50.25"));
        
        // When
        Money result = money1.add(money2);
        
        // Then
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should throw CurrencyMismatchException when adding Money with different currencies")
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"), "USD");
        Money money2 = new Money(new BigDecimal("50.25"), "EUR");
        
        // When & Then
        assertThatThrownBy(() -> money1.add(money2))
            .isInstanceOf(CurrencyMismatchException.class)
            .hasMessageContaining("currency");
    }

    @Test
    @DisplayName("Should subtract two Money objects with same currency")
    void shouldSubtractMoneyWithSameCurrency() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"));
        Money money2 = new Money(new BigDecimal("50.25"));
        
        // When
        Money result = money1.subtract(money2);
        
        // Then
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("50.25"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should throw CurrencyMismatchException when subtracting Money with different currencies")
    void shouldThrowExceptionWhenSubtractingDifferentCurrencies() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"), "USD");
        Money money2 = new Money(new BigDecimal("50.25"), "EUR");
        
        // When & Then
        assertThatThrownBy(() -> money1.subtract(money2))
            .isInstanceOf(CurrencyMismatchException.class)
            .hasMessageContaining("currency");
    }

    @Test
    @DisplayName("Should multiply Money by a positive integer")
    void shouldMultiplyMoneyByInteger() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        
        // When
        Money result = money.multiply(3);
        
        // Then
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("31.50"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should multiply Money by zero")
    void shouldMultiplyMoneyByZero() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        
        // When
        Money result = money.multiply(0);
        
        // Then
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should be equal when Money objects have same amount and currency")
    void shouldBeEqualWhenSameAmountAndCurrency() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"), "USD");
        Money money2 = new Money(new BigDecimal("100.50"), "USD");
        
        // When & Then
        assertThat(money1).isEqualTo(money2);
        assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when Money objects have different amounts")
    void shouldNotBeEqualWhenDifferentAmounts() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"), "USD");
        Money money2 = new Money(new BigDecimal("100.51"), "USD");
        
        // When & Then
        assertThat(money1).isNotEqualTo(money2);
    }

    @Test
    @DisplayName("Should not be equal when Money objects have different currencies")
    void shouldNotBeEqualWhenDifferentCurrencies() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"), "USD");
        Money money2 = new Money(new BigDecimal("100.50"), "EUR");
        
        // When & Then
        assertThat(money1).isNotEqualTo(money2);
    }
}
