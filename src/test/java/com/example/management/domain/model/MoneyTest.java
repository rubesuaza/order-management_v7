package com.example.management.domain.model;

import com.example.management.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithDefaultCurrency() {
        Money money = new Money(new BigDecimal("10.50"));
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("10.50"));
        assertThat(money.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldCreateMoneyWithExplicitCurrency() {
        Money money = new Money(new BigDecimal("100.00"), "EUR");
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(money.getCurrency()).isEqualTo("EUR");
    }

    @Test
    void shouldAddMoneyWithSameCurrency() {
        Money a = new Money(new BigDecimal("10.00"));
        Money b = new Money(new BigDecimal("5.50"));
        Money result = a.add(b);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("15.50"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldThrowWhenAddingDifferentCurrencies() {
        Money usd = new Money(new BigDecimal("10.00"), "USD");
        Money eur = new Money(new BigDecimal("5.00"), "EUR");
        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(CurrencyMismatchException.class)
                .hasMessageContaining("USD")
                .hasMessageContaining("EUR");
    }

    @Test
    void shouldSubtractMoneyWithSameCurrency() {
        Money a = new Money(new BigDecimal("10.00"));
        Money b = new Money(new BigDecimal("3.25"));
        Money result = a.subtract(b);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("6.75"));
    }

    @Test
    void shouldThrowWhenSubtractingDifferentCurrencies() {
        Money usd = new Money(new BigDecimal("10.00"), "USD");
        Money eur = new Money(new BigDecimal("5.00"), "EUR");
        assertThatThrownBy(() -> usd.subtract(eur))
                .isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void shouldMultiplyMoneyByInteger() {
        Money money = new Money(new BigDecimal("2.50"));
        Money result = money.multiply(4);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldCompareMoneyValues() {
        Money ten = new Money(new BigDecimal("10.00"));
        Money five = new Money(new BigDecimal("5.00"));
        assertThat(ten.isGreaterThanOrEqual(five)).isTrue();
        assertThat(five.isGreaterThanOrEqual(ten)).isFalse();
        assertThat(ten.isGreaterThanOrEqual(ten)).isTrue();
    }

    @Test
    void shouldThrowWhenComparingDifferentCurrencies() {
        Money usd = new Money(new BigDecimal("10.00"), "USD");
        Money eur = new Money(new BigDecimal("5.00"), "EUR");
        assertThatThrownBy(() -> usd.isGreaterThanOrEqual(eur))
                .isInstanceOf(CurrencyMismatchException.class);
    }
}
