package com.salesmanager.test.business.services;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.business.modules.email.Email;

/**
 * Mocking EmailService to verify email behavior without sending real emails
 */
public class EmailServiceMockTest {

    static class OrderNotificationService {
        private EmailService emailService;

        public OrderNotificationService(EmailService emailService) {
            this.emailService = emailService;
        }

        public void sendOrderConfirmation(String customerEmail, String orderId,
                double totalAmount, MerchantStore store) throws ServiceException, Exception {

            if (customerEmail == null || customerEmail.isEmpty()) {
                throw new ServiceException("Customer email is required");
            }
            if (totalAmount <= 0) {
                throw new ServiceException("Order amount must be positive");
            }

            Email email = new Email();
            email.setTo(customerEmail);
            email.setSubject("Order Confirmation: " + orderId);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("orderId", orderId);
            tokens.put("totalAmount", String.valueOf(totalAmount));
            tokens.put("storeName", store.getStorename());
            email.setTemplateTokens(tokens);

            emailService.sendHtmlEmail(store, email);
        }

        public void notifyAdminHighValueOrder(String adminEmail, String orderId,
                double totalAmount) throws ServiceException, Exception {

            if (totalAmount > 10000) {
                Email email = new Email();
                email.setTo(adminEmail);
                email.setSubject("High-value order alert: " + orderId);

                emailService.sendHtmlEmail(null, email);
            }
        }
    }

    @Mock
    private EmailService mockEmailService;

    @Mock
    private MerchantStore mockStore;

    private OrderNotificationService notificationService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new OrderNotificationService(mockEmailService);
        when(mockStore.getStorename()).thenReturn("Test Store");
    }

    @Test
    public void testOrderConfirmationEmailIsSent() throws Exception {
        String customerEmail = "customer@example.com";
        String orderId = "ORD-12345";
        double totalAmount = 99.99;

        notificationService.sendOrderConfirmation(customerEmail, orderId, totalAmount, mockStore);

        verify(mockEmailService, times(1)).sendHtmlEmail(any(MerchantStore.class), any(Email.class));
    }

    @Test
    public void testOrderConfirmationEmailContentIsCorrect() throws Exception {
        String customerEmail = "john@example.com";
        String orderId = "ORD-67890";
        double totalAmount = 299.50;

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);

        notificationService.sendOrderConfirmation(customerEmail, orderId, totalAmount, mockStore);

        verify(mockEmailService).sendHtmlEmail(any(MerchantStore.class), emailCaptor.capture());

        Email email = emailCaptor.getValue();
        assertEquals(customerEmail, email.getTo());
        assertTrue(email.getSubject().contains("ORD-67890"));
        assertEquals("ORD-67890", email.getTemplateTokens().get("orderId"));
    }

    @Test
    public void testOrderConfirmationThrowsExceptionForEmptyEmail() {
        assertThrows(ServiceException.class, () -> {
            notificationService.sendOrderConfirmation("", "ORD-111", 50.0, mockStore);
        });

        try {
            verify(mockEmailService, never()).sendHtmlEmail(any(), any());
        } catch (Exception e) {
        }
    }

    @Test
    public void testOrderConfirmationHandlesEmailException() throws Exception {
        doThrow(new Exception("SMTP error"))
                .when(mockEmailService)
                .sendHtmlEmail(any(), any());

        assertThrows(Exception.class, () -> {
            notificationService.sendOrderConfirmation("c@e.com", "ORD-333", 75.0, mockStore);
        });
    }

    @Test
    public void testAdminNotificationSentForHighValueOrder() throws Exception {
        notificationService.notifyAdminHighValueOrder("admin@s.com", "ORD-999", 15000.0);
        verify(mockEmailService, times(1)).sendHtmlEmail(isNull(), any(Email.class));
    }

    @Test
    public void testAdminNotificationNotSentForNormalOrder() throws Exception {
        notificationService.notifyAdminHighValueOrder("admin@s.com", "ORD-low", 5000.0);
        verify(mockEmailService, never()).sendHtmlEmail(any(), any());
    }
}
