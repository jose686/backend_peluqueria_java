package com.peluqueria.backend.appointments.services;

import com.peluqueria.backend.appointments.entities.Appointment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "app.notification.provider", havingValue = "meta")
@Slf4j
public class MetaWhatsAppSenderServiceImpl implements NotificationSenderService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.meta.whatsapp.phone-number-id:}")
    private String phoneNumberId;

    @Value("${app.meta.whatsapp.access-token:}")
    private String accessToken;

    @Value("${app.meta.whatsapp.template.otp:otp_verification}")
    private String otpTemplateName;

    @Value("${app.meta.whatsapp.template.confirmation:appointment_confirmation}")
    private String confirmationTemplateName;

    @Override
    public void sendOtp(String telefono, String pin) {
        log.info("Enviando WhatsApp OTP {} a {}", pin, telefono);
        sendTemplateMessage(telefono, otpTemplateName, List.of(pin));
    }

    @Override
    public void sendAppointmentConfirmation(Appointment appointment) {
        String clienteNombre = appointment.getCustomer() != null ? appointment.getCustomer().getNombre() : appointment.getUser().getNombre();
        String fecha = appointment.getFecha().toString();
        String hora = appointment.getHoraInicio().toString();
        String servicio = appointment.getServiceItem().getNombre();
        String profesional = appointment.getWorker().getNombre();
        String telefono = appointment.getCustomer() != null ? appointment.getCustomer().getTelefono() : appointment.getUser().getTelefono();

        log.info("Enviando WhatsApp de confirmación de cita a {}", telefono);
        sendTemplateMessage(telefono, confirmationTemplateName, List.of(clienteNombre, fecha, hora, servicio, profesional));
    }

    private void sendTemplateMessage(String to, String templateName, List<String> parameters) {
        if (phoneNumberId == null || phoneNumberId.isEmpty() || accessToken == null || accessToken.isEmpty()) {
            log.warn("Meta WhatsApp API no configurada correctamente (falta phone-number-id o access-token). Mensaje no enviado.");
            return;
        }

        String url = "https://graph.facebook.com/v20.0/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // Estructura del payload JSON para WhatsApp Cloud API
        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", to);
        body.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName);
        
        Map<String, String> language = new HashMap<>();
        language.put("code", "es");
        template.put("language", language);

        List<Map<String, Object>> components = List.of(Map.of(
            "type", "body",
            "parameters", parameters.stream().map(param -> Map.of("type", "text", "text", param)).toList()
        ));
        template.put("components", components);
        body.put("template", template);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            log.info("WhatsApp enviado con éxito. Respuesta: {}", response.getBody());
        } catch (Exception e) {
            log.error("Error al enviar WhatsApp a través de la API de Meta: {}", e.getMessage(), e);
        }
    }
}
