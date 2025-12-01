package com.github.kkaravitis.voting.adapter;

import com.github.kkaravitis.voting.domain.MeetingProposalsRepository;
import java.util.Map;
import java.util.Set;

/**
 * In-memory implementation of {@link MeetingProposalsRepository} backed by a {@link Map}.
 */
public class InMemoryMeetingProposalsRepository implements MeetingProposalsRepository {

    private final Map<String, Set<String>> meetingProposals;

    /**
     * Creates a new {@link InMemoryMeetingProposalsRepository} with the given mapping
     * from meeting identifiers to their valid proposal identifiers.
     *
     * @param meetingProposals a map where each key is a meeting identifier and each value
     *                         is the set of valid proposal identifiers for that meeting
     * @throws NullPointerException if {@code meetingProposals} is {@code null}
     */
    public InMemoryMeetingProposalsRepository(Map<String, Set<String>> meetingProposals) {
        this.meetingProposals = Map.copyOf(meetingProposals);
    }

    /**
     * Returns the set of valid proposal identifiers for the specified meeting.
     *
     * @param meetingId the identifier of the meeting
     * @return the set of proposal identifiers for the meeting, or {@code null}
     *         if the meeting does not exist.
     */
    @Override
    public Set<String> getProposalsForMeeting(String meetingId) {
        return meetingProposals.get(meetingId);
    }
}

