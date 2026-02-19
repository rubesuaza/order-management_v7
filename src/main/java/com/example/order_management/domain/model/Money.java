package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.CurrencyMismatchException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object representing monetary amounts.
 * Immutable and supports operations: addition, subtraction, and multiplication.
 */
public final class Money {
    
    private final BigDecimal amount;
    private final String currency;
    
    private static final String DEFAULT_CURRENCY = "USD";
    
    public Money(BigDecimal amount) {
        this(amount, DEFAULT_CURRENCY);
    }
    
    public Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank");
        }
        this.amount = amount;
        this.currency = currency;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    /**
     * Adds another Money amount. Both must have the same currency.
     * 
     * @param other the Money to add
     * @return a new Money instance with the sum
     * @throws CurrencyMismatchException if currencies don't match
     */
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(
                String.format("Cannot add amounts with different currencies: %s and %s", 
                    this.currency, other.currency)
            );
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    /**
     * Subtracts another Money amount. Both must have the same currency.
     * 
     * @param other the Money to subtract
     * @return a new Money instance with the difference
     * @throws CurrencyMismatchException if currencies don't match
     */
    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(
                String.format("Cannot subtract amounts with different currencies: %s and %s", 
                    this.currency, other.currency)
            );
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }
    
    /**
     * Multiplies this Money by an integer factor.
     * 
     * @param factor the multiplier
     * @return a new Money instance with the multiplied amount
     */
    public Money multiply(int factor) {
        return new Money(this.amount.multiply(new BigDecimal(factor)), this.currency);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
    
    @Override
    public String toString() {
        return String.format("%s %s", amount, currency);
    }
}
