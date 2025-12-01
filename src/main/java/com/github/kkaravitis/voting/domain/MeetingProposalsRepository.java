package com.github.kkaravitis.voting.domain;

import java.util.Set;

/**
 * Provides the set of valid proposal identifiers for a given meeting.
 *
 * @author Konstantinos Karavitis
 */
public interface MeetingProposalsRepository {
    /**
     * Returns the set of valid proposal identifiers for the given meeting.
     *
     * @param meetingId the identifier of the meeting
     * @return a set of proposal identifiers, or {@code null} if the meeting is unknown
     */
    Set<String> getProposalsForMeeting(String meetingId);
}
