package com.github.kkaravitis.voting;

import com.github.kkaravitis.voting.domain.InvalidProposalException;
import com.github.kkaravitis.voting.domain.MeetingProposalsRepository;
import com.github.kkaravitis.voting.domain.Vote;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Application service responsible for validating and processing shareholder votes.
 *
 * <p>This service enforces the following business rules:</p>
 * <ul>
 *     <li>A new vote (i.e. the shareholder has not voted before) is always accepted.</li>
 *     <li>An existing vote (i.e. the shareholder has already voted) can be changed
 *         only if the current date is strictly before the meeting's {@code recordDate}.</li>
 *     <li>An attempt to change a vote on or after the {@code recordDate} is rejected.</li>
 *     <li>A vote is rejected with an {@link InvalidProposalException} if the referenced
 *         {@code proposalId} is not valid for the given {@code meetingId}, according to
 *         the configured {@link MeetingProposalsRepository}.</li>
 * </ul>
 *
 * <p>The service itself is stateless and may be safely reused across threads as long as the
 * supplied {@link MeetingProposalsRepository} implementation is thread-safe.</p>
 *
 * @author Konstantinos Karavitis
 */
public class VotingService {

    private final MeetingProposalsRepository meetingProposalsRepository;

    /**
     * Creates a new {@code VotingService} that uses the given provider to resolve the valid proposals for a meeting.
     *
     * @param meetingProposalsRepository the provider used to look up valid proposal identifiers for a given meeting
     * @throws NullPointerException if {@code meetingProposalsProvider} is {@code null}
     */
    public VotingService(MeetingProposalsRepository meetingProposalsRepository) {
        Objects.requireNonNull(meetingProposalsRepository, "MeetingProposalsProvider must be provided");
        this.meetingProposalsRepository = meetingProposalsRepository;
    }

    /**
     * Processes an incoming {@link Vote} for a shareholder meeting and determines whether it should be accepted according to the business rules.
     *
     * <p>The vote is handled as follows:</p>
     * <ul>
     *     <li>If the proposal is not valid for the given meeting, an
     *         {@link InvalidProposalException} is thrown.</li>
     *     <li>If the shareholder has not voted before (i.e. their identifier is not present
     *         in {@code existingVoters}), the vote is accepted and {@code true} is returned.</li>
     *     <li>If the shareholder has already voted, the vote is treated as a change and is
     *         accepted only if the current date is strictly before {@code recordDate}.</li>
     *     <li>If the shareholder has already voted and the current date is on or after
     *         {@code recordDate}, the change is rejected and {@code false} is returned.</li>
     * </ul>
     *
     * @param vote           the vote to be processed
     * @param existingVoters the set of shareholder identifiers who have already voted
     * @param recordDate     the record date of the meeting; vote changes are allowed only before this date
     * @return {@code true} if the vote is accepted (either as a new vote or a valid change), {@code false} if it is
     * rejected as an invalid attempt to change an
     * existing vote
     * @throws InvalidProposalException if the proposal referenced by the vote is not valid for the given meeting
     */
    public boolean processVote(Vote vote,
          Set<String> existingVoters,
          LocalDate recordDate) throws InvalidProposalException {

        Objects.requireNonNull(vote, "vote must not be null");
        Objects.requireNonNull(existingVoters, "existingVoters must not be null");
        Objects.requireNonNull(recordDate, "recordDate must not be null");

        boolean isProposalValid =
              Optional.ofNullable(meetingProposalsRepository
                          .getProposalsForMeeting(vote.meetingId()))
                    .map(proposals -> proposals.contains(vote.proposalId()))
                    .orElse(false);
        if (!isProposalValid) {
            throw new InvalidProposalException(vote.proposalId(), vote.meetingId());
        }

        boolean varFiltersCg = existingVoters.contains(vote.shareholderId());

        if (!varFiltersCg) {
            return true;
        }

        return LocalDate.now().isBefore(recordDate);
    }
}
