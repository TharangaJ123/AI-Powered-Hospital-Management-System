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
                                             String appointmentId, String specialty,
                                             String consultationType, String reason, String doctorNotes) {
        return """
                <!DOCTYPE html><html><head><meta charset="UTF-8"/>
                <style>
                  body{font-family:Arial,sans-serif;background:#f4f7fb;margin:0;padding:0}
                  .wrap{max-width:600px;margin:40px auto;background:#fff;border-radius:8px;
                        box-shadow:0 2px 8px rgba(0,0,0,.1);overflow:hidden}
                  .hdr{background:#1a73e8;padding:24px;text-align:center;color:#fff;font-size:20px}
                  .body{padding:28px;color:#333}
                  .row{margin-bottom:12px; display: flex; align-items: baseline;}
                  .lbl{font-weight:bold;color:#555;width:150px; flex-shrink: 0;}
                  .val{color: #222;}
                  .badge{background:#e8f0fe;color:#1a73e8;border-radius:4px;padding:2px 8px;font-size:13px; font-weight: bold;}
                  .ftr{background:#f4f7fb;text-align:center;padding:14px;font-size:12px;color:#999}
                  .section-title{border-bottom: 2px solid #eef2f7; padding-bottom: 8px; margin-bottom: 16px; color: #1a73e8; font-weight: bold; text-transform: uppercase; font-size: 14px;}
                </style></head><body>
                <div class="wrap">
                  <div class="hdr">Appointment Summary \u2013 OminiHealth</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your appointment has been successfully confirmed. Here are the complete details for your upcoming consultation</p>
                    
                    <div class="section-title">Common Details</div>
                    <div class="row"><span class="lbl">Reference:</span><span class="badge">%s</span></div>
                    <div class="row"><span class="lbl">Consultation Type:</span><span class="val">%s</span></div>
                    
                    <div class="section-title">Patient Info</div>
                    <div class="row"><span class="lbl">Patient Name:</span><span class="val">%s</span></div>
                    
                    <div class="section-title">Doctor Info</div>
                    <div class="row"><span class="lbl">Doctor Name:</span><span class="val">Dr. %s</span></div>
                    <div class="row"><span class="lbl">Specialty:</span><span class="val">%s</span></div>
                    
                    <div class="section-title">Schedule</div>
                    <div class="row"><span class="lbl">Date:</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">Time:</span><span class="val">%s</span></div>
                    
                    <div class="section-title">Additional Info</div>
                    <div class="row"><span class="lbl">Reason for Visit:</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">Doctor Notes:</span><span class="val">%s</span></div>

                    <p style="margin-top:24px; padding: 12px; background: #fff8e1; border-radius: 6px; color: #856404; font-size: 14px;">
                      <strong>Note:</strong> Please arrive 15 minutes before your scheduled time. For online consultations, the link will be active 5 minutes prior.
                    </p>
                  </div>
                  <div class="ftr">This is an automated message from OminiHealth Hospital Management System. Please do not reply.</div>
                </div></body></html>
                """.formatted(recipientName, appointmentId, (consultationType != null ? consultationType : "General"),
                patientName, doctorName, specialty, date, time, 
                (reason != null ? reason : "N/A"), (doctorNotes != null ? doctorNotes : "None provided"));
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
                  <div class="hdr">Appointment Cancelled \u2013 OminiHealth</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your appointment (<strong>%s</strong>) with <strong>Dr. %s</strong>
                       on <strong>%s at %s</strong> has been cancelled.</p>
                    <p>Please rebook via the OminiHealth portal if needed.</p>
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
                  <div class="hdr">Consultation Completed \u2013 OminiHealth</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your telemedicine consultation (<strong>%s</strong>) with <strong>Dr. %s</strong>
                       on <strong>%s</strong> is now complete.</p>
                    <p>Your prescription and medical summary are available in your patient portal.</p>
                    <p>Thank you for using OminiHealth. Stay healthy!</p>
                  </div>
                  <div class="ftr">This is an automated message. Please do not reply.</div>
                </div></body></html>
                """.formatted(recipientName, appointmentId, doctorName, date);
    }
    public String buildUserRegisteredBody(String recipientName, String email) {
        return """
                <!DOCTYPE html><html><head><meta charset="UTF-8"/>
                <style>
                  body{font-family:Arial,sans-serif;background:#f4f7fb;margin:0;padding:0}
                  .wrap{max-width:600px;margin:40px auto;background:#fff;border-radius:8px;
                        box-shadow:0 2px 8px rgba(0,0,0,.1);overflow:hidden}
                  .hdr{background:#1a73e8;padding:24px;text-align:center;color:#fff;font-size:20px}
                  .body{padding:28px;color:#333}
                  .row{margin-bottom:12px; display: flex; align-items: baseline;}
                  .lbl{font-weight:bold;color:#555;width:150px; flex-shrink: 0;}
                  .val{color: #222;}
                  .badge{background:#e8f0fe;color:#1a73e8;border-radius:4px;padding:2px 8px;font-size:13px; font-weight: bold;}
                  .ftr{background:#f4f7fb;text-align:center;padding:14px;font-size:12px;color:#999}
                  .section-title{border-bottom: 2px solid #eef2f7; padding-bottom: 8px; margin-bottom: 16px; color: #1a73e8; font-weight: bold; text-transform: uppercase; font-size: 14px;}
                </style></head><body>
                <div class="wrap">
                  <div class="hdr">Welcome to OminiHealth!</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Welcome to the <strong>OminiHealth Hospital Management System</strong>. Your registration is complete, and your account has been successfully created.</p>
                    
                    <div class="section-title">Account Details</div>
                    <div class="row"><span class="lbl">Account Name:</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">Registered Email:</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">Status:</span><span class="badge">Active</span></div>
                    
                    <p style="margin-top:24px; padding: 12px; background: #e8f0fe; border-radius: 6px; color: #1a73e8; font-size: 14px;">
                      <strong>Next Steps:</strong> You can now log into your Web Portal to book appointments, view prescriptions, or consult with our professionals.
                    </p>
                  </div>
                  <div class="ftr">This is an automated message from OminiHealth. Please do not reply.</div>
                </div></body></html>
                """.formatted(recipientName, recipientName, email);
    }
}
