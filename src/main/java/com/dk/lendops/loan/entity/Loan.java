package com.dk.lendops.loan.entity;

import com.dk.lendops.customer.entity.Customer;
import com.dk.lendops.loan.enums.LoanStatus;
import com.dk.lendops.loan.enums.LoanStructureType;
import com.dk.lendops.product.entity.Product;
import com.dk.lendops.product.enums.BillingType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Loan entity
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString(exclude = {"customer", "product", "installments"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_ref", nullable = false, unique = true, length = 100)
    private String loanRef;

    // Link to customer
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Link to product
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "principal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "total_repayable_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalRepayableAmount;

    @Column(name = "disbursed_at", nullable = false)
    private LocalDateTime disbursedAt;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    // From product config
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false, length = 50)
    private BillingType billingType;

    // From product config
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_structure_type", nullable = false, length = 50)
    private LoanStructureType loanStructureType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private LoanStatus status;

    @Column(name = "amount_paid", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "outstanding_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal outstandingAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Installments only for installment-based loans
    @Builder.Default
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanInstallment> installments = new ArrayList<>();

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
