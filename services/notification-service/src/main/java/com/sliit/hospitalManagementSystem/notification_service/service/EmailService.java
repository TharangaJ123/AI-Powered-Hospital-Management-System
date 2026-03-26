package com.sliit.hospitalManagementSystem.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body, boolean isHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, isHtml);
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email delivery failed: " + e.getMessage(), e);
        }
    }

    // ── HTML Template Builders ────────────────────────────────────────────────

    public String buildAppointmentBookedBody(String recipientName, String doctorName,
                                             String patientName, String date, String time,
                                             String appointmentId, String specialty) {
        return """
                <!DOCTYPE html><html><head><meta charset="UTF-8"/>
                <style>
                  body{font-family:Arial,sans-serif;background:#f4f7fb;margin:0;padding:0}
                  .wrap{max-width:600px;margin:40px auto;background:#fff;border-radius:8px;
                        box-shadow:0 2px 8px rgba(0,0,0,.1);overflow:hidden}
                  .hdr{background:#1a73e8;padding:24px;text-align:center;color:#fff;font-size:20px}
                  .body{padding:28px;color:#333}
                  .row{margin-bottom:10px}
                  .lbl{font-weight:bold;color:#555;min-width:140px;display:inline-block}
                  .badge{background:#e8f0fe;color:#1a73e8;border-radius:4px;padding:2px 8px;font-size:13px}
                  .ftr{background:#f4f7fb;text-align:center;padding:14px;font-size:12px;color:#999}
                </style></head><body>
                <div class="wrap">
                  <div class="hdr">✅ Appointment Confirmed \u2013 MediConnect</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your appointment has been successfully booked:</p>
                    <div class="row"><span class="lbl">Ref:</span><span class="badge">%s</span></div>
                    <div class="row"><span class="lbl">Patient:</span> %s</div>
                    <div class="row"><span class="lbl">Doctor:</span> Dr. %s</div>
                    <div class="row"><span class="lbl">Specialty:</span> %s</div>
                    <div class="row"><span class="lbl">Date:</span> %s</div>
                    <div class="row"><span class="lbl">Time:</span> %s</div>
                    <p style="margin-top:20px">Please be ready 5 minutes before your scheduled time.</p>
                  </div>
                  <div class="ftr">This is an automated message. Please do not reply.</div>
                </div></body></html>
                """.formatted(recipientName, appointmentId, patientName, doctorName,
                specialty, date, time);
    }

    public String buildAppointmentCancelledBody(String recipientName, String doctorName,
                                                String date, String time, String appointmentId) {
        return """
                <!DOCTYPE html><html><head><meta charset="UTF-8"/>
                <style>
                  body{font-family:Arial,sans-serif;background:#f4f7fb;margin:0;padding:0}
                  .wrap{max-width:600px;margin:40px auto;background:#fff;border-radius:8px;
                        box-shadow:0 2px 8px rgba(0,0,0,.1);overflow:hidden}
                  .hdr{background:#d93025;padding:24px;text-align:center;color:#fff;font-size:20px}
                  .body{padding:28px;color:#333}
                  .ftr{background:#f4f7fb;text-align:center;padding:14px;font-size:12px;color:#999}
                </style></head><body>
                <div class="wrap">
                  <div class="hdr">\u274C Appointment Cancelled \u2013 MediConnect</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your appointment (<strong>%s</strong>) with <strong>Dr. %s</strong>
                       on <strong>%s at %s</strong> has been cancelled.</p>
                    <p>Please rebook via the MediConnect portal if needed.</p>
                  </div>
                  <div class="ftr">This is an automated message. Please do not reply.</div>
                </div></body></html>
                """.formatted(recipientName, appointmentId, doctorName, date, time);
    }

    public String buildConsultationCompletedBody(String recipientName, String doctorName,
                                                 String date, String appointmentId) {
        return """
                <!DOCTYPE html><html><head><meta charset="UTF-8"/>
                <style>
                  body{font-family:Arial,sans-serif;background:#f4f7fb;margin:0;padding:0}
                  .wrap{max-width:600px;margin:40px auto;background:#fff;border-radius:8px;
                        box-shadow:0 2px 8px rgba(0,0,0,.1);overflow:hidden}
                  .hdr{background:#0f9d58;padding:24px;text-align:center;color:#fff;font-size:20px}
                  .body{padding:28px;color:#333}
                  .ftr{background:#f4f7fb;text-align:center;padding:14px;font-size:12px;color:#999}
                </style></head><body>
                <div class="wrap">
                  <div class="hdr">\uD83C\uDF93 Consultation Completed \u2013 MediConnect</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your telemedicine consultation (<strong>%s</strong>) with <strong>Dr. %s</strong>
                       on <strong>%s</strong> is now complete.</p>
                    <p>Your prescription and medical summary are available in your patient portal.</p>
                    <p>Thank you for using MediConnect. Stay healthy!</p>
                  </div>
                  <div class="ftr">This is an automated message. Please do not reply.</div>
                </div></body></html>
                """.formatted(recipientName, appointmentId, doctorName, date);
    }
}
