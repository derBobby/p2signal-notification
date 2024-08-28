package eu.planlos.p2signalnotification;

import eu.planlos.javapretixconnector.model.Booking;
import eu.planlos.javapretixconnector.service.PretixBookingService;
import eu.planlos.javapretixconnector.service.PretixEventFilterService;
import eu.planlos.javasignalconnector.SignalService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.planlos.javapretixconnector.model.dto.PretixSupportedActions.ORDER_NEED_APPROVAL;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    PretixBookingService pretixBookingService;

    @Mock
    PretixEventFilterService pretixEventFilterService;

    @Mock
    SignalService signalService;

    @InjectMocks
    NotificationService notificationService;

    private final static Booking mockBooking = mock(Booking.class);

    private final static String HOOK_ORGANIZER = "organizer";
    private final static String HOOK_EVENT = "event";
    private final static String HOOK_CODE = "XCODE";
    private final static String EMAIL = "email@example.com";
    private final static String FIRSTNAME = "Firstname";
    private final static String LASTNAME = "Lastname";

    @BeforeAll
    public static void mockBooking() {
        when(mockBooking.getEmail()).thenReturn(EMAIL);
        when(mockBooking.getFirstname()).thenReturn(FIRSTNAME);
        when(mockBooking.getLastname()).thenReturn(LASTNAME);
    }

    /**
     * New Order tests
     */
    @Test
    public void orderApprovalRequired_notificationIsSent() {
        // Prepare
        //      objects
        //      methods
        when(pretixBookingService.loadOrFetch(HOOK_ORGANIZER, HOOK_EVENT, HOOK_CODE)).thenReturn(mockBooking);

        // Act
        notificationService.handleWebhook(HOOK_ORGANIZER, HOOK_EVENT, HOOK_CODE, ORDER_NEED_APPROVAL);

        // Check
        verify(signalService).sendMessageToRecipients(anyString());
    }
}