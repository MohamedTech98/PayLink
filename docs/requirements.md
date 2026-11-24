# Requirements Pack — Working Draft

> Status: initial placeholder. Complete and review this document with the team before the Phase 1 tag.

## Problem summary

PayLink is a backend bill-payment and wallet system. Customers use wallet funds to pay utility and telecom bills through external billers. The system must correctly handle holds, confirmations, failures, duplicate requests, notifications, and reconciliation.

## Stakeholders

- Customers
- Agents
- Support employees
- Administrators
- External billers
- PayLink operations and finance teams

## User stories

Add at least 25 stories. Each must have a priority (Must / Should / Could) and acceptance criteria in Given / When / Then form.

| ID | User story | Priority | Acceptance criteria |
|---|---|---|---|
| US-01 | TODO | Must | Given ... When ... Then ... |

## Non-functional requirements

Define at least 6 measurable NFRs. Start from the brief's targets and confirm measurement methods with the team:

| Quality | Target from brief | Measurement / evidence |
|---|---|---|
| Correctness | Zero double charges and zero negative balances in duplicate/concurrency tests | TODO |
| Performance | 100 payments/second sustained for 1 minute against mock billers | TODO |
| Resilience | An outage of one biller does not stop payments to other billers | TODO |
| Modifiability | Adding a biller touches only integration code and configuration | TODO |
| Auditability | 100% of money movements have an audit record with who, when and why | TODO |
| Testability | At least 70% line coverage on domain logic, measured in CI | TODO |

## Ambiguities and team decisions

Record each question, the options considered, the decision, its reason, and who approved it.

| Question / ambiguity | Options | Decision | Reason / owner |
|---|---|---|---|
| TODO | TODO | TODO | TODO |
