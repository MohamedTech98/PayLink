# PayLink - Requirements Pack (Phase 1)

| | |
|---|---|
| **Project** | PayLink Year Work Project: bill-payment and wallet backend |
| **Phase** | Phase 1 - Requirements, design, DevOps foundation (due Wednesday 25 November 2026) |
| **Document** | Requirements Pack (`docs/requirements.md`, `docs/requirements.pdf`) |
| **Scope of this document** | Problem summary, stakeholders, business rules, functional requirements and user stories, non-functional requirements, ambiguities and decisions |
| **Version / status** | 1.1 - Draft for team review |
| **Date** | 23 November 2026 |
| **Prepared by** | Person 2 (Requirements) - team and author names to be completed before submission |
| **Source of truth** | *PayLink Year Work Project - Student Brief* (referred to below as "the brief") |

## Table of Contents

1. [Introduction](#1-introduction)
2. [Problem Summary](#2-problem-summary)
3. [Stakeholders](#3-stakeholders)
4. [Business Rules and Constraints](#4-business-rules-and-constraints)
5. [Functional Requirements Overview](#5-functional-requirements-overview)
6. [User Stories and Acceptance Criteria](#6-user-stories-and-acceptance-criteria)
7. [Non-Functional Requirements](#7-non-functional-requirements)
8. [Hard-Case Coverage](#8-hard-case-coverage)
9. [Ambiguities, Assumptions and Decisions](#9-ambiguities-assumptions-and-decisions)
10. [Appendices](#10-appendices)

## 1. Introduction

### 1.1 Purpose

This document is the requirements pack for Phase 1 of the PayLink project. It turns the brief into a prioritised, testable set of requirements that the design pack (use-case, class, sequence, state and ER diagrams) and the Phase 2 implementation will be built and checked against.

### 1.2 Source of truth and rules for this document

- The brief is the only source of official requirements. Functional requirements FR-1 to FR-28, the priorities (Must, Should, Could), the business rules (section 2.6 of the brief), the payment life cycle (section 2.5), the eight hard cases (section 2.7) and the six non-functional targets (section 3.3) are reproduced **without change**.
- Where the brief is silent or unclear, this document does **not** present a guess as an official requirement. Each such point is recorded in [section 9](#9-ambiguities-assumptions-and-decisions) with a *proposed decision*, which is a **working assumption** until the instructor or team confirms it. Acceptance criteria that depend on an assumption cite its ID (for example "see AMB-02").
- The document was prepared with the help of an AI assistant. The team must review it, and the AI interactions must be recorded honestly in `docs/prompt-log.md`.

### 1.3 Conventions

| Item | Convention |
|---|---|
| Functional requirement | `FR-n` - identical to the brief (FR-1 to FR-28) |
| User story | `US-nn` - one user story per functional requirement; `US-nn` implements `FR-n` |
| Acceptance criterion | `AC-n.m` - the m-th criterion of user story n, written as Given / When / Then |
| Non-functional requirement | `NFR-n` (NFR-1 to NFR-6) |
| Business rule | `BR-nn` (section 4) |
| Hard case | `HC-n` - the eight hard cases of section 2.7 of the brief |
| Objective, challenge, stakeholder | `O-n` (section 2.3), `C-n` (section 2.4), `SH-n` (section 3) |
| Ambiguity or assumption | `AMB-nn` (section 9) |
| Priorities | Copied exactly from the brief. **Must**: mandatory and may not be dropped. **Should**: important and expected. **Could**: desirable if time allows |
| Money | All amounts are in Egyptian pounds (EGP) and use exact decimal arithmetic, never floating point (BR-13) |

## 2. Problem Summary

### 2.1 What PayLink is

PayLink is a company in Egypt, comparable to Fawry or Vodafone Cash. Millions of people pay their monthly utility bills (electricity, water, mobile and internet) through the PayLink mobile app or at agents, which are small shops with a PayLink terminal. PayLink keeps each customer's money in a digital wallet and pays the biller on the customer's behalf. It earns money from a small service fee on every payment and pays commission to agents for payments made at their terminals.

### 2.2 The problem to be solved

Customers do not want to queue at the electricity office, and billers (the utility companies) want to be paid quickly and correctly. Because PayLink holds customers' money, an error is costly: a customer charged twice, or charged for a payment the biller never received, destroys trust, costs money and may bring regulators into the picture. Management needs a **new backend that is correct with money, easy to extend with new billers, and ready for the busiest day of the month**.

### 2.3 Purpose and objectives

The purpose of the system is to let customers and agents pay bills from a wallet reliably and safely, with PayLink always knowing exactly where every pound is.

| ID | Objective |
|---|---|
| O-1 | **Be correct with money**: no double charges, no negative balances, every money movement recorded in a ledger and audited. |
| O-2 | **Be easy to extend**: a new biller needs only a new integration and configuration. |
| O-3 | **Handle peak load**: sustain 100 payments per second for one minute. |
| O-4 | **Handle failure safely**: a timeout leaves the money held and the payment Unknown, and money is only returned when PayLink is sure the biller did not receive it. |
| O-5 | **Protect the business**: apply daily limits, a velocity rule and blocked-account checks before money is held. |
| O-6 | **Detect differences**: compare PayLink's records with the billers' settlement files every night and report differences. |
| O-7 | **Be transparent**: give customers clear reasons and notifications, and give support a searchable payment history. |
| O-8 | **Be deliverable**: run locally with Docker and be built, linted and tested automatically by a CI pipeline. |

### 2.4 Major challenges

| ID | Challenge | Why it is hard | Addressed by |
|---|---|---|---|
| C-1 | Uncertain outcome of a biller call | A biller may not answer within 10 seconds, and the payment may or may not have been received (HC-2). | FR-11, FR-13, FR-26 |
| C-2 | Duplicate requests | A slow network can make the app send the same payment twice (HC-1). | FR-10 |
| C-3 | Concurrent access to one wallet | Two payments at the same moment must not spend the same money (HC-4). | FR-5, FR-12, NFR-1 |
| C-4 | Heterogeneous billers | REST/JSON, SOAP-style XML, asynchronous callbacks and batch CSV, with different field names, error codes and speeds. | FR-14, FR-15, NFR-4 |
| C-5 | Asynchronous and batch confirmation | TopUpNet confirms by callback, possibly twice (HC-6); NetCo confirms by file the next morning. | FR-14, FR-26 |
| C-6 | Varied fee rules | Fixed, percentage, tiered and promotional fees, plus agent commission. | FR-16, FR-17, FR-24 |
| C-7 | Abuse and fraud | Limits, bursts of payments and blocked accounts must be checked before money is held. | FR-18, FR-19, FR-21 |
| C-8 | Side effects must not break payments | A failing SMS provider must not stop a payment from completing (HC-5). | FR-22, FR-23 |
| C-9 | Differences between records | PayLink and the biller may disagree about a payment (HC-7). | FR-26 |
| C-10 | Accountability and regulation | Every money movement and admin action must be traceable. | FR-25, NFR-5 |
| C-11 | Performance under load | 100 payments per second, with billers that take up to 4 seconds. | NFR-2 |
| C-12 | Change | A new biller must be added (HC-8) and, in Phase 2, ElectroCo changes its API (new field names, piasters, signed requests). | FR-15, NFR-4 |

### 2.5 Expected system capabilities

- **Accounts and Wallet**: register customers and agents; Basic and Verified levels; wallets with available and held balances computed from a ledger; top-ups; holds; refunds.
- **Biller Catalog**: billers, services, required input fields, min and max amounts, active or disabled status.
- **Payment Processing**: the payment life cycle, idempotency and orchestration of the payment steps (login and limits, inquiry, fee, fraud checks, hold, biller call, confirmation, receipt).
- **Biller Integration**: one adapter per biller hiding protocol details, with timeout, retry and callback handling.
- **Fees and Commissions**: fixed, percentage and tiered fees, promotion periods and agent commission.
- **Notifications**: SMS, email and push messages with templates and retry.
- **Fraud Checks** (extra module): daily limit, velocity and blocked-account checks as a chain of rules.
- **Reconciliation** (extra module): nightly comparison with settlement files and a difference report.
- **Cross-cutting**: audit log, configuration of business rules, REST API with OpenAPI documentation.

### 2.6 One payment in brief

The brief describes a payment in nine steps: (1) the system identifies the customer and checks the verification level; (2) it asks the biller for the bill; (3) it adds the service fee and shows the total; (4) it runs the fraud checks (daily limit, number of payments in the last minute, blocked account); (5) it moves the total from the available balance to the held balance; (6) it sends the payment to the biller with a unique reference; (7) on confirmation it turns the hold into a final charge, credits the fee income and records any agent commission; (8) it notifies the customer by SMS and push; (9) overnight it reconciles against the biller's settlement file. Example from the brief: a bill of 350.00 EGP plus a fee of 2.50 EGP gives a total of 352.50 EGP.

### 2.7 Scope

| In scope | Out of scope or optional |
|---|---|
| The six mandatory modules and the two extra modules listed above | Real card processing, real KYC and real SMS, email or push delivery: all are **simulated** (mock servers and providers) |
| Three billers with different formats: ElectroCo, AquaCo, TopUpNet (FR-15) | **NetCo** daily batch integration is **optional** (FR-15, brief section 2.4) |
| A backend with at least 10 database tables and at least 20 REST endpoints | A polished UI: a simple web page or command-line client is enough; marks are for the backend |
| Phase 2 change request: ElectroCo API v2 (must be absorbable by the integration alone) | Currencies other than EGP |

## 3. Stakeholders

The **Source** column shows whether the stakeholder is named in the brief (*Brief*) or follows from its text (*Derived*), for example the "admin" and "support employee" roles in FR-6, FR-20 and FR-21, and the mention of regulators in section 2.1.

### 3.1 Stakeholder register

| ID | Stakeholder | Source | Role and main responsibilities | Main interests and concerns | Main requirements |
|---|---|---|---|---|---|
| SH-1 | **Customer** (for example Sara) | Brief | Primary user who owns a wallet and pays bills through the mobile app. Registers, tops up, runs inquiries, confirms payments, views balance and history, may request refunds of completed payments (Could). | Fast and correct payments; never being charged twice or for a payment the biller did not receive; clear reasons for failures; receipts; safe money. | FR-1 to FR-5, FR-7 to FR-10, FR-22, FR-27 |
| SH-2 | **Agent** | Brief | Runs a shop with a PayLink terminal. Takes cash top-ups, pays bills for customers, holds a wallet that receives commission. | Commission (40% of the service fee) credited correctly; a reliable terminal; quick results for waiting customers. | FR-4, FR-17 |
| SH-3 | **Administrator** | Brief (FR-6) | Operates and configures the system. Adds, edits and disables billers and services, upgrades customers to Verified, edits fees and limits, views daily totals. | Safe configuration changes without redeployment; correct fees; visible business results; an audit trail of their own actions. | FR-2, FR-6, FR-24, FR-25, FR-28 |
| SH-4 | **Support employee** | Brief (FR-20) | Handles customer problems and exceptions. Searches payments, reads the full history, blocks and unblocks accounts, approves refunds, reviews reconciliation differences. | A complete history per payment; clear difference reports; quick action on suspicious accounts. | FR-20, FR-21, FR-26, FR-27 |
| SH-5 | **Billers**: ElectroCo, AquaCo, TopUpNet, NetCo (optional) | Brief | External companies and systems that issue bills and receive payments. Answer inquiries, accept payments, return results by reply, callback or file, send settlement files. | Being paid quickly and correctly; no duplicate or unreferenced payments; stable interface contracts; consistent reconciliation. | FR-8, FR-13 to FR-15, FR-26, NFR-4 |
| SH-6 | **PayLink management and business owners** | Brief ("Management") | Sponsor the project and own the business: define the business rules and priorities, accept the system. | Correct handling of money; fee income; ease of adding billers; readiness for peak days; customer trust. | FR-16, FR-25, NFR-1 to NFR-4 |
| SH-7 | **Finance, compliance and auditors** | Derived | Rely on the ledger, audit log and reconciliation report. | Every money movement traceable (who, when, why); balances provable from the ledger. | FR-5, FR-25, FR-26, NFR-1, NFR-5 |
| SH-8 | **Regulators** | Brief (section 2.1, as a risk) | External authorities supervising companies that hold customer money. | No loss or double charge of customer funds; records exist. | FR-5, FR-25, NFR-1, NFR-5 |
| SH-9 | **Notification providers** (SMS, email, push) | Brief (SMS provider, HC-5) | External services used to deliver messages and report failures. | Correct use of their interfaces. They may be unavailable, and that must not affect payments. | FR-22, FR-23 |
| SH-10 | **Simulated card service** | Derived from FR-4 | External or mocked service that approves or declines simulated card top-ups. | A clear and stable simulation contract. | FR-4 |
| SH-11 | **Engineering team** (the student team) | Brief | Designs, builds, tests and deploys PayLink: requirements and product, quality and DevOps, architecture. | Clear and consistent requirements; testable criteria; a maintainable architecture; a green pipeline. | NFR-4, NFR-6, all FR |
| SH-12 | **Instructors and teaching assistants** | Brief | Provide the brief and the mock-biller contracts, grade the tagged versions, run the individual discussion. | Complete, testable, prioritised requirements with ambiguities resolved, consistent with the diagrams; honest AI usage. | All |

### 3.2 Actors for the design pack

The following actors are proposed so that the use-case diagram, the sequence diagrams and this document use the same names.

| Actor | Type | Examples of use cases |
|---|---|---|
| Customer | Primary, human | Register, top up, inquire, pay a bill, view balance and history, request refund |
| Agent | Primary, human | Pay on behalf of a customer, cash top-up for a customer |
| Administrator | Primary, human | Manage billers, upgrade verification, edit fees and limits, view daily totals |
| Support employee | Primary, human | Search a payment, block or unblock an account, review reconciliation, approve refund |
| Biller systems (ElectroCo, AquaCo, TopUpNet, NetCo) | External system | Answer inquiries, accept payments, send callbacks, send settlement files |
| SMS, email and push providers | External system | Deliver notifications |
| Scheduler / time | System actor | Re-query Unknown payments, retry notifications, run the nightly reconciliation, lift timed blocks |

## 4. Business Rules and Constraints

### 4.1 Business rules (brief, section 2.6)

All numbers below are fixed by the brief so that every team solves the same problem. They **must be stored in configuration, not in code** (BR-14).

| ID | Rule | Value (exactly as in the brief) | Related FR |
|---|---|---|---|
| BR-01 | Daily payment limit, Basic account | 5,000 EGP per day | FR-18, FR-2 |
| BR-02 | Daily payment limit, Verified account | 30,000 EGP per day | FR-18, FR-2 |
| BR-03 | Minimum and maximum single payment | Minimum 5 EGP, maximum 10,000 EGP | FR-18 |
| BR-04 | Fee for electricity and water | Fixed 2.50 EGP | FR-16 |
| BR-05 | Fee for mobile top-up | 1.5% of the amount, minimum 1 EGP | FR-16 |
| BR-06 | Fee for internet | Tiered: 2 EGP up to 200 EGP, 4 EGP above | FR-16 |
| BR-07 | Promotion | During a promotion period the fee is 0 for one chosen biller (configurable start and end dates) | FR-16 |
| BR-08 | Agent commission | 40% of the service fee, credited to the agent's wallet when the payment completes | FR-17 |
| BR-09 | Velocity rule | More than 5 payments in 60 seconds from one account is blocked for 10 minutes | FR-19 |
| BR-10 | Biller timeout | 10 seconds for live billers; after that the payment becomes Unknown | FR-13 |
| BR-11 | Retries of Unknown payments | Re-queried up to 3 times (after 1, 5 and 15 minutes) before it waits for reconciliation | FR-13 |
| BR-12 | Notification retry | An SMS or email that fails is retried 3 times, then marked undelivered | FR-23 |
| BR-13 | Rounding and number type | All money uses exact decimal types, never floating point | All money FRs |
| BR-14 | Configuration | The numbers above are put in configuration, not in code | FR-24 |

### 4.2 Payment life cycle (brief, section 2.5)

Every payment is in exactly one state, and only the transitions below are allowed (FR-11).

| From | Event | To |
|---|---|---|
| Initiated | Fraud checks pass and wallet has enough funds | FundsHeld |
| Initiated | Fraud check fails or funds are insufficient | Rejected |
| FundsHeld | Request sent to biller | SentToBiller |
| SentToBiller | Biller confirms | Completed |
| SentToBiller | Biller refuses | Failed |
| SentToBiller | No answer within the timeout | Unknown |
| Unknown | Biller later confirms (callback, retry or settlement file) | Completed |
| Unknown | Biller later refuses, or reconciliation finds no payment | Failed |
| Failed | Hold released to the wallet | Refunded |

**Completed, Rejected and Refunded are final.** A payment in Unknown is never refunded automatically: money is only returned once the system is sure the biller did not receive it.

### 4.3 Other rules stated in the brief

| ID | Rule | Source |
|---|---|---|
| BR-15 | A wallet has an available balance and a held balance. A hold is money reserved for a payment that is not yet confirmed. | Brief 2.3 |
| BR-16 | A balance is the sum of its ledger entries; a balance is never overwritten directly. | Brief 2.3, FR-5 |
| BR-17 | The hold covers the customer total (bill amount plus service fee), for example 352.50 EGP for a 350.00 EGP bill and a 2.50 EGP fee. It is made before the biller is called. | Brief 2.2 steps 3 and 5 |
| BR-18 | On confirmation the hold becomes a final charge, PayLink credits its fee income and records the commission if an agent was involved. | Brief 2.2 step 7 |
| BR-19 | Each payment is sent to the biller with a unique reference. | Brief 2.2 step 6 |
| BR-20 | Fraud checks cover the daily limit, the number of payments in the last minute and whether the account is blocked. | Brief 2.2 step 4 |
| BR-21 | AquaCo rejects payments above the bill amount; TopUpNet replies "accepted" first and calls back later with "success" or "failed"; NetCo has no live inquiry. | Brief 2.4 |
| BR-22 | A duplicate TopUpNet callback is ignored and logged. | Brief 2.7, case 6 |
| BR-23 | Idempotency: a request sent twice with the same idempotency key has the effect of sending it once. | Brief 2.3 |
| BR-24 | NetCo (daily batch CSV) is optional. | Brief 3.2, FR-15 |

### 4.4 Billers (brief, section 2.4)

| Biller | Service | Protocol and format | Behaviour to handle |
|---|---|---|---|
| ElectroCo | Electricity bills | REST, JSON | Fast (under 1 second). Inquiry by meter number. Payment confirmed immediately. In Phase 2 a new API version changes field names, moves amounts from pounds to piasters and requires signed requests. |
| AquaCo | Water bills | SOAP-style XML over HTTP | Slower (2 to 4 seconds). Different field names and error codes. Rejects payments above the bill amount. |
| TopUpNet | Mobile top-up | REST, JSON, asynchronous | Replies "accepted" first, then calls the callback URL minutes later with "success" or "failed". |
| NetCo (optional) | Internet bills | Daily batch CSV file | No live inquiry. PayLink uploads a file of payments once per day and receives a result file the next morning. |

### 4.5 Technical constraints (brief, sections 3.1 and 3.4)

| ID | Constraint |
|---|---|
| TC-1 | At least 6 modules, each with its own package or service, its own tests and a clear public interface. |
| TC-2 | Language and framework chosen by the team (Java Spring Boot, .NET, Node NestJS or Python FastAPI), declared in Phase 1 and fixed afterwards. |
| TC-3 | Relational database with at least 10 tables and schema migrations stored in the repository. |
| TC-4 | At least 20 REST endpoints documented with OpenAPI, plus one asynchronous flow. |
| TC-5 | At least 5 design patterns applied for a real reason. |
| TC-6 | A layered or hexagonal modular monolith first, then Notifications and Reconciliation evolved to an event-driven or service-based style (Phase 2). |
| TC-7 | A Dockerfile per runnable component and one docker compose file starting the application, the database and the mock billers. |
| TC-8 | A GitHub Actions workflow that builds, lints, tests, reports coverage, builds images and deploys the stack locally. |

## 5. Functional Requirements Overview

The table lists all 28 functional requirements with the priority given in the brief. "Primary module" is a **proposal** to keep the requirements consistent with the design pack; the brief defines the modules (section 3.1) but does not assign requirements to them.

| FR | Requirement (brief wording) | Priority | Primary module (proposed) | User story | Hard cases |
|---|---|---|---|---|---|
| FR-1 | A customer can register and has a basic verification level | **Must** | Accounts and Wallet | US-01 | - |
| FR-2 | A customer can be upgraded to verified, which raises limits | **Should** | Accounts and Wallet | US-02 | - |
| FR-3 | A customer can view wallet balance (available and held) and transaction history | **Must** | Accounts and Wallet | US-03 | - |
| FR-4 | A customer can top up the wallet (simulated card or agent cash) | **Must** | Accounts and Wallet | US-04 | - |
| FR-5 | Every balance change creates a ledger entry; balances are computed from the ledger | **Must** | Accounts and Wallet | US-05 | HC-4 |
| FR-6 | An admin can add, edit and disable billers and services | **Must** | Biller Catalog | US-06 | HC-8 |
| FR-7 | A customer can see the list of billers and the input fields each requires | **Must** | Biller Catalog | US-07 | - |
| FR-8 | A customer can run a bill inquiry and see the amount, fee and total before paying | **Must** | Payment Processing | US-08 | - |
| FR-9 | A customer can pay a bill from the wallet | **Must** | Payment Processing | US-09 | HC-1, HC-4 |
| FR-10 | A repeated request with the same idempotency key returns the original result | **Must** | Payment Processing | US-10 | HC-1 |
| FR-11 | A payment follows the life cycle in section 2.5 and no other transitions | **Must** | Payment Processing | US-11 | HC-2, HC-3 |
| FR-12 | Funds are held before the biller is called and released if the payment fails | **Must** | Payment Processing | US-12 | HC-3, HC-4 |
| FR-13 | A payment that times out becomes Unknown and is re-queried by the retry rules | **Must** | Payment Processing | US-13 | HC-2 |
| FR-14 | The system accepts asynchronous callbacks from TopUpNet and ignores duplicates | **Must** | Biller Integration | US-14 | HC-6 |
| FR-15 | The system supports at least 3 billers with different formats (ElectroCo, AquaCo, TopUpNet); NetCo batch is optional | **Must** | Biller Integration | US-15 | HC-3, HC-8 |
| FR-16 | Fees follow the rules in section 2.6, including the promotion period | **Must** | Fees and Commissions | US-16 | - |
| FR-17 | An agent can pay on behalf of a customer and earns commission | **Should** | Payment Processing | US-17 | - |
| FR-18 | Payments above the single-payment limit or the daily limit are rejected with a clear reason | **Must** | Fraud Checks | US-18 | - |
| FR-19 | More than 5 payments in 60 seconds blocks the account for 10 minutes | **Should** | Fraud Checks | US-19 | - |
| FR-20 | A support employee can search a payment by reference and see its full history | **Must** | Payment Processing | US-20 | - |
| FR-21 | A support employee can block and unblock an account | **Should** | Accounts and Wallet | US-21 | - |
| FR-22 | A customer receives an SMS and a push message on completion and on failure | **Must** | Notifications | US-22 | HC-3, HC-5 |
| FR-23 | Failed notifications are retried 3 times then marked undelivered | **Should** | Notifications | US-23 | HC-5 |
| FR-24 | An admin can edit fee and limit values without redeploying | **Should** | Fees and Commissions (configuration) | US-24 | - |
| FR-25 | Every money movement and admin action is written to an audit log | **Must** | Cross-cutting (audit) | US-25 | - |
| FR-26 | The system produces a daily reconciliation report from a settlement file | **Should** | Reconciliation | US-26 | HC-7 |
| FR-27 | A customer can request a refund of a completed payment; support approves it | **Could** | Accounts and Wallet | US-27 | - |
| FR-28 | An admin can view daily totals: payments, fees earned, commissions paid, failures per biller | **Could** | Cross-cutting (reporting) | US-28 | - |

**Priority summary:** 19 Must (FR-1, 3 to 16, 18, 20, 22, 25), 7 Should (FR-2, 17, 19, 21, 23, 24, 26) and 2 Could (FR-27, 28): 28 in total.

### 5.1 Requirements by module

The brief defines eight modules (six required, two marked "extra") but does not assign requirements to them, so this mapping is a proposal to keep the design pack consistent. Audit logging (FR-25) and reporting (FR-28) cross all modules.

| Module | Responsibility (brief, section 3.1) | Primary requirements | Also involved in |
|---|---|---|---|
| Accounts and Wallet | Customers, agents, verification levels, wallets, ledger entries, holds, top-ups, refunds | FR-1 to FR-5, FR-21, FR-27 | FR-12 (holds) |
| Biller Catalog | Billers, services, required input fields, min and max amounts, active or disabled | FR-6, FR-7 | FR-8 |
| Payment Processing | Payment life cycle, idempotency, orchestration of the payment steps | FR-8 to FR-13, FR-17, FR-20 | FR-27 |
| Biller Integration | One adapter per biller that hides its protocol; timeout, retry and callback handling | FR-14, FR-15 | FR-8, FR-13 |
| Fees and Commissions | Fixed, percentage, tiered and promotional fees; agent commission | FR-16, FR-24 | FR-8, FR-17 |
| Notifications | SMS, email and push messages with retry; message templates | FR-22, FR-23 | FR-9 |
| Fraud Checks (extra) | Daily limit, velocity and blocked-account checks as a chain of rules | FR-18, FR-19 | FR-21 |
| Reconciliation (extra) | Nightly comparison with settlement files and a difference report | FR-26 | FR-13 |
| Cross-cutting | Audit log, reporting, configuration | FR-25, FR-28 | All modules |

## 6. User Stories and Acceptance Criteria

Each user story implements exactly one functional requirement, keeps the priority of the brief, and has acceptance criteria in Given / When / Then form. Amounts in examples use the business rules of section 4. Where an example depends on a working assumption, the assumption is cited (see [section 9](#9-ambiguities-assumptions-and-decisions)).

### 6.1 Accounts and Wallet

#### US-01 Register a customer (FR-1, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Accounts and Wallet |
| Brief requirement (verbatim) | A customer can register and has a basic verification level |
| Related items | BR-01; AMB-03, AMB-05 |

**Story:** As a new customer, I want to register for a PayLink account, so that I can hold a wallet and pay my bills from my phone.

**Acceptance criteria**

- **AC-1.1**
  - **Given** a person who is not registered and supplies valid registration details
  - **When** they submit the registration request
  - **Then** a customer account is created with verification level **Basic** and a wallet with 0.00 EGP available and 0.00 EGP held (see AMB-03, AMB-05).
- **AC-1.2**
  - **Given** a registration request that uses an identifier (mobile number) that already belongs to an account
  - **When** it is submitted
  - **Then** it is rejected with a clear error and no second account is created.
- **AC-1.3**
  - **Given** a registration request with a missing or invalid mandatory field
  - **When** it is submitted
  - **Then** it is rejected with a validation error that names each invalid field and nothing is stored.
- **AC-1.4**
  - **Given** a newly registered customer
  - **When** the customer's daily limit is evaluated
  - **Then** the Basic limit of 5,000 EGP per day applies (BR-01).

#### US-02 Upgrade a customer to Verified (FR-2, Should)

| Attribute | Detail |
|---|---|
| Priority | Should |
| Module (proposed) | Accounts and Wallet |
| Brief requirement (verbatim) | A customer can be upgraded to verified, which raises limits |
| Related items | BR-01, BR-02; AMB-04 |

**Story:** As an administrator, I want to upgrade a customer from Basic to Verified, so that the customer can pay up to 30,000 EGP per day instead of 5,000 EGP.

**Acceptance criteria**

- **AC-2.1**
  - **Given** a customer with verification level Basic
  - **When** an administrator upgrades the customer
  - **Then** the level becomes **Verified** and the daily limit of 30,000 EGP applies to payments initiated afterwards (BR-02; see AMB-04).
- **AC-2.2**
  - **Given** a Basic customer who has already paid 4,000 EGP today and is then upgraded
  - **When** the customer pays 3,000 EGP
  - **Then** the payment passes the daily-limit check (4,000 + 3,000 is below 30,000).
- **AC-2.3**
  - **Given** a customer who is already Verified
  - **When** an administrator upgrades the customer again
  - **Then** nothing changes and the response says the customer is already Verified.
- **AC-2.4**
  - **Given** a user who is not an administrator
  - **When** that user tries to upgrade a customer
  - **Then** the request is refused and the level is unchanged.
- **AC-2.5**
  - **Given** a successful upgrade
  - **When** it completes
  - **Then** an audit record with who, when and why is written (FR-25).

#### US-03 View wallet balance and history (FR-3, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Accounts and Wallet |
| Brief requirement (verbatim) | A customer can view wallet balance (available and held) and transaction history |
| Related items | BR-15, BR-16 |

**Story:** As a customer, I want to view my available balance, my held balance and my transaction history, so that I always know how much money I can spend and where it went.

**Acceptance criteria**

- **AC-3.1**
  - **Given** a customer with a wallet
  - **When** the customer requests the wallet
  - **Then** both the **available** balance and the **held** balance are shown, and each equals the sum of the corresponding ledger entries.
- **AC-3.2**
  - **Given** a customer with 1,000.00 EGP available who has a payment in state FundsHeld with a total of 352.50 EGP
  - **When** the customer views the wallet
  - **Then** it shows 647.50 EGP available and 352.50 EGP held.
- **AC-3.3**
  - **Given** a customer with several transactions (top-ups, holds, charges, releases, refunds)
  - **When** the customer requests the history
  - **Then** each entry shows date and time, type, amount and reference, sorted from newest to oldest.
- **AC-3.4**
  - **Given** a customer with no transactions
  - **When** the customer views the wallet and history
  - **Then** balances show 0.00 EGP and the history is empty.
- **AC-3.5**
  - **Given** two customers
  - **When** one customer requests a wallet or history
  - **Then** only that customer's own data is returned.

#### US-04 Top up the wallet (FR-4, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Accounts and Wallet |
| Brief requirement (verbatim) | A customer can top up the wallet (simulated card or agent cash) |
| Related items | AMB-05 |

**Story:** As a customer, I want to top up my wallet by simulated card or through an agent with cash, so that I have funds available to pay bills.

**Acceptance criteria**

- **AC-4.1**
  - **Given** a customer and a valid simulated card
  - **When** the customer tops up 500.00 EGP and the simulated card payment succeeds
  - **Then** the available balance increases by 500.00 EGP and a ledger entry of type top-up is created.
- **AC-4.2**
  - **Given** a simulated card payment that is declined
  - **When** the customer tops up
  - **Then** the balance and ledger are unchanged and the customer sees a clear reason.
- **AC-4.3**
  - **Given** an agent with a PayLink terminal and a customer who hands over 300.00 EGP in cash
  - **When** the agent performs a cash top-up for that customer
  - **Then** the customer's available balance increases by 300.00 EGP and the ledger entry records the agent who performed it (see AMB-05).
- **AC-4.4**
  - **Given** a top-up request with an amount of zero, a negative amount or more than two decimal places
  - **When** it is submitted
  - **Then** it is rejected with a validation error and no ledger entry is created.
- **AC-4.5**
  - **Given** a top-up that succeeds
  - **When** it completes
  - **Then** an audit record is written (FR-25).

#### US-05 Ledger-based balances (FR-5, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Accounts and Wallet |
| Brief requirement (verbatim) | Every balance change creates a ledger entry; balances are computed from the ledger |
| Related items | BR-13, BR-16; NFR-1, NFR-5; HC-4; AMB-29 |

**Story:** As a finance officer, I want every balance change recorded as a ledger entry and every balance computed from those entries, so that wallet figures are always provable and traceable.

**Acceptance criteria**

- **AC-5.1**
  - **Given** any operation that changes a balance (top-up, hold, hold release, final charge, fee income, commission, refund)
  - **When** the operation completes
  - **Then** at least one ledger entry exists with wallet, amount, direction (available or held), type, reference and timestamp.
- **AC-5.2**
  - **Given** a payment with a total of 352.50 EGP
  - **When** the funds are held
  - **Then** the ledger shows entries that decrease the available balance by 352.50 EGP and increase the held balance by 352.50 EGP, and no balance field is overwritten directly.
- **AC-5.3**
  - **Given** any wallet
  - **When** its balances are requested
  - **Then** available and held balances equal the sums of that wallet's ledger entries.
- **AC-5.4**
  - **Given** an existing ledger entry
  - **When** any user or component tries to modify or delete it
  - **Then** the attempt is refused; corrections are made only by adding new entries (derived from BR-16; see AMB-29).
- **AC-5.5**
  - **Given** any sequence of operations, including simultaneous payments (HC-4)
  - **When** the operations finish
  - **Then** no wallet has a negative available balance.
- **AC-5.6**
  - **Given** an operation that posts ledger entries and fails before it commits
  - **When** the failure occurs
  - **Then** none of its entries is persisted and the balances are unchanged (all entries of one operation are posted in one atomic transaction).
- **AC-5.7**
  - **Given** a hold of 352.50 EGP followed by a release of 352.50 EGP for the same payment
  - **When** the balances are computed
  - **Then** the available balance returns to its value before the hold, the held balance is 0.00 EGP, and the hold and the release remain visible as separate entries.

### 6.2 Biller Catalog

#### US-06 Manage billers and services (FR-6, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Biller Catalog |
| Brief requirement (verbatim) | An admin can add, edit and disable billers and services |
| Related items | HC-8; AMB-20 |

**Story:** As an administrator, I want to add, edit and disable billers and their services, so that the catalog always reflects the companies customers can currently pay.

**Acceptance criteria**

- **AC-6.1**
  - **Given** an administrator
  - **When** the administrator adds a biller with a service, its required input fields (for example meter number or phone number), minimum and maximum amounts and status Active
  - **Then** the biller and service are stored and become visible to customers.
- **AC-6.2**
  - **Given** an existing biller or service
  - **When** an administrator edits its fields, minimum or maximum amounts
  - **Then** the changes apply to payments initiated afterwards and payments already in progress are unaffected.
- **AC-6.3**
  - **Given** an active biller
  - **When** an administrator disables it
  - **Then** it no longer appears in the customer list and new payment attempts to it are refused with a clear reason, while payments already in progress, callbacks and reconciliation for that biller are still processed (see AMB-20).
- **AC-6.4**
  - **Given** a request with an invalid configuration (for example minimum greater than maximum, or a missing required field)
  - **When** an administrator submits it
  - **Then** it is rejected with a validation error.
- **AC-6.5**
  - **Given** a user who is not an administrator
  - **When** that user tries to add, edit or disable a biller
  - **Then** the request is refused.
- **AC-6.6**
  - **Given** any change to billers or services
  - **When** it is saved
  - **Then** an audit record with who, when and what changed is written (FR-25).

#### US-07 List billers and required input fields (FR-7, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Biller Catalog |
| Brief requirement (verbatim) | A customer can see the list of billers and the input fields each requires |
| Related items | - |

**Story:** As a customer, I want to see the list of billers and the input fields each one requires, so that I know what information I must provide to pay a bill.

**Acceptance criteria**

- **AC-7.1**
  - **Given** active billers in the catalog
  - **When** a customer requests the biller list
  - **Then** each biller is shown with its service and its required input fields (for example a meter number for electricity and a phone number for mobile top-up).
- **AC-7.2**
  - **Given** a disabled biller
  - **When** a customer requests the biller list
  - **Then** the disabled biller is not shown.
- **AC-7.3**
  - **Given** a biller with minimum and maximum amounts
  - **When** the customer views it
  - **Then** the amount limits are shown with the biller.
- **AC-7.4**
  - **Given** an empty catalog
  - **When** a customer requests the list
  - **Then** an empty list is returned without error.

### 6.3 Payment Processing

#### US-08 Bill inquiry with fee and total (FR-8, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Payment Processing |
| Brief requirement (verbatim) | A customer can run a bill inquiry and see the amount, fee and total before paying |
| Related items | BR-04 to BR-07; AMB-10, AMB-19 |

**Story:** As a customer, I want to run a bill inquiry and see the bill amount, the service fee and the total before I pay, so that I can confirm the exact cost before committing any money.

**Acceptance criteria**

- **AC-8.1**
  - **Given** an active electricity biller (ElectroCo), a valid meter number and a bill of 350.00 EGP
  - **When** the customer runs an inquiry
  - **Then** the response shows amount 350.00 EGP, fee 2.50 EGP and total 352.50 EGP.
- **AC-8.2**
  - **Given** a biller promotion that is active for this biller
  - **When** the customer runs an inquiry
  - **Then** the fee shown is 0.00 EGP and the total equals the bill amount (BR-07).
- **AC-8.3**
  - **Given** an input that the biller does not recognise (for example an unknown meter number)
  - **When** the customer runs an inquiry
  - **Then** the customer sees a clear "no bill found" message and no payment is created.
- **AC-8.4**
  - **Given** a biller that does not answer within the 10-second timeout or that is down
  - **When** the customer runs an inquiry
  - **Then** the customer sees a clear message that the biller is unavailable and no money moves (see AMB-19).
- **AC-8.5**
  - **Given** any inquiry
  - **When** it completes
  - **Then** no ledger entry is created and no balance changes.
- **AC-8.6**
  - **Given** a disabled biller
  - **When** the customer runs an inquiry
  - **Then** it is refused with a clear reason.
- **AC-8.7**
  - **Given** TopUpNet with a valid phone number and a customer-chosen amount of 100.00 EGP
  - **When** the customer requests the fee and total preview
  - **Then** the customer sees an amount of 100.00 EGP, a fee of 1.50 EGP and a total of 101.50 EGP (see AMB-10).

#### US-09 Pay a bill from the wallet (FR-9, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Payment Processing |
| Brief requirement (verbatim) | A customer can pay a bill from the wallet |
| Related items | BR-03, BR-17 to BR-19; HC-1, HC-4 |

**Story:** As a customer, I want to pay a bill from my wallet, so that I do not have to queue at the utility company's office.

**Acceptance criteria**

- **AC-9.1**
  - **Given** an authenticated customer with 1,000.00 EGP available, an active biller and an inquiry total of 352.50 EGP
  - **When** the customer confirms the payment with a new idempotency key and ElectroCo confirms it
  - **Then** the payment ends in state **Completed**, the wallet shows 647.50 EGP available and 0.00 EGP held, and the ledger records the hold and the final charge.
- **AC-9.2**
  - **Given** a customer with 300.00 EGP available and a payment total of 352.50 EGP
  - **When** the customer pays
  - **Then** the payment is **Rejected** with the reason "insufficient funds", nothing is held, the biller is not called and the wallet is unchanged.
- **AC-9.3**
  - **Given** a payment that fails the fraud checks (daily limit, velocity or blocked account)
  - **When** the customer pays
  - **Then** the payment is **Rejected** with the specific reason, nothing is held and the biller is not called (see US-18, US-19, US-21).
- **AC-9.4**
  - **Given** a payment that completes
  - **When** it reaches Completed
  - **Then** PayLink's fee income is credited (2.50 EGP in the example), the commission is recorded if an agent was involved (US-17) and the customer is notified (US-22).
- **AC-9.5**
  - **Given** a payment request sent to a biller
  - **When** the request is sent
  - **Then** it carries a unique payment reference that is stored with the payment (BR-19).
- **AC-9.6**
  - **Given** a disabled biller, or a missing or invalid required input field
  - **When** the customer submits a payment
  - **Then** the request is refused with a validation error and no payment or money movement is created.
- **AC-9.7**
  - **Given** an unauthenticated request
  - **When** a payment is submitted
  - **Then** the request is refused and nothing is created (see AMB-03).

#### US-10 Idempotent payment requests (FR-10, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Payment Processing |
| Brief requirement (verbatim) | A repeated request with the same idempotency key returns the original result |
| Related items | NFR-1; BR-23; HC-1; AMB-07 |

**Story:** As a customer on an unreliable mobile network, I want a repeated payment request with the same idempotency key to return the original result, so that I am never charged twice for the same bill.

**Acceptance criteria**

- **AC-10.1**
  - **Given** a payment request that was processed with idempotency key K
  - **When** the same request is sent again with key K because the network was slow (HC-1)
  - **Then** exactly one payment and one charge exist and the second request returns the result of the first.
- **AC-10.2**
  - **Given** a payment with key K that is still being processed
  - **When** the same request arrives again with key K
  - **Then** no second payment is created and the response shows the current status of the existing payment.
- **AC-10.3**
  - **Given** ten identical requests with key K sent at the same time
  - **When** they are all processed
  - **Then** exactly one payment is created, only one hold and one charge appear in the ledger, and all ten responses refer to the same payment.
- **AC-10.4**
  - **Given** a payment with key K
  - **When** a request with key K but a different amount or biller input is sent
  - **Then** it is rejected with a conflict error and no new payment is created (see AMB-07).
- **AC-10.5**
  - **Given** a new idempotency key
  - **When** a request is sent
  - **Then** it is treated as a new payment.
- **AC-10.6**
  - **Given** a payment request without an idempotency key
  - **When** it is submitted
  - **Then** it is refused with a validation error (see AMB-07).
- **AC-10.7**
  - **Given** a payment with key K that ended Rejected or Failed
  - **When** the same request with key K is received again
  - **Then** the original result is returned and the payment is not evaluated again.

#### US-11 Payment life cycle (FR-11, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Payment Processing |
| Brief requirement (verbatim) | A payment follows the life cycle in section 2.5 and no other transitions |
| Related items | Section 4.2; BR-16 to BR-18; AMB-26 |

**Story:** As the PayLink operations owner, I want every payment to follow the defined life cycle and no other transitions, so that each payment is always in exactly one valid state.

**Acceptance criteria**

- **AC-11.1**
  - **Given** a payment in Initiated that passes fraud checks and has enough funds
  - **When** the funds are held
  - **Then** it moves to **FundsHeld**.
- **AC-11.2**
  - **Given** a payment in Initiated
  - **When** a fraud check fails or the funds are insufficient
  - **Then** it moves to **Rejected**.
- **AC-11.3**
  - **Given** a payment in FundsHeld
  - **When** the request is sent to the biller
  - **Then** it moves to **SentToBiller**.
- **AC-11.4**
  - **Given** a payment in SentToBiller
  - **When** the biller confirms, refuses or does not answer within the timeout
  - **Then** it moves to **Completed**, **Failed** or **Unknown** respectively.
- **AC-11.5a**
  - **Given** a payment in Unknown
  - **When** the biller later confirms (callback, retry or settlement file)
  - **Then** it moves to **Completed**.
- **AC-11.5b**
  - **Given** a payment in Unknown
  - **When** the biller later refuses or reconciliation finds no payment
  - **Then** it moves to **Failed**.
- **AC-11.6**
  - **Given** a payment in Failed
  - **When** the hold is released to the wallet
  - **Then** it moves to **Refunded**.
- **AC-11.7**
  - **Given** a payment in Completed, Rejected or Refunded
  - **When** any transition is attempted
  - **Then** it is refused and the state is unchanged.
- **AC-11.8**
  - **Given** any transition that is not in the life-cycle table (for example FundsHeld to Completed, or Completed to Failed)
  - **When** it is attempted
  - **Then** it is refused, the state is unchanged and the attempt is logged.
- **AC-11.9**
  - **Given** a payment in Unknown
  - **When** any rule runs
  - **Then** the money is not refunded automatically; money is only returned after the payment has become Failed.
- **AC-11.10**
  - **Given** any state change
  - **When** it happens
  - **Then** the transition, its cause and its timestamp are recorded so that the full history is available (FR-20).

#### US-12 Hold and release of funds (FR-12, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Payment Processing |
| Brief requirement (verbatim) | Funds are held before the biller is called and released if the payment fails |
| Related items | BR-17; NFR-1; HC-3, HC-4 |

**Story:** As a customer, I want my funds to be held before the biller is called and released if the payment fails, so that my money is safe and I am never charged for a payment the biller did not receive.

**Acceptance criteria**

- **AC-12.1**
  - **Given** a payment with a total of 352.50 EGP and sufficient funds
  - **When** the fraud checks pass
  - **Then** 352.50 EGP moves from the available balance to the held balance **before** any request is sent to the biller.
- **AC-12.2**
  - **Given** a payment in SentToBiller
  - **When** the biller refuses (for example AquaCo returns an error code, HC-3)
  - **Then** the payment becomes Failed, the full held amount including the fee is released to the available balance, the payment becomes Refunded, and no fee income or commission is recorded.
- **AC-12.3**
  - **Given** a payment in Unknown
  - **When** time passes or re-queries are inconclusive
  - **Then** the funds remain held and are not released automatically.
- **AC-12.4**
  - **Given** a payment in SentToBiller
  - **When** the biller confirms
  - **Then** the hold is converted into a final charge (held balance decreases by the total, nothing returns to the available balance).
- **AC-12.5**
  - **Given** a wallet with 500.00 EGP available and two payments with a total of 352.50 EGP each that arrive at the same moment (HC-4)
  - **When** both are processed
  - **Then** exactly one payment moves to FundsHeld (available becomes 147.50 EGP), the other is **Rejected** for insufficient funds, and the available balance never becomes negative.

#### US-13 Timeout, Unknown and re-query (FR-13, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Payment Processing |
| Brief requirement (verbatim) | A payment that times out becomes Unknown and is re-queried by the retry rules |
| Related items | BR-10, BR-11; HC-2; AMB-17, AMB-18 |

**Story:** As a customer, I want a payment that receives no biller answer in time to be shown as "being processed" and checked again automatically, so that I am neither wrongly charged nor wrongly refunded while the outcome is uncertain.

**Acceptance criteria**

- **AC-13.1**
  - **Given** a payment in SentToBiller and ElectroCo that takes 12 seconds to answer (HC-2)
  - **When** the 10-second timeout passes
  - **Then** the payment becomes **Unknown**, the customer is told the payment is being processed, and the money stays held.
- **AC-13.2**
  - **Given** a payment that became Unknown
  - **When** time passes
  - **Then** the system re-queries the biller up to 3 times, after 1, 5 and 15 minutes (BR-11; see AMB-17).
- **AC-13.3**
  - **Given** a re-query that returns "paid"
  - **When** the answer is processed
  - **Then** the payment becomes Completed and the hold becomes a final charge.
- **AC-13.4**
  - **Given** a re-query that returns "refused"
  - **When** the answer is processed
  - **Then** the payment becomes Failed, the hold is released and the payment becomes Refunded.
- **AC-13.5**
  - **Given** a payment for which all 3 re-queries were inconclusive
  - **When** the last re-query ends
  - **Then** the payment stays Unknown with its funds held, waits for reconciliation and is not refunded (see AMB-18).
- **AC-13.6**
  - **Given** a payment in Unknown
  - **When** the customer asks for its status
  - **Then** the response says it is being processed, not failed and not completed.
- **AC-13.7**
  - **Given** a payment that became Unknown after a timeout
  - **When** a late reply to the original biller request arrives
  - **Then** the reply is not trusted directly and the payment is resolved by the next re-query (see AMB-17).

#### US-14 TopUpNet callbacks and duplicates (FR-14, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Biller Integration |
| Brief requirement (verbatim) | The system accepts asynchronous callbacks from TopUpNet and ignores duplicates |
| Related items | BR-22; HC-6; AMB-16 |

**Story:** As the payment platform, I want to accept TopUpNet's asynchronous callbacks and ignore duplicates, so that a mobile top-up completes exactly once even if the callback is repeated.

**Acceptance criteria**

- **AC-14.1**
  - **Given** a TopUpNet payment that was answered "accepted" and is in SentToBiller
  - **When** the callback "success" arrives
  - **Then** the payment becomes Completed.
- **AC-14.2**
  - **Given** a TopUpNet payment in SentToBiller
  - **When** the callback "failed" arrives
  - **Then** the payment becomes Failed, the hold is released and the payment becomes Refunded.
- **AC-14.3**
  - **Given** a payment already Completed through a "success" callback
  - **When** the same "success" callback arrives a second time (HC-6)
  - **Then** the payment completes once, the second callback is ignored and logged, and no second ledger entry, fee, commission or notification is created.
- **AC-14.4**
  - **Given** a callback with a payment reference that PayLink does not know
  - **When** it arrives
  - **Then** no payment changes and the callback is logged.
- **AC-14.5**
  - **Given** a payment already in a final state
  - **When** a contradicting callback arrives (for example "failed" after Completed)
  - **Then** the state does not change and the callback is logged as a conflict.
- **AC-14.6**
  - **Given** a payment in Unknown
  - **When** a TopUpNet callback arrives
  - **Then** the payment is resolved according to the callback (Completed or Failed) as allowed by the life cycle.

### 6.4 Biller Integration

#### US-15 Support billers with different formats (FR-15, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Biller Integration |
| Brief requirement (verbatim) | The system supports at least 3 billers with different formats (ElectroCo, AquaCo, TopUpNet); NetCo batch is optional |
| Related items | BR-21; section 4.4; NFR-3, NFR-4; HC-3, HC-8; AMB-10, AMB-19, AMB-20 |

**Story:** As a customer, I want to pay electricity (ElectroCo), water (AquaCo) and mobile top-up (TopUpNet) bills through one consistent experience, so that I do not need to care how each biller works internally.

**Acceptance criteria**

- **AC-15.1**
  - **Given** ElectroCo (REST, JSON, answers in under 1 second)
  - **When** a customer inquires and pays an electricity bill
  - **Then** the inquiry returns the bill and the payment is confirmed immediately and becomes Completed.
- **AC-15.2**
  - **Given** AquaCo (SOAP-style XML, answers in 2 to 4 seconds)
  - **When** a customer inquires and pays a water bill
  - **Then** the payment is processed within the 10-second timeout and becomes Completed, and the customer sees the same fields and messages as for any other biller.
- **AC-15.3**
  - **Given** AquaCo
  - **When** it refuses a payment with an error code, for example because the payment is above the bill amount (HC-3)
  - **Then** the payment becomes Failed, the hold is released, and the customer receives an SMS with a clear reason (see US-22, AMB-10, AMB-19).
- **AC-15.4**
  - **Given** TopUpNet (REST, JSON, asynchronous)
  - **When** a customer tops up a mobile phone
  - **Then** the first answer "accepted" keeps the payment in SentToBiller and the later callback completes or fails it (see US-14, AMB-16).
- **AC-15.5**
  - **Given** a new biller that must be added (HC-8)
  - **When** its integration and configuration are added
  - **Then** payments to it work and **no existing module has to change**; only a new integration and configuration are added (NFR-4).
- **AC-15.6**
  - **Given** one biller that is down or very slow (for example ElectroCo)
  - **When** payments to the other billers are made
  - **Then** those payments are processed normally (NFR-3).
- **AC-15.7**
  - **Given** the optional NetCo batch biller
  - **When** it is not implemented
  - **Then** the system still meets FR-15 with the three required billers (see AMB-20).
- **AC-15.8**
  - **Given** a payment to any biller
  - **When** it is processed by the rest of the system
  - **Then** modules other than Biller Integration see only PayLink's own payment model and never the biller's field names, error codes or format.

### 6.5 Fees and Commissions

#### US-16 Fee calculation (FR-16, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Fees and Commissions |
| Brief requirement (verbatim) | Fees follow the rules in section 2.6, including the promotion period |
| Related items | BR-04 to BR-07, BR-13, BR-14; AMB-12, AMB-13, AMB-14 |

**Story:** As PayLink's business owner, I want service fees calculated by the defined rules including the promotion period, so that revenue is correct and customers see the right total.

**Acceptance criteria**

- **AC-16.1**
  - **Given** an electricity or water payment of any amount
  - **When** the fee is calculated
  - **Then** the fee is a fixed 2.50 EGP (350.00 EGP gives a total of 352.50 EGP).
- **AC-16.2a**
  - **Given** a mobile top-up of 100.00 EGP
  - **When** the fee is calculated
  - **Then** it is 1.5% of the amount, 1.50 EGP.
- **AC-16.2b**
  - **Given** a mobile top-up of 50.00 EGP
  - **When** the fee is calculated
  - **Then** 1.5% would be 0.75 EGP, so the minimum fee of 1.00 EGP applies and the total is 51.00 EGP.
- **AC-16.3**
  - **Given** a mobile top-up of 123.45 EGP
  - **When** the fee is calculated
  - **Then** the exact percentage 1.85175 EGP is rounded to 1.85 EGP using exact decimal arithmetic (see AMB-12).
- **AC-16.4a**
  - **Given** an internet payment of 150.00 EGP or 200.00 EGP
  - **When** the fee is calculated
  - **Then** the fee is 2.00 EGP.
- **AC-16.4b**
  - **Given** an internet payment of 200.01 EGP or 500.00 EGP
  - **When** the fee is calculated
  - **Then** the fee is 4.00 EGP (see AMB-13).
- **AC-16.5**
  - **Given** a promotion configured for biller B with a start date and an end date
  - **When** a payment to B is initiated within the promotion period
  - **Then** the fee is 0.00 EGP.
- **AC-16.6**
  - **Given** the same promotion
  - **When** a payment to B is initiated before the start or after the end, or a payment to a different biller is initiated during the period
  - **Then** the normal fee applies (see AMB-14).
- **AC-16.7**
  - **Given** any fee calculation
  - **When** it is performed
  - **Then** all values (fixed fee, percentage, minimum, tiers, threshold, promotion biller and dates) are read from configuration, not from code, and all calculations use exact decimal types (BR-13, BR-14).

#### US-17 Agent payments and commission (FR-17, Should)

| Attribute | Detail |
|---|---|
| Priority | Should |
| Module (proposed) | Payment Processing |
| Brief requirement (verbatim) | An agent can pay on behalf of a customer and earns commission |
| Related items | BR-08; AMB-06, AMB-12, AMB-14 |

**Story:** As an agent, I want to pay a bill on behalf of a customer and earn a commission, so that my shop is rewarded for serving customers.

**Acceptance criteria**

- **AC-17.1**
  - **Given** an agent and a customer's payment with a fee of 2.50 EGP
  - **When** the payment becomes Completed
  - **Then** 1.00 EGP (40% of the fee) is credited to the agent's wallet with a ledger entry (BR-08).
- **AC-17.2**
  - **Given** an agent payment with a fee of 1.50 EGP (a 100.00 EGP mobile top-up)
  - **When** it becomes Completed
  - **Then** the commission is 0.60 EGP.
- **AC-17.3**
  - **Given** an agent payment that is Rejected, Failed, Unknown or Refunded
  - **When** the payment is evaluated
  - **Then** no commission is credited; commission is credited only when the payment completes. If a payment becomes Completed later from Unknown, the commission is credited at that time.
- **AC-17.4**
  - **Given** an agent payment during a promotion where the fee is 0.00 EGP
  - **When** the payment completes
  - **Then** the commission is 0.00 EGP (see AMB-14).
- **AC-17.5**
  - **Given** a payment made by the customer directly
  - **When** it completes
  - **Then** no commission is created.
- **AC-17.6**
  - **Given** a user who is not an agent
  - **When** that user tries to pay on behalf of a customer
  - **Then** the request is refused.
- **AC-17.7**
  - **Given** an agent payment
  - **When** it is stored
  - **Then** it records the agent and the customer, the customer's wallet is charged and the customer's limits apply (see AMB-06).

### 6.6 Fraud Checks

#### US-18 Single-payment and daily limits (FR-18, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Fraud Checks |
| Brief requirement (verbatim) | Payments above the single-payment limit or the daily limit are rejected with a clear reason |
| Related items | BR-01 to BR-03; AMB-01, AMB-02, AMB-32 |

**Story:** As a customer, I want a payment above the single-payment limit or above my daily limit to be rejected with a clear reason, so that I understand what to change and PayLink's limits are enforced.

**Acceptance criteria**

- **AC-18.1a**
  - **Given** a payment of 4.99 EGP
  - **When** it is submitted
  - **Then** it is rejected because it is below the minimum of 5 EGP.
- **AC-18.1b**
  - **Given** a payment of 5.00 EGP
  - **When** it is submitted
  - **Then** the minimum check passes.
- **AC-18.2a**
  - **Given** a payment of 10,000.01 EGP
  - **When** it is submitted
  - **Then** it is rejected because it is above the maximum of 10,000 EGP.
- **AC-18.2b**
  - **Given** a payment of 10,000.00 EGP from a Verified customer
  - **When** it is submitted
  - **Then** the single-payment check passes.
- **AC-18.3**
  - **Given** a Basic customer who has already paid 4,800 EGP today
  - **When** the customer pays 300 EGP
  - **Then** the payment is **Rejected** with the reason "daily limit exceeded" because 5,100 EGP is above 5,000 EGP (see AMB-01, AMB-02).
- **AC-18.4**
  - **Given** a Basic customer who has already paid 4,700 EGP today
  - **When** the customer pays 300 EGP
  - **Then** the payment passes because the daily total is exactly 5,000 EGP ("at most 5,000 EGP per day").
- **AC-18.5**
  - **Given** a Verified customer who has already paid 29,800 EGP today
  - **When** the customer pays 300 EGP
  - **Then** the payment is Rejected with the reason "daily limit exceeded" (limit 30,000 EGP).
- **AC-18.6**
  - **Given** a payment rejected for a limit
  - **When** it is rejected
  - **Then** no hold is made, the biller is not called, the payment is recorded as Rejected with the reason, and the customer sees the reason and the limit that applies.
- **AC-18.7**
  - **Given** the limits in configuration
  - **When** an administrator changes them (FR-24)
  - **Then** the new values apply to payments initiated afterwards.
- **AC-18.8**
  - **Given** a payment that fails more than one rule
  - **When** it is evaluated
  - **Then** the reason of the first failing rule is returned, in the order single-payment minimum and maximum, daily limit, velocity, blocked account (see AMB-32).

#### US-19 Velocity rule (FR-19, Should)

| Attribute | Detail |
|---|---|
| Priority | Should |
| Module (proposed) | Fraud Checks |
| Brief requirement (verbatim) | More than 5 payments in 60 seconds blocks the account for 10 minutes |
| Related items | BR-09; AMB-08, AMB-09, AMB-32 |

**Story:** As PayLink's risk owner, I want an account that makes more than 5 payments in 60 seconds to be blocked for 10 minutes, so that rapid automated or fraudulent payment activity is stopped.

**Acceptance criteria**

- **AC-19.1**
  - **Given** an account that has made 5 payments within 60 seconds
  - **When** it makes a 6th payment within the same 60 seconds
  - **Then** the 6th payment is Rejected and the account is blocked for 10 minutes (see AMB-08).
- **AC-19.2**
  - **Given** an account that has made exactly 5 payments within 60 seconds
  - **When** the 5th payment is evaluated
  - **Then** it is **not** blocked, because only "more than 5" triggers the rule.
- **AC-19.3**
  - **Given** an account that makes 5 payments spread over more than 60 seconds
  - **When** each is evaluated
  - **Then** the velocity rule does not trigger.
- **AC-19.4**
  - **Given** an account blocked by the velocity rule
  - **When** it tries to pay within the 10 minutes
  - **Then** the payment is Rejected with the reason "account temporarily blocked" and the time remaining.
- **AC-19.5**
  - **Given** a velocity block that is 10 minutes old
  - **When** the account tries to pay
  - **Then** the payment is processed normally without manual action.
- **AC-19.6**
  - **Given** a velocity block
  - **When** it is applied
  - **Then** an audit record is written with the account, time and reason (FR-25).
- **AC-19.7**
  - **Given** a repeated request with an already used idempotency key (FR-10)
  - **When** it is received
  - **Then** it is not counted as a new payment for the velocity rule (see AMB-08).

### 6.7 Support and administration

#### US-20 Search a payment and see its history (FR-20, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Payment Processing |
| Brief requirement (verbatim) | A support employee can search a payment by reference and see its full history |
| Related items | AMB-28 |

**Story:** As a support employee, I want to search a payment by its reference and see its full history, so that I can answer customer complaints and investigate problems.

**Acceptance criteria**

- **AC-20.1**
  - **Given** an existing payment reference
  - **When** a support employee searches for it
  - **Then** the response shows the payment's customer, biller, amount, fee, total, current state and the biller reference.
- **AC-20.2**
  - **Given** the same payment
  - **When** the support employee opens it
  - **Then** the full history is shown in time order: every state transition with cause and time, related ledger entries, biller requests and answers, re-query attempts and notification attempts.
- **AC-20.3**
  - **Given** a payment that became Unknown
  - **When** the support employee views its history
  - **Then** the history shows the timeout, each re-query with its result, and whether it is waiting for reconciliation.
- **AC-20.4**
  - **Given** a reference that does not exist
  - **When** it is searched
  - **Then** a clear "not found" message is returned.
- **AC-20.5**
  - **Given** a user who is not a support employee
  - **When** that user tries to search by reference
  - **Then** the request is refused.

#### US-21 Block and unblock an account (FR-21, Should)

| Attribute | Detail |
|---|---|
| Priority | Should |
| Module (proposed) | Accounts and Wallet |
| Brief requirement (verbatim) | A support employee can block and unblock an account |
| Related items | BR-20; AMB-09, AMB-32 |

**Story:** As a support employee, I want to block and unblock an account, so that I can stop suspicious activity and quickly restore legitimate customers.

**Acceptance criteria**

- **AC-21.1**
  - **Given** an active account
  - **When** a support employee blocks it with a reason
  - **Then** the account is marked blocked and the reason is recorded.
- **AC-21.2**
  - **Given** a blocked account
  - **When** its owner tries to pay
  - **Then** the payment is **Rejected** by the blocked-account fraud check with a clear reason, nothing is held and the biller is not called (see AMB-09).
- **AC-21.3**
  - **Given** a blocked account
  - **When** a support employee unblocks it
  - **Then** the account can pay again immediately.
- **AC-21.4**
  - **Given** a payment already past the fraud-check stage when the account is blocked
  - **When** the block is applied
  - **Then** that payment continues through its normal life cycle (see AMB-09).
- **AC-21.5**
  - **Given** a user who is not a support employee
  - **When** that user tries to block or unblock an account
  - **Then** the request is refused.
- **AC-21.6**
  - **Given** any block or unblock
  - **When** it happens
  - **Then** an audit record with who, when and why is written (FR-25).
- **AC-21.7**
  - **Given** a block or unblock request without a reason
  - **When** it is submitted
  - **Then** the request is rejected and the account state is unchanged.

### 6.8 Notifications

#### US-22 Completion and failure notifications (FR-22, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Notifications |
| Brief requirement (verbatim) | A customer receives an SMS and a push message on completion and on failure |
| Related items | HC-2, HC-3, HC-5; AMB-11, AMB-23 |

**Story:** As a customer, I want to receive an SMS and a push message when my payment completes or fails, so that I have a receipt or know the reason for a failure immediately.

**Acceptance criteria**

- **AC-22.1**
  - **Given** a payment that becomes Completed
  - **When** it completes
  - **Then** the customer receives an SMS and a push message that state the biller, amount, fee and payment reference.
- **AC-22.2**
  - **Given** a payment that becomes Failed (for example AquaCo refuses with an error code, HC-3)
  - **When** it fails
  - **Then** the customer receives an SMS and a push message with a clear reason (see AMB-19).
- **AC-22.3**
  - **Given** a payment that is Unknown
  - **When** the customer submits it
  - **Then** the response tells the customer the payment is being processed, and the customer is notified when it later becomes Completed or Failed.
- **AC-22.4**
  - **Given** a payment that is Rejected before any money moves
  - **When** it is rejected
  - **Then** the customer sees the reason in the immediate response (see AMB-11).
- **AC-22.5**
  - **Given** a duplicate callback or duplicate request (HC-1, HC-6)
  - **When** it is ignored
  - **Then** no second notification is sent.
- **AC-22.6**
  - **Given** the SMS provider is down when a payment completes (HC-5)
  - **When** the notification fails
  - **Then** the payment still stays Completed (see US-23).
- **AC-22.7**
  - **Given** a notification
  - **When** it is created
  - **Then** it is built from a message template for the event and channel (see AMB-23).

#### US-23 Notification retries (FR-23, Should)

| Attribute | Detail |
|---|---|
| Priority | Should |
| Module (proposed) | Notifications |
| Brief requirement (verbatim) | Failed notifications are retried 3 times then marked undelivered |
| Related items | BR-12; HC-5; AMB-21, AMB-22 |

**Story:** As a customer, I want failed notifications to be retried 3 times before they are marked undelivered, so that a temporary provider outage does not make me miss important messages.

**Acceptance criteria**

- **AC-23.1**
  - **Given** an SMS (or email) that fails to send
  - **When** the failure occurs
  - **Then** the system retries it up to 3 times (see AMB-21).
- **AC-23.2**
  - **Given** the SMS provider is down for all attempts (HC-5)
  - **When** the 3 retries have failed
  - **Then** the notification is marked **undelivered**, and the payment's state and balance are not affected.
- **AC-23.3**
  - **Given** a notification that succeeds on a retry
  - **When** it succeeds
  - **Then** it is marked delivered and no further retries are made.
- **AC-23.4**
  - **Given** each attempt
  - **When** it is made
  - **Then** the attempt number, time and outcome are recorded.
- **AC-23.5**
  - **Given** a push message that fails
  - **When** it fails
  - **Then** the same retry rule is applied (see AMB-22).

### 6.9 Configuration, audit and reporting

#### US-24 Edit fees and limits without redeploying (FR-24, Should)

| Attribute | Detail |
|---|---|
| Priority | Should |
| Module (proposed) | Fees and Commissions (configuration) |
| Brief requirement (verbatim) | An admin can edit fee and limit values without redeploying |
| Related items | BR-14; AMB-15, AMB-33 |

**Story:** As an administrator, I want to edit fee and limit values without redeploying the system, so that business rules can change quickly and safely.

**Acceptance criteria**

- **AC-24.1**
  - **Given** an administrator
  - **When** the administrator changes the electricity and water fee from 2.50 EGP to 3.00 EGP
  - **Then** the next payment uses 3.00 EGP with no restart or redeploy.
- **AC-24.2**
  - **Given** a payment that was already initiated
  - **When** the fee changes afterwards
  - **Then** that payment keeps the fee it was calculated with (see AMB-15).
- **AC-24.3**
  - **Given** an editable limit (daily limit for Basic or Verified, minimum or maximum single payment)
  - **When** an administrator changes it
  - **Then** the new value is used for payments initiated afterwards.
- **AC-24.4**
  - **Given** an invalid value (negative, non-numeric, minimum greater than maximum)
  - **When** it is submitted
  - **Then** it is rejected and the old value remains.
- **AC-24.5**
  - **Given** any change
  - **When** it is saved
  - **Then** an audit record with who, when, old value, new value and reason is written (FR-25).
- **AC-24.6**
  - **Given** a user who is not an administrator
  - **When** that user tries to edit a value
  - **Then** the request is refused.

#### US-25 Audit log (FR-25, Must)

| Attribute | Detail |
|---|---|
| Priority | Must |
| Module (proposed) | Cross-cutting (audit) |
| Brief requirement (verbatim) | Every money movement and admin action is written to an audit log |
| Related items | NFR-5; AMB-28 |

**Story:** As an auditor, I want every money movement and every admin action written to an audit log, so that PayLink can prove who did what, when and why.

**Acceptance criteria**

- **AC-25.1**
  - **Given** any money movement (top-up, hold, release, final charge, fee income, commission, refund)
  - **When** it occurs
  - **Then** an audit record exists with who (customer, agent, staff member or system), when (timestamp) and why (reason or payment reference) (see AMB-28).
- **AC-25.2**
  - **Given** any admin action (biller changes, verification upgrade, fee or limit changes, block or unblock, refund approval)
  - **When** it occurs
  - **Then** an audit record exists with who, when, why and the values before and after.
- **AC-25.3**
  - **Given** all ledger entries created during a test run
  - **When** each one is checked
  - **Then** 100% have a matching audit record (NFR-5).
- **AC-25.4**
  - **Given** an audit record
  - **When** any user or component tries to change or delete it
  - **Then** the attempt is refused (see AMB-28).
- **AC-25.5**
  - **Given** an automated action by the system (for example a timeout, a re-query or a reconciliation result)
  - **When** it moves money
  - **Then** the audit record names the system as the actor and the reason.

#### US-26 Daily reconciliation report (FR-26, Should)

| Attribute | Detail |
|---|---|
| Priority | Should |
| Module (proposed) | Reconciliation |
| Brief requirement (verbatim) | The system produces a daily reconciliation report from a settlement file |
| Related items | HC-7; AMB-01, AMB-18, AMB-24, AMB-25 |

**Story:** As a support employee, I want a daily reconciliation report built from each biller's settlement file, so that differences between PayLink's records and the biller's records are found and reviewed.

**Acceptance criteria**

- **AC-26.1**
  - **Given** a settlement file from a biller for a day
  - **When** the reconciliation runs
  - **Then** PayLink's payments for that biller and day are compared with the file and a report is produced (see AMB-24).
- **AC-26.2**
  - **Given** a payment that is Completed in PayLink and listed in the file with the same amount
  - **When** they are compared
  - **Then** the payment is reported as matched.
- **AC-26.3**
  - **Given** the settlement file lists a payment that PayLink has as **Failed** (HC-7)
  - **When** they are compared
  - **Then** the difference is flagged in the report for support to review, and the payment state is not changed automatically (see AMB-25).
- **AC-26.4**
  - **Given** a payment in Unknown that the settlement file lists as paid
  - **When** they are compared
  - **Then** the payment becomes Completed (life-cycle rule "settlement file").
- **AC-26.5**
  - **Given** a payment in Unknown that the settlement file does not list
  - **When** reconciliation finds no payment
  - **Then** the payment becomes Failed, the hold is released and the payment becomes Refunded (life-cycle rule "reconciliation finds no payment"; see AMB-25).
- **AC-26.6**
  - **Given** a payment that is Completed in PayLink but missing from the file, a file entry with no matching PayLink payment, or a matching entry with a different amount
  - **When** they are compared
  - **Then** each is flagged in the report for support to review.
- **AC-26.7**
  - **Given** a report
  - **When** it is produced
  - **Then** it lists totals (matched, flagged, resolved) and each difference with payment reference, PayLink state, file entry and type of difference.

#### US-27 Refund of a completed payment (FR-27, Could)

| Attribute | Detail |
|---|---|
| Priority | Could |
| Module (proposed) | Accounts and Wallet |
| Brief requirement (verbatim) | A customer can request a refund of a completed payment; support approves it |
| Related items | AMB-26 |

**Story:** As a customer, I want to request a refund of a completed payment that support approves, so that I can recover my money when a completed payment was a mistake or is disputed.

**Acceptance criteria**

- **AC-27.1**
  - **Given** a Completed payment
  - **When** the customer requests a refund with a reason
  - **Then** a refund request is created with status Pending and no money moves yet.
- **AC-27.2**
  - **Given** a Pending refund request
  - **When** a support employee approves it
  - **Then** the refund amount is credited to the customer's wallet through ledger entries, an audit record is written and the customer is notified (see AMB-26).
- **AC-27.3**
  - **Given** a Pending refund request
  - **When** a support employee rejects it
  - **Then** no money moves and the customer is told the reason.
- **AC-27.4**
  - **Given** a payment that is not Completed (for example Failed, Unknown or already Refunded)
  - **When** the customer requests a refund
  - **Then** the request is rejected; a Failed payment is refunded automatically, and an Unknown payment is never refunded automatically.
- **AC-27.5**
  - **Given** a payment with a refund already requested or approved
  - **When** another refund request is made
  - **Then** it is rejected, so a payment cannot be refunded twice.
- **AC-27.6**
  - **Given** an approved refund
  - **When** it completes
  - **Then** the payment's own life-cycle state remains Completed, and the refund is a separate record (the life cycle of section 2.5 has no transition out of Completed; see AMB-26).
- **AC-27.7**
  - **Given** a user who is not a support employee
  - **When** that user tries to approve a refund
  - **Then** the request is refused.

#### US-28 Daily totals (FR-28, Could)

| Attribute | Detail |
|---|---|
| Priority | Could |
| Module (proposed) | Cross-cutting (reporting) |
| Brief requirement (verbatim) | An admin can view daily totals: payments, fees earned, commissions paid, failures per biller |
| Related items | AMB-01, AMB-27 |

**Story:** As an administrator, I want to view daily totals of payments, fees earned, commissions paid and failures per biller, so that I can monitor the health and revenue of the business.

**Acceptance criteria**

- **AC-28.1**
  - **Given** a day with payments
  - **When** an administrator requests the totals for that day
  - **Then** the response shows the number and total amount of payments, the total fees earned, the total commissions paid and the number of failures per biller (see AMB-27).
- **AC-28.2**
  - **Given** a day with no payments
  - **When** an administrator requests the totals
  - **Then** all totals are 0 and no error occurs.
- **AC-28.3**
  - **Given** any day
  - **When** the totals are calculated
  - **Then** fees earned and commissions paid equal the sums of the corresponding ledger entries for that day.
- **AC-28.4**
  - **Given** a user who is not an administrator
  - **When** that user requests the totals
  - **Then** the request is refused.

## 7. Non-Functional Requirements

The brief (section 3.3) names six expected quality targets. The targets below are copied **exactly** from the brief; the descriptions and verification methods say how each target is shown by a test or report. The brief requires that "your tests or reports must show the number".

| ID | Quality | Target (exactly as in the brief) | Priority |
|---|---|---|---|
| NFR-1 | Correctness | Zero double charges and zero negative balances in the concurrency and duplicate tests | Must |
| NFR-2 | Performance | 100 payments per second sustained for 1 minute against the mock billers | Must |
| NFR-3 | Resilience | A biller outage does not stop payments to other billers | Must |
| NFR-4 | Modifiability | Adding a biller touches only the Biller Integration module and configuration | Must |
| NFR-5 | Auditability | 100% of money movements have an audit record with who, when and why | Must |
| NFR-6 | Testability | At least 70% line coverage on domain logic, measured in CI | Must |

The brief does not assign priorities to non-functional requirements. They are marked Must here because section 3.3 lists them as expected and the final grading criteria depend on them; this is recorded as an assumption in [section 9](#9-ambiguities-assumptions-and-decisions) (AMB-34). AMB-30 and AMB-31 give the details of how two of the targets are measured.

### 7.1 NFR-1 Correctness

| | |
|---|---|
| **Description** | Money is never created, lost or duplicated. A customer is never charged twice for one payment (duplicate requests, repeated callbacks) and a wallet never becomes negative (simultaneous payments). |
| **Measurable target** | **Zero double charges and zero negative balances** in the concurrency and duplicate tests. |
| **Related requirements** | FR-5, FR-9, FR-10, FR-12, FR-14; HC-1, HC-4, HC-6; BR-13 |
| **How it is verified** | Automated tests in CI: (1) same idempotency key sent repeatedly and in parallel (HC-1, AC-10.1 to AC-10.3) with exactly one payment and one charge; (2) simultaneous payments against a balance that covers only one (HC-4, AC-12.5); (3) duplicate TopUpNet callbacks (HC-6, AC-14.3). After each test the suite asserts that the ledger total per payment equals one charge and that no wallet has a negative available balance, using a database query over all wallets. The tests are repeated many times to expose race conditions. An invariant check runs after each test: every balance equals the sum of its ledger entries, no wallet is negative and no payment has more than one final charge. Result: count of double charges = 0 and count of negative balances = 0. |

### 7.2 NFR-2 Performance

| | |
|---|---|
| **Description** | The system handles the busiest day of the month. |
| **Measurable target** | **100 payments per second sustained for 1 minute** against the mock billers (at least 6,000 payments). |
| **Related requirements** | FR-9, FR-15; TC-7 |
| **How it is verified** | A load test (for example k6, Locust or JMeter, chosen with the technology stack) runs against the full docker compose stack with the mock billers, sending 100 payments per second for 60 seconds from many different customer accounts so that the velocity rule is not triggered by one account (AMB-30). The report states the measured throughput, error rate and response-time percentiles, and confirms that NFR-1 still holds after the run (no double charge, no negative balance). |

### 7.3 NFR-3 Resilience

| | |
|---|---|
| **Description** | A failure or slowness of one biller stays isolated to that biller. |
| **Measurable target** | **A biller outage does not stop payments to other billers.** |
| **Related requirements** | FR-13, FR-15; BR-10; HC-2 |
| **How it is verified** | An automated test switches a mock biller (for example ElectroCo) to fail on purpose (refuse, or answer after more than 10 seconds) while payments to AquaCo and TopUpNet are submitted. The test passes only if every payment to the healthy billers completes normally while the failing biller's payments become Failed or Unknown as defined by the life cycle. The results are compared with a baseline run without failures (no increase in failures for the healthy billers), and the test is repeated under load to show that the failing biller does not exhaust shared resources. The timeout of 10 seconds bounds how long a failing biller can hold resources. |

### 7.4 NFR-4 Modifiability

| | |
|---|---|
| **Description** | New billers and biller API changes (including the Phase 2 ElectroCo v2 change) are absorbed by the Biller Integration module and configuration only. |
| **Measurable target** | **Adding a biller touches only the Biller Integration module and configuration.** |
| **Related requirements** | FR-6, FR-15; HC-8; TC-1, TC-6 |
| **How it is verified** | The team adds a new mock biller as a demonstration change and the pull request diff is checked: all changed files are inside the Biller Integration module, configuration or tests of that module. An automated CI check (for example a script that lists changed paths) and a dependency check on the module boundaries show that no other module changed or depends on a biller-specific class. The change set is inspected with `git diff --stat`. The same check is used for the ElectroCo v2 change in Phase 2. |

### 7.5 NFR-5 Auditability

| | |
|---|---|
| **Description** | Every money movement can be traced to a person or system, a time and a reason. |
| **Measurable target** | **100% of money movements have an audit record with who, when and why.** |
| **Related requirements** | FR-5, FR-25; BR-16 |
| **How it is verified** | After the integration tests and the load test, a query compares the ledger entries with the audit records: every ledger entry must have a linked audit record with a non-empty actor (who), timestamp (when) and reason (why). A query for ledger entries without a matching complete audit record must return 0 rows, and the reported ratio of audited to total money movements must be exactly 100%. Admin actions are checked with the same query for their audit records. |

### 7.6 NFR-6 Testability

| | |
|---|---|
| **Description** | The domain logic is covered by automated tests so that changes can be made safely. |
| **Measurable target** | **At least 70% line coverage on domain logic, measured in CI.** |
| **Related requirements** | All domain rules; TC-8 |
| **How it is verified** | The GitHub Actions workflow runs the test suite with a coverage tool for the chosen language and publishes the coverage report on every push; the domain-logic packages are defined in AMB-31. The build is configured to fail if the measured line coverage of domain logic is below 70%. In Phase 1 the pipeline runs at least one test; the 70% target applies to the final system in Phase 2. |

## 8. Hard-Case Coverage

Each hard case of section 2.7 of the brief becomes at least one automated test. The table shows which requirements and acceptance criteria describe the required outcome, so the test can be written directly from them.

| HC | Situation (brief) | Required outcome (brief) | Requirements | Acceptance criteria |
|---|---|---|---|---|
| HC-1 | The mobile app sends the same payment twice because the network was slow | One payment, one charge; the second request returns the first result | FR-9, FR-10 | AC-10.1, AC-10.2, AC-10.3 |
| HC-2 | ElectroCo takes 12 seconds to answer | Payment becomes Unknown, customer is told it is being processed, money stays held | FR-11, FR-13 | AC-13.1, AC-13.6, AC-12.3 |
| HC-3 | AquaCo refuses with an error code | Payment becomes Failed, hold is released, customer gets an SMS with a clear reason | FR-12, FR-15, FR-22 | AC-12.2, AC-15.3, AC-22.2 |
| HC-4 | Two payments from the same wallet arrive at the same moment and the balance covers only one | One succeeds, the other is Rejected; the balance never goes negative | FR-5, FR-9, FR-12 | AC-12.5, AC-5.5 |
| HC-5 | The SMS provider is down when a payment completes | The payment still completes; the SMS is retried and finally marked undelivered | FR-22, FR-23 | AC-22.6, AC-23.2 |
| HC-6 | TopUpNet calls the callback twice with "success" | Payment completes once; the second callback is ignored and logged | FR-14 | AC-14.3 |
| HC-7 | The nightly settlement file lists a payment PayLink has as Failed | The difference is flagged in the reconciliation report for support to review | FR-26 | AC-26.3 |
| HC-8 | A new biller must be added | Only a new integration and configuration are added; existing modules are unchanged | FR-6, FR-15 | AC-15.5, NFR-4 |

## 9. Ambiguities, Assumptions and Decisions

### 9.1 How to read this section

- The brief is the source of truth. This section lists points where it is **silent, unclear or open to more than one reading**. They were identified while analysing the brief with an AI assistant and checked against the brief's text.
- The column **Proposed decision** is a **working assumption**, not an official requirement. Until the instructor or team confirms a decision, its status stays **Open (proposed)**. If a decision changes, the affected acceptance criteria, the diagrams and the tests must be updated together.
- The decisions were chosen to be the simplest reading consistent with the brief and none of them changes a number, priority or life-cycle transition of the brief. Items of type **Derived** follow directly from the brief's wording but are not stated in it.

### 9.2 Points confirmed by the brief (no assumption needed)

| Point | Where it is stated in the brief |
|---|---|
| The hold covers the bill amount plus the service fee (352.50 EGP in the example), and it is made before the biller is called. | Section 2.2, steps 3 and 5 |
| Fee income and agent commission are recorded only when the payment is confirmed. | Section 2.2, step 7; section 2.6 |
| A Failed payment's hold is released to the wallet (payment becomes Refunded). | Section 2.5 |
| A payment in Unknown is never refunded automatically. | Section 2.5 |
| Completed, Rejected and Refunded are final. | Section 2.5 |
| A payment from the same wallet that cannot be covered is Rejected, and the balance never goes negative. | Section 2.7, case 4 |
| NetCo is optional; ElectroCo, AquaCo and TopUpNet are required. | FR-15; section 2.4 |
| Business-rule values are fixed and kept in configuration. | Section 2.6 |

### 9.3 Ambiguity register

Type: **Gap** = not specified in the brief; **Interpretation** = the text can be read in more than one way; **Derived** = follows from the brief but is not stated.

#### A. Accounts, wallet and limits

| ID | Ambiguity | Why it matters | Proposed decision | Type / status |
|---|---|---|---|---|
| AMB-01 | "Per day" for the daily limit is not defined (calendar day or rolling 24 hours, and which time zone). | Decides when a customer's daily total resets and how limit tests are written. | The day is the calendar day in Egypt local time (Africa/Cairo) and resets at midnight. All timestamps are stored in UTC and converted for this calculation. | Gap / Open (proposed) |
| AMB-02 | It is not stated whether the daily and the single-payment limits apply to the bill amount or to the total including the fee, nor which payments count towards the daily total. | Changes which payments are rejected near the limit. | Limits apply to the **bill amount** (excluding the PayLink fee). The daily total counts payments in FundsHeld, SentToBiller, Unknown and Completed; Rejected, Failed and Refunded payments do not count. The minimum (5 EGP) and maximum (10,000 EGP) are inclusive. | Interpretation / Open (proposed) |
| AMB-03 | How users register and log in, and which roles exist, is not specified (the brief mentions "login" in step 1 and roles such as admin, support and agent). | Needed for every authorisation rule and for the registration and login endpoints. | Registration with a unique mobile number and a password; token-based authentication; four roles: customer, agent, administrator, support employee. Real identity verification is simulated. | Gap / Open (proposed) |
| AMB-04 | Who upgrades a customer to Verified and how is not stated (FR-2). | Decides the actor of FR-2 and the audit record. | An administrator upgrades the customer (verification is simulated). Only upgrade from Basic to Verified is required. | Gap / Open (proposed) |
| AMB-05 | When a wallet is created, whether top-ups have limits or a maximum balance, and whether an agent's own wallet is involved in a cash top-up are not stated. | Affects FR-1, FR-4 and the ledger. | One EGP wallet is created automatically at registration (also for agents, whose wallet receives commission). No top-up limit and no maximum balance beyond a positive amount with at most two decimals. A cash top-up through an agent credits the customer's wallet and records the agent; it does not debit the agent's wallet. | Gap / Open (proposed) |
| AMB-06 | When an agent pays on behalf of a customer (FR-17), it is not stated whose wallet is charged, whose limits apply, and how the customer agrees. | Decides the ledger entries, limit checks and commission. | The **customer's** wallet is charged and the **customer's** limits apply; the agent is recorded on the payment; the commission goes to the agent's wallet. Customer consent (for example a one-time code) is out of scope and treated as simulated. | Gap / Open (proposed) |

#### B. Payment processing and fraud checks

| ID | Ambiguity | Why it matters | Proposed decision | Type / status |
|---|---|---|---|---|
| AMB-07 | The scope and lifetime of the idempotency key, and the behaviour for the same key with different data, are not stated. | Needed to implement FR-10 safely and to test HC-1. | The key is required on payment requests and is unique per account. A repeat with the same data returns the stored result (the current status if still processing). The same key with different data is rejected with a conflict error. Keys are kept for at least 24 hours (configurable). | Gap / Open (proposed) |
| AMB-08 | The velocity rule does not say what counts as a "payment" (attempts or only accepted payments), whether the window is sliding, or whether the triggering payment is itself blocked. | Decides when the block starts and how the rule is tested. | A sliding 60-second window counts payment attempts with a new idempotency key (repeats of the same key do not count). The 6th attempt in the window is Rejected and the account is blocked for 10 minutes; the block lifts automatically afterwards. | Interpretation / Open (proposed) |
| AMB-09 | The brief does not say what "blocked" prevents, how a manual block (FR-21) and an automatic velocity block interact, or what happens to payments already in progress. | Decides the blocked-account fraud check and the support functions. | A block rejects **new payment attempts**; the customer can still log in and view the wallet. Payments already past the fraud-check stage continue through their life cycle. A manual block lasts until a support employee unblocks it; unblocking also lifts a velocity block. The order of the checks is in AMB-32. | Gap / Open (proposed) |
| AMB-10 | It is not stated whether a customer may pay a different amount than the inquired bill (partial payment) or choose the amount (mobile top-up). | AquaCo rejects payments above the bill amount (HC-3); the payment API must define the amount. | The payment request carries an amount. The effective range is the intersection of the biller's range and the global range of 5 to 10,000 EGP. For mobile top-up the customer chooses the amount within that range. For electricity and water the amount is the inquired bill; whether a lower amount is accepted is decided by the biller, and amounts above the bill are refused by AquaCo as in HC-3. | Gap / Open (proposed) |
| AMB-11 | FR-22 asks for SMS and push messages on "completion and failure", but Rejected is a different state from Failed. | Decides which events create notifications. | Rejected payments are answered immediately with the reason and create no SMS or push message. Completed and Failed create SMS and push. Unknown is announced in the immediate response and the customer is notified when it later becomes Completed or Failed. | Interpretation / Open (proposed) |
| AMB-32 | The brief lists the fraud checks (daily limit, number of payments in the last minute, blocked account) but not their order, nor which reason is shown when several rules fail. | The order decides the customer-facing reason and the test results. | After validating the single-payment minimum and maximum, evaluate the daily limit, the velocity rule and the blocked-account check in the order of brief section 2.2 and return the reason of the first failing rule. | Gap / Open (proposed) |

#### C. Fees and commission

| ID | Ambiguity | Why it matters | Proposed decision | Type / status |
|---|---|---|---|---|
| AMB-12 | BR-13 requires exact decimals but gives no rounding mode or scale for the 1.5% fee and the 40% commission. | Cents of difference between implementations and tests. | Scale of 2 decimals (piasters), rounding half up. The 1.5% fee is calculated, rounded, and then the 1.00 EGP minimum applies. The commission is 40% of the fee actually charged, rounded half up. | Gap / Open (proposed) |
| AMB-13 | The internet tier says "2 EGP up to 200 EGP, 4 EGP above": the boundary value 200.00 is not explicit. | Affects the fee for exactly 200.00 EGP. | Amount up to and including 200.00 EGP: 2.00 EGP; above 200.00 EGP: 4.00 EGP. | Interpretation / Open (proposed) |
| AMB-14 | The promotion rule leaves open: whether more than one promotion can run at once, whether the end date is inclusive, which time zone applies, and whether commission is still paid when the fee is 0. | Affects fees, revenue and agent income. | One promotion biller at a time; configured dates are inclusive of the whole start and end day (Africa/Cairo). The fee is evaluated when the payment is initiated. The commission is 40% of the fee actually charged, so it is 0.00 EGP during a promotion. | Gap / Open (proposed) |
| AMB-15 | FR-24 does not say which values are editable or whether a change affects payments already in progress. | Needed for the configuration design. | Fees, tiers, minimum fee, daily and single-payment limits and promotion settings are editable; changes apply only to payments initiated afterwards. Velocity and timeout settings are also kept in configuration. | Gap / Open (proposed) |
| AMB-33 | It is not stated how long the fee and total shown by an inquiry stay valid when the bill or the configuration changes before the customer pays. | A customer could see one total and be charged another. | The fee is fixed when the payment is initiated and the payment keeps it. The payment uses the amount of the inquiry, and a bill that changed in the meantime is detected through the biller's answer. | Gap / Open (proposed) |

#### D. Billers and the payment life cycle

| ID | Ambiguity | Why it matters | Proposed decision | Type / status |
|---|---|---|---|---|
| AMB-16 | TopUpNet answers "accepted" and calls back "minutes later". The 10-second timeout covers live billers, but the brief does not say how long to wait for the callback or what happens if it never arrives. | A payment could stay in SentToBiller forever. | The 10-second timeout applies to the first answer. After "accepted" the payment stays in SentToBiller until the callback arrives. If no callback arrives within a configurable waiting period (proposed default 30 minutes), the payment becomes Unknown, which is already an allowed transition. | Gap / Open (proposed) |
| AMB-17 | The re-query schedule "after 1, 5 and 15 minutes" does not say from which moment the times are counted, and whether each biller offers a payment-status query. | Decides the retry command schedule and the biller contracts. | Times are counted from the moment the payment became Unknown (1, 5 and 15 minutes after it). A late reply to the original request after the timeout is not trusted directly; the next re-query resolves the payment. The mock billers offer a payment-status query for ElectroCo, AquaCo and TopUpNet. To be confirmed with the instructor's biller contract. | Interpretation / Open (proposed) |
| AMB-18 | After 3 inconclusive re-queries the payment "waits for reconciliation", but the brief does not say what happens if reconciliation does not resolve it. | Funds could stay held indefinitely. | The payment stays Unknown with funds held, is never refunded automatically, and appears in the reconciliation report as unresolved, with its age, for support to review. No manual state change is added to the life cycle. | Gap / Open (proposed) |
| AMB-19 | It is not stated whether the 10-second timeout also applies to inquiries, and how biller-specific error codes become customer messages (AquaCo has its own codes). | Needed for FR-8, HC-3 and for clear reasons in SMS. | The same 10-second timeout applies to inquiries; a failed inquiry creates no payment and moves no money. Each adapter maps biller error codes to a common list of PayLink failure reasons stored on the payment. | Gap / Open (proposed) |
| AMB-20 | NetCo (daily batch CSV, no live inquiry) is optional, and the 10-second timeout applies to live billers only. Disabling a biller while payments are in progress is also not covered. | Defines the scope and avoids contradicting the life cycle. | NetCo is out of the mandatory scope. If implemented later, the customer enters the amount, the fee follows BR-06, and its payments stay in SentToBiller until the result file arrives (the 10-second timeout does not apply). Disabling a biller blocks new payments only; callbacks, re-queries and reconciliation for in-progress payments continue. | Gap / Open (proposed) |

#### E. Notifications

| ID | Ambiguity | Why it matters | Proposed decision | Type / status |
|---|---|---|---|---|
| AMB-21 | "Retried 3 times" can mean 3 attempts in total or the first attempt plus 3 retries; the delay between retries is not given. | Decides the number of provider calls in the HC-5 test. | The first attempt plus 3 retries (4 attempts), then the message is marked undelivered. The delay is configurable (proposed default 1 minute; tests use a very small delay). | Interpretation / Open (proposed) |
| AMB-22 | The retry rule mentions SMS and email, while FR-22 asks for SMS and push; push retry and the use of email are not described. | Decides channel behaviour. | The same retry rule applies to every channel (SMS, email, push). SMS and push are sent for the events of FR-22; email is supported by the notification module but not required for any event. | Gap / Open (proposed) |
| AMB-23 | The language and content of message templates are not specified. | Needed for "clear reason" messages. | Templates are stored per event and channel in English, with a language field so that Arabic can be added without code change. | Gap / Open (proposed) |

#### F. Reconciliation and refunds

| ID | Ambiguity | Why it matters | Proposed decision | Type / status |
|---|---|---|---|---|
| AMB-24 | The settlement file format, delivery, time cut-off and matching key are not specified. | Needed for the reconciliation design and the mock billers. | A daily CSV per biller with payment reference, biller reference, amount, timestamp and status, matched by the unique payment reference sent to the biller (BR-19). The day's cut-off is midnight Africa/Cairo. Files are produced by the mock billers. | Gap / Open (proposed) |
| AMB-25 | The life cycle defines two reconciliation outcomes for Unknown payments, and HC-7 defines the case "Failed in PayLink, paid in the file". Other differences are not defined. | Decides which differences change state and which are only reported. | Unknown and listed as paid: Completed. Unknown and not listed after the cut-off: Failed (then Refunded). Failed but listed as paid (HC-7), Completed but not listed, unknown file entries and amount mismatches: **flagged only**, with no automatic state change, because the life cycle has no transition for them. | Interpretation / Open (proposed) |
| AMB-26 | FR-27 (Could) lets a customer ask for a refund of a Completed payment, but Completed is final and the life cycle has no transition from Completed to Refunded. Fee, commission and biller treatment are also not stated. | Direct conflict risk between FR-27 and FR-11. | The refund is a separate refund request record; the payment stays Completed. On approval the customer's wallet is credited with the amount and fee through compensating ledger entries, and the PayLink fee income and any agent commission are reversed. Recovering the money from the biller is out of scope and is visible in reconciliation. To be confirmed with the instructor before implementation. | Interpretation / Open (proposed) |
| AMB-27 | FR-28 asks for "failures per biller" without defining "failure". | Decides how the report is calculated. | A failure is a payment that reached Failed (including those later Refunded) on that day. Rejected payments are counted separately. | Interpretation / Open (proposed) |

#### G. Quality and data

| ID | Ambiguity | Why it matters | Proposed decision | Type / status |
|---|---|---|---|---|
| AMB-28 | "Who, when and why" for the audit log is not defined, and neither are retention and protection against change. | Needed for NFR-5. | Each audit record holds actor (id and type: customer, agent, administrator, support or system), UTC timestamp, action, affected entity and payment reference, reason, and before and after values for administrative changes. Records are append-only. Retention is for the lifetime of the project. | Gap / Open (proposed) |
| AMB-29 | The brief says balances are computed from the ledger and never overwritten, but does not say entries are immutable. | Needed so that the ledger can be trusted. | Ledger entries are never edited or deleted; corrections are new compensating entries. | Derived / Open (proposed) |
| AMB-30 | NFR-2 gives a throughput target but no error-rate or response-time target, no biller latency, and no account distribution. The velocity rule would block a single account sending many payments. | Decides how the load test is designed and reported. | The load test uses at least 1,000 different customer accounts and documented mock-biller latencies. It reports throughput, error rate and p95 latency, and must also show NFR-1 holds. The target (100 payments per second for 60 seconds) is not changed. | Gap / Open (proposed) |
| AMB-31 | "Domain logic" for the 70% coverage target is not defined. | Decides which code counts in the coverage report. | Domain logic is the business-rule code: payment life cycle, wallet and ledger, fees and commissions, fraud rules, reconciliation matching and notification retry policy. Controllers, framework configuration, generated code and pure input and output adapters are excluded. | Gap / Open (proposed) |
| AMB-34 | The brief gives priorities to FR-1 to FR-28 only; the six non-functional requirements have none. | The deliverable asks for priorities, so each NFR needs one. | All six NFRs are treated as **Must** because the brief lists them as expected and requires a measured number for each. | Gap / Open (proposed) |
| AMB-35 | The language and framework must be declared in Phase 1 (brief, section 3.4), but this is outside the requirements section. | The choice decides the test, coverage, load-test and dependency-check tools used to verify NFR-2, NFR-4 and NFR-6. | Decide as a team (Person 1 and the architecture lead), record it in the repository documentation and update the verification tools in section 7 if needed. | Team decision / Open |

### 9.4 Questions to confirm first with the instructor or team

These open points have the biggest effect on the design and tests, so they should be settled before the design pack is finalised:

1. **AMB-26** - How should FR-27 relate to the life cycle in which Completed is final?
2. **AMB-02** - Do the limits apply to the bill amount or to the total including the fee?
3. **AMB-16 and AMB-17** - How long do we wait for the TopUpNet callback, and do the mock billers offer a payment-status query for re-queries?
4. **AMB-06** - Which wallet and which limits apply when an agent pays on behalf of a customer?
5. **AMB-24 and AMB-25** - What is the settlement-file format and which differences may change a payment's state?
6. **AMB-07** - Idempotency-key rules (scope, lifetime and conflicting data).
7. **AMB-35** - The technology stack (language, framework, database, test and coverage tools) must be declared in Phase 1 (brief, section 3.4); NFR-2, NFR-4 and NFR-6 depend on it.
8. **AMB-32** - Order of the fraud checks and the reason shown when several rules fail.

## 10. Appendices

### Appendix A - Glossary

| Term | Meaning in this project (brief, section 2.3) |
|---|---|
| Biller | A company that issues bills and receives payments (electricity, water, mobile operator, internet provider) |
| Bill inquiry | Asking the biller what a customer owes before paying |
| Wallet | A customer account holding money in EGP, with an available balance and a held balance |
| Ledger | A list of entries that records every money movement. A balance is the sum of its ledger entries; a balance is never overwritten directly |
| Hold | Money reserved for a payment that is not yet confirmed |
| Service fee | What PayLink charges the customer per payment |
| Commission | What PayLink pays an agent for a payment made at the agent's terminal |
| Idempotency key | A unique value sent with a request so that sending it twice has the effect of sending it once |
| Settlement file | The biller's daily list of payments it received |
| Reconciliation | Comparing PayLink's payments with the settlement file and flagging differences |
| Refund | Returning held or charged money to the wallet after a failed or cancelled payment |
| Agent | A small shop with a PayLink terminal that can take payments and cash top-ups for customers |
| Basic / Verified | Account verification levels with daily limits of 5,000 EGP and 30,000 EGP |

### Appendix B - Consistency check against the brief

| Check | Result |
|---|---|
| All 28 functional requirements (FR-1 to FR-28) are present with the brief's wording and a user story each | 28 of 28 |
| Every user story has acceptance criteria in Given / When / Then form (180 criteria in total) | 28 of 28 |
| Every user story states its priority, module, the verbatim brief requirement and its related items | 28 of 28 |
| Priorities equal the brief: 19 Must, 7 Should, 2 Could | Matches |
| Six non-functional requirements with the exact targets of the brief, a description and a verification method | 6 of 6 |
| Business rules BR-01 to BR-14 reproduce every value of section 2.6 of the brief; the life-cycle table (4.2) reproduces section 2.5 and the biller table (4.4) reproduces section 2.4 | Matches |
| All eight hard cases are traced to acceptance criteria | 8 of 8 |
| Assumptions are separated from confirmed requirements and none changes a number, priority or transition of the brief | Yes (section 9) |

### Appendix C - Reference

*PayLink Year Work Project - Student Brief*, sections 2.2 to 2.7, 3.1 to 3.4 and 4.
