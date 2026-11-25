# PayLink Phase 1 – Complete Prompt Log

**Project:** PayLink  
**Phase:** Phase 1 – Requirements and Design  
**Date of work:** 2026-11-23  
**Responsible contributors:** `Abdelrhman Mohamed` (domain class and two sequence diagrams); `Youssef Mahmoud` (payment state diagram and database ERD); `Person 2 – name to be filled in` (requirements pack). Replace these role labels with the actual teammate names before submission.  
**AI tool / mode:** `ChatGPT` for P1–P9 (the exact model/version was not recorded separately in the team prompt history); `Claude` (Anthropic, model shown in the app as Claude Sonnet 5.5) for P10–P15.

## 1. Prompt log summary

| ID  | Date       | Responsible person | AI tool / mode | Stage                                           | Prompt purpose                                                                                              | Summary of AI answer / issue found                                                                                                                                                                                                                                | Verdict                                    |
| --- | ---------- | ------------------ | -------------- | ----------------------------------------------- | ----------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------ |
| P1  | 2026-11-23 | Abdelrhman Mohamed | ChatGPT        | Domain class diagram – initial simplified model | Generate a small, implementable domain model from the official brief                                        | The first simplified proposal was easier to read, but had incorrect LedgerEntry source associations, omitted the Tiered fee type, and missed some relationships/constraints.                                                                                      | **Fixed** – follow-up prompt P2 was used.  |
| P2  | 2026-11-23 | Abdelrhman Mohamed | ChatGPT        | Domain class diagram – refinement               | Correct the ledger model, fee types, inquiries, promotions, wallet ownership, audit and payment constraints | The revised model corrected the main issues. The final source was then manually checked and small consistency details were retained in the simplified version.                                                                                                    | **Accepted after review**                  |
| P3  | 2026-11-23 | Abdelrhman Mohamed | ChatGPT        | Pay a Bill sequence – initial simplified flow   | Generate a short sequence with about nine participants and the main alternatives                            | The first output had the right overall structure, but did not fully forward TopUpNet's Accepted response, omitted some commission/retry details, and placed payment completion before wallet capture.                                                             | **Fixed** – follow-up prompt P4 was used.  |
| P4  | 2026-11-23 | Abdelrhman Mohamed | ChatGPT        | Pay a Bill sequence – refinement                | Correct callback handling, commission, capture order, Unknown retries and no-inquiry explanation            | The returned answer still needed manual correction. The final source was adjusted to keep capture before Completed, forward Accepted, include commission paths and document retries.                                                                              | **Fixed after manual review**              |
| P5  | 2026-11-23 | Abdelrhman Mohamed | ChatGPT        | Refund a Failed Payment sequence – initial flow | Generate a short six-participant failed-payment release sequence                                            | The first output showed the right business flow, but the separate Hold check/release could allow a race condition. The already-Released branch also did not recover Payment to Refunded after verifying the earlier release.                                      | **Fixed** – follow-up prompt P6 was used.  |
| P6  | 2026-11-23 | Abdelrhman Mohamed | ChatGPT        | Refund a Failed Payment sequence – refinement   | Make the release atomic/idempotent and handle Active, Released and Captured holds                           | The revised answer made release atomic and idempotent, recovered Payment to Refunded only when a prior release is verified, and prevented release of a Captured hold.                                                                                             | **Accepted after review**                  |
| P7  | 2026-11-23 | Youssef Mahmoud    | ChatGPT        | Payment State Diagram – initial generation      | Generate the eight required states and only the transitions in section 2.5                                  | The AI generated all eight states and the specified transitions correctly. A small manual wording correction clarified that the 10-second timeout applies to live billers, not the intentionally asynchronous TopUpNet flow.                                      | **Accepted after minor manual correction** |
| P8  | 2026-11-23 | Youssef Mahmoud    | ChatGPT        | Database ERD – initial generation               | Generate a compact relational ERD with at least 10 tables, PKs, FKs and cardinalities                       | The first ERD had a useful 15-table structure, but needed stronger user/service/payment attributes, a safer LedgerEntry source design, and clearer audit/cardinality constraints.                                                                                 | **Fixed** – follow-up prompt P9 was used.  |
| P9  | 2026-11-23 | Youssef Mahmoud    | ChatGPT        | Database ERD – refinement                       | Correct the ERD's fields, foreign keys, uniqueness, role constraints and ledger source references           | The revised ERD corrected the main issues. Final manual edits clarified the audit relationship/cardinality, financial precision, one-source-per-ledger-entry constraint, audit creation requirement, role checks and the scope of deferred reconciliation tables. | **Accepted after manual review**           |
| P10 | 2026-11-23 | Person 2 | Claude | Preparation – read the brief | Read the project brief and confirm understanding before giving tasks | The AI read the brief (its first file view was truncated in the middle, so it re-read the missing lines) and summarised the modules, payment states, billers, business rules and Phase 1 and 2 deliverables. | **Accepted after check** – the summary matched the brief; the PDF copy sent afterwards had the same content. |
| P11 | 2026-11-23 | Person 2 | Claude | Requirements pack – initial generation | Generate `requirements.md` and `requirements.pdf`: problem summary, stakeholders, 28 user stories with Given/When/Then, 6 NFRs, business rules, ambiguities | The AI produced a 28-story pack with 166 acceptance criteria and 31 ambiguities. Problems found: admin and regulators were marked "derived" although the brief names them (FR-6, section 2.1); five criteria put two scenarios in one line; criteria were long single lines that are hard to turn into tests; the first PDF render had wrapped ID columns ("AMB-" / "01"). | **Fixed** – follow-up prompts P12 and P13 were used. |
| P12 | 2026-11-23 | Person 2 | Claude | Requirements pack – review | Compare the AI version with another version of the requirements document supplied by the user and say which is better | The AI compared both. The other version was stronger in layout and traceability (verbatim brief requirement per story, separate Given/When/Then lines, objective and challenge IDs, stakeholder IDs, extra ambiguities); the AI version had extras of its own (actor list, hard-case table, consistency table). | **Accepted** – used to decide the next prompt. |
| P13 | 2026-11-23 | Person 2 | Claude | Requirements pack – improvement | Edit the AI's own version using the strengths of the other file | The AI added per-story attribute tables, separate Given/When/Then lines, objectives and challenges tables, stakeholder IDs, a billers table, a module table, 9 extra criteria and 4 extra ambiguities. Problems found: the AI first started from the wrong base (the other file) and the user had to correct it; the document became much larger than what the brief requires, and after a CSS change the ID columns wrapped again in the PDF (found by the AI's own render check and corrected). | **Fixed** – follow-up prompts P14 and P15 were used. |
| P14 | 2026-11-23 | Person 2 | Claude | Requirements pack – scope check | Ask whether all the extra sections were required, since only the problem definition and the functional and non-functional requirements are required | The AI answered that the extras were not required, separated the core sections from the extras and offered a shorter version. | **Accepted** |
| P15 | 2026-11-23 | Person 2 | Claude | Requirements pack – final trimmed version | Produce the shorter version | The AI kept the problem summary, stakeholders, business rules, the 28 user stories (180 criteria), the 6 NFRs and the 12 most important ambiguities, removed the extras and regenerated the PDF (21 pages). It checked that all 28 stories, priorities and Given/When/Then structures are correct and that every identifier in the Markdown appears in the PDF. | **Accepted after review** – the team must still read it, and Person 2 must be able to explain every point. |

The log contains **fifteen prompt entries**: nine (P1–P9) across the five Phase 1 diagrams, recorded with ChatGPT, and six (P10–P15) for the requirements pack, recorded with Claude. Several entries (P1, P3, P5, P8, P11, P13) record an initial AI result that was wrong or needed work and was corrected through a follow-up prompt, which exceeds the brief's minimum of five entries and two improved prompts. Section 3 lists the questions the AI raised during requirements analysis.

---

## 2. Full prompts used

### P1 – Domain class diagram: initial simplified model

**Date:** 2026-11-23  
**Responsible person:** Abdelrhman Mohamed
**AI tool / mode:** ChatGPT  
**Stage:** Design – Domain Class Diagram  
**Verdict:** Fixed

```text
Act as a Software Engineering student with good UML and Object-Oriented Design knowledge.

I am working on a university project called PayLink. I have attached the official project requirements.

Your task is to create a SIMPLE, CLEAN, and CORRECT UML Domain Class Diagram for Phase 1.

IMPORTANT:
This is a university project, not a production banking system.
I need a reasonable domain model that is easy to understand, explain, and implement later.

Do not overengineer the design.

## 1. System Overview

PayLink is a digital wallet system that allows customers to pay:
- Electricity bills
- Water bills
- Mobile top-ups
- Internet bills

Customers use their wallets to pay bills. The system calculates fees, checks payment limits, reserves funds, contacts billers, and records payment results.

Agents can pay on behalf of customers and receive commissions.

## 2. Required Domain Concepts

Consider these classes as the starting point:
- User
- Wallet
- Payment
- Hold
- LedgerEntry
- Biller
- Service
- BillInquiry
- FeePolicy
- Promotion
- Commission
- WalletTopUp
- Notification
- AuditLogEntry

You may add a class only if it is genuinely necessary.
You may combine concepts when doing so does not violate the business requirements.

## 3. Simplification Rules

1. Use one User class with a UserRole enumeration (Customer, Agent, Admin, SupportEmployee) instead of creating unnecessary subclasses.
2. Avoid creating separate classes for every fee calculation strategy. A FeePolicy class with an appropriate fee type and configuration is sufficient for this diagram.
3. Store the calculated fee amount in Payment rather than introducing a separate Fee class.
4. Do not create separate classes for controllers, repositories, DTOs, application services, or external API adapters.
5. Do not include unnecessary methods. Focus mainly on attributes and relationships.
6. Avoid duplicating information that can be obtained through an existing relationship.
7. Use simple, readable relationships and correct UML multiplicities.

## 4. Important Business Rules

### Wallet and Money
- A customer and an agent can own wallets.
- Wallets have available and held balances.
- Balances are calculated from LedgerEntry records.
- Every money movement must create appropriate ledger entries.
- Use exact Decimal monetary values, never floating-point values.

### Payments
- A customer can make multiple payments.
- An agent may optionally process a payment on behalf of a customer.
- A payment belongs to a billable Service.
- A service belongs to a Biller.
- A payment can have at most one Hold.
- A BillInquiry is optional because not all billers support live inquiries.
- A payment has exactly one status at a time.
- The same customer and idempotency key must not create duplicate payments.

### Fees and Commissions
- Fees are calculated according to the service's FeePolicy.
- A Promotion can make the applicable fee zero.
- An agent receives 40% of the service fee when the payment completes.

### Other Requirements
- Wallet top-ups must be represented.
- Notifications must be represented.
- AuditLogEntry must record money movements and important actions.

## 5. Payment Lifecycle

Use a PaymentStatus enumeration containing:
- Initiated
- FundsHeld
- SentToBiller
- Completed
- Failed
- Unknown
- Rejected
- Refunded

Only these transitions are valid:
Initiated -> FundsHeld
Initiated -> Rejected
FundsHeld -> SentToBiller
SentToBiller -> Completed
SentToBiller -> Failed
SentToBiller -> Unknown
Unknown -> Completed
Unknown -> Failed
Failed -> Refunded

Completed, Rejected, and Refunded are final states.
Unknown payments must keep their funds held.

The automatic release of a failed payment is represented by releasing its Hold, not by creating a separate Refund class.

## 6. Diagram Quality

- The diagram should have a clean and readable layout.
- Use meaningful attributes.
- Show inheritance only where genuinely necessary.
- Show relationships and multiplicities.
- Use enums for appropriate statuses and types.
- Avoid unnecessary dependencies and decorative relationships.
- Be consistent with the supplied project brief.

Aim for approximately 14 important classes, excluding enumerations. This is a guideline, not a strict limit.

## 7. Output

Provide:
1. A short explanation of the model.
2. A table listing each class and its responsibility.
3. The complete PlantUML code in one code block.
4. A short explanation of the important relationships and multiplicities.
5. A list of business requirements that are not explicitly represented by a class.

Do not create a huge enterprise-level diagram.
Do not add features that are not in the requirements.
Do not sacrifice important business rules just to reduce the number of classes.

The result should be a simple, academically appropriate Domain Class Diagram that the team can understand and implement in Phase 2.
```

### P2 – Domain class diagram: refinement prompt

**Date:** 2026-11-23  
**Responsible person:**Abdelrhman Mohamed
**AI tool / mode:** ChatGPT  
**Stage:** Design – Domain Class Diagram refinement  
**Verdict:** Accepted after review

```text
Act as a Senior Software Engineer and UML Domain Model Reviewer.

I have attached:
1. The official PayLink project brief.
2. My current simplified Domain Class Diagram in PlantUML.

Your task is to correct the existing diagram while keeping it SIMPLE and suitable for a university Software Engineering project.

IMPORTANT:
I do not want an enterprise-level design.
Do not add unnecessary classes, methods, abstractions, or relationships.
Preserve the current simplified structure and approximately 14 main classes.

Apply these corrections:

1. LedgerEntry:
Use entryId, amount, direction, bucket, sourceType, sourceId and createdAt. Use EntryDirection (Debit/Credit), WalletBucket (Available/Held), and LedgerSourceType (Hold, WalletTopUp, Commission). Keep Wallet as the LedgerEntry association and document that each entry has exactly one financial source.

2. FeePolicy:
Support Fixed, Percentage and Tiered fee types. Include the relevant configuration values for fixed, percentage/minimum, and tiered fees. Keep FeePolicy as one class; do not create subclasses.

3. NotificationChannel:
Use SMS, Email and Push. Include Undelivered in NotificationStatus.

4. BillInquiry:
Add relationships showing that a User requests an inquiry and a Service is associated with it. Only a User with the Customer role can request one. Keep Payment-BillInquiry optional.

5. Promotion:
Connect Promotion to Biller. Keep its optional association with Payment. A promotion applies to one biller during its configured active date range.

6. Wallet ownership:
Keep one User class and specify that only Customer and Agent roles may own a wallet. A User may own at most one wallet.

7. Attributes:
Biller uses isActive rather than a String status and does not need a separate category if Service already identifies the service type. Service uses isActive. Payment has billAmount, feeAmount and totalAmount. WalletTopUp has a TopUpSource (SimulatedCard, AgentCash).

8. CommissionStatus:
Use Pending and Credited.

9. AuditLogEntry:
Keep it separate. Every LedgerEntry must have exactly one audit record, while an audit record may also describe a non-financial action. Do not force every AuditLogEntry to belong to a LedgerEntry.

Preserve the important business rules:
- Wallet balances are derived from LedgerEntry records.
- Every balance-changing movement has appropriate ledger entries.
- Money uses exact Decimal values.
- A Payment has at most one Hold.
- Unknown payments retain their holds.
- Failed payments release their Hold before becoming Refunded.
- Keep the specified payment lifecycle.
- Duplicate requests with the same customer and idempotency key return the original result.
- Agent commission is 40% of the service fee when the payment completes.
- Do not introduce Completed -> Refunded.

Output:
1. A short explanation of corrections.
2. Complete revised PlantUML source.
3. Main relationships and any remaining team decisions.

Keep the diagram readable, simple and implementable. Do not redesign the project or add unnecessary classes.
```

### P3 – Pay a Bill sequence: initial simplified prompt

**Date:** 2026-11-23  
**Responsible person:** Abdelrhman Mohamed
**AI tool / mode:** ChatGPT  
**Stage:** Design – Pay a Bill Sequence Diagram  
**Verdict:** Fixed

```text
Act as a Software Engineering student with good UML and Object-Oriented Design knowledge.

I am working on a university project called PayLink.
I have attached the official project brief and my simplified Domain Class Diagram.

Create a SIMPLE and READABLE UML Sequence Diagram for "Pay a Bill".
This is a university project. Do not create a complicated enterprise-level sequence diagram. I need a diagram that is easy to understand, explain, and implement.

Main workflow:
1. Customer requests to pay a bill.
2. Payment Processing retrieves the requested service from Biller Catalog.
3. If supported, the system performs a bill inquiry through the appropriate biller.
4. The system retrieves the bill amount.
5. Payment Processing calculates the service fee using FeePolicy.
6. The system displays bill amount, fee and total to the customer.
7. Customer confirms the payment.
8. System checks the idempotency key.
9. System creates a Payment.
10. System performs fraud checks.
11. Wallet checks and reserves the required funds.
12. Payment moves to FundsHeld and then SentToBiller.
13. Biller Adapter sends the payment request to the external biller.
14. The system receives the payment result.
15. Wallet and Payment are updated.
16. Notification Service informs the customer.

Use approximately these participants:
- Customer (actor)
- Payment Processing
- Biller Catalog
- BillInquiry
- Payment
- Wallet
- Biller Adapter
- External Biller
- Notification Service

Do not add separate lifelines for every class or financial operation. Fee calculation and fraud validation can be shown as internal operations of Payment Processing. Do not add controllers, repositories, database tables, DTOs or unnecessary classes.

Important alternatives using alt:
A. Duplicate request: same customer and key returns the original Payment result; no second payment or charge.
B. Fraud checks fail: Payment becomes Rejected; no funds are reserved.
C. Insufficient funds: Payment becomes Rejected; no request is sent to the biller.
D. Biller confirms: Payment becomes Completed; Wallet captures funds; if an Agent processed it, credit 40% of the fee as commission; notify customer.
E. Biller refuses: Payment becomes Failed; reference the separate "Refund a Failed Payment" sequence instead of repeating the release process.
F. Live biller timeout after 10 seconds: Payment becomes Unknown; funds remain held; no automatic refund; notify customer.

TopUpNet asynchronous flow:
- Initial response is Accepted, not final confirmation.
- Payment remains SentToBiller while waiting.
- A later callback contains Success or Failed.
- Process the final result only once; ignore duplicate callbacks.
- Do not automatically apply the 10-second live-biller timeout to TopUpNet; identify this as an ambiguity if needed.

Financial rules:
- Electricity/water: 2.50 EGP.
- Mobile: 1.5%, minimum 1 EGP.
- Internet: 2 EGP up to 200 EGP, 4 EGP above.
- Applicable promotion makes fee zero.
- Basic daily limit: 5,000 EGP; verified: 30,000 EGP.
- Minimum payment: 5 EGP; maximum: 10,000 EGP.
- More than 5 payments within 60 seconds blocks an account for 10 minutes.
- Total = bill amount + fee. External biller receives bill amount only.

Wallet.reserveFunds() must be shown as an atomic operation that checks available balance, safely reserves funds, prevents concurrent double spending, and internally creates the Hold and required LedgerEntries. captureFunds() is also an atomic financial operation. These internal operations may be explained in notes; do not add separate Hold or LedgerEntry lifelines.

Use only the specified payment transitions:
Initiated -> FundsHeld / Rejected;
FundsHeld -> SentToBiller;
SentToBiller -> Completed / Failed / Unknown;
Unknown -> Completed / Failed;
Failed -> Refunded.
Completed, Rejected and Refunded are final. Unknown retains its Hold.

Keep the diagram readable. Use alt for important alternatives. Do not include the full reconciliation workflow, detailed notification retry loops, or the full failed-payment refund process. Use the attached Domain Class Diagram as the source of truth.

Return a short workflow explanation, participant list, complete PlantUML source, explanation of alternatives, and ambiguities. Check syntax. Do not generate an image.
```

### P4 – Pay a Bill sequence: refinement prompt

**Date:** 2026-11-23  
**Responsible person:**Abdelrhman Mohamed  
**AI tool / mode:** ChatGPT  
**Stage:** Design – Pay a Bill Sequence refinement  
**Verdict:** Fixed after manual review

```text
Act as a Senior Software Engineer and UML Sequence Diagram Reviewer.

I have attached the official PayLink brief and my current simplified Pay a Bill Sequence Diagram in PlantUML.

Correct the existing diagram; do not redesign it.

- Preserve the current 9 participants and overall structure.
- Keep it simple and suitable for a university project.
- Do not introduce unnecessary classes, participants or interactions.

Apply these corrections:

1. TopUpNet: forward the initial Accepted response from the Adapter to Payment Processing. Accepted is not final; Payment remains SentToBiller and funds remain held until a later callback. Keep callbacks conditional on the TopUpNet branch.

2. Agent commission: after successful capture, show an optional 40% of feeAmount commission credit for an Agent-processed payment. Apply the same rule to synchronous success, successful TopUpNet callbacks and successful late results. Do not generate duplicate commission on duplicate callbacks. Do not add a Commission participant; show the Wallet operation and explain that it records the commission and ledger movement.

3. Success ordering: capture held funds through an atomic Wallet operation first. Only after capture succeeds should Payment become Completed. Apply this to synchronous and asynchronous success. Do not invent unspecified recovery behavior if capture fails; identify it as an assumption or open decision.

4. Unknown retries: add a short note for retries after 1, 5 and 15 minutes, then waiting for reconciliation. Keep funds held and do not add unnecessary participants or a large retry sub-sequence.

5. No inquiry: clarify that the amount may be supplied by the customer for mobile top-up. NetCo batch processing is separate and optional for Phase 1; do not imply every no-inquiry biller uses the same flow.

6. Callback idempotency: distinguish the initial Accepted response, later Success/Failed callback, first callback and duplicate callback. Duplicates must not complete, capture, create ledger movements or credit commission twice.

Preserve the other correct behavior:
- Idempotency is checked before creating a new Payment.
- Fraud checks occur before reserving funds.
- Funds are reserved before contacting the biller.
- Wallet reservation is atomic and prevents concurrent double spending.
- Biller receives billAmount, not total including fee.
- Fraud failure and insufficient funds result in Rejected.
- Biller refusal results in Failed and references the separate failed-payment refund sequence.
- Live timeout after 10 seconds results in Unknown with the Hold retained.
- Notification failure must not reverse payment success.

Use correct alt/opt/loop fragments, short messages and readable PlantUML. Return the corrections, complete source and remaining ambiguities. Do not expand the diagram.
```

### P5 – Refund a Failed Payment sequence: initial prompt

**Date:** 2026-11-23  
**Responsible person:** Abdelrhman Mohamed
**AI tool / mode:** ChatGPT  
**Stage:** Design – Refund a Failed Payment Sequence  
**Verdict:** Fixed

```text
Act as a Software Engineering student with good UML Sequence Diagram knowledge.

I am working on a university project called PayLink. I have attached the official requirements and my simplified Domain Class Diagram.

Create a SIMPLE UML Sequence Diagram for "Refund a Failed Payment".

This diagram represents releasing held wallet funds after a bill payment has failed. It is NOT the optional FR-27 refund of an already Completed payment.

Main workflow:
1. Payment Processing identifies the failed payment.
2. System verifies Payment.status is Failed.
3. System retrieves the associated Hold.
4. System asks Wallet to release held funds.
5. Wallet verifies that the Hold is still Active.
6. Wallet releases funds.
7. Wallet records the required LedgerEntries.
8. Hold becomes Released.
9. Payment becomes Refunded.
10. Notification Service informs the customer.

Use six participants:
- Customer (actor)
- Payment Processing
- Payment
- Hold
- Wallet
- Notification Service

Do not add unnecessary participants. LedgerEntry and AuditLogEntry may be explained as internal Wallet operations rather than separate lifelines.

Alternative scenarios using alt:
A. Payment is not Failed: do not release funds or change Payment status.
B. Hold is already Released: do not release funds again, create duplicate LedgerEntries or credit the wallet twice.
C. Successful release: record financial movements, set Hold to Released, then set Payment to Refunded.
D. Release fails: do not mark Payment Refunded; identify unspecified recovery behavior as an assumption.

Business rules:
- Payment must be Failed before this process.
- Unknown cannot be refunded automatically; it must first be confirmed unsuccessful and move to Failed.
- A Payment has at most one Hold.
- Releasing a Hold moves funds from Held to Available.
- Every balance-changing operation creates appropriate LedgerEntries.
- Every financial LedgerEntry has its corresponding audit record.
- Release must be idempotent to prevent duplicate fund releases.
- Payment becomes Refunded only after successful Hold release.
- Failed -> Refunded is the only transition represented here.

Do not include customer-requested refunds, support approval, refunds of Completed payments, or a new wallet top-up.

Return a short explanation, participants, complete PlantUML code, alternative scenarios and remaining assumptions. Keep it short, readable and consistent with the other diagrams. Do not generate an image.
```

### P6 – Refund a Failed Payment sequence: refinement prompt

**Date:** 2026-11-23  
**Responsible person:**Abdelrhman Mohamed
**AI tool / mode:** ChatGPT  
**Stage:** Design – Refund a Failed Payment refinement  
**Verdict:** Accepted after review

```text
Review and correct my existing PayLink "Refund a Failed Payment" sequence diagram.

Keep the same six participants and preserve the simple structure.

Make only these corrections:

1. Make Wallet.releaseHeldFunds(holdId) one atomic operation that:
   - Checks whether the Hold is Active.
   - Moves money from Held to Available.
   - Creates the required LedgerEntries.
   - Creates corresponding audit records.
   - Changes Hold status to Released.

2. Make the release operation idempotent.

3. If the Hold is already Released but Payment is still Failed:
   - Do not create additional LedgerEntries.
   - Do not release funds again.
   - Safely update Payment to Refunded, provided the existing release is verified.

4. Explicitly handle the Captured Hold case:
   - Do not release funds.
   - Do not change Payment to Refunded.
   - Report an inconsistent financial state.

5. Preserve the rule that only Failed payments can enter this process. Unknown payments must first be confirmed unsuccessful.

Do not add participants or unnecessary interactions.

Return the complete corrected PlantUML source code and briefly explain the changes. Check the PlantUML syntax.
```

---

### P7 – Payment State Diagram: initial prompt

**Date:** 2026-11-23  
**Responsible person:** Youssef Mahmoud
**AI tool / mode:** ChatGPT  
**Stage:** Design – Payment State Diagram  
**Verdict:** Accepted after minor manual correction

```text
Act as a UML State Machine Diagram Designer.

I am working on a university Software Engineering project called PayLink.
I have attached the official project brief.

Your task is to create a SIMPLE UML State Diagram for the Payment lifecycle.

## 1. Required States

Use exactly these payment states:
- Initiated
- FundsHeld
- SentToBiller
- Completed
- Failed
- Unknown
- Rejected
- Refunded

## 2. Allowed Transitions

Follow section 2.5 of the official project brief exactly.

Initiated -> FundsHeld
Event: Fraud checks pass and wallet has sufficient funds.

Initiated -> Rejected
Event: Fraud checks fail or funds are insufficient.

FundsHeld -> SentToBiller
Event: Payment request is sent to biller.

SentToBiller -> Completed
Event: Biller confirms payment.

SentToBiller -> Failed
Event: Biller refuses payment.

SentToBiller -> Unknown
Event: Biller does not respond within timeout.

Unknown -> Completed
Event: Biller later confirms payment.

Unknown -> Failed
Event: Biller later refuses or reconciliation finds no payment.

Failed -> Refunded
Event: Hold is successfully released to the wallet.

## 3. Important Business Rules

- Completed, Rejected and Refunded are final states.
- Unknown payments keep their funds held.
- Unknown payments must never be refunded automatically.
- A payment becomes Refunded only after its Hold is successfully released.
- No other payment transitions are allowed.

## 4. Additional Information

Include a small note explaining:
- Live biller timeout is 10 seconds.
- Unknown payments are retried after 1, 5 and 15 minutes.
- After three unsuccessful retries, the payment waits for reconciliation.

Do not introduce additional payment states for retrying.

## 5. Diagram Requirements

- Use UML State Machine notation.
- Include initial and final-state notation where appropriate.
- Show transition events clearly.
- Keep the diagram simple and readable.
- Avoid unnecessary classes or participants.
- Use PlantUML.

## 6. Output

1. Explain the states and transitions briefly.
2. Provide the complete PlantUML source code.
3. Identify any ambiguities.

Do not generate an image. I need editable PlantUML code.
```

### P8 – Database ERD: initial prompt

**Date:** 2026-11-23  
**Responsible person:** Youssef Mahmoud
**AI tool / mode:** ChatGPT  
**Stage:** Design – Database ER Diagram  
**Verdict:** Fixed – follow-up prompt P9 was used

```text
Act as a Database Designer and Software Engineering Student.

I am designing the database for PayLink, a digital wallet and bill-payment system.

I have attached:
1. Official PayLink project brief.
2. My approved simplified Domain Class Diagram.
3. My approved Payment State Diagram.

Create a SIMPLE and CORRECT relational Database ER Diagram.

## 1. Requirements

The project requires:
- A relational database.
- At least 10 tables.
- Primary keys and foreign keys.
- Correct relationships and cardinalities.
- Normalized database structure.
- Consistency with the Domain Class Diagram.

## 2. Suggested Tables

Start with these tables:
1. users
2. wallets
3. billers
4. services
5. fee_policies
6. promotions
7. bill_inquiries
8. payments
9. holds
10. ledger_entries
11. wallet_topups
12. commissions
13. notifications
14. audit_log_entries

You may adjust this list only when necessary.

## 3. Important Table Requirements

USERS:
Store all user types using a role field: Customer, Agent, Admin, SupportEmployee.

WALLETS:
Each eligible user can own at most one wallet. Do not store availableBalance and heldBalance as independent values because they must be calculated from ledger entries.

PAYMENTS:
Include payment_id, customer_id, agent_id (nullable), service_id, inquiry_id (nullable), bill_amount, fee_amount, total_amount, idempotency_key, status and created_at. Enforce uniqueness on (customer_id, idempotency_key).

HOLDS:
Each Payment can have at most one Hold.

LEDGER_ENTRIES:
Include entry_id, wallet_id, amount, direction, bucket, source_type, source_id and created_at. Explain how ledger entries represent available and held balance movements. Avoid designing relationships that force one ledger entry to belong to multiple financial sources.

BILLERS AND SERVICES:
One Biller provides one or more Services. Each Service belongs to one Biller and uses a FeePolicy.

FEE_POLICIES:
Support Fixed, Percentage and Tiered fees.

PROMOTIONS:
Each Promotion applies to one Biller during a specified date range.

COMMISSIONS:
A completed Payment involving an Agent may generate a Commission equal to 40% of its service fee.

WALLET_TOPUPS:
Support simulated card top-ups and agent cash top-ups.

NOTIFICATIONS:
Support SMS, Email and Push.

AUDIT_LOG_ENTRIES:
Every financial LedgerEntry must have an audit record. Administrative actions must also be auditable.

## 4. Database Design Constraints

- Use appropriate primary keys and foreign keys.
- Show nullability and unique constraints.
- Use DECIMAL rather than FLOAT for money.
- Avoid redundant foreign keys.
- A Payment references Service; its Biller can be obtained through Service.
- Do not store derived wallet balances.
- Do not use unnecessary database tables.
- Do not add controllers, repositories or software service classes.
- Ensure the database supports the official Payment lifecycle.

If a polymorphic source relationship cannot be enforced with foreign keys, explain the issue and suggest a practical relational design.

## 5. Output

1. List all tables with their important columns.
2. Identify primary and foreign keys.
3. Explain the main relationships.
4. Explain important constraints.
5. Provide complete PlantUML ER Diagram code.
6. Identify any design assumptions.

The result should be easy for a university team to understand and implement.

Do not generate an image. I need editable PlantUML source code.
```

### P9 – Database ERD: correction prompt

**Date:** 2026-11-23  
**Responsible person:**Youssef Mahmoud  
**AI tool / mode:** ChatGPT  
**Stage:** Design – Database ERD refinement  
**Verdict:** Accepted after manual review

```text
Act as a Database Designer reviewing my existing PayLink relational ER Diagram.

I have attached:
1. The official PayLink project brief.
2. My current PlantUML ER Diagram.

Your task is to CORRECT the existing ERD, not redesign it.

IMPORTANT:
- Keep approximately the same 15 tables.
- Keep the design appropriate for a university project.
- Do not add unnecessary tables.
- Preserve correct primary keys, foreign keys, and relationships.
- Keep the diagram readable.

## Required Corrections

1. USERS
Replace is_verified with verification_level and add account_status and blocked_until. Verification levels are Basic and Verified. Only Customers use verification levels.

2. SERVICES
Add required_input_fields, minimum_amount and maximum_amount.

3. BILLERS
Add integration_protocol to represent REST, SOAP, asynchronous callbacks and batch integration.

4. PAYMENTS
Add payment_reference (unique, not null) and bill_reference (not null). Keep UNIQUE(customer_id, idempotency_key). Make inquiry_id nullable and unique so one inquiry cannot be reused by multiple payments.

5. LEDGER_ENTRIES
Replace source_type and source_id with hold_id (nullable FK), topup_id (nullable FK) and commission_id (nullable FK). Enforce that exactly one of these three source foreign keys is populated. Keep wallet_id as a mandatory FK.

6. AUDIT_LOG_ENTRIES
Keep ledger_entry_id nullable because administrative actions do not necessarily involve a financial movement. Document that every financial LedgerEntry must have exactly one corresponding AuditLogEntry.

7. NOTIFICATIONS
Add retry_count with a default value of zero. Include Undelivered in NotificationStatus.

8. HOLDS
Add captured_at as a nullable timestamp.

9. BUSINESS CONSTRAINTS
Document:
- Only Customer users can be payment customers.
- Only Agent users can process payments as agents.
- Only Customers and Agents can own wallets.
- Agent cash top-ups require an agent.
- Simulated card top-ups do not require an agent.
- A promotion must belong to the same biller as the payment's Service.
- Fee policy tiers must not overlap.

## Preserve

- The minimum 10-table requirement.
- Decimal monetary values.
- Available and held balances derived from ledger entries.
- At most one Hold per Payment.
- The payment lifecycle from the official brief.
- The existing fee policy and fee tier design.
- All existing useful relationships.

Do not introduce unnecessary classes, controllers, or application services.

## Output

1. Briefly explain the corrections.
2. Return the complete corrected PlantUML ER Diagram.
3. Explain the main foreign keys and cardinalities.
4. List any remaining database constraints that require application logic or triggers.
5. Check the PlantUML syntax.

Do not generate an image. I need editable PlantUML source code.
```

### P10 – Preparation: read the brief

**Date:** 2026-11-23  
**Responsible person:** Person 2  
**AI tool / mode:** Claude  
**Stage:** Preparation  
**Verdict:** Accepted after check

```text
hi this my project read it and i will ask you to do some tasks on it
```

Attached: `PayLink_Year_Work_Project_-_Student_Brief (1).md`. A PDF copy of the same brief was sent in a second message with the note "this is the complete file".

### P11 – Requirements pack: initial generation

**Date:** 2026-11-23  
**Responsible person:** Person 2  
**AI tool / mode:** Claude  
**Stage:** Requirements – Problem summary, stakeholders, functional and non-functional requirements, user stories  
**Verdict:** Fixed

```text
I am responsible for **Person 2** in our PayLink Year Work Project.

I want you to complete the entire Person 2 task professionally and prepare the actual project deliverables in the correct format and directory structure.

## 1. First, Understand the Project

Carefully read the original project brief:

**PayLink Year Work Project - Student Brief (1).pdf**

Use this PDF as the primary source of truth. Do not invent requirements or change the business rules, priorities, or specifications provided in it.

Also, inspect the existing project repository and its directory structure before making any changes. Follow the project's existing conventions and coordinate with the work already done by other team members.

## 2. My Responsibilities (Person 2)

I am responsible for the following Phase 1 deliverables:

### A. Problem Summary
Write a clear and professional problem summary explaining:
* What PayLink is.
* The problem the system is intended to solve.
* The main purpose and objectives of the system.
* The major challenges the system must handle.
* The expected system capabilities.

### B. Stakeholders
Identify all relevant stakeholders and describe:
* Who they are.
* Their roles in the system.
* Their main responsibilities and interests.

### C. Functional Requirements
Create **at least 25 user stories** based on the functional requirements in the original project brief.

Important:
* Include all 28 functional requirements (FR-1 through FR-28).
* Do not omit or weaken any requirement marked Must.
* Preserve the original Must, Should, and Could priorities exactly.
* Write every requirement using the standard user story format:

"As a [user/role], I want [feature/action], so that [benefit]."

For every user story, provide clear and testable acceptance criteria using the **Given / When / Then** format.

Make sure the acceptance criteria accurately reflect the original project specifications and business rules.

### D. Non-Functional Requirements
Include all six non-functional requirements specified in the brief:

1. Correctness
2. Performance
3. Resilience
4. Modifiability
5. Auditability
6. Testability

For each requirement, provide:
* A clear description.
* A measurable target.
* How the requirement can be verified or tested.

Preserve the exact targets specified in the project brief, including:
* Zero double charges and zero negative balances.
* 100 payments per second sustained for one minute.
* A biller outage must not stop payments to other billers.
* Adding a biller must only affect the Biller Integration module and configuration.
* 100% of money movements must have audit records.
* At least 70% line coverage on domain logic, measured in CI.

### E. Business Rules and Constraints
Include the business rules from the original brief, such as:
* Daily payment limits for Basic and Verified accounts.
* Minimum and maximum payment amounts.
* Service fees for electricity, water, mobile top-ups, and internet.
* Promotion rules.
* Agent commission.
* Velocity rule.
* Biller timeout.
* Payment retry schedule.
* Notification retry rules.
* Exact decimal types for monetary calculations.

Present them in a clear, organized table.

### F. Ambiguities and Open Questions
Identify unclear or unspecified points in the original brief.

For each one:
* State the ambiguity clearly.
* Explain why it matters.
* Suggest a possible decision or implementation approach.
* Clearly distinguish confirmed requirements from proposed assumptions.

Do not silently invent missing business rules or present assumptions as official requirements.

## 3. Required Deliverables and File Structure

Create the actual files inside the existing repository.

The expected structure should be:

docs/
├── requirements.md
└── requirements.pdf

If the repository already uses a different requirements-document structure, inspect it and follow the existing convention without unnecessarily changing unrelated files.

### requirements.md
This must be the complete, well-structured Markdown version of the requirements document.

### requirements.pdf
Generate a professional PDF containing the same complete content as the Markdown document.

The PDF must be readable, properly formatted, and suitable for university submission.

## 4. Documentation Quality Requirements

The final document must:
* Have a professional academic and software engineering documentation style.
* Use consistent headings and numbering.
* Use tables wherever they improve readability.
* Use consistent requirement IDs (FR-1 to FR-28, NFR-1 to NFR-6, etc.).
* Clearly distinguish Functional Requirements from Non-Functional Requirements.
* Include a table of contents if appropriate.
* Avoid unnecessary repetition and vague descriptions.
* Use correct English grammar and technical terminology.
* Be detailed enough for a software engineering project submission.
* Keep all information consistent with the original PDF.

Do not produce a short summary instead of the actual deliverables.

## 5. Important Validation Before Finishing

Before considering the task complete:

1. Verify that all 28 functional requirements are included.
2. Verify that every user story has Given / When / Then acceptance criteria.
3. Verify that all original priorities are preserved.
4. Verify that all six non-functional requirements and their exact measurable targets are included.
5. Verify that all business rules and numerical values match the original brief.
6. Verify that the ambiguities section does not contradict the official requirements.
7. Verify that the Markdown and PDF contain the same information.
8. Verify that both files are saved in the correct location.
9. Do not modify other team members' files or unrelated project components.

## 6. Final Response

When finished, provide:

* A list of all files you created or modified.
* The exact paths of the deliverables.
* A brief summary of what you completed.
* Any assumptions or unresolved questions that require discussion with the team or instructor.
* Confirmation that you checked the document against the original project brief.

**Important:** Do not just tell me how to create these documents. Actually create the files in the project workspace and make them ready for review and submission.

(The message also repeated the Phase 1 submission requirements from the brief and the proposed task distribution: Person 1 – GitHub, Docker and GitHub Actions; Person 2 – problem summary, stakeholders, functional and non-functional requirements, user stories; Person 3 – domain class diagram and sequence diagrams; Person 4 – use case diagram, state diagram and ERD.)

```

### P12 – Requirements pack: comparison with another version

**Date:** 2026-11-23  
**Responsible person:** Person 2  
**AI tool / mode:** Claude  
**Stage:** Requirements – review  
**Verdict:** Accepted

Original message (Egyptian Arabic) with the attached file `requirements (2).pdf`:

```text
تب انهي احسن ده ولا بتاعك
```

English meaning: "OK, which is better, this one or yours?"

### P13 – Requirements pack: improve the AI version using the other file

**Date:** 2026-11-23  
**Responsible person:** Person 2  
**AI tool / mode:** Claude  
**Stage:** Requirements – improvement  
**Verdict:** Fixed

Original messages (Egyptian Arabic). The first one was sent after the AI offered to merge the two versions. The AI then started rebuilding from the user's other file instead of its own version, so the second message corrected the direction:

```text
تب اعملي انت الكلام ده
```

```text
لا عدل على النسخه بتعاتك انت من الملف التاني اللي انا باعتهولك
```

English meaning: "Then you do it" and "No, edit your own version using the other file I sent you."

### P14 – Requirements pack: scope check

**Date:** 2026-11-23  
**Responsible person:** Person 2  
**AI tool / mode:** Claude  
**Stage:** Requirements – scope  
**Verdict:** Accepted

```text
انت عامل حاجات كتير هل كل الحاجات اللي انت عاملها دي مطلوبة ولا ايه اصل ده بس اللي مطلوب
problem definition and fun and non (all requirements
```

English meaning: "You did a lot of things. Is everything you did required? Because this is all that is required: problem definition and functional and non-functional (all requirements)."

### P15 – Requirements pack: trimmed final version

**Date:** 2026-11-23  
**Responsible person:** Person 2  
**AI tool / mode:** Claude  
**Stage:** Requirements – final version  
**Verdict:** Accepted after review

```text
اعمل
```

English meaning: "Do it" (in reply to the AI's offer to make a shorter version with only the required sections).

---

## 3. Questions raised by the AI during requirements analysis

While writing the requirements pack (P11 to P15) the AI found points where the brief is silent or can be read in more than one way. The final `docs/requirements.md` keeps the 12 most important ones (section 7, AMB-01 to AMB-12) with a proposed decision. **All of them are working assumptions that are still open** until the instructor or the team confirms them.

| ID | Question raised by the AI | Proposed decision (open) |
| --- | --- | --- |
| AMB-01 | What does "per day" mean for the daily limit (calendar day or rolling 24 hours, and which time zone)? | Calendar day in Africa/Cairo, timestamps stored in UTC |
| AMB-02 | Do the single-payment and daily limits apply to the bill amount or to the total with the fee, and which payments count? | Bill amount without the fee; Rejected, Failed and Refunded payments do not count |
| AMB-03 | How do users register and log in, and which roles exist? | Unique phone number and password, token authentication, four roles |
| AMB-04 | When an agent pays for a customer, whose wallet is charged and whose limits apply? | The customer's wallet and limits; the agent only receives the commission |
| AMB-05 | What is the scope and lifetime of the idempotency key, and what happens with the same key and different data? | Unique per customer, kept at least 24 hours, conflicting reuse is rejected |
| AMB-06 | What counts as a payment for the velocity rule, is the window sliding, and is the sixth payment itself rejected? | Sliding window, new attempts count, the sixth is rejected and the block starts |
| AMB-07 | Which rounding mode applies to the 1.5% fee and the 40% commission? | Two decimals, round half up |
| AMB-08 | How do promotion dates work (inclusive, one at a time) and what is the commission when the fee is 0? | Inclusive dates, one promotion at a time, commission 0.00 |
| AMB-09 | How long do we wait for the TopUpNet callback after "accepted"? | Stay in SentToBiller, then Unknown after a configurable waiting time |
| AMB-10 | From when are the re-queries at 1, 5 and 15 minutes counted, and does every biller offer a status query? | From the moment the payment became Unknown; mock billers offer a status query |
| AMB-11 | What is the settlement file format, and which differences may change a payment's state? | Daily CSV per biller; only the two rules of the brief change state, others are flagged |
| AMB-12 | FR-27 allows refunding a Completed payment, but Completed is final in the life cycle. How do they fit together? | A separate refund record; the payment stays Completed; confirm with the instructor |

---

## 4. Final files

The requirements files prepared for the team are:

- `docs/requirements.md` — Person 2
- `docs/requirements.pdf` — Person 2

The editable PlantUML design files prepared for the team are:

- `PayLink_Simple_Domain_Class_Diagram_Phase1.puml` —Abdelrhman Mohamed
- `PayLink_Pay_Bill_Sequence_Phase1.puml` —Abdelrhman Mohamed
- `PayLink_Refund_Failed_Payment_Sequence_Phase1.puml` — Abdelrhman Mohamed
- `PayLink_Payment_State_Diagram_Phase1.puml` —Youssef Mahmoud
- `PayLink_Relational_Database_ERD_Phase1.puml` — Youssef Mahmoud

**Review status:** The state diagram follows the specified payment lifecycle. The ERD has 15 core tables and includes primary keys, foreign keys, uniqueness and business-constraint notes. The ERD intentionally defers settlement/reconciliation tables (SettlementFile, SettlementRecord, ReconciliationReport and ReconciliationDifference); add them if the team implements FR-26. All five source files should still be rendered and visually inspected before submission.
