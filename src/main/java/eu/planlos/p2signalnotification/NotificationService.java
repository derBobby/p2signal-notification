package eu.planlos.p2signalnotification;

import eu.planlos.javapretixconnector.IPretixWebHookHandler;
import eu.planlos.javapretixconnector.model.PretixException;
import eu.planlos.javapretixconnector.model.Answer;
import eu.planlos.javapretixconnector.model.Booking;
import eu.planlos.javapretixconnector.model.Position;
import eu.planlos.javapretixconnector.model.Question;
import eu.planlos.javapretixconnector.model.dto.PretixSupportedActions;
import eu.planlos.javapretixconnector.model.dto.WebHookResult;
import eu.planlos.javapretixconnector.service.PretixBookingService;
import eu.planlos.javapretixconnector.service.PretixEventFilterService;
import eu.planlos.javasignalconnector.SignalService;
import eu.planlos.javasignalconnector.model.SignalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.planlos.javapretixconnector.model.QnaMapUtility.extractQnaMap;

/**
 * Core class of the application. Has callback interface for Pretix package and runs request against Nextcloud API
 */
@Slf4j
@Service
public class NotificationService implements IPretixWebHookHandler {

    private final PretixBookingService pretixBookingService;
    private final PretixEventFilterService pretixEventFilterService;
    private final SignalService signalService;

    public NotificationService(PretixBookingService pretixBookingService, PretixEventFilterService pretixEventFilterService, SignalService signalService) {
        this.pretixBookingService = pretixBookingService;
        this.pretixEventFilterService = pretixEventFilterService;
        this.signalService = signalService;
    }

    @Override
    public WebHookResult handleWebhook(String organizer, String event, String code, PretixSupportedActions action) {
        return processNotificationForRequest(action, organizer, event, code);
    }

    private WebHookResult processNotificationForRequest(PretixSupportedActions action, String organizer, String event, String code) {

        try {
            Booking booking = pretixBookingService.loadOrFetch(organizer, event, code);
            log.info("Order found: {}", booking);

            if (pretixEventFilterService.bookingNotWantedByAnyFilter(action, booking)) {
                return new WebHookResult(true, "Booking was not selected by any filter");
            }

            List<String> participantList = booking.getPositionList().stream()
                    .map(this::extractNameFromPosition)
                    .flatMap(Optional::stream)
                    .toList();
            String messageSubject = String.format("%s: %s from %s for %s", action.getDescription(), code, booking.getEmail(), event);
            String messageUrl = pretixBookingService.getOrderUrl(event, code);

            String signalMessage = buildMessage(messageSubject, participantList, messageUrl);

            signalService.sendMessageToRecipients(signalMessage);

            return new WebHookResult(true, String.format("Message was sent for %d participants", participantList.size()));

        } catch (SignalException e) {
            String errorMessage = String.format("Error sending signal message: %s", e.getMessage());
            log.error(errorMessage);
            return new WebHookResult(false, errorMessage);
        } catch (PretixException f) {
            String errorMessage = String.format("Error loading Booking from Pretix for order code %s: %s", code, f.getMessage());
            log.error(errorMessage);
            return new WebHookResult(false, errorMessage);
        }
    }

    private String buildMessage(String email, List<String> participantList, String url) {
        return String.format(
                "%s\\n\\n%s\\n\\n%s",
                email,
                String.join("\\n", participantList),
                url);
    }

    private Optional<String> extractNameFromPosition(Position position) {

        if(position.getProduct().getProductType().isAddon()) {
            return Optional.empty();
        }

        Map<Question, Answer> qnaMap = position.getQnA();
        Map<String, String> qnaStringMap = extractQnaMap(qnaMap);

        String firstname = qnaStringMap.get("Kind Vorname");
        String lastname = qnaStringMap.get("Kind Nachname");

        return Optional.of(String.format("- %s %s", firstname, lastname));
    }
}