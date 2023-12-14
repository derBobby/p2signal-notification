package eu.planlos.p2ncintegrator;

import eu.planlos.javapretixconnector.IPretixWebHookHandler;
import eu.planlos.javapretixconnector.model.Answer;
import eu.planlos.javapretixconnector.model.Booking;
import eu.planlos.javapretixconnector.model.Position;
import eu.planlos.javapretixconnector.model.Question;
import eu.planlos.javapretixconnector.model.dto.PretixSupportedActions;
import eu.planlos.javapretixconnector.model.dto.WebHookResult;
import eu.planlos.javapretixconnector.service.PretixBookingService;
import eu.planlos.javapretixconnector.service.PretixEventFilterService;
import eu.planlos.javasignalconnector.SignalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static eu.planlos.javapretixconnector.model.QnaMapUtility.extractQnaMap;
import static eu.planlos.javapretixconnector.model.dto.PretixSupportedActions.*;

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
    public WebHookResult handleWebhook(PretixSupportedActions action, String event, String code) {

        List<PretixSupportedActions> supportedActionsList = List.of(
                ORDER_PLACED, ORDER_NEED_APPROVAL, ORDER_APPROVED, ORDER_CANCELED);

        if(supportedActionsList.contains(action)) {
            return processNotificationForRequest(action.getAction(), event, code);
        }

        return new WebHookResult(true, "Notification has been sent.");
    }

    private WebHookResult processNotificationForRequest(String action, String event, String code) {

        try {
            Booking booking = pretixBookingService.loadOrFetch(event, code);
            log.info("Order found: {}", booking);


            List<Position> positionsList = booking.getPositionList();
            List<String> participantList = positionsList.stream()
                    .map(this::extractNameFromPosition)
                    .flatMap(Optional::stream)
                    .toList();

            String email = String.format("New order from: %s", booking.getEmail());
            String signalMessage = String.join(
                    "\\n",
                    Stream.concat(Stream.of(email),participantList.stream()).toList()
            );

            signalService.sendMessageToRecipients(signalMessage);

            return new WebHookResult(true, String.format("Message was sent for %d participants", participantList.size()));

        } catch (Exception e) {
            String errorMessage = String.format("Error sending signal message for order code %s: %s", code, e.getMessage());
            log.error(errorMessage);
            return new WebHookResult(false, errorMessage);
        }
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