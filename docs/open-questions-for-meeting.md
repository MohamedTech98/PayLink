# PayLink Phase 1 - Open Questions for the Team Meeting

**Purpose:** these are points where the brief is silent or can be read in more than one way. They were found by the AI assistant while analysing the brief. For each one the team (or the instructor) decides and writes the **reason**. The "AI proposal" column is only a suggestion, not a decision.

**After the meeting:** copy each decision and reason into `docs/requirements.md` (section 7, change the status from *Open (proposed)* to *Decided*), and make sure the diagrams (class, sequence, state, ERD) follow the decisions.

## A. Example: network problems

The brief already decides the two main cases, so no decision is needed for them:

| Case | What the brief says | Where |
|---|---|---|
| The app sends the same payment twice because the network was slow | One payment, one charge; the second request returns the first result | HC-1, FR-10 |
| The biller does not answer within 10 seconds | The payment becomes Unknown, the customer is told it is being processed, the money stays held | HC-2, FR-13 |

The remaining details are in questions 5, 9 and 10 below.

## B. Questions to decide

| # | Question | Options | AI proposal and why | Team decision | Reason |
|---|---|---|---|---|---|
| 1 | What does "per day" mean for the daily limit? (AMB-01) | (a) calendar day in Cairo time; (b) rolling 24 hours | (a): simplest, matches "per day", easy to test | | |
| 2 | Do the limits (5 to 10,000 EGP, daily limit) apply to the bill amount or to the total with the fee? (AMB-02) | (a) bill amount only; (b) amount plus fee | (a): the fee is PayLink's charge, and the brief says a customer "may pay at most 5,000 EGP" | | |
| 3 | How do users register and log in, and which roles exist? (AMB-03) | (a) phone and password with a token and four roles; (b) simpler fixed role keys | (a): needed for admin, support and agent permissions and still simple | | |
| 4 | When an agent pays for a customer, whose wallet is charged and whose limits apply? (AMB-04) | (a) the customer's wallet and limits; (b) the agent's own wallet | (a): the agent only receives the commission, which is already credited to the agent's wallet | | |
| 5 | How does the idempotency key work (who creates it, how long it is kept, same key with different data)? (AMB-05) | (a) per customer, kept 24 hours, conflicting reuse rejected; (b) other rules | (a): prevents one customer's key blocking another's and still catches late duplicates | | |
| 6 | Velocity rule: what counts as a payment, is the window sliding, is the 6th payment rejected? (AMB-06) | (a) sliding window, every new attempt counts, 6th is rejected and the block starts; (b) fixed window or only completed payments | (a): stops bursts even if the attempts fail | | |
| 7 | Which rounding applies to the 1.5% fee and the 40% commission? (AMB-07) | (a) 2 decimals, round half up; (b) banker's rounding | (a): common rule, gives 1.85 for 1.85175 | | |
| 8 | Promotion: are the dates inclusive, can two run at once, what is the commission when the fee is 0? (AMB-08) | (a) inclusive dates, one at a time, commission 0.00; (b) commission still 40% of the normal fee | (a): the brief says commission is 40% of the service fee, and the fee is 0 | | |
| 9 | TopUpNet: how long do we wait for the callback after "accepted"? (AMB-09) | (a) stay SentToBiller, then Unknown after a configurable time; (b) Unknown immediately | (a): "minutes later" is normal for TopUpNet, so it should not be treated as a failure right away | | |
| 10 | The re-queries at 1, 5 and 15 minutes: counted from when, and do the mock billers offer a status query? (AMB-10) | (a) from the moment the payment became Unknown, with a status query; (b) from the previous attempt | (a): easier to test and to explain | | |
| 11 | Settlement file: which format, and which differences may change a payment's state? (AMB-11) | (a) daily CSV, only the two rules of the brief change state, others are flagged; (b) more automatic corrections | (a): the life cycle has no transition for the other cases, so they must be flagged only (HC-7) | | |
| 12 | FR-27 lets a customer refund a Completed payment, but Completed is final in the life cycle. (AMB-12) | (a) separate refund record, the payment stays Completed; (b) add a Completed to Refunded transition | (a): (b) would break FR-11 ("no other transitions"). **Ask the instructor to confirm.** | | |

## C. Smaller questions (optional, default in brackets)

| Question | Default if the team does not decide |
|---|---|
| What does a block prevent, and what happens to payments already in progress? | New payments are rejected; payments in progress continue; the customer can still view the wallet |
| In which order are the fraud checks evaluated? | Single-payment limits, daily limit, velocity, blocked account; the first failing reason is shown |
| Are Rejected payments notified by SMS? | No, the reason is in the immediate response; SMS and push only for Completed and Failed |
| Does "retried 3 times" mean 3 attempts or the first attempt plus 3 retries? | First attempt plus 3 retries |
| How long do the fee and total from an inquiry stay valid? | The fee is fixed when the payment is initiated |
| How are biller error codes shown to the customer? | Each adapter maps its codes to a common list of PayLink reasons |
| What happens if reconciliation never resolves an Unknown payment? | It stays Unknown with funds held and is listed as unresolved for support |
| Which language and framework do we use? (needed before the CI and test tools can be chosen) | Team decision with Person 1 |
