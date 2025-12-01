package com.github.kkaravitis.voting.domain;

/**
 * Represents a single vote cast by a shareholder in a specific meeting
 * for a given proposal.
 *
 * <p>A {@link Vote} is immutable and uniquely identified by the combination
 * of {@code shareholderId}, {@code meetingId}, and {@code proposalId}.</p>
 *
 * @param shareholderId the unique identifier of the shareholder casting the vote
 * @param meetingId     the unique identifier of the shareholder meeting
 * @param proposalId    the unique identifier of the proposal being voted on
 *
 * @author Konstantinos Karavitis
 */
public record Vote (String shareholderId, String meetingId, String proposalId) {}
