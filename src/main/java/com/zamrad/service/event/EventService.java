package com.zamrad.service.event;

import com.zamrad.domain.events.Event;
import com.zamrad.domain.events.EventAdditionalDetails;
import com.zamrad.domain.events.EventSlot;
import com.zamrad.domain.events.EventStatus;
import com.zamrad.domain.profiles.Profile;
import com.zamrad.dto.events.*;
import com.zamrad.repository.EventRepository;
import com.zamrad.repository.EventSlotRepository;
import com.zamrad.repository.ProfileRepository;
import com.zamrad.service.artist.ArtistPitchExists;
import com.zamrad.service.artist.ArtistProfileNotFoundException;
import com.zamrad.service.artist.ProfileService;
import com.zamrad.service.booker.BookerProfileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static com.zamrad.service.event.EventDtoMappers.EVENT_DTO_MAPPER;
import static com.zamrad.service.event.EventDtoMappers.EVENT_SLOT_DTO_MAPPER;
import static java.util.stream.Collectors.toList;

@Component
public class EventService {
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final EventSlotRepository eventSlotRepository;
    private final ProfileRepository profileRepository;

    @Autowired
    public EventService(EventRepository eventRepository, EventSlotRepository eventSlotRepository, ProfileRepository profileRepository, ProfileService profileService) {
        this.eventRepository = eventRepository;
        this.eventSlotRepository = eventSlotRepository;
        this.profileRepository = profileRepository;
    }

    public EventDto getEvent(UUID eventId) {
        final Optional<Event> event = Optional.ofNullable(eventRepository.getOne(eventId));

        if (event.isPresent()) {
            return EVENT_DTO_MAPPER.apply(event.get());
        }

        throw new EventNotFoundException("Event does not exist.");
    }

    public Event createEvent(NewEventDto newEventDto, Long facebookId) {
        final Optional<Profile> profileOptional = Optional.ofNullable(profileRepository.findByFacebookId(facebookId));

        if (profileOptional.isPresent()) {
            final Profile profile = profileOptional.get();

            if (profile.getProfileType().equals("booker") && newEventDto.getNewEventSlotDto() != null) {
                final Event event = createNewEvent(newEventDto, profile);
                return eventRepository.save(event);
            }
            throw new UnauthorisedEventCreationException("An event can only be created by a booker.");
        }

        throw new BookerProfileNotFoundException("Booker profile does not exist.");
    }

    private Event createNewEvent(NewEventDto newEventDto, Profile profile) {
        final Event event = Event.builder()
                .profile(profile)
                .status(EventStatus.PENDING.getValue())
                .title(newEventDto.getTitle())
                .address(newEventDto.getAddress())
                .date(LocalDate.now(Clock.systemUTC()))
                .description(newEventDto.getDescription())
                .paymentType(newEventDto.getPaymentType())
                .venueType(newEventDto.getVenueType())
                .venueCapacity(Long.valueOf(newEventDto.getVenueCapacity()))
                .build();

        final EventSlot newEventSlot = createNewEventSlot(newEventDto.getNewEventSlotDto());

        Set<EventSlot> eventSlots = new HashSet<>();
        eventSlots.add(newEventSlot);
        newEventSlot.setEvent(event);
        event.setEventSlots(eventSlots);

        final EventAdditionalDetailsDto eventAdditionalDetailsDto = newEventDto.getEventAdditionalDetailsDto();
        if (eventAdditionalDetailsDto != null) {
            EventAdditionalDetails eventAdditionalDetails = EventAdditionalDetails.builder()
                    .ageRestriction(eventAdditionalDetailsDto.getAgeRestriction().charAt(0))
                    .djBooth(eventAdditionalDetailsDto.getDjBooth().charAt(0))
                    .noiseRestriction(eventAdditionalDetailsDto.getNoiseRestriction().charAt(0))
                    .paSystem(eventAdditionalDetailsDto.getPaSystem().charAt(0))
                    .soundEngineer(eventAdditionalDetailsDto.getSoundEngineer().charAt(0))
                    .wheelchair(eventAdditionalDetailsDto.getWheelchair().charAt(0))
                    .stageDepth(Long.valueOf(eventAdditionalDetailsDto.getStageDepth()))
                    .stageWidth(Long.valueOf(eventAdditionalDetailsDto.getStageWidth()))
                    .stageLighting(eventAdditionalDetailsDto.getStageLighting().charAt(0))
                    .venueCapacity(Long.valueOf(eventAdditionalDetailsDto.getVenueCapacity()))
                    .build();

            event.setEventAdditionalDetails(eventAdditionalDetails);
        }

        return event;
    }

    public EventSlot addEventSlot(UUID eventId, NewEventSlotDto newEventSlotDto) {
        final EventSlot eventSlot = createNewEventSlot(newEventSlotDto);

        Event event = getSingleEvent(eventId);

        Set<EventSlot> currentEventSlots = event.getEventSlots();
        if (currentEventSlots == null || currentEventSlots.size() == 0) {
            currentEventSlots = new HashSet<>();
        }

        currentEventSlots.add(eventSlot);
        event.setEventSlots(currentEventSlots);

        eventRepository.save(event);

        return eventSlot;
    }

    private Event getSingleEvent(UUID eventId) {
        final Optional<Event> eventOptional = Optional.ofNullable(eventRepository.getOne(eventId));

        Event event;

        if (eventOptional.isPresent()) {
            event = eventOptional.get();
        } else {
            throw new EventNotFoundException("Event not found.");
        }

        return event;
    }

    private EventSlot createNewEventSlot(NewEventSlotDto newEventSlotDto) {
        return EventSlot.builder()
                .status(EventStatus.PENDING.getValue())
                .artistType(newEventSlotDto.getArtistType())
                .endTime(newEventSlotDto.getEndTime())
                .startTime(newEventSlotDto.getStartTime())
                .genre(newEventSlotDto.getGenre())
                .price(new BigDecimal(newEventSlotDto.getSlotFee()))
                .build();
    }

    public List<ArtistCalendarEvent> getArtistsEvents(UUID artistId) {
        final Optional<Profile> artistProfileOptional = Optional.ofNullable(profileRepository.getOne(artistId));

        if (artistProfileOptional.isPresent()) {
            final Profile profile = artistProfileOptional.get();

            final List<EventSlot> allSlotsPitchedFor = eventSlotRepository.findByPitchers(profile);
            final List<Event> events = allSlotsPitchedFor.stream().map(eventRepository::findByEventSlots).collect(toList());

            return events.stream().map(event -> createArtistCalendarEventDto(event, profile.getId())).collect(toList());
        }
        throw new ArtistProfileNotFoundException("Artist does not exist.");
    }

    public List<BookerCalendarEvent> getBookerEvents(UUID bookerId) {
        final Optional<Profile> artistProfileOptional = Optional.ofNullable(profileRepository.getOne(bookerId));

        if (artistProfileOptional.isPresent()) {
            final Profile profile = artistProfileOptional.get();

            if (profile.getProfileType().equals("booker")) {
                final List<Event> events = eventRepository.findByProfile(profile);
                return events.stream().map(event -> createBookerCalendarEventDto(event, profile.getId())).collect(toList());
            }
        }
        throw new ArtistProfileNotFoundException("Artist does not exist.");
    }

    private BookerCalendarEvent createBookerCalendarEventDto(Event event, UUID id) {
        final Stream<EventSlotDto> eventSlots = event.getEventSlots().stream().map(EVENT_SLOT_DTO_MAPPER);
        return BookerCalendarEvent.builder()
                .eventId(event.getId().toString())
                .eventPhotoUrl(event.getPhotoUrl())
                .eventTitle(event.getTitle())
                .eventDate(event.getDate().toString())
                .eventAddress(event.getAddress())
                .eventStatus(event.getStatus())
                .venueType(event.getVenueType())
                .eventSlots(eventSlots.collect(toList()))
                .build();
    }

    private Set<EventSlot> getEventSlots(UUID eventId) {
        final Optional<Event> event = Optional.ofNullable(eventRepository.getOne(eventId));
        if (event.isPresent()) return event.get().getEventSlots();
        else throw new EventNotFoundException("Event does not exist.");
    }

    private Event getEventDetails(UUID eventId) {
        Optional<Event> eventOptional = Optional.ofNullable(eventRepository.getOne(eventId));

        if (eventOptional.isPresent()) return eventOptional.get();
        else throw new EventNotFoundException("Event does not exist.");
    }

    public EventDto updateEvent(UUID eventId, UpdateEventDto updateEventDto) {
        final Event event = getEventDetails(eventId);

        if (!Objects.equals(event.getStatus(), EventStatus.DRAFT)) {
            updateEventDetails(updateEventDto, event);
        }

        return EVENT_DTO_MAPPER.apply(eventRepository.save(event));
    }

    /**
     * Updates details of a single event slot.
     *
     * @param eventId
     * @param eventSlotId
     * @param updateEventSlotDto
     * @return
     */
    public EventSlotDto updateEventSlot(UUID eventId, UUID eventSlotId, UpdateEventSlotDto updateEventSlotDto) {
        final Event event = getEventDetails(eventId);
        final Optional<EventSlot> eventSlot = event.getEventSlots().stream().filter(slot -> slot.getId().equals(eventSlotId)).findFirst();

        EventSlot eventSlotToUpdate;
        if (eventSlot.isPresent()) eventSlotToUpdate = eventSlot.get();
        else throw new EventSlotNotFoundException("Event slot not found.");

        updateEventSlotDetails(updateEventSlotDto, eventSlotToUpdate);

        final Event updatedEvent = eventRepository.save(event);
        final EventSlot updatedEventSlot = updatedEvent.getEventSlots().stream().filter(slot -> slot.getId().equals(eventId)).findFirst().get();
        return EVENT_SLOT_DTO_MAPPER.apply(updatedEventSlot);
    }

    private void updateEventSlotDetails(UpdateEventSlotDto updateEventSlotDto, EventSlot eventSlotToUpdate) {
        if (updateEventSlotDto.getPrice() != null) eventSlotToUpdate.setPrice(updateEventSlotDto.getPrice());
        if (updateEventSlotDto.getArtistType() != null)
            eventSlotToUpdate.setArtistType(updateEventSlotDto.getArtistType());
        if (updateEventSlotDto.getStartTime() != null)
            eventSlotToUpdate.setStartTime(updateEventSlotDto.getStartTime());
        if (updateEventSlotDto.getEndTime() != null) eventSlotToUpdate.setEndTime(updateEventSlotDto.getEndTime());
    }

    private void updateEventDetails(UpdateEventDto updateEventDto, Event event) {
        if (updateEventDto.getAddress() != null) event.setAddress(updateEventDto.getAddress());
        if (updateEventDto.getDate() != null) event.setDate(LocalDate.parse(updateEventDto.getDate(), FORMATTER));
        if (updateEventDto.getDescription() != null) event.setDescription(updateEventDto.getDescription());
        if (updateEventDto.getTitle() != null) event.setTitle(updateEventDto.getTitle());
    }

    /**
     * Associates an artist to an event slot i.e. artist has pitched for that event slot.
     *
     * @param eventSlotId the slot within the event
     * @param profile     artist to associate
     */
    public void pitchForEventSlot(UUID eventSlotId, Profile profile) {
        final EventSlot eventSlot = getEventSlot(eventSlotId);
        Set<Profile> pitchers = eventSlot.getPitchers();

        if (pitchers == null || pitchers.size() == 0) {
            pitchers = new HashSet<>();
        }

        final boolean addition = pitchers.add(profile);
        if (!addition) throw new ArtistPitchExists("Artist has already pitched for this event slot.");

        eventSlot.setPitchers(pitchers);

        eventSlotRepository.save(eventSlot);
    }

    private EventSlot getEventSlot(UUID eventSlotId) {
        final Optional<EventSlot> eventSlotOptional = Optional.ofNullable(eventSlotRepository.findOne(eventSlotId));
        return eventSlotOptional.orElseThrow(() -> new EventNotFoundException("Event slot not found."));
    }

    //TODO Needs parameters
    public List<EventDto> getAllEvents() {
        if (eventRepository.count() > 0) {
            return eventRepository.findAll().stream().map(EVENT_DTO_MAPPER).collect(toList());
        }
        return Collections.emptyList();
    }

    /**
     * Retrieves a list of Pitch DTOs for a given event and event slot.
     *
     * @param eventSlotId event slot id with pitches.
     * @return list of pitches
     */
    public List<Pitch> getPitches(UUID eventSlotId) {
        final Optional<EventSlot> eventSlotOptional = Optional.ofNullable(eventSlotRepository.getOne(eventSlotId));

        if (eventSlotOptional.isPresent()) {
            final EventSlot eventSlot = eventSlotOptional.get();
            final Set<Profile> pitchers = eventSlot.getPitchers();

            final List<Pitch> pitches = new ArrayList<>();

            for (Profile pitcher : pitchers) {
                final Pitch pitch = Pitch.builder()
                        .artistId(pitcher.getId().toString())
                        .artistName(String.format("%s %s", pitcher.getFirstName(), pitcher.getSecondName()))
                        .eventSlotFee(eventSlot.getPrice().toString())
                        .eventSlotId(eventSlotId.toString())
                        .photoUrl(pitcher.getPhotoUrl())
                        .build();
                pitches.add(pitch);
            }

            return pitches;
        } else {
            throw new EventSlotNotFoundException("Event slot not found.");
        }
    }

    /**
     * Filter through event slots and returns those which artist has pitched for.
     *
     * @param event    event with slots pitched for
     * @param artistId id of artist/pitcher
     * @return ArtistCalendarEvent - dto to be used when dispaying calendar events
     */
    private ArtistCalendarEvent createArtistCalendarEventDto(Event event, UUID artistId) {

        final List<EventSlot> eventSlots = event.getEventSlots().stream().filter(eventSlot -> hasArtistPitched(eventSlot, artistId)).collect(toList());

        return ArtistCalendarEvent.builder()
                .eventId(event.getId().toString())
                .eventTitle(event.getTitle())
                .eventAddress(event.getAddress())
                .eventDate(event.getDate().toString())
                .eventStatus(event.getStatus())
                .venueType(event.getVenueType())
                .eventPhotoUrl(event.getPhotoUrl())
                .eventSlots(eventSlots.stream().map(EVENT_SLOT_DTO_MAPPER).collect(toList()))
                .build();
    }

    /**
     * Retrieves list of pitchers for each event slot within an event
     *
     * @param event event with event slots
     * @return
     */
    private Map<EventSlot, Set<Profile>> getEventSlotPitcherMapping(Event event) {
        Map<EventSlot, Set<Profile>> mapping = new HashMap<>();
        event.getEventSlots().forEach(eventSlot -> mapping.put(eventSlot, eventSlot.getPitchers()));
        return mapping;
    }

    /**
     * Checks if the artist has pitched for the event slot
     *
     * @return whether artist has pitched for the event slot in question
     */
    private boolean hasArtistPitched(EventSlot eventSlot, UUID artistId) {
        final Optional<Profile> pitcher = eventSlot.getPitchers()
                .stream()
                .filter(artistProfile -> artistProfile.getId() == artistId)
                .findFirst();

        return pitcher.map(artistProfile -> true).orElse(false);
    }

    public void deleteEvent(UUID eventId) {
        try {
            eventRepository.delete(eventId);
        } catch (EmptyResultDataAccessException e) {
            throw new EventNotFoundException("Event does not exist.");
        }
    }

    public void deleteEventSlot(UUID eventSlotId) {
        final EventSlot eventSlot = eventSlotRepository.getOne(eventSlotId);
        final int numberOfEventSlots = eventSlot.getEvent().getEventSlots().size();

        if (numberOfEventSlots > 1) eventSlotRepository.delete(eventSlotId);
        else
            throw new RuntimeException("Event slot cannot be deleted - event has only one slot. Please delete event instead.");
    }

    public void confirmPitch(UUID eventSlotId, UUID artistId) {
        final Optional<EventSlot> eventSlotOptional = Optional.ofNullable(eventSlotRepository.getOne(eventSlotId));

        if (eventSlotOptional.isPresent()) {
            final EventSlot eventSlot = eventSlotOptional.get();

            final Optional<Profile> pitcherOptional = eventSlot.getPitchers().stream().filter(artist -> artist.getId().equals(artistId)).findFirst();

            if (pitcherOptional.isPresent()) {
                eventSlot.setStatus(EventStatus.CONFIRMED.getValue());
            } else {
                throw new ArtistProfileNotFoundException("Artist is not a pitcher for this event slot.");
            }
        }

        throw new EventSlotNotFoundException("Event slot not found.");
    }
}
