package com.github.kkaravitis.voting;

import com.github.kkaravitis.voting.adapter.InMemoryMeetingProposalsRepository;
import com.github.kkaravitis.voting.domain.InvalidProposalException;
import com.github.kkaravitis.voting.domain.Vote;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link VotingService}.
 *
 * <p>These tests verify the core business rules for validating shareholder votes:</p>
 * <ul>
 *     <li>Invalid proposals cause an {@link InvalidProposalException}.</li>
 *     <li>New votes are always accepted.</li>
 *     <li>Vote changes after the record date are rejected.</li>
 *     <li>Vote changes before the record date are accepted.</li>
 * </ul>
 *
 * @author Konstantinos Karavitis
 */
class VotingServiceTest {

    private VotingService votingService;

    /**
     * Verifies that when a vote references a proposal that is not valid
     * for the given meeting, the service throws {@link InvalidProposalException}.
     */
    @Test
    void invalidProposalShouldThrowInvalidProposalException() {
        // given
        final String meetingId = "MEETING1";
        Map<String, Set<String>> meetingProposals = Map.of(
              meetingId, Set.of("P1", "P2")
        );
        InMemoryMeetingProposalsRepository inMemoryMeetingProposalsRepository =
              new InMemoryMeetingProposalsRepository(meetingProposals);
        final String proposalId = "invalidProposal";
        votingService = new VotingService(inMemoryMeetingProposalsRepository);

        Vote vote = new Vote("shareHolderId", meetingId, proposalId);
        LocalDate recordDate = LocalDate.now();

        // when and then
        assertThrows(InvalidProposalException.class, () ->
              votingService.processVote(vote, new HashSet<>(), recordDate));
    }

    /**
     * Verifies that a new vote (from a shareholder who has not voted before)
     * is always accepted.
     */
    @Test
    void newVoteIsAlwaysAccepted() throws InvalidProposalException {
        // given
        final String meetingId = "MEETING1";
        final String proposalId = "P1";
        Map<String, Set<String>> meetingProposals = Map.of(
              meetingId, Set.of(proposalId)
        );
        InMemoryMeetingProposalsRepository inMemoryMeetingProposalsRepository =
              new InMemoryMeetingProposalsRepository(meetingProposals);

        votingService = new VotingService(inMemoryMeetingProposalsRepository);

        Vote vote = new Vote("newVoter", meetingId, proposalId);
        LocalDate recordDate = LocalDate.now();
        Set<String> existingVoters = Set.of("existingVoter");

        // when
        boolean result = votingService.processVote(vote, existingVoters, recordDate);

        //then
        assertTrue(result);
    }

    /**
     * Verifies that when a shareholder has already voted and the current date
     * is after the record date, an attempt to change the vote is rejected.
     */
    @Test
    void voteChangesAfterRecordDateAreRejected() throws InvalidProposalException {
        // given
        final String meetingId = "MEETING1";
        final String proposalId = "P1";
        Map<String, Set<String>> meetingProposals = Map.of(
              meetingId, Set.of(proposalId)
        );
        InMemoryMeetingProposalsRepository inMemoryMeetingProposalsRepository =
              new InMemoryMeetingProposalsRepository(meetingProposals);

        votingService = new VotingService(inMemoryMeetingProposalsRepository);

        String voter = "existingVoter";
        Vote vote = new Vote(voter, meetingId, proposalId);
        Set<String> existingVoters = Set.of(voter);
        LocalDate recordDate = LocalDate.now().minusDays(3);

        // when
        boolean result = votingService.processVote(vote, existingVoters, recordDate);

        //then
        assertFalse(result);
    }

    /**
     * Verifies that when a shareholder has already voted and the current date
     * is before the record date, an attempt to change the vote is accepted.
     */
    @Test
    void voteCanBeChangedWhenCurrentDateIsBeforeRecordDate() throws InvalidProposalException {
        // given
        final String meetingId = "MEETING1";
        final String proposalId = "P1";
        Map<String, Set<String>> meetingProposals = Map.of(
              meetingId, Set.of(proposalId)
        );
        InMemoryMeetingProposalsRepository inMemoryMeetingProposalsRepository =
              new InMemoryMeetingProposalsRepository(meetingProposals);

        votingService = new VotingService(inMemoryMeetingProposalsRepository);

        String voter = "existingVoter";
        Vote vote = new Vote(voter, meetingId, proposalId);
        Set<String> existingVoters = Set.of(voter);
        LocalDate recordDate = LocalDate.now().plusDays(2);

        // when
        boolean result = votingService.processVote(vote, existingVoters, recordDate);

        //then
        assertTrue(result);
    }
}