package com.github.kkaravitis.voting.domain;

/**
 * Exception thrown when a vote references a proposal that is not valid
 * for the given meeting.
 *
 * @author Konstantinos Karavitis
 */
public class InvalidProposalException extends Exception {

    static final String MESSAGE_TEMPLATE = "The proposal with proposal Id %s "
          + "for the meeting with meeting id %s is invalid";

    /**
     * Creates a new {@code InvalidProposalException} for the given proposal and meeting.
     *
     * @param proposalId the identifier of the proposal that was found to be invalid
     * @param meetingId  the identifier of the meeting for which the proposal is invalid
     */
    public InvalidProposalException(String proposalId, String meetingId) {
        super(String.format(MESSAGE_TEMPLATE, proposalId, meetingId));
    }
}
