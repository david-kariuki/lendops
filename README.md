***

# LendOps (Lending Operations)

LendOps is a Spring Boot based lending operations application built as a modular monolith. It covers the core lending
lifecycle from product setup and customer onboarding to loan creation, repayment processing, overdue handling, scheduled
processes, and notification persistence.

## Modules Covered

### Common

This module contains shared concerns used across the application, including:

- Standard API response structure.
- Global exception handling.
- Health check endpoint.

### Product

This module defines loan products and the configuration that drives how loans behave.

Covered capabilities:

- Create product.
- Get product by code.
- Get all products.
- Update product configuration by type.
- Maintain active config versioning.

Supported product config types:

- `TENURE`
- `FEES`
- `BILLING`
- `LOAN_STRUCTURE`

How it works:

- A product is created once.
- Each config type is stored separately in `product_config`.
- Only one active config exists per config type at a time.
- When a config is updated, the current active version is deactivated and a new version is created.

Key validation covered:

- Duplicate product code.
- Duplicate config types in one request.
- Jakarta validation for config fields.
- Business validation such as tenure ranges and fee value requirements.

<br> 

***

### Customer

This module manages borrowers and their active loan limits.

Covered capabilities:

- Create customer.
- Get customer by reference.
- Update customer details.
- Update customer limit.

How it works:

- Each customer has one active limit at a time.
- When limit is updated, the old active limit is deactivated and a new one is created.

Key validation covered:

- Duplicate customer reference.
- Duplicate phone number.
- Duplicate email address.
- Customer not found.
- Active customer limit retrieval.

<br> 

***

### Loan

This module owns loan creation, loan retrieval, installment creation, overdue business rules, and fee application
relevant to the loan lifecycle.

Covered capabilities:

- Create loan.
- Get loan by reference.
- Generate installments for installment-based products.
- Apply service fee at loan creation.
- Mark loans and installments overdue.
- Apply late fee once when overdue first happens.

How it works:

- Loan creation depends on customer, active customer limit, product, and active product configs.
- Tenure config determines due dates.
- Billing config and structure config are stored on the loan at creation time.
- Service fee is added into total repayable amount during loan creation.
- Installment loans generate installment rows and track per-installment balances.
- Loan-level balances are also tracked for easier repayment and overdue processing.

Loan creation flow:

1. Fetch customer by customer reference.
2. Fetch active customer limit.
3. Validate requested amount against limit.
4. Fetch product by code.
5. Load active configs for the product.
6. Derive due date from tenure config.
7. Calculate total repayable amount from principal plus service fee.
8. Create loan.
9. Create installments if the product structure is installment-based.
10. Persist loan created notification.

Installment logic:

- Principal is split across installments.
- Total repayable amount is also split across installments.
- The last installment absorbs any rounding difference so totals remain exact.

Overdue logic:

- Lump sum loan becomes overdue if due date has passed and outstanding amount is still greater than zero.
- Installment loan becomes overdue if any unpaid installment has passed its due date.
- When overdue first happens, late fee is applied once if enabled.
- Installment loan totals are recalculated from installment balances to keep the loan balance accurate.
- Overdue notification is created only once when loan status first changes to `OVERDUE`.

<br> 

***

### Repayment

This module records repayments and applies them to loans and installments.

Covered capabilities:

- Create repayment.
- Reject duplicate payment references.
- Reject repayment on closed loans.
- Reject overpayment.
- Allocate payment from earliest installment to latest.
- Close loan when outstanding amount becomes zero.
- Persist repayment notification.

How it works:

- Repayment is recorded as its own entity for audit and history.
- Repayment amount is allocated to installments in installment number order.
- Each installment updates:
  - Amount paid
  - Outstanding amount
  - Status when fully paid
- Loan updates:
  - Amount paid.
  - Outstanding amount.
  - Status when fully settled.

Allocation rule:

- Earliest due installment is settled first.
- Only the amount needed for the current installment is applied.
- Remaining repayment amount moves to the next installment.

<br> 

***

### Job

This module is responsible for scheduled execution, not business rules.

Covered capability:

- Scheduled overdue loan processing job.

How it works:

- Scheduler runs periodically.
- Job calls the loan service overdue method.
- Loan module applies the actual overdue logic.

This keeps business rules in the loan domain and scheduling in the job module.

<br> 

***

### Notification

This module persists notification records for major business events.
For this case study, I have not integrated any mailing or SMS services. I did this demonstration by effectively
storing the notification details in the database.
This can later be improved by a mail server or SMS gateway integration that listens to new notification records and
sends out actual notifications.

Covered capabilities:

- Create notification record
- Get notifications by customer reference
- Get notifications by loan reference

Notification events currently covered:

- `LOAN_CREATED`
- `REPAYMENT_RECEIVED`
- `LOAN_OVERDUE`

How it works:

- Notifications are stored in the database.
- Current implementation persists notifications instead of sending them through an external provider.
- Retrieval endpoints allow inspection of notifications by customer or loan.

***

<br> 

## API Summary

#### Health

- `GET /api/v1/health`

#### Customers

- `POST /api/v1/customers`
- `GET /api/v1/customers/{customerRef}`
- `PUT /api/v1/customers/{customerRef}`
- `PUT /api/v1/customers/{customerRef}/limit`

#### Products

- `POST /api/v1/products`
- `GET /api/v1/products/{code}`
- `GET /api/v1/products`
- `PUT /api/v1/products/{code}/configs`

#### Loans

- `POST /api/v1/loans`
- `GET /api/v1/loans/{loanRef}`

#### Repayments

- `POST /api/v1/repayments`

#### Notifications

- `GET /api/v1/notifications/customers/{customerRef}`
- `GET /api/v1/notifications/loans/{loanRef}`

***
<br>

## Core Business Rules Implemented

### Product

- Product code must be unique.
- Config types must not be duplicated within one create request.
- Only one active config version per config type is used at a time.
- Config payloads are validated both structurally and through service-level business rules.

### Customer

- Customer reference must be unique.
- Phone number must be unique.
- Email address must be unique.
- One active customer limit exists at a time.

### Loan

- Requested amount must not exceed active customer limit.
- Loan behavior is driven by active product configs.
- Installment schedule is created only when loan structure is `INSTALLMENT`.
- Service fee is added during loan creation when enabled.
- Loan and installment balances track both paid and outstanding amounts.

### Repayment

- Payment reference must be unique.
- Overpayment is rejected.
- Repayment against a closed loan is rejected.
- Repayment is allocated from the earliest installment to latest.
- Fully settled installments become `PAID`.
- Fully settled loans become `CLOSED`.

### Overdue

- Overdue is determined by due date and outstanding balance.
- Late fee is applied once when overdue first happens.
- Overdue notification is created once when the loan first becomes overdue.

***
<br>

## Design Decisions Made

### Modular monolith / Single Service

I chose a modular monolith design for this application to keep all related business logic in one codebase while
still maintaining clear module boundaries.
This allows for easier development and testing without the overhead of inter-service communication.
Each module has its own service and repository layer, but they all run within the same application.

When the solution grows, based on the requirements and business needs, this can grow into a Modulith and
later multiple independent microservices.

### Product-driven loan behavior

Loan creation does not hardcode rules. Instead, it reads active product configs for:

- Tenure.
- Fees.
- Billing.
- Loan structure.

This makes the system easier to extend with new product behaviors.

### Loan owns overdue business rules

Overdue rules belong to the loan domain because they are part of the lending business lifecycle.
The job module only triggers scheduled execution.

### Repayment has its own entity

Repayment is stored separately so the system has:

- Repayment history.
- Audit trail.
- Payment references.
- Support for multiple payments over time.

***
<br>

## Assumptions Made

- Service fee is applied once at loan creation.
- Late fee is applied once when a loan or installment first becomes overdue.
- Daily fee is configured, but I did not apply it in the current implementation. I can later have a different scheduled
  process to manage this.
- Overpayment is rejected rather than partially accepted into suspense or excess handling.
- Repayment allocation follows installment order from earliest to latest.
- Notification persistence is sufficient for the case study, without external delivery.
- Authentication and authorization are out of the current scope.
- Repayment reversal and refund flows are out of the current scope.
- Scheduler timing can be tuned for local testing but conceptually runs as a periodic overdue process.

***
<br>

## Remaining Gaps or Possible Future Enhancements

These are not required to understand the current implementation, but they are natural next steps:

- Implement daily fee processing if required.
- Add loan listing and filtering endpoints.
- Add repayment history retrieval endpoint.
- Add notification status update flow, for example `PENDING` to `SENT`.
- Integrate with actual SMS or email providers/gateways.
- Add pagination for retrieval APIs.
- Add automated tests for overdue job and notification retrieval.
- Add API documentation examples for all endpoints.

***
<br>

## How to Run

### Prerequisites

- Java 17 or compatible project Java version
- MySQL
- Gradle, this is what I chose for this project.

### Application startup

1. Create the MySQL database
2. Configure datasource properties
3. Run the application
4. Flyway migrations will create and evolve the schema automatically.
5. Test on Postman.

***
<br>

## Testing Notes

#### Postman Collection

Import below collection;
> https://api.postman.com/collections/29677428-fed3641a-305b-449c-9cea-8a9eee1d6c78?access_key=PMAT-01KPN3X98Z92T5XR4H4WB3WN0A

The project has been tested with Postman using:

- Customer creation and duplicate scenarios.
- Product creation and invalid config scenarios.
- Lump sum loan creation and repayment.
- Installment loan creation and repayment.
- Overdue processing.
- Notification retrieval.

***
<br>

