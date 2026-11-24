# Test Strategy — Working Draft

## Test levels
- Unit tests for domain rules and application services.
- Integration tests for persistence and API/database interactions.
- Automated tests for every hard case listed in the project brief.
- Performance test against mock billers for the 100 payments/second target.

## Money correctness
- Use `BigDecimal` for all monetary values.
- Test duplicate requests and concurrent wallet spending.
- Assert that available balances never become negative.
- Assert every movement is auditable.

## Coverage
Target at least 70% line coverage for domain logic, reported by CI. Define exclusions explicitly and do not inflate coverage by testing trivial code only.

## Required hard-case test checklist
- [ ] Duplicate payment returns the original result and charges once.
- [ ] ElectroCo timeout (12 seconds) leaves payment Unknown and funds held.
- [ ] AquaCo rejection fails payment, releases hold, and records reason.
- [ ] Two concurrent payments with insufficient combined balance: only one succeeds.
- [ ] SMS outage does not fail payment; retries are performed and eventual undelivered status is recorded.
- [ ] Repeated TopUpNet callback completes payment once; duplicate is ignored/logged.
- [ ] Settlement includes a locally Failed payment: discrepancy is flagged.
- [ ] New biller can be added by changing integration and configuration only.
