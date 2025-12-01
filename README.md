# Voting Service

This project implements a simple voting domain service for shareholder meetings.

## Business Rules

The core logic is implemented in `VotingService.processVote(...)` and enforces:

- If a vote references a `proposalId` that is **not valid** for the given `meetingId`,
  an `InvalidProposalException` is thrown.
- Valid proposals per meeting are resolved via a `MeetingProposalsRepository`
  abstraction, with an in-memory implementation for this exercise.
- A **new vote** (shareholder has not voted before) is always accepted when the proposal is valid.
- An **existing vote** (shareholder has already voted) can be changed **only if**
  the current date is **before** the meeting’s `recordDate`.
- An attempt to change a vote **on or after** the `recordDate` is rejected.


The method returns:

- `true` – vote accepted (either a new vote or a valid change),
- `false` – invalid attempt to change an existing vote after the record date.

---

## Requirements

To build and run the project you need:

- **JDK 21**
- **Maven** (any recent 3.x version)

You can verify your setup with:

```bash
java -version
mvn -version
```

Make sure `java` points to a JDK 21 installation.

---

## How to Run

This project is exercised purely via unit tests.  
There is no standalone main application; the logic is verified by running the test suite.

From the project root, run:

```bash
mvn test
```

Maven will:

1. Download dependencies.
2. Compile the main sources and tests.
3. Run all unit tests.

If the tests pass, the voting logic is working as expected.

You can also run the tests from your IDE by right-clicking on the test class
(e.g. `VotingServiceTest`) and selecting **Run tests**.

---

## Project Structure

Packages are organized in a simple domain/adapters style:

- `com.github.kkaravitis.voting.domain`
    - `Vote` – value object representing a single vote.
    - `InvalidProposalException` – thrown when a proposal is not valid for a meeting.
    - `MeetingProposalsRepository` – domain abstraction for accessing valid proposals.
    - `VotingService` – domain service that validates / evaluates votes.
- `com.github.kkaravitis.voting.adapter`
    - `InMemoryMeetingProposalsRepository` – in-memory implementation of
      `MeetingProposalsRepository` used for this exercise and in tests.

---

## Example Usage

A minimal example of constructing and using the service:

```java
Map<String, Set<String>> meetingProposals = Map.of(
        "MEETING1", Set.of("P1", "P2"),
        "MEETING2", Set.of("P3", "P4")
);

MeetingProposalsRepository repository =
        new InMemoryMeetingProposalsRepository(meetingProposals);

VotingService votingService = new VotingService(repository);

Set<String> existingVoters = new HashSet<>();
LocalDate recordDate = LocalDate.now().plusDays(1);

Vote vote = new Vote("shareholder-1", "MEETING1", "P1");

boolean accepted = votingService.processVote(vote, existingVoters, recordDate);
```

---

## Assumptions & Simplifications
- Proposal data is stored in memory via `InMemoryMeetingProposalsRepository`.  
  In a real system this would likely be backed by a database or external service.

- `processVote` assumes non-null arguments; nulls are treated as programming
  errors and validated via `Objects.requireNonNull`.
