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

class VotingServiceTest {

    private VotingService votingService;

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

        // then
        assertThrows(InvalidProposalException.class, () ->
              votingService.processVote(vote, new HashSet<>(), recordDate));
    }

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