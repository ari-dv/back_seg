package Proyecto.MegaWeb2.__BackEnd.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async // se envía en segundo plano
    public void enviarCorreo(String destino, String asunto, String htmlContenido) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8"
            );

            helper.setTo(destino);
            helper.setSubject(asunto);

            // 🔥 ESTA LÍNEA SOLUCIONA TODO
            helper.setText(htmlContenido, true); // true = HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Error enviando correo HTML: " + e.getMessage());
        }
    }
}
