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

    // The Spring Boot mail sender component for dispatching emails
    private final JavaMailSender mailSender;

    // Core method to send a Mime email with optional HTML support
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

    // Validation helper to identify missing or corrupted data strings
    private boolean isInvalid(String val) {
        return val == null || val.trim().isEmpty() || val.equalsIgnoreCase("null") || val.equalsIgnoreCase("undefined");
    }

    // Generates a stylized HTML email body for newly booked appointments
    public String buildAppointmentBookedBody(String recipientName, String doctorName,
                                             String patientName, String date, String time,
                                             String appointmentId, String specialty,
                                             String consultationType, String reason, String doctorNotes) {
        
        log.info("=== BREAKPOINT: buildAppointmentBookedBody ===");
        log.info("recipientName: {}", recipientName);
        log.info("doctorName: {}", doctorName);
        log.info("patientName: {}", patientName);
        log.info("date: {}", date);
        log.info("time: {}", time);
        log.info("appointmentId: {}", appointmentId);
        log.info("specialty: {}", specialty);
        log.info("consultationType: {}", consultationType);
        log.info("reason: {}", reason);
        log.info("doctorNotes: {}", doctorNotes);
        log.info("===============================================");
        
        if (isInvalid(specialty) || specialty.equalsIgnoreCase("General")) {
            if (!isInvalid(consultationType) && !consultationType.equalsIgnoreCase("In-Person")) {
                specialty = consultationType.replace(" Consultation", "");
            } else {
                specialty = "General";
            }
        }
        
        if (isInvalid(consultationType) || consultationType.equalsIgnoreCase("In-Person")) {
            consultationType = specialty + " Consultation";
        }

        if (isInvalid(doctorName)) doctorName = "Assigned Doctor";
        if (isInvalid(reason)) reason = "N/A";
        if (isInvalid(doctorNotes)) doctorNotes = "None provided";
        
        return """
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8"/>
                <style>
                  body {
                    font-family: 'Segoe UI', Arial, sans-serif;
                    background: linear-gradient(135deg, #e3f2fd, #fce4ec);
                    margin: 0;
                    padding: 0;
                  }

                  .wrap {
                    max-width: 650px;
                    margin: 40px auto;
                    background: #ffffff;
                    border-radius: 12px;
                    box-shadow: 0 8px 20px rgba(0,0,0,0.15);
                    overflow: hidden;
                  }

                  .hdr {
                    background: linear-gradient(135deg, #1a73e8, #6a11cb);
                    padding: 28px;
                    text-align: center;
                    color: #fff;
                  }

                  .hdr h1 {
                    margin: 0;
                    font-size: 22px;
                    letter-spacing: 0.5px;
                  }

                  .hdr p {
                    margin: 6px 0 0;
                    font-size: 13px;
                    opacity: 0.9;
                  }

                  .body {
                    padding: 30px;
                    color: #333;
                  }

                  .section {
                    margin-bottom: 22px;
                    padding: 16px;
                    border-radius: 10px;
                    background: #f9fbff;
                    border-left: 4px solid #1a73e8;
                  }

                  .section-title {
                    font-size: 13px;
                    font-weight: bold;
                    color: #1a73e8;
                    margin-bottom: 10px;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                  }

                  .row {
                    display: flex;
                    align-items: flex-start;
                    margin-bottom: 12px;
                    font-size: 14px;
                  }

                  .lbl {
                    font-weight: 600;
                    color: #555;
                    width: 140px;
                    flex-shrink: 0;
                  }

                  .val {
                    color: #222;
                    flex: 1;
                    word-wrap: break-word;
                  }

                  .badge {
                    background: #e3f2fd;
                    color: #1a73e8;
                    border-radius: 20px;
                    padding: 4px 10px;
                    font-size: 12px;
                    font-weight: bold;
                  }

                  .highlight {
                    background: linear-gradient(135deg, #fff8e1, #fff3cd);
                    padding: 14px;
                    border-radius: 8px;
                    font-size: 14px;
                    color: #856404;
                    margin-top: 20px;
                  }

                  .btn {
                    display: block;
                    text-align: center;
                    margin: 25px auto 10px;
                    padding: 12px;
                    width: 200px;
                    background: linear-gradient(135deg, #1a73e8, #6a11cb);
                    color: white;
                    text-decoration: none;
                    border-radius: 25px;
                    font-weight: bold;
                    font-size: 14px;
                  }

                  .ftr {
                    background: #f4f7fb;
                    text-align: center;
                    padding: 16px;
                    font-size: 12px;
                    color: #888;
                  }
                </style>
                </head>

                <body>
                <div class="wrap">

                  <div class="hdr">
                    <h1>\uD83E\uDE7A Appointment Confirmed</h1>
                    <p>OmniHealth Hospital System</p>
                  </div>

                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your appointment has been successfully confirmed. Here are the complete details for your upcoming consultation.</p>

                    <div class="section">
                      <div class="section-title">\uD83D\uDCCC Common Details</div>
                      <div class="row"><span class="lbl">Reference ID:</span><span class="badge">APT - %s</span></div>
                      <div class="row"><span class="lbl">Consultation Type:</span><span class="val">%s</span></div>
                    </div>

                    <div class="section">
                      <div class="section-title">\uD83D\uDC64 Patient Details</div>
                      <div class="row"><span class="lbl">Patient Name:</span><span class="val">%s</span></div>
                    </div>

                    <div class="section">
                      <div class="section-title">\uD83D\uDC68\u200D\u2695\uFE0F Doctor Details</div>
                      <div class="row"><span class="lbl">Doctor Name:</span><span class="val">%s</span></div>
                      <div class="row"><span class="lbl">Specialization:</span><span class="val">%s</span></div>
                    </div>

                    <div class="section">
                      <div class="section-title">\uD83D\uDCC5 Schedule Detail</div>
                      <div class="row"><span class="lbl">Date:</span><span class="val">%s</span></div>
                      <div class="row"><span class="lbl">Time:</span><span class="val">%s</span></div>
                    </div>

                    <div class="section">
                      <div class="section-title">\uD83D\uDCDD Additional Info</div>
                      <div class="row"><span class="lbl">Reason:</span><span class="val">%s</span></div>
                      <div class="row"><span class="lbl">Notes:</span><span class="val">%s</span></div>
                    </div>

                    <div class="highlight">
                      <strong>Reminder:</strong> Please arrive 15 minutes early. For online consultations, your meeting link will activate 15 minutes before the scheduled time.
                    </div>

                  </div>

                  <div class="ftr">
                    © 2026 OmniHealth Hospital Management System <br/>
                    This is an automated message. Please do not reply.
                  </div>

                </div>
                </body>
                </html>
                """.formatted(
                    recipientName, 
                    appointmentId, 
                    consultationType,
                    patientName, 
                    (doctorName.startsWith("Dr. ") ? doctorName : "Dr. " + doctorName).trim(), 
                    specialty, 
                    date, 
                    time, 
                    reason, 
                    doctorNotes
                );
    }

    // Generates a red-themed HTML email alert for cancelled medical appointments
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
                  <div class="hdr">Appointment Cancelled \u2013 OmniHealth</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your appointment (<strong>%s</strong>) with <strong>Dr. %s</strong>
                       on <strong>%s at %s</strong> has been cancelled.</p>
                    <p>Please rebook via the OmniHealth portal if needed.</p>
                  </div>
                  <div class="ftr">This is an automated message. Please do not reply.</div>
                </div></body></html>
                """.formatted(recipientName, appointmentId, doctorName, date, time);
    }

    // Generates a green-themed HTML email summarizing a completed consultation
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
                  <div class="hdr">Consultation Completed \u2013 OmniHealth</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your telemedicine consultation (<strong>%s</strong>) with <strong>Dr. %s</strong>
                       on <strong>%s</strong> is now complete.</p>
                    <p>Your prescription and medical summary are available in your patient portal.</p>
                    <p>Thank you for using OmniHealth. Stay healthy!</p>
                  </div>
                  <div class="ftr">This is an automated message. Please do not reply.</div>
                </div></body></html>
                """.formatted(recipientName, appointmentId, doctorName, date);
    }
    // Generates a welcome email for newly registered users on the platform
    public String buildUserRegisteredBody(String recipientName, String email) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8"/>
                <style>
                  body{
                    font-family: 'Segoe UI', Arial, sans-serif;
                    background: linear-gradient(135deg, #eef2ff, #f4f7fb);
                    margin:0;
                    padding:0
                  }
                  .wrap{
                    max-width:600px;
                    margin:40px auto;
                    background:#fff;
                    border-radius:12px;
                    box-shadow:0 8px 20px rgba(0,0,0,.08);
                    overflow:hidden
                  }
                  .hdr{
                    background: linear-gradient(135deg, #1a73e8, #4facfe);
                    padding:28px;
                    text-align:center;
                    color:#fff
                  }
                  .hdr h1{
                    margin:0;
                    font-size:22px;
                    letter-spacing:0.5px
                  }
                  .body{
                    padding:30px;
                    color:#333;
                    line-height:1.6
                  }
                  .highlight{
                    color:#1a73e8;
                    font-weight:bold
                  }
                  .row{
                    margin-bottom:12px;
                    display:flex;
                    align-items:baseline;
                  }
                  .lbl{
                    font-weight:bold;
                    color:#555;
                    width:160px;
                  }
                  .val{
                    color:#222;
                  }
                  .badge{
                    background:#e8f0fe;
                    color:#1a73e8;
                    border-radius:20px;
                    padding:4px 12px;
                    font-size:12px;
                    font-weight:bold;
                  }
                  .card{
                    background:#f8fbff;
                    border-radius:8px;
                    padding:16px;
                    margin-top:10px;
                  }
                  .cta{
                    text-align:center;
                    margin-top:24px;
                  }
                  .btn{
                    display:inline-block;
                    background:#1a73e8;
                    color:#fff;
                    padding:12px 22px;
                    border-radius:6px;
                    text-decoration:none;
                    font-weight:bold;
                    transition:0.3s;
                  }
                  .btn:hover{
                    background:#0c5ed7;
                  }
                  .ftr{
                    background:#f4f7fb;
                    text-align:center;
                    padding:16px;
                    font-size:12px;
                    color:#888
                  }
                </style>
                </head>
                
                <body>
                <div class="wrap">
                
                  <div class="hdr">
                    <h1>🏥 Welcome to OmniHealth</h1>
                  </div>
                
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                
                <p>
                  Welcome to the <span class="highlight">OmniHealth Hospital Management System</span>.
                  Your registration is complete, and your account has been successfully created.
                </p>
                
                <div class="card">
                  <div class="row"><span class="lbl">Account Name:</span><span class="val">%s</span></div>
                  <div class="row"><span class="lbl">Registered Email:</span><span class="val">%s</span></div>
                  <div class="row"><span class="lbl">Account Status:</span><span class="badge">Active</span></div>
                </div>
                
               <p style="margin-top:10px;">
  If you need any assistance, our support team is always ready to help.<br>
  Stay healthy !
</p>
                
                 <p style="margin-top:24px; padding: 12px; background: #e8f0fe; border-radius: 6px; color: #1a73e8; font-size: 14px;">
                                      <strong>Next Steps:</strong> You can now log into your Web Portal to book appointments, view prescriptions, or consult with our professionals.
                                    </p>
                                  </div>
                                  <div class="ftr" style="margin-top:-40px;">This is an automated message from OmniHealth. Please do not reply.</div>
                  </div>
                
                  
                
                </div>
                </body>
                </html>
                """.formatted(recipientName, recipientName, email);
    }

    // Generates a detailed transaction receipt for successful payments
    public String buildPaymentSuccessBody(String recipientName, String orderId, String amount, 
                                         String paymentId, String items, String date) {
        return """
                <!DOCTYPE html><html><head><meta charset="UTF-8"/>
                <style>
                  body{font-family:Arial,sans-serif;background:#f4f7fb;margin:0;padding:0}
                  .wrap{max-width:600px;margin:40px auto;background:#fff;border-radius:8px;
                        box-shadow:0 2px 8px rgba(0,0,0,.1);overflow:hidden}
                  .hdr{background:#0f9d58;padding:24px;text-align:center;color:#fff;font-size:20px}
                  .body{padding:28px;color:#333}
                  .row{margin-bottom:12px; display: flex; align-items: baseline;}
                  .lbl{font-weight:bold;color:#555;width:150px; flex-shrink: 0;}
                  .val{color: #222;}
                  .badge{background:#e8f0fe;color:#1a73e8;border-radius:4px;padding:2px 8px;font-size:13px; font-weight: bold;}
                  .success-badge{background:#e8f5e8;color:#0f9d58;border-radius:4px;padding:2px 8px;font-size:13px; font-weight: bold;}
                  .ftr{background:#f4f7fb;text-align:center;padding:14px;font-size:12px;color:#999}
                  .section-title{border-bottom: 2px solid #eef2f7; padding-bottom: 8px; margin-bottom: 16px; color: #1a73e8; font-weight: bold; text-transform: uppercase; font-size: 14px;}
                  .highlight{background:#f0f7ff;padding:16px;border-radius:8px;margin:16px 0;border-left:4px solid #1a73e8}
                </style></head><body>
                <div class="wrap">
                  <div class="hdr">Payment Successful \u2013 OmniHealth</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your payment has been <strong>successfully processed</strong>. Thank you for choosing OmniHealth for your healthcare needs.</p>
                    
                    <div class="highlight">
                      <p style="margin:0; color:#1a73e8; font-weight:bold;">Payment Confirmation</p>
                    </div>
                    
                    <div class="section-title">Transaction Details</div>
                    <div class="row"><span class="lbl">Order ID:</span><span class="badge">%s</span></div>
                    <div class="row"><span class="lbl">Payment ID:</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">Amount Paid:</span><span class="val" style="color:#0f9d58; font-weight:bold;">Rs. %s</span></div>
                    <div class="row"><span class="lbl">Payment Date:</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">Status:</span><span class="success-badge">SUCCESS</span></div>
                    
                    <div class="section-title">Payment For</div>
                    <div class="row"><span class="lbl">Items:</span><span class="val">%s</span></div>

                    <p style="margin-top:24px; padding: 12px; background: #f0f7ff; border-radius: 6px; color: #1a73e8; font-size: 14px;">
                      <strong>Next Steps:</strong> Your appointment booking has been confirmed. You will receive a separate email with your appointment details shortly.
                    </p>
                    
                    <p style="margin-top:16px; text-align: center; color: #666; font-size: 14px;">
                      Need help? Contact our support team at support@OmniHealth.com
                    </p>
                  </div>
                  <div class="ftr">This is an automated message from OmniHealth Hospital Management System. Please do not reply.</div>
                </div></body></html>
                """.formatted(recipientName, orderId, paymentId, amount, date, items);
    }

    // Generates an urgent notification email when a payment transaction fails
    public String buildPaymentFailureBody(String recipientName, String orderId, String amount, 
                                         String errorMessage, String date) {
        return """
                <!DOCTYPE html><html><head><meta charset="UTF-8"/>
                <style>
                  body{font-family:Arial,sans-serif;background:#f4f7fb;margin:0;padding:0}
                  .wrap{max-width:600px;margin:40px auto;background:#fff;border-radius:8px;
                        box-shadow:0 2px 8px rgba(0,0,0,.1);overflow:hidden}
                  .hdr{background:#d93025;padding:24px;text-align:center;color:#fff;font-size:20px}
                  .body{padding:28px;color:#333}
                  .row{margin-bottom:12px; display: flex; align-items: baseline;}
                  .lbl{font-weight:bold;color:#555;width:150px; flex-shrink: 0;}
                  .val{color: #222;}
                  .badge{background:#e8f0fe;color:#1a73e8;border-radius:4px;padding:2px 8px;font-size:13px; font-weight: bold;}
                  .error-badge{background:#fce8e6;color:#d93025;border-radius:4px;padding:2px 8px;font-size:13px; font-weight: bold;}
                  .ftr{background:#f4f7fb;text-align:center;padding:14px;font-size:12px;color:#999}
                  .section-title{border-bottom: 2px solid #eef2f7; padding-bottom: 8px; margin-bottom: 16px; color: #1a73e8; font-weight: bold; text-transform: uppercase; font-size: 14px;}
                  .highlight{background:#fef2f2;padding:16px;border-radius:8px;margin:16px 0;border-left:4px solid #d93025}
                </style></head><body>
                <div class="wrap">
                  <div class="hdr">Payment Failed \u2013 OmniHealth</div>
                  <div class="body">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>We encountered an issue while processing your payment. The transaction could not be completed successfully.</p>
                    
                    <div class="highlight">
                      <p style="margin:0; color:#d93025; font-weight:bold;">Payment Declined</p>
                    </div>
                    
                    <div class="section-title">Transaction Details</div>
                    <div class="row"><span class="lbl">Order ID:</span><span class="badge">%s</span></div>
                    <div class="row"><span class="lbl">Attempted Amount:</span><span class="val">Rs. %s</span></div>
                    <div class="row"><span class="lbl">Payment Date:</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">Status:</span><span class="error-badge">FAILED</span></div>
                    
                    <div class="section-title">Error Information</div>
                    <div class="row"><span class="lbl">Reason:</span><span class="val" style="color:#d93025;">%s</span></div>

                    <p style="margin-top:24px; padding: 12px; background: #fef2f2; border-radius: 6px; color: #d93025; font-size: 14px;">
                      <strong>What to do next:</strong><br>
                      1. Check your payment details and try again<br>
                      2. Ensure sufficient funds are available<br>
                      3. Contact your bank if the issue persists<br>
                      4. Try using a different payment method
                    </p>
                    
                    <p style="margin-top:16px; text-align: center; color: #666; font-size: 14px;">
                      Need assistance? Contact our support team at support@OmniHealth.com or call our helpline.
                    </p>
                  </div>
                  <div class="ftr">This is an automated message from OmniHealth Hospital Management System. Please do not reply.</div>
                </div></body></html>
                """.formatted(recipientName, orderId, amount, date, errorMessage);
    }
}
